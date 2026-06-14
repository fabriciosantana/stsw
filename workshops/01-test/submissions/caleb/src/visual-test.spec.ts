import { test, expect } from '@playwright/test';

// Configura o Playwright para rodar com o navegador aberto (headed) 
// e adicionar um pequeno atraso (slowMo) para podermos ver as ações acontecendo.
test.use({
  actionTimeout: 0,
});

test.describe('LegalFlow - Teste Visual Completo da UI', () => {

  test('Deve interagir e demonstrar as ferramentas do Canvas e Menu', async ({ page }) => {
    
    // Função auxiliar para injetar log
    const logNoNavegador = async (mensagem: string) => {
      console.log(`[▶] Executando: ${mensagem}`);
      await page.evaluate((msg) => {
        let el = document.getElementById('pw-logger');
        if (!el) {
          el = document.createElement('div');
          el.id = 'pw-logger';
          el.style.position = 'fixed';
          el.style.bottom = '20px';
          el.style.left = '50%';
          el.style.transform = 'translateX(-50%)';
          el.style.background = 'rgba(0, 0, 0, 0.85)';
          el.style.color = '#00FF00';
          el.style.padding = '10px 20px';
          el.style.borderRadius = '8px';
          el.style.zIndex = '999999';
          el.style.fontSize = '18px';
          el.style.fontFamily = 'monospace';
          el.style.boxShadow = '0 4px 6px rgba(0,0,0,0.3)';
          document.body.appendChild(el);
        }
        el.innerText = msg;
      }, mensagem);
      await page.waitForTimeout(1500); 
    };

    await logNoNavegador('1. Acessando a aplicação...');
    await page.goto('http://localhost:3005');
    await page.setViewportSize({ width: 1366, height: 768 });

    const canvas = page.locator('.react-flow__renderer');
    await expect(canvas).toBeVisible({ timeout: 10000 });

    await logNoNavegador('2. Abrindo Template...');
    // Fecha o modal de boas-vindas se ficar na frente, clicando nele ou no fundo
    const btnComecarZere = page.getByText('Começar do zero').first();
    const btnEscolherTemplate = page.getByText('Escolher novo template').first();
    let escolheuTemplate = false;
    
    if (await btnEscolherTemplate.isVisible({ timeout: 3000 }).catch(() => false)) {
        await btnEscolherTemplate.click();
        escolheuTemplate = true;
    } else if (await btnComecarZere.isVisible({ timeout: 2000 }).catch(() => false)) {
        await btnComecarZere.click();
    }

    if (!escolheuTemplate) {
        const templatesBtn = page.getByRole('button', { name: /Templates/i }).first();
        if (await templatesBtn.isVisible()) {
          await templatesBtn.click();
        }
    }

    await page.waitForTimeout(1000);
    // Tenta clicar no primeiro botão 'Usar' da galeria
    const btnUsar = page.getByRole('button', { name: 'Usar' }).first();
    if (await btnUsar.isVisible({ timeout: 5000 }).catch(() => false)) {
        await btnUsar.click();
        await logNoNavegador('Carregando os nós do Template no canvas...');
        // Espera renderizar > 1 nó (já que é template)
        await expect(page.locator('.react-flow__node').nth(1)).toBeVisible({ timeout: 10000 });
    } else {
        await logNoNavegador('Nenhum template encontrado fechando... vamos começar em branco');
        await page.keyboard.press('Escape');
    }

    // Pega a contagem de nós
    const blockArrastado = page.getByText('Filtro por Palavras').first();
    const initialNodesCount = await page.locator('.react-flow__node').count();
    
    await logNoNavegador('3. Arrastando novo bloco "Filtro por Palavras" para o Canvas...');
    const dropZone = page.locator('.react-flow__renderer');
    const canvasBox = await dropZone.boundingBox();
    if (!canvasBox) throw new Error('Canvas não encontrado!');

    // Usa exclusivamente a função oficial de Drag and Drop que inclui os eventos e animação do mouse
    await blockArrastado.dragTo(dropZone, { 
      targetPosition: { x: 400, y: 250 } 
    });

    // Aguarda o nó aparecer (que será o `.last()`)
    await expect(page.locator('.react-flow__node').nth(initialNodesCount)).toBeVisible({ timeout: 10000 });
    
    await logNoNavegador('4. Tentando ligar os nós (Testando regras de conexão)...');
    
    // Procura handles na tela. O template já tem vários fluxos!
    // A nossa entrada será no NÓ NOVO que acabamos de colocar
    const newTargetNode = page.locator('.react-flow__node').last();
    const targetHandle = newTargetNode.locator('.react-flow__handle.target').first();
    
    // pegaremos o "source" do PRIMEIRO nó disponivel no template
    const sourceHandle = page.locator('.react-flow__node').first().locator('.react-flow__handle.source').first();

    if (await sourceHandle.isVisible() && await targetHandle.isVisible()) {
        const sBox = await sourceHandle.boundingBox();
        const tBox = await targetHandle.boundingBox();
        if (sBox && tBox) {
            // Usa force: true para ignorar o minimap interceptando o ponteiro de hover
            await sourceHandle.hover({ force: true });
            await page.mouse.down();
            await page.mouse.move(tBox.x + tBox.width / 2, tBox.y + tBox.height / 2, { steps: 30 });
            await targetHandle.hover({ force: true });
            await page.mouse.up();
            await page.waitForTimeout(1000); 
        }
    }

    await logNoNavegador('5. Editando as propriedades do novo nó...');
    await newTargetNode.click();
    await page.waitForTimeout(1000);
    
    const painelLabel = page.getByText('CONFIGURAÇÃO').first();
    if (await painelLabel.isVisible()) {
      const inputNome = page.getByLabel('Nome do passo').or(page.getByPlaceholder('Ex: ').first());
      if (await inputNome.isVisible()) {
        await inputNome.fill('Passo Atualizado Automático');
        await page.waitForTimeout(500);
      }
      // Fecha clicando no Canvas
      await page.mouse.click(canvasBox.x + 300, canvasBox.y + 100); 
      await page.waitForTimeout(500);
    }

    await logNoNavegador('6. Testando os Controles de Zoom...');
    const zoomIn = page.locator('.react-flow__controls-zoomin');
    const zoomOut = page.locator('.react-flow__controls-zoomout');
    
    // Verifica se os botões existem e estão ativados
    if (await zoomOut.isVisible() && await zoomOut.isEnabled()) {
      await zoomOut.click();
      await page.waitForTimeout(1000);
    }
    if (await zoomIn.isVisible() && await zoomIn.isEnabled()) {
      await zoomIn.click();
      await page.waitForTimeout(500);
      if (await zoomIn.isEnabled()) {
          await zoomIn.click();
      }
      await page.waitForTimeout(1000);
    }

    await logNoNavegador('7. Testando exportação p/ Mermaid...');
    const mermaidBtn = page.getByRole('button', { name: /Mermaid/i }).first();
    if (await mermaidBtn.isVisible()) {
      await mermaidBtn.click();
      await page.waitForTimeout(3000); // tempo de ver o fluxo
      await page.keyboard.press('Escape'); 
      const btnX = page.locator('button:has(svg.lucide-x)').first();
      if(await btnX.isVisible().catch(()=>false)){
          await btnX.click();
      }
    }

    await logNoNavegador('8. Demonstração Finalizada com Sucesso! 🚀');
    await page.waitForTimeout(3000);
  });
});
