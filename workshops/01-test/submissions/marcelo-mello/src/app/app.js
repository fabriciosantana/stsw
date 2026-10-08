// IDP Tarefas: Login · Tarefas (+ Loja) · Kanban · Galeria · Foco (Pomodoro). JavaScript puro.

const $ = (id) => document.getElementById(id);

// ---------- LOGIN ----------
$('form-login').addEventListener('submit', async (evento) => {
  evento.preventDefault();
  const email = $('email').value.trim();
  const senha = $('senha').value;
  const erro = $('erro-login');
  erro.hidden = true;

  if (!email || !senha) {
    mostrarErro(erro, 'Preencha e-mail e senha');
    return;
  }

  const botao = $('botao-entrar');
  botao.disabled = true;
  botao.textContent = 'Entrando…';

  try {
    const resposta = await fetch('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, senha }),
    });
    const dados = await resposta.json();
    if (!resposta.ok) throw new Error(dados.erro);
    abrirAreaLogada(dados.nome);
  } catch (e) {
    mostrarErro(erro, e.message || 'Falha no login');
  } finally {
    botao.disabled = false;
    botao.textContent = 'Entrar';
  }
});

$('botao-sair').addEventListener('click', () => {
  $('area-logada').hidden = true;
  $('botao-sair').hidden = true;
  $('menu-principal').hidden = true;
  $('botao-menu').hidden = true;
  $('tela-login').hidden = false;
  $('form-login').reset();
  history.replaceState(null, '', location.pathname);
});

function abrirAreaLogada(nome) {
  $('tela-login').hidden = true;
  $('area-logada').hidden = false;
  $('botao-sair').hidden = false;
  $('menu-principal').hidden = false;
  $('botao-menu').hidden = false;
  $('saudacao').textContent = `Olá, ${nome}!`;
  carregarProdutos();
  mostrarPagina(location.hash.slice(1) || 'tarefas');
}

function mostrarErro(elemento, mensagem) {
  elemento.textContent = mensagem;
  elemento.hidden = false;
}

// Aviso flutuante (toast) no canto da tela
let timerAviso;
function avisar(mensagem) {
  const aviso = $('aviso');
  aviso.textContent = mensagem;
  aviso.hidden = false;
  clearTimeout(timerAviso);
  timerAviso = setTimeout(() => (aviso.hidden = true), 4000);
}

// ---------- NAVEGAÇÃO (rotas por #hash) e MENU MOBILE ----------
const PAGINAS = ['tarefas', 'kanban', 'galeria', 'foco'];

function mostrarPagina(nome) {
  if (!PAGINAS.includes(nome)) nome = 'tarefas';
  for (const p of PAGINAS) $(`pagina-${p}`).hidden = p !== nome;
  for (const link of $('menu-principal').querySelectorAll('a')) {
    if (link.getAttribute('href') === `#${nome}`) link.setAttribute('aria-current', 'page');
    else link.removeAttribute('aria-current');
  }
  fecharMenu();
}

window.addEventListener('hashchange', () => {
  if (!$('area-logada').hidden) mostrarPagina(location.hash.slice(1));
});

$('botao-menu').addEventListener('click', () => {
  const aberto = $('menu-principal').classList.toggle('aberto');
  $('botao-menu').setAttribute('aria-expanded', String(aberto));
});

function fecharMenu() {
  $('menu-principal').classList.remove('aberto');
  $('botao-menu').setAttribute('aria-expanded', 'false');
}

// ---------- TAREFAS ----------
let pendentes = 0;

$('form-tarefa').addEventListener('submit', (evento) => {
  evento.preventDefault();
  const campo = $('nova-tarefa');
  const texto = campo.value.trim();
  const erro = $('erro-tarefa');
  erro.hidden = true;

  if (!texto) {
    mostrarErro(erro, 'Digite o nome da tarefa');
    return;
  }

  adicionarTarefa(texto);
  campo.value = '';
  campo.focus();
});

function adicionarTarefa(texto) {
  const id = 't' + Date.now() + Math.random().toString(16).slice(2, 6);
  const item = document.createElement('li');
  item.innerHTML = `
    <input type="checkbox" id="${id}">
    <label for="${id}"></label>
    <button type="button" class="remover">Remover</button>`;
  item.querySelector('label').textContent = texto;
  item.querySelector('.remover').setAttribute('aria-label', `Remover ${texto}`);

  const caixa = item.querySelector('input');
  caixa.addEventListener('change', () => {
    item.classList.toggle('feita', caixa.checked);
    if (caixa.checked) {
      pendentes--;
    }
    atualizarContador();
  });

  item.querySelector('.remover').addEventListener('click', () => {
    if (!caixa.checked) pendentes--;
    item.remove();
    atualizarContador();
  });

  $('lista-tarefas').appendChild(item);
  pendentes++;
  atualizarContador();
}

