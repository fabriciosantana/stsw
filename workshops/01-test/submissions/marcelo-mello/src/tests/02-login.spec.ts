// Login: formulário, auto-waiting (o servidor responde em 1 s) e mensagens de erro.

import { test, expect } from '@playwright/test';
import { USUARIO } from './helpers';

test.describe('Login', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('entra com usuário e senha válidos', async ({ page }) => {
    await page.getByLabel('E-mail').fill(USUARIO.email);
    await page.getByLabel('Senha').fill(USUARIO.senha);
    await page.getByRole('button', { name: 'Entrar' }).click();

    // O expect tenta de novo até o painel aparecer (5 s)
    await expect(page.getByRole('heading', { name: 'Olá, Marcelo!' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Sair' })).toBeVisible();
  });

  test('mostra erro com senha errada', async ({ page }) => {
    await page.getByLabel('E-mail').fill(USUARIO.email);
    await page.getByLabel('Senha').fill('senha-errada');
    await page.getByRole('button', { name: 'Entrar' }).click();

    await expect(page.getByRole('alert')).toHaveText('E-mail ou senha inválidos');
    await expect(page.getByRole('heading', { name: 'Entrar' })).toBeVisible();
  });

  test('avisa quando os campos estão vazios', async ({ page }) => {
    await page.getByRole('button', { name: 'Entrar' }).click();

    await expect(page.getByRole('alert')).toHaveText('Preencha e-mail e senha');
  });

  test('sair volta para a tela de login', async ({ page }) => {
    await page.getByLabel('E-mail').fill(USUARIO.email);
    await page.getByLabel('Senha').fill(USUARIO.senha);
    await page.getByRole('button', { name: 'Entrar' }).click();
    await page.getByRole('button', { name: 'Sair' }).click();

    await expect(page.getByRole('heading', { name: 'Entrar' })).toBeVisible();
    await expect(page.getByLabel('E-mail')).toHaveValue('');
  });
});
