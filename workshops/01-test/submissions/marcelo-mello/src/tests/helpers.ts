import { type Page, expect } from '@playwright/test';

export const USUARIO = { email: 'aluno@idp.edu.br', senha: 'playwright123' };

/** Faz login e espera a área logada aparecer. */
export async function fazerLogin(page: Page) {
  await page.goto('/');
  await page.getByLabel('E-mail').fill(USUARIO.email);
  await page.getByLabel('Senha').fill(USUARIO.senha);
  await page.getByRole('button', { name: 'Entrar' }).click();
  await expect(page.getByRole('heading', { name: 'Olá, Marcelo!' })).toBeVisible();
}

/** Adiciona uma tarefa pelo formulário. */
export async function adicionarTarefa(page: Page, texto: string) {
  await page.getByLabel('Nova tarefa').fill(texto);
  await page.getByRole('button', { name: 'Adicionar' }).click();
}

const TITULOS = {
  Tarefas: 'Minhas tarefas',
  Kanban: 'Quadro Kanban',
  Galeria: 'Galeria',
  Foco: 'Modo foco',
} as const;

/** Navega pelo menu principal (no celular, abre o botão "Menu" antes). */
export async function irPara(page: Page, pagina: keyof typeof TITULOS) {
  const botaoMenu = page.getByRole('button', { name: 'Menu' });
  if (await botaoMenu.isVisible()) await botaoMenu.click();

  await page.getByRole('navigation', { name: 'Principal' }).getByRole('link', { name: pagina }).click();
  await expect(page.getByRole('heading', { name: TITULOS[pagina] })).toBeVisible();
}
