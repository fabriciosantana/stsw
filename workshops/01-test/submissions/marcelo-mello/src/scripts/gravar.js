// npm run gravar: sobe a aplicação e abre o Codegen do Playwright.

const { spawn } = require('child_process');
const http = require('http');
const path = require('path');

const URL_APP = 'http://localhost:3000';
const raiz = path.join(__dirname, '..');

function servidorNoAr() {
  return new Promise((resolve) => {
    http.get(URL_APP, (res) => { res.resume(); resolve(true); }).on('error', () => resolve(false));
  });
}

async function esperarServidor(tentativas = 50) {
  for (let i = 0; i < tentativas; i++) {
    if (await servidorNoAr()) return;
    await new Promise((r) => setTimeout(r, 200));
  }
  throw new Error('O servidor não subiu em ' + URL_APP);
}

(async () => {
  let servidor = null;
  if (!(await servidorNoAr())) {
    servidor = spawn(process.execPath, [path.join(raiz, 'server.js')], { stdio: 'inherit', cwd: raiz });
    await esperarServidor();
  }

  const cli = require.resolve('@playwright/test/cli', { paths: [raiz] });
  const codegen = spawn(process.execPath, [cli, 'codegen', '--viewport-size=1280,800', URL_APP], {
    stdio: 'inherit',
    cwd: raiz,
  });

  const encerrar = () => { if (servidor) servidor.kill(); };
  codegen.on('exit', (codigo) => { encerrar(); process.exit(codigo ?? 0); });
  process.on('SIGINT', () => { codegen.kill(); encerrar(); });
})();
