// Galeria: upload, leitura de pixels da imagem exibida, nova aba (popup) e imagem quebrada (@bug).

import path from 'path';
import { test, expect, type Locator } from '@playwright/test';
import { fazerLogin, irPara } from './helpers';

const FOTO_TESTE = path.join(__dirname, 'fixtures', 'foto-teste.png'); // 320×240, quadrado amarelo no centro

/** Lê a cor [R, G, B] de um pixel da imagem, no tamanho original dela. */
async function corDoPixel(imagem: Locator, x: number, y: number) {
  return imagem.evaluate((img: HTMLImageElement, [px, py]) => {
    const tela = document.createElement('canvas');
    tela.width = img.naturalWidth;
    tela.height = img.naturalHeight;
    const contexto = tela.getContext('2d')!;
    contexto.drawImage(img, 0, 0);
    return Array.from(contexto.getImageData(px, py, 1, 1).data.slice(0, 3));
  }, [x, y]);
}

test.describe('Galeria', () => {
  test.beforeEach(async ({ page }) => {
    await fazerLogin(page);
    await irPara(page, 'Galeria');
  });

  test('filtra as fotos por categoria', async ({ page }) => {
    const filtros = page.getByRole('group', { name: 'Filtrar por categoria' });
    const fotos = page.getByTestId('galeria').getByRole('figure');

    await expect(fotos).toHaveCount(7);

    await filtros.getByRole('button', { name: 'Espaço' }).click();
    await expect(filtros.getByRole('button', { name: 'Espaço' })).toHaveAttribute('aria-pressed', 'true');
    await expect(fotos).toHaveCount(2);
    await expect(fotos).toContainText(['Planeta anelado', 'Foguete']);

    await filtros.getByRole('button', { name: 'Minhas fotos' }).click();
    await expect(fotos).toHaveCount(0);
    await expect(page.getByText('Nenhuma foto nesta categoria.')).toBeVisible();
  });

  test('amplia uma foto e navega com mouse e teclado', async ({ page }) => {
    await page.getByRole('button', { name: 'Montanhas' }).click();

    const visualizador = page.getByRole('dialog');
    await expect(visualizador).toBeVisible();
    await expect(visualizador).toContainText('Montanhas — Natureza (1 de 7)');
    await expect(visualizador.getByRole('img')).toHaveAttribute('src', /montanhas\.svg$/);

    await page.keyboard.press('ArrowRight');
    await expect(visualizador).toContainText('Praia — Natureza (2 de 7)');

    await visualizador.getByRole('button', { name: 'Anterior' }).click();
    await visualizador.getByRole('button', { name: 'Anterior' }).click();
    await expect(visualizador).toContainText('Foguete — Espaço (7 de 7)');

    await page.keyboard.press('Escape');
    await expect(visualizador).toBeHidden();
  });

  test('abre a imagem original em outra aba', { tag: '@destaque' }, async ({ page }) => {
    await page.getByRole('button', { name: 'Ponte vermelha' }).click();

    // Registra a espera pela nova aba antes do clique
    const novaAbaPromise = page.waitForEvent('popup');
    await page.getByRole('link', { name: 'Abrir original' }).click();
    const novaAba = await novaAbaPromise;

    await expect(novaAba).toHaveURL(/\/img\/galeria\/ponte\.svg$/);
    await expect(novaAba.locator('svg')).toBeVisible();

    await novaAba.close();
    await expect(page.getByRole('dialog')).toBeVisible();
  });

  test('envia uma foto e confere que a imagem exibida é a mesma do arquivo', { tag: '@destaque' }, async ({ page }) => {
    const seletorPromise = page.waitForEvent('filechooser');
    await page.getByText('Enviar foto').click();
    const seletor = await seletorPromise;
    await seletor.setFiles(FOTO_TESTE);

    await expect(page.getByText('Foto "foto-teste.png" enviada (879 bytes)')).toBeVisible();
    const foto = page.getByAltText('foto-teste.png');
    await expect(foto).toBeVisible();

    // Tamanho original do arquivo
    await expect.poll(() => foto.evaluate((img: HTMLImageElement) => img.complete && img.naturalWidth)).toBe(320);
    expect(await foto.evaluate((img: HTMLImageElement) => img.naturalHeight)).toBe(240);

    // Pixels conhecidos do arquivo enviado
    expect(await corDoPixel(foto, 160, 120)).toEqual([242, 184, 75]); // centro: quadrado amarelo
    expect(await corDoPixel(foto, 5, 5)).toEqual([30, 122, 48]);      // canto: fundo verde
    expect(await corDoPixel(foto, 40, 40)).toEqual([245, 244, 239]);  // bolinha clara
  });

  test('recusa um arquivo que não é imagem', async ({ page }) => {
    // Arquivo criado em memória
    await page.getByLabel('Enviar foto').setInputFiles({
      name: 'notas.txt',
      mimeType: 'text/plain',
      buffer: Buffer.from('eu não sou uma imagem'),
    });

    await expect(page.getByRole('alert')).toHaveText('Formato inválido: "notas.txt" não é uma imagem');
    await expect(page.getByTestId('galeria').getByRole('figure')).toHaveCount(7);
  });

  test('recusa uma imagem maior que 2 MB', async ({ page }) => {
    await page.getByLabel('Enviar foto').setInputFiles({
      name: 'gigante.png',
      mimeType: 'image/png',
      buffer: Buffer.alloc(3 * 1024 * 1024), // 3 MB
    });

    await expect(page.getByRole('alert')).toHaveText('Arquivo muito grande: o limite é 2 MB (enviado: 3 MB)');
  });
});

// Bug proposital: uma foto da galeria aponta para um arquivo inexistente
test('todas as imagens da galeria carregam', { tag: '@bug' }, async ({ page }) => {
  // Registra respostas HTTP de erro
  const errosDeRede: string[] = [];
  page.on('response', (resposta) => {
    if (resposta.status() >= 400) errosDeRede.push(`${resposta.status()} ${new URL(resposta.url()).pathname}`);
  });

  await fazerLogin(page);
  await irPara(page, 'Galeria');

  const imagens = page.getByTestId('galeria').locator('img');
  await expect(imagens).toHaveCount(7);
  await expect.poll(() => imagens.evaluateAll((lista: HTMLImageElement[]) => lista.every((i) => i.complete))).toBe(true);

  const quebradas = await imagens.evaluateAll((lista: HTMLImageElement[]) =>
    lista.filter((i) => i.naturalWidth === 0).map((i) => i.alt),
  );

  // expect.soft registra a falha e continua, exibindo as duas evidências
  expect.soft(quebradas, 'Fotos que não aparecem na tela').toEqual([]);
  expect.soft(errosDeRede, 'Erros de rede').toEqual([]);
});
