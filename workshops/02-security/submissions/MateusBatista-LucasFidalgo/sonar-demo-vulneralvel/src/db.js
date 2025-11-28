// db.js - exemplo de código vulnerável a SQL Injection

const sqlite3 = require('sqlite3').verbose();

// Banco em arquivo para demo
const db = new sqlite3.Database('users.db');

// Cria tabela e um usuário de exemplo
db.serialize(() => {
  db.run(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      username TEXT NOT NULL,
      password TEXT NOT NULL
    )
  `);

  db.run(`DELETE FROM users`);

  // Senha em texto plano (péssima prática)
  db.run(
    `INSERT INTO users (username, password) VALUES ('admin', 'admin123')`
  );
});

/**
 * Função vulnerável: concatena diretamente o input na query
 * Ex.: username = 'admin' OR '1'='1'
 */
function findUserByUsernameAndPassword(username, password, callback) {
  const query = `
    SELECT * FROM users
    WHERE username = '${username}'
      AND password = '${password}'
  `;

  console.log('Executando query insegura:', query);

  db.get(query, (err, row) => {
    if (err) {
      return callback(err);
    }
    callback(null, row);
  });
}

module.exports = {
  findUserByUsernameAndPassword
};
