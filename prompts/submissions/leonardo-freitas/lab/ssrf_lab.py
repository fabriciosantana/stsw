#!/usr/bin/env python3
"""Lab SSRF estritamente local; nenhum serviço externo é requisitado.

Demonstra o problema de confiança: uma interface HTTP acessível ao cliente
pede ao próprio servidor para buscar um recurso de outro serviço local.
Não use em redes externas e não exponha as portas a interfaces públicas.
"""
from __future__ import annotations
import argparse
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from threading import Thread
from urllib.parse import parse_qs, quote, urlsplit
from urllib.request import Request, build_opener, ProxyHandler, HTTPRedirectHandler
from urllib.error import HTTPError, URLError

FRONTEND = 19080
INTERNAL = 19081
ASSETS = 19082
INTERNAL_URL = f"http://127.0.0.1:{INTERNAL}/admin/status"
PUBLIC_URL = f"http://127.0.0.1:{ASSETS}/image/demo.png"


class NoRedirects(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None

OPENER = build_opener(ProxyHandler({}), NoRedirects())


class Handler(BaseHTTPRequestHandler):
    server_version = "LocalSSRFDemo/1.0"

    def log_message(self, fmt, *args):
        # Sem logs pessoais: a evidência da demonstração está no stdout da CLI.
        return

    def reply(self, status: int, payload: str):
        output = payload.encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "text/plain; charset=utf-8")
        self.send_header("Content-Length", str(len(output)))
        self.end_headers()
        self.wfile.write(output)

    def do_GET(self):
        u = urlsplit(self.path)
        port = self.server.server_address[1]
        if port == INTERNAL:
            if u.path == "/admin/status":
                self.reply(200, "INTERNAL-ONLY: exemplo ficticio de dados administrativos")
            else:
                self.reply(404, "Nao encontrado")
            return
        if port == ASSETS:
            if u.path == "/image/demo.png":
                self.reply(200, "PUBLIC-ASSET: imagem de demonstracao (conteudo ficticio)")
            elif u.path == "/redirect":
                # Simula um redirect do host aparentemente confiável.
                self.send_response(302)
                self.send_header("Location", INTERNAL_URL)
                self.end_headers()
            else:
                self.reply(404, "Nao encontrado")
            return
        if port != FRONTEND:
            self.reply(404, "Servico nao identificado")
            return
        if u.path not in ("/fetch", "/safe-fetch"):
            self.reply(404, "Use /fetch ou /safe-fetch")
            return
        uri = parse_qs(u.query).get("url", [""])[0]
        parsed = urlsplit(uri)
        # LAB: restringe QUALQUER alvo a duas portas local-only, mas /fetch
        # AINDA E VULNERAVEL: permite que o cliente escolha serviço interno.
        in_scope = (parsed.scheme == "http" and parsed.hostname == "127.0.0.1"
                    and parsed.port in (INTERNAL, ASSETS))
        if not in_scope:
            self.reply(400, "Fora do escopo estrito do laboratorio")
            return
        if u.path == "/safe-fetch":
            # Defesa *ilustrativa* por URL exata: host, porta e caminho.
            # Para produção também é necessário controle de egress e DNS.
            if uri != PUBLIC_URL:
                self.reply(403, "BLOCKED: destino nao permitido pela allowlist")
                return
        try:
            req = Request(uri, headers={"User-Agent": "SSRF-LocalLab/1"})
            with OPENER.open(req, timeout=2) as response:
                self.reply(response.status, response.read(16384).decode("utf-8"))
        except HTTPError as err:
            self.reply(502, f"Proxy didatico: resposta upstream {err.code}")
        except (URLError, OSError, ValueError) as err:
            self.reply(502, "Falha de acesso no lab (detalhes suprimidos)")


def start(port: int):
    httpd = ThreadingHTTPServer(("127.0.0.1", port), Handler)
    th = Thread(target=httpd.serve_forever, daemon=True)
    th.start()
    return httpd


def request(url: str):
    try:
        with OPENER.open(url, timeout=4) as resp:
            return resp.status, resp.read(16384).decode("utf-8")
    except HTTPError as err:
        return err.code, err.read(16384).decode("utf-8")


def run_demo():
    servers = [start(x) for x in (INTERNAL, ASSETS, FRONTEND)]
    try:
        scenarios = [
            ("1. Recurso publico via endpoint vulneravel", "/fetch", PUBLIC_URL, 200, "PUBLIC-ASSET"),
            ("2. SSRF: acessa servico INTERNO via servidor", "/fetch", INTERNAL_URL, 200, "INTERNAL-ONLY"),
            ("3. Controle: allowlist bloqueia SSRF interno", "/safe-fetch", INTERNAL_URL, 403, "BLOCKED"),
            ("4. Controle: permite URL aprovada", "/safe-fetch", PUBLIC_URL, 200, "PUBLIC-ASSET"),
        ]
        for label, path, target, status_expected, token in scenarios:
            url = f"http://127.0.0.1:{FRONTEND}{path}?url=" + quote(target, safe="")
            code, body = request(url)
            assert code == status_expected and token in body, f"{label}: {code=} {body=}"  # real assertion
            print("PASS", label)
            print("     URL inserida:", target)
            print("     HTTP:", code, "| Resposta:", body)
        print("\nRESULTADO: 4 cenarios PASSARAM; a falha da rota vulneravel foi reproduzida e bloqueada pela allowlist.")
        print("LIMITACAO: NAO constitui execucao da ferramenta SSRFmap nem prova de um lab PortSwigger resolvido.")
    finally:
        for s in servers:
            s.shutdown()
            s.server_close()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Simulacao de SSRF em localhost somente")
    parser.add_argument("--demo", action="store_true", help="Executa verificacoes locais e encerra")
    parser.add_argument("--serve", action="store_true", help="Mantem lab local ativo para teste manual")
    args = parser.parse_args()
    if args.serve:
        servers = [start(x) for x in (INTERNAL, ASSETS, FRONTEND)]
        print(f"LAB LOCAL: http://127.0.0.1:{FRONTEND} (Ctrl+C encerra)", flush=True)
        try:
            from threading import Event
            Event().wait()
        except KeyboardInterrupt:
            print("Parando...")
        finally:
            for s in servers:
                s.shutdown()
                s.server_close()
    else:
        run_demo()