const express = require('express');
const app = express();
const PORT = 3001;
const fs = require('fs');
const path = require('path');

// VULNERABILIDADE 1: body-parser antigo (DoS)
app.use(express.urlencoded({ extended: true })); // extended: true pode causar DoS
app.use(express.json());

// VULNERABILIDADE 2: Sem validação de entrada
// VULNERABILIDADE 3: XSS (Cross-Site Scripting)
app.get('/', (req, res) => {
  const name = req.query.name || 'Visitante';
  // VULNERÁVEL: Renderiza input do usuário sem sanitização
  const html = `
    <!DOCTYPE html>
    <html>
    <head>
      <title>Vulnerable App - Demonstração de Segurança</title>
      <style>
        body { font-family: Arial; max-width: 800px; margin: 50px auto; padding: 20px; }
        .warning { background: #fff3cd; border: 2px solid #ffc107; padding: 15px; border-radius: 5px; margin: 20px 0; }
        .vuln { background: #f8d7da; border: 2px solid #dc3545; padding: 15px; border-radius: 5px; margin: 10px 0; }
        input { padding: 10px; width: 300px; margin: 10px 0; }
        button { padding: 10px 20px; background: #007bff; color: white; border: none; cursor: pointer; }
        button:hover { background: #0056b3; }
      </style>
    </head>
    <body>
      <h1>🔓 Vulnerable App - Demonstração</h1>
      
      <div class="warning">
        <strong>⚠️ ATENÇÃO:</strong> Esta aplicação contém vulnerabilidades intencionais 
        para fins educacionais. NÃO use em produção!
      </div>

      <h2>Olá, ${name}!</h2>
      
      <div class="vuln">
        <h3>Vulnerabilidades Conhecidas:</h3>
        <ul>
          <li><strong>Express 4.16.0</strong> - Versão antiga com múltiplas CVEs</li>
          <li><strong>XSS</strong> - Cross-Site Scripting (teste: ?name=&lt;script&gt;alert('XSS')&lt;/script&gt;)</li>
          <li><strong>DoS</strong> - Denial of Service via body-parser</li>
          <li><strong>Path Traversal</strong> - Acesso a arquivos do sistema</li>
        </ul>
      </div>

      <h3>Teste de Vulnerabilidades:</h3>
      <form method="GET" action="/">
        <input type="text" name="name" placeholder="Digite seu nome (teste XSS)" value="${name}">
        <button type="submit">Enviar</button>
      </form>

      <h3>Endpoints Vulneráveis:</h3>
      <ul>
        <li><a href="/file?path=package.json">/file?path=package.json</a> - Path Traversal</li>
        <li><a href="/search?q=test">/search?q=test</a> - SQL Injection (simulado)</li>
        <li><a href="/user/1">/user/1</a> - IDOR (Insecure Direct Object Reference)</li>
      </ul>

      <hr>
      <p><small>Esta aplicação usa Express 4.16.0 com vulnerabilidades conhecidas.</small></p>
    </body>
    </html>
  `;
  res.send(html);
});

// VULNERABILIDADE 4: Path Traversal
app.get('/file', (req, res) => {
  const filePath = req.query.path || 'package.json';
  // VULNERÁVEL: Não valida o caminho, permite acesso a qualquer arquivo
  const fullPath = path.join(__dirname, filePath);
  
  try {
    if (fs.existsSync(fullPath)) {
      const content = fs.readFileSync(fullPath, 'utf8');
      res.send(`<pre>${content}</pre>`);
    } else {
      res.send('Arquivo não encontrado');
    }
  } catch (error) {
    res.send(`Erro: ${error.message}`);
  }
});

// VULNERABILIDADE 5: SQL Injection (simulado)
app.get('/search', (req, res) => {
  const query = req.query.q || '';
  // VULNERÁVEL: Concatena input do usuário diretamente (simulado)
  const sqlQuery = `SELECT * FROM users WHERE name LIKE '%${query}%'`;
  res.send(`
    <h2>Resultado da Busca</h2>
    <p><strong>Query executada (VULNERÁVEL):</strong></p>
    <code>${sqlQuery}</code>
    <p style="color: red;">⚠️ Esta query é vulnerável a SQL Injection!</p>
    <p><a href="/">Voltar</a></p>
  `);
});

// VULNERABILIDADE 6: IDOR (Insecure Direct Object Reference)
app.get('/user/:id', (req, res) => {
  const userId = req.params.id;
  // VULNERÁVEL: Não verifica se o usuário tem permissão para acessar este ID
  const users = {
    '1': { id: 1, name: 'Admin', email: 'admin@example.com', role: 'admin' },
    '2': { id: 2, name: 'User', email: 'user@example.com', role: 'user' }
  };
  
  const user = users[userId];
  if (user) {
    res.send(`
      <h2>Dados do Usuário</h2>
      <pre>${JSON.stringify(user, null, 2)}</pre>
      <p style="color: red;">⚠️ VULNERÁVEL: Qualquer ID pode ser acessado sem autenticação!</p>
      <p><a href="/">Voltar</a></p>
    `);
  } else {
    res.send('Usuário não encontrado');
  }
});

app.listen(PORT, () => {
  console.log(`🚨 Vulnerable App rodando em http://localhost:${PORT}`);
  console.log(`⚠️  ATENÇÃO: Esta aplicação contém vulnerabilidades intencionais!`);
});