function atualizarContador() {
  const texto =
    pendentes === 0 ? 'Nenhuma tarefa pendente'
    : pendentes === 1 ? '1 tarefa pendente'
    : `${pendentes} tarefas pendentes`;
  $('contador').textContent = texto;
}

// ---------- LOJA (API) ----------
async function carregarProdutos() {
  const lista = $('lista-produtos');
  const carregando = $('carregando-produtos');
  const erro = $('erro-produtos');
  lista.innerHTML = '';
  erro.hidden = true;
  carregando.hidden = false;

  try {
    const resposta = await fetch('/api/produtos');
    if (!resposta.ok) throw new Error();
    const produtos = await resposta.json();
    for (const p of produtos) {
      const li = document.createElement('li');
      const preco = p.preco === 0
        ? 'Grátis'
        : p.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
      li.innerHTML = `<span></span><strong></strong>`;
      li.querySelector('span').textContent = p.nome;
      li.querySelector('strong').textContent = preco;
      lista.appendChild(li);
    }
    if (produtos.length === 0) mostrarErro(erro, 'Nenhum produto disponível');
  } catch {
    mostrarErro(erro, 'Não foi possível carregar os produtos');
  } finally {
    carregando.hidden = true;
  }
}

// ---------- KANBAN (arrastar e soltar, diálogo de confirmação, exportar CSV) ----------
const COLUNAS = ['A fazer', 'Fazendo', 'Feito'];
let cartoes = [
  { id: 1, texto: 'Estudar Playwright', coluna: 'A fazer' },
  { id: 2, texto: 'Configurar o MCP', coluna: 'A fazer' },
  { id: 3, texto: 'Montar os slides', coluna: 'Fazendo' },
  { id: 4, texto: 'Instalar o Node', coluna: 'Feito' },
];
let proximoId = 5;

function desenharQuadro() {
  const quadro = $('quadro');
  quadro.innerHTML = '';

  COLUNAS.forEach((nomeColuna, indice) => {
    const doGrupo = cartoes.filter((c) => c.coluna === nomeColuna);
    const coluna = document.createElement('section');
    coluna.className = 'coluna';
    coluna.setAttribute('aria-label', nomeColuna);
    coluna.innerHTML = `<h3><span></span> <span class="qtd"></span></h3><ul></ul>`;
    coluna.querySelector('h3 span').textContent = nomeColuna;
    coluna.querySelector('.qtd').textContent = doGrupo.length;

    const lista = coluna.querySelector('ul');
    lista.setAttribute('aria-label', nomeColuna);
    lista.dataset.coluna = nomeColuna;

    // ---- alvo do arrastar e soltar ----
    lista.addEventListener('dragover', (e) => {
      e.preventDefault();
      lista.classList.add('alvo');
    });
    lista.addEventListener('dragleave', () => lista.classList.remove('alvo'));
    lista.addEventListener('drop', (e) => {
      e.preventDefault();
      lista.classList.remove('alvo');
      moverCartao(Number(e.dataTransfer.getData('text/plain')), nomeColuna);
    });

    for (const cartao of doGrupo) {
      const item = document.createElement('li');
      item.className = 'cartao-kanban';
      item.draggable = true;
      item.dataset.id = cartao.id;
      item.innerHTML = `<span class="texto"></span><span class="botoes"></span>`;
      item.querySelector('.texto').textContent = cartao.texto;
      item.addEventListener('dragstart', (e) => {
        e.dataTransfer.setData('text/plain', String(cartao.id));
        item.classList.add('arrastando');
      });
      item.addEventListener('dragend', () => item.classList.remove('arrastando'));

      const botoes = item.querySelector('.botoes');
      if (indice > 0) botoes.append(botaoIcone('←', `Mover ${cartao.texto} para ${COLUNAS[indice - 1]}`, () => moverCartao(cartao.id, COLUNAS[indice - 1])));
      if (indice < COLUNAS.length - 1) botoes.append(botaoIcone('→', `Mover ${cartao.texto} para ${COLUNAS[indice + 1]}`, () => moverCartao(cartao.id, COLUNAS[indice + 1])));
      botoes.append(botaoIcone('×', `Excluir ${cartao.texto}`, () => excluirCartao(cartao.id), 'perigo'));

      lista.appendChild(item);
    }
    quadro.appendChild(coluna);
  });
}

