// Servidor HTTP da aplicação IDP Tarefas (somente módulos nativos do Node). Porta 3000.

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 3000;
const PASTA_APP = path.join(__dirname, 'app');

// Usuário de teste
const USUARIO = { email: 'aluno@idp.edu.br', senha: 'playwright123', nome: 'Marcelo' };

const PRODUTOS = [
  { id: 1, nome: 'Curso de Playwright', preco: 0 },
  { id: 2, nome: 'Livro de Testes de Software', preco: 89.9 },
  { id: 3, nome: 'Caneca "Funciona na minha máquina"', preco: 39.9 },
];

const TIPOS = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.webp': 'image/webp',
};

function json(res, status, dados) {
  res.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8' });
  res.end(JSON.stringify(dados));
}

function lerCorpo(req) {
  return new Promise((resolve) => {
    let corpo = '';
    req.on('data', (parte) => (corpo += parte));
    req.on('end', () => {
      try { resolve(JSON.parse(corpo || '{}')); } catch { resolve({}); }
    });
  });
}

const servidor = http.createServer(async (req, res) => {
  const url = new URL(req.url, `http://${req.headers.host}`);

  // ---------- API ----------
  if (url.pathname === '/api/login' && req.method === 'POST') {
    const { email, senha } = await lerCorpo(req);
    // Atraso proposital de 1 s
    setTimeout(() => {
      if (email === USUARIO.email && senha === USUARIO.senha) {
        json(res, 200, { ok: true, nome: USUARIO.nome });
      } else {
        json(res, 401, { ok: false, erro: 'E-mail ou senha inválidos' });
      }
    }, 1000);
    return;
  }

  if (url.pathname === '/api/produtos' && req.method === 'GET') {
    setTimeout(() => json(res, 200, PRODUTOS), 300);
    return;
  }

  // ---------- Arquivos estáticos ----------
  const arquivo =
    url.pathname === '/' ? '/index.html'
    : url.pathname === '/favicon.ico' ? '/favicon.svg'
    : url.pathname;
  const caminho = path.normalize(path.join(PASTA_APP, arquivo));
  if (!caminho.startsWith(PASTA_APP)) {
    res.writeHead(403).end();
    return;
  }
  fs.readFile(caminho, (erro, conteudo) => {
    if (erro) {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' }).end('Não encontrado');
      return;
    }
    res.writeHead(200, { 'Content-Type': TIPOS[path.extname(caminho)] || 'application/octet-stream' });
    res.end(conteudo);
  });
});

servidor.listen(PORT, () => {
  console.log(`IDP Tarefas rodando em http://localhost:${PORT}`);
});
