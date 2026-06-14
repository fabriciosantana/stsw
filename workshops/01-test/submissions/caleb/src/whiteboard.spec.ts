import { test, expect } from '@playwright/test';

test.describe('LegalFlow Whiteboard UI - Final Test', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3005');

    const startBlankButton = page.getByText('Começar do zero', { exact: false });
    if (await startBlankButton.isVisible()) {
      await startBlankButton.click();
    }

    await expect(page.locator('.react-flow__renderer')).toBeVisible({ timeout: 10000 });
  });

  test('deve permitir arrastar blocos da paleta lateral para o whiteboard', async ({ page }) => {
    // Usando a informação do log de inspeção: "Entrada de Dados" está presente.
    // O erro anterior foi que após o dragTo, o nó não apareceu.
    // Pode ser que o alvo (canvas) ou o drag não funcionou como esperado no ambiente headless/simulado.

    const blockToDrag = page.getByText('Entrada de Dados');
    await expect(blockToDrag).toBeVisible();

    const canvas = page.locator('.react-flow__renderer');

    // Tenta o dragTo padrão
    await blockToDrag.dragTo(canvas);

    // Verifica se um novo nó apareceu no canvas
    const newNode = page.locator('.react-flow__node').first();
    try {
      await expect(newNode).toBeVisible({ timeout: 5000 });
    } catch (e) {
      // Se falhar, tenta uma abordagem manual de mouse para garantir que o evento de drag foi disparado corretamente
      const blockBox = await blockToDrag.boundingBox();
      const canvasBox = await canvas.boundingBox();

      if (blockBox && canvasBox) {
        await page.mouse.move(blockBox.x + blockBox.width / 2, blockBox.y + blockBox.height / 2);
        await page.mouse.down();
        // Move para o centro do canvas com passos para simular movimento humano
        await page.mouse.move(canvasBox.x + canvasBox.width / 2, canvasBox.y + canvasBox.height / 2, { steps: 10 });
        await page.mouse.up();

        await expect(newNode).toBeVisible({ timeout: 5000 });
      } else {
        throw new Error('Não foi possível obter as dimensões do bloco ou do canvas para tentativa manual.');
      }
    }
  });

  test('deve permitir conectar dois nós arrastando entre eles', async ({ page }) => {
    const sidebar = page.locator('div:has-text("BLOCOS DO FLUXO")').first();
    const canvas = page.locator('.react-flow__renderer');

    // Busca os blocos de forma mais robusta
    const block1 = page.getByText('Entrada de Dados');
    const block2 = page.getByText('Classificação IA');

    await expect(block1).toBeVisible();
    await expect(block2).toBeVisible();

    // 1. Arrastar os dois nós para o canvas
    // Usando abordagem manual com mouse para garantir precisão no Drag and Drop
    const canvasBox = await canvas.boundingBox();
    if (!canvasBox) throw new Error('Canvas não encontrado');

    // Arraste do bloco 1 para o canto superior esquerdo do canvas
    const box1 = await block1.boundingBox();
    if (box1) {
      await page.mouse.move(box1.x + box1.width / 2, box1.y + box1.height / 2);
      await page.mouse.down();
      await page.mouse.move(canvasBox.x + 100, canvasBox.y + 100, { steps: 10 });
      await page.mouse.up();
    }

    // Arraste do bloco 2 para o canto inferior direito do canvas
    const box2 = await block2.boundingBox();
    if (box2) {
      await page.mouse.move(box2.x + box2.width / 2, box2.y + box2.height / 2);
      await page.mouse.down();
      await page.mouse.move(canvasBox.x + canvasBox.width - 100, canvasBox.y + canvasBox.height - 100, { steps: 10 });
      await page.mouse.up();
    }

    // Espera os nós serem renderizados no DOM do React Flow
    const nodes = page.locator('.react-flow__node');
    await expect(nodes).toHaveCount(2, { timeout: 5000 });

    // 2. Tenta conectar o nó 1 ao nó 2 via drag de mouse (centro para centro)
    const node1Box = await nodes.nth(0).boundingBox();
    const node2Box = await nodes.nth(1).boundingBox();

    if (node1Box && node2Box) {
      await page.mouse.move(node1Box.x + node1Box.width / 2, node1Box.y + node1Box.height / 2);
      await page.mouse.down();
      // Move para o segundo nó com passos para simular a linha de conexão sendo desenhada
      await page.mouse.move(node2Box.x + node2Box.width / 2, node2Box.y + node2Box.height / 2, { steps: 20 });
      await page.mouse.up();

      // 3. Verifica se uma edge (conexão) foi criada
      const edge = page.locator('.react-flow__edge');
      await expect(edge.first()).toBeVisible({ timeout: 5000 });
    } else {
      throw new Error('Não foi possível obter as dimensões dos nós para conectar.');
    }
  });
});
