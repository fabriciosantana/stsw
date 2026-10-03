// Emulação de celular (Pixel 7): menu, toque e layout responsivo.

import { test, expect, devices } from '@playwright/test';
import { fazerLogin, irPara } from './helpers';

const { defaultBrowserType, ...pixel7 } = devices['Pixel 7'];
test.use(pixel7);

test.skip(({ browserName }) => browserName === 'firefox', 'O Firefox não suporta emulação de celular');

test.describe('No celular', () => {
  test.beforeEach(async ({ page }) => {
    await fazerLogin(page);
  });

  test('o menu vira um botão "Menu"', { tag: '@destaque' }, async ({ page }) => {
    const menu = page.getByRole('button', { name: 'Menu' });
    const linkGaleria = page.getByRole('link', { name: 'Galeria' });

    await expect(menu).toBeVisible();
    await expect(linkGaleria).toBeHidden();

    await menu.tap();
    await expect(menu).toHaveAttribute('aria-expanded', 'true');
    await linkGaleria.tap();

    await expect(page.getByRole('heading', { name: 'Galeria' })).toBeVisible();
    await expect(linkGaleria).toBeHidden();
  });

  test('nenhuma página fica mais larga que a tela', async ({ page }) => {
    for (const pagina of ['Tarefas', 'Kanban', 'Galeria', 'Foco'] as const) {
      await irPara(page, pagina);
      const larguras = await page.evaluate(() => ({
        conteudo: document.documentElement.scrollWidth,
        tela: window.innerWidth,
      }));
      expect(larguras.conteudo, `rolagem horizontal na página ${pagina}`).toBeLessThanOrEqual(larguras.tela);
    }
  });

  test('as colunas do Kanban ficam uma embaixo da outra', async ({ page }) => {
    await irPara(page, 'Kanban');

    const aFazer = await page.getByRole('region', { name: 'A fazer' }).boundingBox();
    const fazendo = await page.getByRole('region', { name: 'Fazendo' }).boundingBox();

    expect(fazendo!.y).toBeGreaterThan(aFazer!.y + aFazer!.height - 1);
    expect(Math.round(fazendo!.x)).toBe(Math.round(aFazer!.x));
  });
});
