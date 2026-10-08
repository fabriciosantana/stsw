// Kanban: arrastar e soltar, diálogo de confirmação e download de CSV.

import fs from 'fs';
import { test, expect } from '@playwright/test';
import { fazerLogin, irPara } from './helpers';

test.describe('Kanban', () => {
  test.beforeEach(async ({ page }) => {
    await fazerLogin(page);
    await irPara(page, 'Kanban');
  });

  test('arrasta um cartão de "A fazer" para "Feito"', { tag: '@destaque' }, async ({ page, isMobile }) => {
    // Drag and drop do HTML5 não funciona com toque
    test.skip(isMobile, 'Arrastar e soltar não funciona com toque; no celular use os botões de seta');

    const aFazer = page.getByRole('list', { name: 'A fazer' });
    const feito = page.getByRole('list', { name: 'Feito' });

    await expect(aFazer.getByRole('listitem')).toHaveCount(2);
    await expect(feito.getByRole('listitem')).toHaveCount(1);

    await page.getByText('Estudar Playwright').dragTo(feito);

    await expect(aFazer.getByRole('listitem')).toHaveCount(1);
    await expect(feito.getByRole('listitem')).toHaveCount(2);
    await expect(feito).toContainText('Estudar Playwright');
    await expect(page.getByText('🎉 "Estudar Playwright" concluído!')).toBeVisible();
  });

  test('move cartões pelos botões, sem usar o mouse para arrastar', async ({ page }) => {
    await page.getByRole('button', { name: 'Mover Montar os slides para Feito' }).click();
    await expect(page.getByRole('list', { name: 'Feito' })).toContainText('Montar os slides');

    await page.getByRole('button', { name: 'Mover Instalar o Node para Fazendo' }).click();
    await expect(page.getByRole('list', { name: 'Fazendo' })).toContainText('Instalar o Node');
  });

  test('cria um cartão novo na coluna "A fazer"', async ({ page }) => {
    await page.getByLabel('Novo cartão').fill('Ensaiar a apresentação');
    await page.getByRole('button', { name: 'Criar cartão' }).click();

    const aFazer = page.getByRole('list', { name: 'A fazer' });
    await expect(aFazer.getByRole('listitem')).toHaveCount(3);
    await expect(aFazer.getByRole('listitem').last()).toContainText('Ensaiar a apresentação');
    await expect(page.getByLabel('Novo cartão')).toBeEmpty();
  });

  test('pede confirmação antes de excluir', async ({ page }) => {
    const mensagens: string[] = [];

    // Cancelar
    page.once('dialog', (dialogo) => {
      mensagens.push(dialogo.message());
      dialogo.dismiss();
    });
    await page.getByRole('button', { name: 'Excluir Configurar o MCP' }).click();
    await expect(page.getByText('Configurar o MCP')).toBeVisible();

    // Confirmar
    page.once('dialog', (dialogo) => {
      mensagens.push(dialogo.message());
      dialogo.accept();
    });
    await page.getByRole('button', { name: 'Excluir Configurar o MCP' }).click();
    await expect(page.getByText('Configurar o MCP')).toBeHidden();
    await expect(page.getByText('Cartão excluído')).toBeVisible();

    expect(mensagens).toEqual([
      'Excluir o cartão "Configurar o MCP"?',
      'Excluir o cartão "Configurar o MCP"?',
    ]);
  });

  test('exporta o quadro em CSV com o que foi movido', async ({ page }) => {
    await page.getByRole('button', { name: 'Mover Estudar Playwright para Fazendo' }).click();

    // Registra a espera pelo download antes do clique
    const downloadPromise = page.waitForEvent('download');
    await page.getByRole('button', { name: 'Exportar CSV' }).click();
    const download = await downloadPromise;

    expect(download.suggestedFilename()).toBe('kanban.csv');
    const conteudo = fs.readFileSync(await download.path(), 'utf-8');
    expect(conteudo.trim().split('\n')).toEqual([
      'coluna,cartao',
      'A fazer,Configurar o MCP',
      'Fazendo,Montar os slides',
      'Fazendo,Estudar Playwright',
      'Feito,Instalar o Node',
    ]);
  });
});
