// Pomodoro: relógio falso (page.clock) para testar 25 minutos sem esperar.

import { test, expect } from '@playwright/test';
import { fazerLogin, irPara } from './helpers';

test.describe('Modo foco (Pomodoro)', () => {
  test.beforeEach(async ({ page }) => {
    // O relógio falso precisa ser instalado antes de abrir a página
    await page.clock.install({ time: new Date('2026-10-01T09:00:00') });
    await fazerLogin(page);
    await irPara(page, 'Foco');
    // A partir daqui o tempo só avança com fastForward
    await page.clock.pauseAt(new Date('2026-10-01T09:05:00'));
  });

  test('um ciclo de 25 minutos testado em menos de 1 segundo', { tag: '@destaque' }, async ({ page }) => {
    const tempo = page.getByRole('timer', { name: 'Tempo restante' });

    await page.getByRole('button', { name: 'Iniciar' }).click();
    await expect(tempo).toHaveText('25:00');

    await page.clock.fastForward('10:00');
    await expect(tempo).toHaveText('15:00');

    await page.clock.fastForward('15:00');
    await expect(page.getByText('Hora da pausa! ☕')).toBeVisible();
    await expect(page.getByTestId('fase')).toHaveText('Pausa');
    await expect(tempo).toHaveText('05:00');
    await expect(page.getByTestId('ciclos')).toHaveText('Ciclos concluídos: 1');
  });

  test('pausar congela o tempo', async ({ page }) => {
    const tempo = page.getByRole('timer', { name: 'Tempo restante' });

    await page.getByRole('button', { name: 'Iniciar' }).click();
    await page.clock.fastForward('00:10');
    await expect(tempo).toHaveText('24:50');

    await page.getByRole('button', { name: 'Pausar' }).click();
    await page.clock.fastForward('05:00');
    await expect(tempo).toHaveText('24:50'); // pausado: não muda

    await page.getByRole('button', { name: 'Iniciar' }).click();
    await page.clock.fastForward('00:50');
    await expect(tempo).toHaveText('24:00');
  });

  test('depois da pausa de 5 minutos, volta ao foco', async ({ page }) => {
    const tempo = page.getByRole('timer', { name: 'Tempo restante' });

    await page.getByRole('button', { name: 'Iniciar' }).click();
    await page.clock.fastForward('25:00');
    await expect(tempo).toHaveText('05:00');

    await page.getByRole('button', { name: 'Iniciar' }).click();
    await page.clock.fastForward('05:00');

    await expect(page.getByText('Pausa encerrada. De volta ao foco! 💪')).toBeVisible();
    await expect(page.getByTestId('fase')).toHaveText('Foco');
    await expect(tempo).toHaveText('25:00');
  });

  test('zerar volta para 25:00', async ({ page }) => {
    const tempo = page.getByRole('timer', { name: 'Tempo restante' });

    await page.getByRole('button', { name: 'Iniciar' }).click();
    await page.clock.fastForward('07:30');
    await expect(tempo).toHaveText('17:30');

    await page.getByRole('button', { name: 'Zerar' }).click();
    await expect(tempo).toHaveText('25:00');
    await expect(page.getByRole('button', { name: 'Iniciar' })).toBeEnabled();
  });
});
