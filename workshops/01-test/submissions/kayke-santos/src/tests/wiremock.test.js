/**
 * Testes Automatizados para WireMock
 * Porta Padrão: 8080
 */

import test from 'node:test';
import assert from 'node:assert/strict';
import { WireMockServer } from '../wiremock/wiremock-server.js'; // 👈 Importa a classe do WireMock

let wiremock;

test.before(async () => {
  // Inicializa e sobe a instância do WireMock
  wiremock = new WireMockServer({ port: 8080 });
  await wiremock.start();
});

test.after(async () => {
  // Desliga o servidor do WireMock após os testes
  await wiremock.stop();
});

// ================= TESTES AUTOMATIZADOS DO WIREMOCK =================

test('[WireMock] 1. GET /api/users - Stub estático mapeado de users.json', async () => {
  const response = await fetch('http://localhost:8080/api/users');
  assert.equal(response.status, 200);
  const data = await response.json();
  assert(Array.isArray(data));
  assert.equal(data[0].nome, 'Kayke Silva');
});

test('[WireMock] 2. POST /api/pagamentos (valor <= 1000) - Regra de sucesso JsonPath', async () => {
  const response = await fetch('http://localhost:8080/api/pagamentos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ valor: 500 })
  });
  assert.equal(response.status, 201);
  const data = await response.json();
  assert.equal(data.status, 'APROVADO');
});

test('[WireMock] 3. POST /api/pagamentos (valor > 1000) - Regra de erro JsonPath', async () => {
  const response = await fetch('http://localhost:8080/api/pagamentos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ valor: 1500 })
  });
  assert.equal(response.status, 422);
  const data = await response.json();
  assert.equal(data.codigoErro, 'SALDO_INSUFICIENTE');
});

test('[WireMock] 4. GET /api/lento - Latência fixa de 3000ms', async () => {
  const start = Date.now();
  const response = await fetch('http://localhost:8080/api/lento');
  const elapsed = Date.now() - start;
  assert.equal(response.status, 200);
  assert(elapsed >= 2900, `Latência deve ser >= 2900ms (foi ${elapsed}ms)`);
});

test('[WireMock] 5. GET /api/falha-critica - Fault Injection (CONNECTION_RESET_BY_PEER)', async () => {
  try {
    await fetch('http://localhost:8080/api/falha-critica');
    assert.fail('Deveria ter falhado com Connection Reset');
  } catch (err) {
    assert(err.message.includes('fetch failed') || err.code === 'ECONNRESET');
  }
});

test('[WireMock] 6. GET /api/retry-test - Cenário Stateful (500 ➔ 200 na 2ª tentativa)', async () => {
  const res1 = await fetch('http://localhost:8080/api/retry-test');
  assert.equal(res1.status, 500);

  const res2 = await fetch('http://localhost:8080/api/retry-test');
  assert.equal(res2.status, 200);
  const data = await res2.json();
  assert.equal(data.status, 'SUCESSO');
});
