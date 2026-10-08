// Tarefas: adicionar, marcar como feita, remover e contador de pendentes.

import { test, expect } from '@playwright/test';
import { fazerLogin, adicionarTarefa } from './helpers';

test.describe('Tarefas', () => {
  test.beforeEach(async ({ page }) => {
    await fazerLogin(page);
  });

  test('adiciona tarefas e atualiza o contador', async ({ page }) => {
    await adicionarTarefa(page, 'Estudar Playwright');
    await adicionarTarefa(page, 'Montar os slides');
    await adicionarTarefa(page, 'Ensaiar a apresentação');

    const itens = page.getByRole('list', { name: 'Lista de tarefas' }).getByRole('listitem');
    await expect(itens).toHaveCount(3);
    await expect(itens).toContainText(['Estudar Playwright', 'Montar os slides', 'Ensaiar a apresentação']);
    await expect(page.getByTestId('contador')).toHaveText('3 tarefas pendentes');
  });

  test('marcar como feita diminui o contador', async ({ page }) => {
    await adicionarTarefa(page, 'Estudar Playwright');
    await adicionarTarefa(page, 'Montar os slides');

    await page.getByRole('checkbox', { name: 'Estudar Playwright' }).check();

    await expect(page.getByRole('checkbox', { name: 'Estudar Playwright' })).toBeChecked();
    await expect(page.getByTestId('contador')).toHaveText('1 tarefa pendente');
  });

  test('remove uma tarefa', async ({ page }) => {
    await adicionarTarefa(page, 'Tarefa que vai sumir');

    await page.getByRole('button', { name: 'Remover Tarefa que vai sumir' }).click();

    await expect(page.getByText('Tarefa que vai sumir')).toBeHidden();
    await expect(page.getByTestId('contador')).toHaveText('Nenhuma tarefa pendente');
  });

  test('não aceita tarefa vazia', async ({ page }) => {
    await page.getByRole('button', { name: 'Adicionar' }).click();

    await expect(page.getByRole('alert').filter({ hasText: 'Digite' })).toHaveText('Digite o nome da tarefa');
    await expect(page.getByTestId('contador')).toHaveText('Nenhuma tarefa pendente');
  });
});
