// npm run mcp:por-dentro: cliente MCP mínimo (JSON-RPC via stdio) que conversa com o Playwright MCP sem IA.

const { spawn } = require('child_process');
const http = require('http');
const path = require('path');
const readline = require('readline');

const raiz = path.join(__dirname, '..');
const URL_APP = 'http://localhost:3000';
const args = process.argv.slice(2);
const semPausa = args.includes('--sem-pausa') || !process.stdin.isTTY;
const argsExtras = args.filter((a) => a !== '--sem-pausa');

// ---------- cores no terminal ----------
const cor = (n) => (t) => `\x1b[${n}m${t}\x1b[0m`;
const azul = cor('36'), verde = cor('32'), amarelo = cor('33'), cinza = cor('90'), negrito = cor('1');

function titulo(texto) {
  console.log('\n' + negrito(amarelo(`━━━ ${texto} ${'━'.repeat(Math.max(0, 70 - texto.length))}`)));
}
function encurtar(texto, max = 1400) {
  return texto.length > max ? texto.slice(0, max) + cinza(`\n… (+${texto.length - max} caracteres)`) : texto;
}
async function pausa() {
  if (semPausa) return;
  const rl = readline.createInterface({ input: process.stdin, output: process.stdout });
  await new Promise((r) => rl.question(cinza('\n[Enter] próximo passo '), () => { rl.close(); r(); }));
}

// ---------- sobe a aplicação, se precisar ----------
function servidorNoAr() {
  return new Promise((r) => http.get(URL_APP, (res) => { res.resume(); r(true); }).on('error', () => r(false)));
}
async function subirApp() {
  if (await servidorNoAr()) return null;
  const app = spawn(process.execPath, [path.join(raiz, 'server.js')], { stdio: 'ignore', cwd: raiz });
  for (let i = 0; i < 50 && !(await servidorNoAr()); i++) await new Promise((r) => setTimeout(r, 200));
  return app;
}

// ---------- cliente MCP mínimo ----------
const cliMcp = path.join(path.dirname(require.resolve('@playwright/mcp/package.json', { paths: [raiz] })), 'cli.js');
const mcp = spawn(process.execPath, [cliMcp, '--browser=chromium', '--isolated', ...argsExtras], {
  cwd: raiz,
  stdio: ['pipe', 'pipe', 'inherit'],
});

let buffer = '';
let proximoId = 0;
const pendentes = new Map();
mcp.stdout.on('data', (dados) => {
  buffer += dados;
  let fim;
  while ((fim = buffer.indexOf('\n')) >= 0) {
    const linha = buffer.slice(0, fim).trim();
    buffer = buffer.slice(fim + 1);
    if (!linha) continue;
    const msg = JSON.parse(linha);
    if (msg.id !== undefined && pendentes.has(msg.id)) {
      pendentes.get(msg.id)(msg);
      pendentes.delete(msg.id);
    }
  }
});

function enviar(method, params, { mostrar = true } = {}) {
  const id = ++proximoId;
  const msg = { jsonrpc: '2.0', id, method, params };
  if (mostrar) console.log(azul('→ cliente envia:  ') + JSON.stringify(msg));
  mcp.stdin.write(JSON.stringify(msg) + '\n');
  return new Promise((r) => pendentes.set(id, r));
}

async function ferramenta(nome, argumentos) {
  const resposta = await enviar('tools/call', { name: nome, arguments: argumentos });
  const texto = (resposta.result?.content || []).map((c) => c.text ?? '[imagem]').join('\n');
  console.log(verde('← servidor responde:\n') + encurtar(texto));
  return texto;
}

// ---------- sequência de chamadas ----------
(async () => {
  const app = await subirApp();

  titulo('1. Aperto de mão (initialize)');
  console.log(cinza('O cliente (Antigravity, VS Code, Claude...) se apresenta e o servidor diz quem é.'));
  const ini = await enviar('initialize', {
    protocolVersion: '2025-06-18',
    capabilities: {},
    clientInfo: { name: 'demo-seminario', version: '1.0' },
  });
  mcp.stdin.write(JSON.stringify({ jsonrpc: '2.0', method: 'notifications/initialized' }) + '\n');
  console.log(verde('← servidor responde: ') + JSON.stringify(ini.result.serverInfo));
  await pausa();

  titulo('2. Quais ferramentas você tem? (tools/list)');
  console.log(cinza('A lista (nome + descrição + parâmetros) é colocada no contexto do modelo de IA.'));
  const lista = await enviar('tools/list', {});
  const nomes = lista.result.tools.map((t) => t.name);
  console.log(verde(`← servidor responde: ${nomes.length} ferramentas`));
  console.log('  ' + nomes.join(', '));
  const navegar = lista.result.tools.find((t) => t.name === 'browser_navigate');
  console.log(cinza('\nExemplo, como a IA "enxerga" uma ferramenta:'));
  console.log(JSON.stringify({ name: navegar.name, description: navegar.description, inputSchema: navegar.inputSchema }, null, 2));
  await pausa();

  titulo('3. A IA decide abrir a página (tools/call browser_navigate)');
  await ferramenta('browser_navigate', { url: URL_APP });
  await pausa();

  titulo('4. O que tem na tela? (tools/call browser_snapshot)');
  console.log(cinza('Não é uma foto: é a árvore de acessibilidade, em texto. Cada elemento ganha um [ref=...].'));
  const snapshot = await ferramenta('browser_snapshot', {});
  const ref = (re) => (snapshot.match(re) || [])[1];
  const refEmail = ref(/textbox "E-mail" \[ref=([\w-]+)\]/);
  const refSenha = ref(/textbox "Senha" \[ref=([\w-]+)\]/);
  const refEntrar = ref(/button "Entrar" \[ref=([\w-]+)\]/);
  console.log(amarelo(`\nRefs encontradas: E-mail=${refEmail}  Senha=${refSenha}  Entrar=${refEntrar}`));
  await pausa();

  titulo('5. A IA preenche o login usando as refs (browser_fill_form)');
  await ferramenta('browser_fill_form', {
    fields: [
      { name: 'E-mail', type: 'textbox', target: refEmail, value: 'aluno@idp.edu.br' },
      { name: 'Senha', type: 'textbox', target: refSenha, value: 'playwright123' },
    ],
  });
  await pausa();

  titulo('6. A IA clica em "Entrar" (browser_click)');
  await ferramenta('browser_click', { element: 'botão Entrar', target: refEntrar });
  await ferramenta('browser_wait_for', { text: 'Olá, Marcelo!' });
  console.log(amarelo('\nRepare: cada resposta traz o CÓDIGO Playwright equivalente. É assim que a IA escreve testes.'));
  await pausa();

  titulo('7. Fim');
  console.log('Isso é tudo o que o Antigravity faz: repete o ciclo');
  console.log(negrito('  ler o snapshot → escolher uma ferramenta → chamar → ler a resposta'));
  console.log('até terminar a tarefa que você pediu no chat.\n');
  await enviar('tools/call', { name: 'browser_close', arguments: {} }, { mostrar: false });
  mcp.kill();
  if (app) app.kill();
  process.exit(0);
})().catch((erro) => {
  console.error(erro);
  mcp.kill();
  process.exit(1);
});