function botaoIcone(simbolo, rotulo, acao, classe = '') {
  const botao = document.createElement('button');
  botao.type = 'button';
  botao.className = `icone ${classe}`;
  botao.textContent = simbolo;
  botao.setAttribute('aria-label', rotulo);
  botao.title = rotulo;
  botao.addEventListener('click', acao);
  return botao;
}

function moverCartao(id, coluna) {
  const cartao = cartoes.find((c) => c.id === id);
  if (!cartao || cartao.coluna === coluna) return;
  cartao.coluna = coluna;
  // vai para o fim da coluna de destino
  cartoes = cartoes.filter((c) => c.id !== id).concat(cartao);
  desenharQuadro();
  if (coluna === 'Feito') avisar(`🎉 "${cartao.texto}" concluído!`);
}

function excluirCartao(id) {
  const cartao = cartoes.find((c) => c.id === id);
  if (!confirm(`Excluir o cartão "${cartao.texto}"?`)) return;
  cartoes = cartoes.filter((c) => c.id !== id);
  desenharQuadro();
  avisar('Cartão excluído');
}

$('form-cartao').addEventListener('submit', (e) => {
  e.preventDefault();
  const campo = $('novo-cartao');
  const texto = campo.value.trim();
  if (!texto) return campo.focus();
  cartoes.push({ id: proximoId++, texto, coluna: 'A fazer' });
  campo.value = '';
  desenharQuadro();
});

