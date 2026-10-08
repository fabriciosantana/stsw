// @bug proposital: desmarcar uma tarefa deveria voltar a contá-la como pendente.

import { test, expect } from '@playwright/test';
import { fazerLogin, adicionarTarefa } from './helpers';

test('desmarcar uma tarefa volta a contá-la como pendente', { tag: '@bug' }, async ({ page }) => {
  await fazerLogin(page);
  await adicionarTarefa(page, 'Estudar Playwright');
  await adicionarTarefa(page, 'Montar os slides');
  await expect(page.getByTestId('contador')).toHaveText('2 tarefas pendentes');

  const tarefa = page.getByRole('checkbox', { name: 'Estudar Playwright' });
  await tarefa.check();
  await expect(page.getByTestId('contador')).toHaveText('1 tarefa pendente');

  await tarefa.uncheck();
  await expect(page.getByTestId('contador')).toHaveText('2 tarefas pendentes');
});
