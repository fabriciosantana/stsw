#!/usr/bin/env python3
"""Executa o exemplo do WSL pela conexão do Testcontainers Desktop no Windows."""

import http.client
import json
import os
from pathlib import Path
import socket
import socketserver
import subprocess
import sys
import tempfile
import threading
from urllib.parse import urlsplit


def copiar(origem, destino):
    """Encaminha bytes, inclusive os streams da API Docker."""
    while True:
        dados = origem(65536)
        if not dados:
            return
        destino(dados)


def gravar(arquivo, dados):
    restantes = memoryview(dados)
    while restantes:
        escritos = arquivo.write(restantes)
        if not escritos:
            raise BrokenPipeError("A conexão local foi encerrada.")
        restantes = restantes[escritos:]


def ponte_windows():
    # O próprio aplicativo atualiza a porta neste arquivo quando é iniciado.
    propriedades = {}
    for linha in (Path.home() / ".testcontainers.properties").read_text().splitlines():
        chave, separador, valor = linha.partition("=")
        if separador:
            propriedades[chave.strip()] = valor.strip()
    endereco = urlsplit(propriedades.get("tc.host", ""))
    if endereco.scheme != "tcp" or endereco.hostname not in ("localhost", "127.0.0.1", "::1"):
        raise RuntimeError("O Testcontainers Desktop não publicou uma conexão local.")

    # O acesso ao Windows ocorre por stdin/stdout; nenhuma porta é exposta na rede.
    import msvcrt

    msvcrt.setmode(sys.stdin.fileno(), os.O_BINARY)
    msvcrt.setmode(sys.stdout.fileno(), os.O_BINARY)
    with socket.create_connection((endereco.hostname, endereco.port), timeout=10) as conexao:
        conexao.settimeout(None)

        def enviar():
            try:
                copiar(lambda tamanho: os.read(sys.stdin.fileno(), tamanho), conexao.sendall)
            except OSError:
                pass
            finally:
                try:
                    conexao.shutdown(socket.SHUT_WR)
                except OSError:
                    pass

        threading.Thread(target=enviar, daemon=True).start()
        copiar(conexao.recv, lambda dados: gravar(sys.stdout.buffer, dados))


def executar():
    pasta = Path(__file__).resolve().parent
    python_windows = Path("/mnt/c/Program Files/LibreOffice/program/python.exe")
    if not python_windows.is_file():
        raise RuntimeError("Não foi encontrado o Python incluído no LibreOffice instalado.")
    script_windows = subprocess.check_output(
        ["wslpath", "-w", str(Path(__file__).resolve())], text=True
    ).strip()

    class Ponte(socketserver.BaseRequestHandler):
        def handle(self):
            processo = subprocess.Popen(
                [str(python_windows), "-I", "-u", script_windows, "--ponte-windows"],
                stdin=subprocess.PIPE,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                bufsize=0,
            )

            def enviar():
                try:
                    copiar(self.request.recv, lambda dados: gravar(processo.stdin, dados))
                except (OSError, ValueError):
                    pass
                finally:
                    try:
                        processo.stdin.close()
                    except OSError:
                        pass

            threading.Thread(target=enviar, daemon=True).start()
            try:
                copiar(lambda tamanho: os.read(processo.stdout.fileno(), tamanho), self.request.sendall)
            except OSError:
                pass
            finally:
                if processo.poll() is None:
                    processo.terminate()
                processo.wait(timeout=5)
                erro = processo.stderr.read().decode(errors="replace")
                if processo.returncode not in (0, 1) and erro:
                    print(erro.strip(), file=sys.stderr)

    class Servidor(socketserver.ThreadingUnixStreamServer):
        daemon_threads = True

    with tempfile.TemporaryDirectory(prefix="testcontainers-dashboard-") as temporario:
        caminho_socket = str(Path(temporario) / "docker.sock")
        with Servidor(caminho_socket, Ponte) as servidor:
            os.chmod(caminho_socket, 0o600)
            threading.Thread(target=servidor.serve_forever, daemon=True).start()
            try:
                conexao = http.client.HTTPConnection("localhost", timeout=15)
                conexao.sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
                conexao.sock.settimeout(15)
                conexao.sock.connect(caminho_socket)
                conexao.request("GET", "/version")
                resposta = conexao.getresponse()
                versao = json.loads(resposta.read())
                conexao.close()
                if resposta.status != 200:
                    raise RuntimeError("O aplicativo não respondeu corretamente à API Docker.")
                print(f"Conectado ao Testcontainers Desktop. Docker {versao['Version']}.", flush=True)
                if "--verificar" in sys.argv:
                    return 0
                ambiente = os.environ.copy()
                ambiente["DOCKER_HOST"] = f"unix://{caminho_socket}"
                # Ryuk roda no Docker real; o socket temporário existe apenas no WSL.
                ambiente["TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE"] = "/var/run/docker.sock"
                resultado = subprocess.run(
                    ["mvn", "-B", "-o", "test"],
                    cwd=pasta,
                    env=ambiente,
                )
                if resultado.returncode == 0:
                    print("Dashboard: https://app.testcontainers.cloud/dashboard", flush=True)
                return resultado.returncode
            finally:
                servidor.shutdown()


if __name__ == "__main__":
    try:
        if "--ponte-windows" in sys.argv:
            ponte_windows()
        else:
            sys.exit(executar())
    except (OSError, ValueError, RuntimeError, subprocess.SubprocessError) as erro:
        print(f"Falha na conexão com o Testcontainers Desktop: {erro}", file=sys.stderr)
        sys.exit(1)