$('botao-exportar').addEventListener('click', () => {
  const aspas = (v) => (/[",\n]/.test(v) ? `"${v.replace(/"/g, '""')}"` : v);
  const linhas = ['coluna,cartao'];
  for (const nome of COLUNAS) {
    for (const c of cartoes.filter((x) => x.coluna === nome)) linhas.push(`${aspas(nome)},${aspas(c.texto)}`);
  }
  const arquivo = new Blob([linhas.join('\n') + '\n'], { type: 'text/csv;charset=utf-8' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(arquivo);
  link.download = 'kanban.csv';
  link.click();
  URL.revokeObjectURL(link.href);
});

desenharQuadro();

// ---------- GALERIA (filtros, visualizador, upload de imagem) ----------
const CATEGORIAS = ['Todas', 'Natureza', 'Cidade', 'Espaço', 'Minhas fotos'];
const fotos = [
  { titulo: 'Montanhas', categoria: 'Natureza', src: '/img/galeria/montanhas.svg' },
  { titulo: 'Praia', categoria: 'Natureza', src: '/img/galeria/praia.svg' },
  { titulo: 'Pôr do sol', categoria: 'Natureza', src: '/img/galeria/pordosol.svg' },
  { titulo: 'Metrópole à noite', categoria: 'Cidade', src: '/img/galeria/cidade.svg' },
  { titulo: 'Ponte vermelha', categoria: 'Cidade', src: '/img/galeria/ponte.svg' },
  { titulo: 'Planeta anelado', categoria: 'Espaço', src: '/img/galeria/planeta.svg' },
  { titulo: 'Foguete', categoria: 'Espaço', src: '/img/galeria/foguete.svg' },
];
let filtroAtual = 'Todas';
let visiveis = [];
let indiceAberto = 0;

function desenharFiltros() {
  const filtros = $('filtros');
  filtros.innerHTML = '';
  for (const categoria of CATEGORIAS) {
    const botao = document.createElement('button');
    botao.type = 'button';
    botao.className = 'filtro';
    botao.textContent = categoria;
    botao.setAttribute('aria-pressed', String(categoria === filtroAtual));
    botao.addEventListener('click', () => {
      filtroAtual = categoria;
      desenharFiltros();
      desenharGaleria();
    });
    filtros.appendChild(botao);
  }
}

function desenharGaleria() {
  const grade = $('grade-fotos');
  grade.innerHTML = '';
  visiveis = fotos.filter((f) => filtroAtual === 'Todas' || f.categoria === filtroAtual);

  visiveis.forEach((foto, indice) => {
    const figura = document.createElement('figure');
    figura.innerHTML = `<button type="button" class="foto"><img loading="eager"></button><figcaption><span></span><small></small></figcaption>`;
    const img = figura.querySelector('img');
    img.src = foto.src;
    img.alt = foto.titulo;
    figura.querySelector('figcaption span').textContent = foto.titulo;
    figura.querySelector('figcaption small').textContent = foto.categoria;
    figura.querySelector('button').addEventListener('click', () => abrirVisualizador(indice));
    grade.appendChild(figura);
  });
  $('sem-fotos').hidden = visiveis.length > 0;
}

function abrirVisualizador(indice) {
  indiceAberto = (indice + visiveis.length) % visiveis.length;
  const foto = visiveis[indiceAberto];
  $('imagem-grande').src = foto.src;
  $('imagem-grande').alt = foto.titulo;
  $('legenda-visualizador').textContent = `${foto.titulo} — ${foto.categoria} (${indiceAberto + 1} de ${visiveis.length})`;
  $('link-original').href = foto.src;
  if (!$('visualizador').open) $('visualizador').showModal();
}

$('botao-proxima').addEventListener('click', () => abrirVisualizador(indiceAberto + 1));
$('botao-anterior').addEventListener('click', () => abrirVisualizador(indiceAberto - 1));
$('botao-fechar').addEventListener('click', () => $('visualizador').close());
$('visualizador').addEventListener('click', (e) => {
  if (e.target === $('visualizador')) $('visualizador').close(); // clique fora da imagem
});
$('visualizador').addEventListener('keydown', (e) => {
  if (e.key === 'ArrowRight') abrirVisualizador(indiceAberto + 1);
  if (e.key === 'ArrowLeft') abrirVisualizador(indiceAberto - 1);
});

const LIMITE_UPLOAD = 2 * 1024 * 1024; // 2 MB

$('enviar-foto').addEventListener('change', (e) => {
  const arquivo = e.target.files[0];
  e.target.value = ''; // permite enviar o mesmo arquivo de novo
  const erro = $('erro-upload');
  const info = $('info-upload');
  erro.hidden = true;
  info.hidden = true;
  if (!arquivo) return;

  if (!arquivo.type.startsWith('image/')) {
    mostrarErro(erro, `Formato inválido: "${arquivo.name}" não é uma imagem`);
    return;
  }
  if (arquivo.size > LIMITE_UPLOAD) {
    mostrarErro(erro, `Arquivo muito grande: o limite é 2 MB (enviado: ${tamanhoLegivel(arquivo.size)})`);
    return;
  }

  fotos.push({ titulo: arquivo.name, categoria: 'Minhas fotos', src: URL.createObjectURL(arquivo) });
  info.textContent = `Foto "${arquivo.name}" enviada (${tamanhoLegivel(arquivo.size)})`;
  info.hidden = false;
  filtroAtual = 'Minhas fotos';
  desenharFiltros();
  desenharGaleria();
});

function tamanhoLegivel(bytes) {
  if (bytes < 1024) return `${bytes} bytes`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toLocaleString('pt-BR', { maximumFractionDigits: 1 })} KB`;
  return `${(bytes / 1024 / 1024).toLocaleString('pt-BR', { maximumFractionDigits: 1 })} MB`;
}

desenharFiltros();
desenharGaleria();

// ---------- FOCO (Pomodoro: 25 min de foco, 5 min de pausa) ----------
const DURACAO = { Foco: 25 * 60 * 1000, Pausa: 5 * 60 * 1000 };
let fase = 'Foco';
let restante = DURACAO.Foco;
let fim = 0;
let relogio = null;
let ciclos = 0;

function formatar(ms) {
  const segundos = Math.ceil(ms / 1000);
  const mm = String(Math.floor(segundos / 60)).padStart(2, '0');
  const ss = String(segundos % 60).padStart(2, '0');
  return `${mm}:${ss}`;
}

function desenharFoco() {
  $('tempo').textContent = formatar(restante);
  $('fase').textContent = fase;
  $('pagina-foco').querySelector('.foco').classList.toggle('pausa', fase === 'Pausa');
  $('ciclos').textContent = `Ciclos concluídos: ${ciclos}`;
  $('botao-iniciar').disabled = relogio !== null;
  $('botao-pausar').disabled = relogio === null;
}

function tique() {
  restante = Math.max(0, fim - Date.now());
  if (restante === 0) return concluirFase();
  desenharFoco();
}

function concluirFase() {
  pararRelogio();
  if (fase === 'Foco') {
    ciclos++;
    fase = 'Pausa';
    avisar('Hora da pausa! ☕');
  } else {
    fase = 'Foco';
    avisar('Pausa encerrada. De volta ao foco! 💪');
  }
  restante = DURACAO[fase];
  desenharFoco();
}

function pararRelogio() {
  clearInterval(relogio);
  relogio = null;
}

$('botao-iniciar').addEventListener('click', () => {
  fim = Date.now() + restante;
  relogio = setInterval(tique, 250);
  desenharFoco();
});

$('botao-pausar').addEventListener('click', () => {
  restante = Math.max(0, fim - Date.now());
  pararRelogio();
  desenharFoco();
});

$('botao-zerar').addEventListener('click', () => {
  pararRelogio();
  fase = 'Foco';
  restante = DURACAO.Foco;
  desenharFoco();
});

desenharFoco();
