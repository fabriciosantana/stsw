// Primeiro teste: navegação, título e localizadores por papel de acessibilidade.

import { test, expect } from '@playwright/test';

test('a página de login abre', async ({ page }) => {
  await page.goto('/');

  await expect(page).toHaveTitle('IDP Tarefas');

  await expect(page.getByRole('heading', { name: 'Entrar' })).toBeVisible();
  await expect(page.getByLabel('E-mail')).toBeVisible();
  await expect(page.getByLabel('Senha')).toBeVisible();
});
