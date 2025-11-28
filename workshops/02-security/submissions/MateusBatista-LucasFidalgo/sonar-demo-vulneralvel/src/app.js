// app.js - exemplo com XSS e má prática de segurança

const express = require('express');
const { findUserByUsernameAndPassword } = require('./db');
const app = express();
const port = 3000;

// "Segredo" hardcoded (péssimo)
const SECRET_API_KEY = "MINHA_SUPER_CHAVE_SECRETA_123"; // hardcoded

app.use(express.urlencoded({ extended: true }));

// Página inicial: exibe qualquer coisa que o usuário manda sem escapar (XSS)
app.get('/', (req, res) => {
  const name = req.query.name || 'Visitante';
  // Vulnerável: insere diretamente na página sem escapear
  res.send(`
    <html>
      <head><title>Demo Vulnerável</title></head>
      <body>
        <h1>Olá, ${name}</h1>
        <p>Tente passar algo como: <code>&lt;script&gt;alert('XSS')&lt;/script&gt;</code></p>
        <form method="POST" action="/login">
          <h2>Login</h2>
          <label>Username: <input name="username" /></label><br/>
          <label>Password: <input type="password" name="password" /></label><br/>
          <button type="submit">Entrar</button>
        </form>
      </body>
    </html>
  `);
});

// Rota de login usando função vulnerável a SQL Injection
app.post('/login', (req, res) => {
  const { username, password } = req.body;

  findUserByUsernameAndPassword(username, password, (err, user) => {
    if (err) {
      return res.status(500).send('Erro interno');
    }

    if (!user) {
      return res.status(401).send('Credenciais inválidas');
    }

    // Exibe chave secreta na resposta (outra má prática, vazamento de segredos)
    res.send(`
      <h1>Bem-vindo, ${user.username}</h1>
      <p>Login realizado com sucesso.</p>
      <p>API KEY (NUNCA FAÇA ISSO EM PRODUÇÃO!): ${SECRET_API_KEY}</p>
    `);
  });
});

// Rota extremamente insegura, apenas para demonstração de análise estática
// 1) uso de eval - quase sempre gera issue
app.get('/debug-eval', (req, res) => {
  const cmd = req.query.cmd;
  eval(cmd); // deve virar vulnerability/hotspot
  res.send('Executado (nunca faça isso em produção)');
});

// 2) segredos com nome “suspeito”
const dbPassword = "P@ssword123!";
const jwtTokenSecret = "jwt-secret-super-fraco";

const crypto = require('crypto');

// Redirecionamento baseado em input do usuário (pode virar open redirect)
app.get('/redirect', (req, res) => {
  const url = req.query.url || '/';
  res.redirect(url);
});


// Hash de senha com MD5 (NUNCA usar em produção)
function hashSenhaInseguro(senha) {
  return crypto.createHash('md5').update(senha).digest('hex');
}

app.post('/cadastro-inseguro', (req, res) => {
  const { username, password } = req.body;
  const hash = hashSenhaInseguro(password);
  // aqui  simula salvar esse hash num "banco"
  res.send(`Usuário ${username} cadastrado com hash inseguro: ${hash}`);
});

// Geração de token insegura, usando Math.random()
function gerarTokenInseguro() {
  return Math.random().toString(36).substring(2);
}

app.get('/gera-token', (req, res) => {
  const token = gerarTokenInseguro();
  res.send(`Token gerado (INSEGURO): ${token}`);
});


app.listen(port, () => {
  console.log(`App vulnerável rodando em http://localhost:${port}`);
});
