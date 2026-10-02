/**
 * Testes Automatizados para Mockoon - API de Livros
 * Porta Padrão: 3001
 */

import test from 'node:test';
import assert from 'node:assert/strict';

test.before(async () => {
  try {
    const res = await fetch('http://localhost:3001/api/livros', { signal: AbortSignal.timeout(1000) });
    if (res.ok) {
      console.log('🔮 Conectado com sucesso ao Mockoon na porta 3001.');
    }
  } catch (error) {
    console.error('\n❌ ERRO: O servidor Mockoon NÃO está rodando na porta 3001!');
    console.error('👉 Abra o Mockoon Desktop, carregue o mockoon-environment.json e clique no botão verde PLAY (ou execute no terminal: npx @mockoon/cli start --data ./mockoon/mockoon-environment.json --port 3001).\n');
    throw new Error('Mockoon não iniciado na porta 3001. Inicie o servidor antes de executar os testes.');
  }
});

// ================= TESTES DO MOCKOON =================

test('[Mockoon] 1. GET /api/livros - Catálogo puxando do Data Bucket', async () => {
  const response = await fetch('http://localhost:3001/api/livros');
  assert.equal(response.status, 200);
  const data = await response.json();
  assert(Array.isArray(data), 'A resposta deve ser uma lista de livros');
  assert(data.length > 0, 'A lista de livros não deve estar vazia');
});

test('[Mockoon] 2. POST /api/livros (preco <= 500) - Regra condicional 201 Created', async () => {
  const response = await fetch('http://localhost:3001/api/livros', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ preco: 120, titulo: "Clean Code" })
  });
  assert.equal(response.status, 201);
  const data = await response.json();
  assert.equal(data.status, 'LIVRO_CADASTRADO');
});

test('[Mockoon] 3. POST /api/livros (preco > 500) - Regra condicional 422 Unprocessable Entity', async () => {
  const response = await fetch('http://localhost:3001/api/livros', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ preco: 850, titulo: "Livro Raro de Colecionador" })
  });
  assert.equal(response.status, 422);
  const data = await response.json();
  assert.equal(data.erro, 'PRECO_EXCESSIVO');
});

test('[Mockoon] 4. GET /api/livros/lento - Latência simulada de 3000ms', async () => {
  const start = Date.now();
  const response = await fetch('http://localhost:3001/api/livros/lento');
  const duration = Date.now() - start;
  assert.equal(response.status, 200);
  assert(duration >= 2900, `A resposta deveria demorar pelo menos 3000ms, mas demorou ${duration}ms`);
});
