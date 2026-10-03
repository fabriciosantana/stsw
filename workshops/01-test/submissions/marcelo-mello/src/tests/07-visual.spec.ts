// Teste visual: compara prints com as referências em tests/__screenshots__ (criadas por npm run visual:base).

import { test, expect } from '@playwright/test';
import { fazerLogin, irPara } from './helpers';

test.describe('Aparência', { tag: '@visual' }, () => {
  test.beforeEach(async ({ page }) => {
    await fazerLogin(page);
  });

  test('a foto ampliada é exatamente a de referência', async ({ page }) => {
    await irPara(page, 'Galeria');
    await page.getByRole('button', { name: 'Montanhas' }).click();

    const foto = page.getByRole('dialog').getByRole('img', { name: 'Montanhas' });
    await expect(foto).toHaveScreenshot('foto-montanhas.png');
  });

  test('a galeria filtrada não mudou de aparência', async ({ page }) => {
    await irPara(page, 'Galeria');
    await page.getByRole('group', { name: 'Filtrar por categoria' }).getByRole('button', { name: 'Espaço' }).click();

    await expect(page.locator('#pagina-galeria')).toHaveScreenshot('galeria-espaco.png');
  });

  test('o quadro Kanban não mudou de aparência', async ({ page }) => {
    await irPara(page, 'Kanban');

    await expect(page.locator('#pagina-kanban')).toHaveScreenshot('kanban.png');
  });
});
