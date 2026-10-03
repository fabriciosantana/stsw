// npm run sabotar: altera cor, bordas e uma imagem para demonstrar o teste visual falhando.

const fs = require('fs');
const path = require('path');

const raiz = path.join(__dirname, '..');

function trocar(arquivo, de, para, descricao) {
  const caminho = path.join(raiz, arquivo);
  const conteudo = fs.readFileSync(caminho, 'utf-8');
  if (!conteudo.includes(de)) {
    console.log(`• ${descricao}: já estava sabotado (rode "npm run resetar" para desfazer)`);
    return;
  }
  fs.writeFileSync(caminho, conteudo.replace(de, para));
  console.log(`✔ ${descricao}`);
}

trocar('app/style.css', '--primaria: #2e6fdb;', '--primaria: #8e44ad;', 'cor principal trocada de azul para roxo');
trocar('app/style.css', '--raio: 14px;', '--raio: 4px;', 'bordas arredondadas trocadas de 14px para 4px');
trocar('app/img/galeria/montanhas.svg', '<circle cx="640" cy="120"', '<circle cx="500" cy="120"', 'sol da foto "Montanhas" mudou de lugar');

console.log('\nAgora rode: npm run demo:visual   (vai falhar)');
console.log('Depois:     npm run relatorio     (veja a diferença)');
