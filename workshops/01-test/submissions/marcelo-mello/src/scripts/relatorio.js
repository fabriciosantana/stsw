// npm run relatorio: abre o relatório HTML em segundo plano (ou reabre, se já estiver aberto).

const { spawn } = require('child_process');
const fs = require('fs');
const http = require('http');
const path = require('path');

const raiz = path.join(__dirname, '..');
const PORTA = 9323;
const URL_RELATORIO = `http://localhost:${PORTA}`;
const ARQUIVO_PID = path.join(raiz, 'node_modules', '.relatorio.pid');

function portaOcupada() {
  return new Promise((r) => {
    http.get(URL_RELATORIO, (res) => { res.resume(); r(true); }).on('error', () => r(false));
  });
}

function abrirNavegador(url) {
  const [cmd, args] =
    process.platform === 'darwin' ? ['open', [url]]
    : process.platform === 'win32' ? ['cmd', ['/c', 'start', '', url]]
    : ['xdg-open', [url]];
  spawn(cmd, args, { stdio: 'ignore', detached: true }).on('error', () => {
    console.log(`Abra no navegador: ${url}`);
  }).unref();
}

async function fechar() {
  if (fs.existsSync(ARQUIVO_PID)) {
    const pid = Number(fs.readFileSync(ARQUIVO_PID, 'utf-8'));
    try { process.kill(pid); } catch { /* já tinha fechado */ }
    fs.rmSync(ARQUIVO_PID, { force: true });
    await new Promise((r) => setTimeout(r, 300));
  }
  console.log(await portaOcupada()
    ? `A porta ${PORTA} continua ocupada por outro programa. No Linux: fuser -k ${PORTA}/tcp`
    : '✔ Relatório fechado.');
}

async function abrir() {
  if (!fs.existsSync(path.join(raiz, 'playwright-report', 'index.html'))) {
    console.log('Ainda não existe relatório. Rode algum teste antes (ex.: npm run demo:falha).');
    process.exit(1);
  }

  if (await portaOcupada()) {
    console.log(`✔ O relatório já está aberto. Mostrando o resultado mais recente em ${URL_RELATORIO}`);
    abrirNavegador(URL_RELATORIO);
    return;
  }

  const pacote = require.resolve('@playwright/test/package.json', { paths: [raiz] });
  const cli = path.join(path.dirname(pacote), 'cli.js');
  const servidor = spawn(process.execPath, [cli, 'show-report', '--port', String(PORTA)], {
    cwd: raiz,
    stdio: 'ignore',
    detached: true,
  });
  servidor.unref();
  fs.writeFileSync(ARQUIVO_PID, String(servidor.pid));

  for (let i = 0; i < 50 && !(await portaOcupada()); i++) await new Promise((r) => setTimeout(r, 200));
  console.log(`✔ Relatório aberto em ${URL_RELATORIO} (o terminal já está livre).`);
  console.log('  Depois de rodar outros testes, use "npm run relatorio" de novo ou aperte F5 na página.');
}

(process.argv.includes('--fechar') ? fechar() : abrir()).catch((e) => {
  console.error(e);
  process.exit(1);
});
