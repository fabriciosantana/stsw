// Loja: mock de rede com page.route e testes de API sem navegador (fixture request).

import { test, expect } from '@playwright/test';
import { fazerLogin } from './helpers';

test.describe('Loja (mock de API)', () => {
  test('mostra os produtos que vieram do servidor real', async ({ page }) => {
    await fazerLogin(page);

    const produtos = page.getByRole('list', { name: 'Produtos' }).getByRole('listitem');
    await expect(produtos).toHaveCount(3);
    await expect(produtos.first()).toContainText('Grátis');
  });

  test('mostra produtos falsos devolvidos pelo mock', async ({ page }) => {
    await page.route('**/api/produtos', (rota) =>
      rota.fulfill({
        json: [{ id: 99, nome: 'Produto inventado pelo teste', preco: 1234.5 }],
      }),
    );

    await fazerLogin(page);

    const produtos = page.getByRole('list', { name: 'Produtos' }).getByRole('listitem');
    await expect(produtos).toHaveCount(1);
    await expect(produtos).toContainText('Produto inventado pelo teste');
    await expect(produtos).toContainText('R$ 1.234,50');
  });

  test('mostra mensagem quando o servidor devolve erro 500', async ({ page }) => {
    await page.route('**/api/produtos', (rota) => rota.fulfill({ status: 500 }));

    await fazerLogin(page);

    await expect(page.getByText('Não foi possível carregar os produtos')).toBeVisible();
  });

  test('mostra aviso quando não há produtos', async ({ page }) => {
    await page.route('**/api/produtos', (rota) => rota.fulfill({ json: [] }));

    await fazerLogin(page);

    await expect(page.getByText('Nenhum produto disponível')).toBeVisible();
  });
});

// Testes de API, sem navegador
test.describe('API (sem navegador)', () => {
  test('GET /api/produtos devolve 3 produtos', async ({ request }) => {
    const resposta = await request.get('/api/produtos');

    expect(resposta.status()).toBe(200);
    const corpo = await resposta.json();
    expect(corpo).toHaveLength(3);
  });

  test('POST /api/login recusa senha errada', async ({ request }) => {
    const resposta = await request.post('/api/login', {
      data: { email: 'aluno@idp.edu.br', senha: 'errada' },
    });

    expect(resposta.status()).toBe(401);
  });
});
