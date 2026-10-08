// npm run resetar: restaura a aplicação original e apaga relatórios e testes gerados.

const fs = require('fs');
const path = require('path');

const raiz = path.join(__dirname, '..');
const backup = path.join(__dirname, 'backup');

const ORIGINAIS = {
  'app.js.bak': 'app/app.js',
  'style.css.bak': 'app/style.css',
  'montanhas.svg.bak': 'app/img/galeria/montanhas.svg',
};

for (const [copia, destino] of Object.entries(ORIGINAIS)) {
  fs.copyFileSync(path.join(backup, copia), path.join(raiz, destino));
}
console.log('✔ app restaurado: bug do contador e imagem quebrada de volta, sabotagem visual desfeita');

for (const pasta of ['tests/gerados-pela-ia', 'test-results', 'playwright-report', '.playwright-mcp']) {
  fs.rmSync(path.join(raiz, pasta), { recursive: true, force: true });
}
console.log('✔ testes gerados pela IA, relatórios e saídas do MCP apagados');
console.log('Pronto.');
