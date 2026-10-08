// Acessibilidade (WCAG) com axe-core.

import AxeBuilder from '@axe-core/playwright';
import { test, expect } from '@playwright/test';
import { fazerLogin, irPara } from './helpers';

test('a tela de login é acessível', async ({ page }) => {
  await page.goto('/');

  const resultado = await new AxeBuilder({ page }).analyze();

  expect(resultado.violations.map((v) => `${v.id}: ${v.help}`)).toEqual([]);
});

for (const pagina of ['Tarefas', 'Kanban', 'Galeria', 'Foco'] as const) {
  test(`a página ${pagina} é acessível`, async ({ page }) => {
    await fazerLogin(page);
    await irPara(page, pagina);

    const resultado = await new AxeBuilder({ page }).analyze();

    expect(resultado.violations.map((v) => `${v.id}: ${v.help}`)).toEqual([]);
  });
}
