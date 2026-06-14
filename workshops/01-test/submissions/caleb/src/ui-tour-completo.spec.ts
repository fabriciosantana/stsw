import { test, expect, Page } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

/**
 * LegalFlow — Tour Visual Completo da UI
 *
 * Varre TODOS os elementos visíveis do editor:
 *  1. Modal de boas-vindas (3 caminhos)
 *  2. Galeria de templates
 *  3. Editor: título, toolbar (Templates / Importar / Exportar / Mermaid)
 *  4. Sidebar de blocos (Entrada, Processamento, Lógica, Saída)
 *  5. Drag-and-drop de cada bloco no canvas
 *  6. Painel de configuração do node (Nome, Categorias, Instruções, Confiança)
 *  7. "Definir como Ponto de Entrada"
 *  8. Controles do React Flow (+ / - / fit / lock)
 *  9. Colapsar/expandir sidebar
 * 10. Conectar dois nodes
 * 11. Deletar um node
 *
 * Mostra EM TEMPO REAL dentro do navegador + console colorido + screenshots.
 */

const APP_URL = process.env.APP_URL || 'http://localhost:3005';
const SCREENSHOT_DIR = path.join(__dirname, '..', 'test-results', 'ui-tour');
const SLOW_MS = Number(process.env.SLOW_MS || 900);

// ANSI colors for console
const c = {
  reset: '\x1b[0m',
  dim: '\x1b[2m',
  green: '\x1b[32m',
  cyan: '\x1b[36m',
  yellow: '\x1b[33m',
  magenta: '\x1b[35m',
  red: '\x1b[31m',
  bold: '\x1b[1m',
};

test.use({
  viewport: { width: 1440, height: 900 },
  actionTimeout: 15_000,
  navigationTimeout: 20_000,
});

// ────────────────────────────────────────────────────────────────────
// Helpers
// ────────────────────────────────────────────────────────────────────

let stepCounter = 0;

function banner(title: string) {
  const line = '─'.repeat(Math.min(80, title.length + 10));
  console.log(`\n${c.cyan}${line}\n  ${c.bold}${title}${c.reset}${c.cyan}\n${line}${c.reset}`);
}

async function step(page: Page, emoji: string, message: string) {
  stepCounter += 1;
  const stamp = new Date().toISOString().substring(11, 19);
  console.log(
    `${c.dim}[${stamp}]${c.reset} ${c.yellow}#${String(stepCounter).padStart(2, '0')}${c.reset} ${emoji} ${c.green}${message}${c.reset}`,
  );

  await page.evaluate(
    ({ msg, n }) => {
      let el = document.getElementById('pw-logger');
      if (!el) {
        el = document.createElement('div');
        el.id = 'pw-logger';
        el.style.cssText = `
          position: fixed; bottom: 24px; left: 50%;
          transform: translateX(-50%);
          background: linear-gradient(135deg, rgba(17,24,39,0.96), rgba(30,41,59,0.96));
          color: #34d399;
          padding: 14px 22px; border-radius: 12px;
          z-index: 2147483647; font-size: 15px;
          font-family: 'SF Mono', ui-monospace, monospace;
          box-shadow: 0 10px 30px rgba(0,0,0,0.35), 0 0 0 1px rgba(255,255,255,0.08);
          backdrop-filter: blur(8px);
          max-width: 80vw; white-space: nowrap;
          overflow: hidden; text-overflow: ellipsis;
        `;
        document.body.appendChild(el);
      }
      el.innerHTML = `<span style="opacity:0.5">#${String(n).padStart(2, '0')}</span>  ${msg}`;
    },
    { msg: message, n: stepCounter },
  );
  await page.waitForTimeout(SLOW_MS);
}

async function shot(page: Page, name: string) {
  if (!fs.existsSync(SCREENSHOT_DIR)) {
    fs.mkdirSync(SCREENSHOT_DIR, { recursive: true });
  }
  const file = path.join(SCREENSHOT_DIR, `${String(stepCounter).padStart(2, '0')}-${name}.png`);
  await page.screenshot({ path: file, fullPage: false });
  console.log(`   ${c.dim}📸 ${path.relative(process.cwd(), file)}${c.reset}`);
}

async function ensureNoOverlay(page: Page) {
  // Fecha qualquer modal/overlay que possa estar bloqueando interações
  const overlay = page.locator('.fixed.inset-0.z-50').first();
  if (await overlay.isVisible({ timeout: 500 }).catch(() => false)) {
    const closeX = page.locator('button:has(svg.lucide-x)').last();
    if (await closeX.isVisible({ timeout: 400 }).catch(() => false)) {
      await closeX.click().catch(() => {});
    } else {
      await page.mouse.click(10, 10).catch(() => {});
    }
    await page.keyboard.press('Escape').catch(() => {});
    await overlay.waitFor({ state: 'hidden', timeout: 2000 }).catch(() => {});
    await page.waitForTimeout(300);
  }
}

async function highlight(page: Page, selector: string) {
  await page
    .evaluate((sel) => {
      const el = document.querySelector(sel) as HTMLElement | null;
      if (!el) return;
      const old = el.style.outline;
      el.style.outline = '3px solid #f59e0b';
      el.style.outlineOffset = '2px';
      setTimeout(() => { el.style.outline = old; }, 700);
    }, selector)
    .catch(() => {});
  await page.waitForTimeout(500);
}

/**
 * O NodePalette usa HTML5 DnD (setData no dragstart). Playwright `page.mouse.*`
 * NÃO dispara esses eventos. Precisamos emitir dragstart → dragover → drop
 * diretamente no DOM com um DataTransfer falso.
 */
async function dragPaletteNodeToCanvas(
  page: Page,
  label: string | RegExp,
  dropX: number,
  dropY: number,
) {
  const source = page.getByText(label).first();
  await source.scrollIntoViewIfNeeded();
  const sbox = await source.boundingBox();
  if (!sbox) throw new Error(`Paleta: bloco "${label}" não encontrado`);

  const canvas = page.locator('.react-flow__pane').first();
  const cbox = await canvas.boundingBox();
  if (!cbox) throw new Error('.react-flow__pane não encontrado');

  // Feedback visual: move o mouse ao longo do caminho (só pra gente ver)
  await page.mouse.move(sbox.x + sbox.width / 2, sbox.y + sbox.height / 2, { steps: 4 });
  await page.waitForTimeout(150);
  const tx = cbox.x + dropX;
  const ty = cbox.y + dropY;
  await page.mouse.move(tx, ty, { steps: 12 });
  await page.waitForTimeout(150);

  const before = await page.locator('.react-flow__node').count();

  // Pega o handle do elemento-paleta pelo ancestor [draggable="true"] do texto clicado
  const srcHandle = await source.evaluateHandle((el) => {
    let n: HTMLElement | null = el as HTMLElement;
    while (n && n.getAttribute('draggable') !== 'true') n = n.parentElement;
    return n;
  });

  // Handle do .react-flow__pane (target real do drop)
  const paneHandle = await canvas.elementHandle();

  // Dispara o DnD com handles reais (não com elementFromPoint)
  await page.evaluate(
    ({ srcEl, tgtEl, tx, ty }) => {
      if (!srcEl) throw new Error('fonte draggable não encontrada');
      if (!tgtEl) throw new Error('pane alvo não encontrada');

      const dt = new DataTransfer();
      const fire = (el: Element, type: string, clientX: number, clientY: number) => {
        const ev = new DragEvent(type, {
          bubbles: true,
          cancelable: true,
          composed: true,
          clientX,
          clientY,
          dataTransfer: dt,
        });
        el.dispatchEvent(ev);
      };

      const sr = (srcEl as HTMLElement).getBoundingClientRect();
      fire(srcEl as HTMLElement, 'dragstart', sr.left + sr.width / 2, sr.top + sr.height / 2);
      fire(tgtEl as HTMLElement, 'dragenter', tx, ty);
      fire(tgtEl as HTMLElement, 'dragover', tx, ty);
      fire(tgtEl as HTMLElement, 'drop', tx, ty);
      fire(srcEl as HTMLElement, 'dragend', tx, ty);
    },
    { srcEl: srcHandle, tgtEl: paneHandle, tx, ty },
  );

  // confirma que o node foi adicionado
  await page
    .waitForFunction(
      (prev) => document.querySelectorAll('.react-flow__node').length > prev,
      before,
      { timeout: 3000 },
    )
    .catch(() => {
      throw new Error(`drop não adicionou node (antes=${before})`);
    });
  await page.waitForTimeout(300);
}

// ────────────────────────────────────────────────────────────────────
// Main tour
// ────────────────────────────────────────────────────────────────────

test.describe('LegalFlow — Tour completo da UI', () => {
  test.setTimeout(5 * 60 * 1000);

  test('percorre cada elemento visível do editor', async ({ page }) => {
    banner('🚀 LegalFlow — UI Tour');
    console.log(`${c.dim}URL: ${APP_URL} | Slow: ${SLOW_MS}ms | Screens: ${SCREENSHOT_DIR}${c.reset}`);

    page.on('console', (msg) => {
      if (msg.type() === 'error') {
        console.log(`   ${c.red}⚠ console.error:${c.reset} ${msg.text().slice(0, 120)}`);
      }
    });
    page.on('pageerror', (err) => console.log(`   ${c.red}✖ pageerror:${c.reset} ${err.message}`));

    // ──────── 1. Carregamento ────────
    await step(page, '🌐', `Carregando ${APP_URL}`);
    await page.goto(APP_URL, { waitUntil: 'domcontentloaded' });
    await expect(page.locator('.react-flow__renderer')).toBeVisible({ timeout: 15_000 });
    await shot(page, 'carregado');

    // ──────── 2. Welcome Modal ────────
    banner('📂 Modal de boas-vindas');
    const welcomeTitle = page.getByText(/Bem-vindo de volta|Começar do zero/i).first();
    const welcomeVisible = await welcomeTitle.isVisible({ timeout: 3000 }).catch(() => false);

    if (welcomeVisible) {
      await step(page, '👀', 'Modal de boas-vindas detectado');
      await shot(page, 'welcome-modal');

      const btnTemplate = page.getByText('Escolher novo template').first();
      if (await btnTemplate.isVisible({ timeout: 2000 }).catch(() => false)) {
        await step(page, '🖱️', 'Clicando em "Escolher novo template"');
        await btnTemplate.click();
      }
    } else {
      await step(page, 'ℹ️', 'Modal de boas-vindas não apareceu (fluxo já salvo)');
    }

    // ──────── 3. Galeria de templates ────────
    banner('🎨 Galeria de Templates');
    const galleryTitle = page.getByText(/Escolha um modelo/i).first();
    const galleryOpen = await galleryTitle.isVisible({ timeout: 3000 }).catch(() => false);

    if (!galleryOpen) {
      await step(page, '🖱️', 'Abrindo galeria via botão "Templates" da toolbar');
      const templatesBtn = page.getByRole('button', { name: /Templates/i }).first();
      if (await templatesBtn.isVisible().catch(() => false)) {
        await templatesBtn.click();
        await page.waitForTimeout(600);
      }
    }

    if (await galleryTitle.isVisible({ timeout: 2000 }).catch(() => false)) {
      await step(page, '📋', 'Galeria aberta — listando templates disponíveis');
      await shot(page, 'galeria');

      const cards = page.locator('button:has-text("Usar")');
      const total = await cards.count();
      console.log(`   ${c.dim}→ ${total} templates encontrados${c.reset}`);

      // Fecha a galeria para começar do zero (assim vemos o drag criar nodes de fato)
      await step(page, '❎', 'Fechando galeria para começar do zero');
      const btnZeroFooter = page.getByText(/Começar do zero/i).first();
      if (await btnZeroFooter.isVisible({ timeout: 1500 }).catch(() => false)) {
        await btnZeroFooter.click();
      } else {
        await page.keyboard.press('Escape').catch(() => {});
      }
      await page.waitForTimeout(600);
    } else {
      await step(page, '⚠️', 'Galeria indisponível — começando do zero');
      const btnZero = page.getByText(/Começar do zero/i).first();
      if (await btnZero.isVisible({ timeout: 1500 }).catch(() => false)) {
        await btnZero.click();
      }
    }

    // Garante que o overlay do modal saiu antes de continuar
    await step(page, '⏳', 'Aguardando fechamento do modal/overlay');
    const overlay = page.locator('.fixed.inset-0.z-50, [role="dialog"]').first();
    await overlay.waitFor({ state: 'hidden', timeout: 5000 }).catch(async () => {
      await page.keyboard.press('Escape').catch(() => {});
      await page.waitForTimeout(400);
      await page.mouse.click(5, 5).catch(() => {});
    });
    await page.waitForTimeout(600);
    await shot(page, 'canvas-inicial');

    // ──────── 4. Título editável ────────
    banner('✏️ Título do fluxo');
    const title = page.locator('input, [contenteditable]').filter({ hasText: /.*/ }).first();
    const topTitle = page.locator('header, nav, [class*="Header"], [class*="toolbar"]').first();
    await step(page, '🏷️', 'Localizando campo de título do fluxo');
    // tenta clicar no texto "Novo Fluxo" / "Classificação..." no topo
    const titleText = page.getByText(/Novo Fluxo|Classificação|Fluxo/i).first();
    if (await titleText.isVisible({ timeout: 2000 }).catch(() => false)) {
      await titleText.click({ clickCount: 2 }).catch(() => {});
      await page.waitForTimeout(300);
      await page.keyboard.press('End');
    }
    await shot(page, 'titulo');

    // ──────── 5. Toolbar: Importar / Exportar (Mermaid testado no final) ────────
    banner('🧰 Toolbar superior');
    await ensureNoOverlay(page);

    for (const name of ['Importar', 'Exportar']) {
      const btn = page.getByRole('button', { name: new RegExp(name, 'i') }).first();
      if (await btn.isVisible({ timeout: 1500 }).catch(() => false)) {
        await step(page, '🔘', `Hover em "${name}"`);
        await btn.hover({ force: true }).catch(() => {});
        await page.waitForTimeout(400);
      }
    }

    // ──────── 6. Sidebar de blocos ────────
    banner('🧱 Sidebar — categorias de blocos');
    const sidebar = page.locator('text=BLOCOS DO FLUXO').first();
    const sidebarVisible = await sidebar.isVisible({ timeout: 2000 }).catch(() => false);

    if (!sidebarVisible) {
      // tenta abrir o collapse (o ícone < na lateral)
      await step(page, '↔️', 'Expandindo sidebar de blocos');
      const toggle = page.locator('button').filter({ hasText: /^[<>]$/ }).first();
      if (await toggle.isVisible({ timeout: 1500 }).catch(() => false)) {
        await toggle.click();
        await page.waitForTimeout(500);
      }
    }
    await shot(page, 'sidebar');

    const categorias = ['Entrada', 'Processamento', 'Lógica', 'Saída'];
    for (const cat of categorias) {
      const header = page.getByText(new RegExp(`^${cat}$`, 'i')).first();
      if (await header.isVisible({ timeout: 1000 }).catch(() => false)) {
        await step(page, '📁', `Categoria visível: ${cat}`);
        await header.hover({ force: true }).catch(() => {});
        await page.waitForTimeout(250);
      }
    }

    // ──────── 7. Drag-and-drop de blocos ────────
    banner('🎯 Drag-and-drop dos blocos para o canvas');
    await ensureNoOverlay(page);
    const blocos: Array<{ label: RegExp; x: number; y: number }> = [
      { label: /Entrada de Dados/i,       x: 220, y: 140 },
      { label: /Filtro por Palavras/i,    x: 440, y: 140 },
      { label: /Classificação IA/i,       x: 660, y: 140 },
      { label: /Contexto .?Instru/i,      x: 220, y: 340 },
      { label: /Cálculo de Prazo/i,       x: 440, y: 340 },
      { label: /Decisão .?Sim .? Não/i,   x: 660, y: 340 },
      { label: /Revisão Humana/i,         x: 220, y: 520 },
      { label: /Ação de Saída/i,          x: 440, y: 520 },
    ];

    let nodesArrastados = 0;
    for (const b of blocos) {
      try {
        await step(page, '🫳', `Arrastando "${String(b.label).replace(/[/\\^$*+?.()|[\]{}i]/g, '')}"`);
        await dragPaletteNodeToCanvas(page, b.label, b.x, b.y);
        nodesArrastados += 1;
      } catch (err) {
        console.log(`   ${c.dim}↳ pulado (${(err as Error).message.slice(0, 60)})${c.reset}`);
      }
    }
    await shot(page, 'nodes-arrastados');
    console.log(`   ${c.dim}→ ${nodesArrastados}/${blocos.length} blocos arrastados${c.reset}`);

    // ──────── 8. Painel de configuração ────────
    banner('⚙️ Painel de configuração do node');
    // Procura especificamente um node de Classificação IA (tem mais campos pra testar)
    const classNode = page
      .locator('.react-flow__node')
      .filter({ hasText: /Classificação|IA|Confirma/i })
      .first();
    const firstNode = (await classNode.count()) > 0 ? classNode : page.locator('.react-flow__node').first();

    if (await firstNode.isVisible({ timeout: 2000 }).catch(() => false)) {
      await step(page, '🖱️', 'Clicando no primeiro node do canvas');
      // dispatch explícito já que React Flow pode não responder a .click() em alguns casos
      const nb = await firstNode.boundingBox();
      if (nb) {
        await page.mouse.click(nb.x + nb.width / 2, nb.y + nb.height / 2);
      } else {
        await firstNode.click({ force: true });
      }
      // Painel de Configuração aparece à direita
      const panel = page.getByText(/CONFIGURAÇÃO/i).first();
      await panel.waitFor({ state: 'visible', timeout: 5000 }).catch(() => {});
      await page.waitForTimeout(500);
      await shot(page, 'config-panel');

      // Nome do passo — procura pelo label e pega o input logo abaixo
      const nomeInput = page.locator('label:has-text("Nome do passo") + input, input').first();
      if (await nomeInput.isVisible({ timeout: 1500 }).catch(() => false)) {
        await step(page, '⌨️', 'Editando "Nome do passo"');
        await nomeInput.click();
        await nomeInput.fill('Teste Automatizado — Playwright');
      }

      // Categorias possíveis
      const catInput = page.getByPlaceholder(/Ex: citação/i).first();
      if (await catInput.isVisible({ timeout: 1500 }).catch(() => false)) {
        await step(page, '🏷️', 'Adicionando categorias: PROCEDENTE, IMPROCEDENTE');
        await catInput.fill('PROCEDENTE');
        await page.keyboard.press('Enter');
        await catInput.fill('IMPROCEDENTE');
        await page.keyboard.press('Enter');
      }

      // Instruções para a IA
      const textarea = page.locator('textarea').first();
      if (await textarea.isVisible({ timeout: 1500 }).catch(() => false)) {
        await step(page, '📝', 'Preenchendo "Instruções para a IA"');
        await textarea.fill(
          'Analise o texto e retorne PROCEDENTE se a sentença for procedente, caso contrário IMPROCEDENTE.',
        );
      }

      // Confiança mínima
      const select = page.locator('select').first();
      if (await select.isVisible({ timeout: 1500 }).catch(() => false)) {
        await step(page, '📊', 'Selecionando confiança mínima');
        await select.selectOption({ index: 0 }).catch(() => {});
      }

      // Definir como Ponto de Entrada
      const entryBtn = page.getByText(/Definir como Ponto de Entrada/i).first();
      if (await entryBtn.isVisible({ timeout: 1500 }).catch(() => false)) {
        await step(page, '🚪', 'Clicando em "Definir como Ponto de Entrada"');
        await entryBtn.click();
      }

      await shot(page, 'config-preenchida');
    } else {
      await step(page, '⚠️', 'Nenhum node presente no canvas para configurar');
    }

    // ──────── 9. Conectar dois nodes ────────
    banner('🔗 Conectando dois nodes');
    const nodes = page.locator('.react-flow__node');
    const nodeCount = await nodes.count();
    if (nodeCount >= 2) {
      await step(page, '🔌', `Tentando conectar nodes 0 → 1 (total: ${nodeCount})`);

      const edgesBefore = await page.locator('.react-flow__edge').count();

      // React Flow 12 usa pointer events em handles ".source" / ".target"
      const srcHandle = nodes.nth(0).locator('.react-flow__handle.source, .react-flow__handle-right').first();
      const tgtHandle = nodes.nth(1).locator('.react-flow__handle.target, .react-flow__handle-left').first();

      const sb = await srcHandle.boundingBox().catch(() => null);
      const tb = await tgtHandle.boundingBox().catch(() => null);

      if (sb && tb) {
        const sx = sb.x + sb.width / 2;
        const sy = sb.y + sb.height / 2;
        const tx = tb.x + tb.width / 2;
        const ty = tb.y + tb.height / 2;

        // Pointer events reais (React Flow usa pointerdown/move/up)
        await page.mouse.move(sx, sy);
        await page.waitForTimeout(200);
        await page.dispatchEvent(
          '.react-flow__pane',
          'pointerdown',
          { clientX: sx, clientY: sy, pointerType: 'mouse', isPrimary: true, button: 0 },
        ).catch(() => {});
        await page.mouse.down();
        await page.mouse.move((sx + tx) / 2, (sy + ty) / 2, { steps: 12 });
        await page.mouse.move(tx, ty, { steps: 12 });
        await page.waitForTimeout(250);
        await page.mouse.up();

        await page.waitForTimeout(600);
        const edgesAfter = await page.locator('.react-flow__edge').count();
        console.log(`   ${c.dim}→ edges: ${edgesBefore} → ${edgesAfter}${c.reset}`);
        await shot(page, 'conectado');
      }
    } else {
      await step(page, 'ℹ️', `Nodes insuficientes para conectar (${nodeCount})`);
    }

    // ──────── 10. Controles do React Flow ────────
    banner('🎛️ Controles React Flow (zoom / fit / lock)');
    for (const title of ['zoom in', 'zoom out', 'fit view', 'lock']) {
      const ctrl = page.locator(`button[title*="${title}" i]:not([disabled]), .react-flow__controls-button[title*="${title}" i]:not([disabled])`).first();
      if (await ctrl.isVisible({ timeout: 800 }).catch(() => false)) {
        await step(page, '🎚️', `Controle: ${title}`);
        await ctrl.click({ trial: false, timeout: 2000 }).catch((e) => {
          console.log(`   ${c.dim}↳ pulado (${(e as Error).message.slice(0, 50)})${c.reset}`);
        });
        await page.waitForTimeout(300);
      } else {
        console.log(`   ${c.dim}↳ "${title}" indisponível/desabilitado${c.reset}`);
      }
    }
    await shot(page, 'controles');

    // ──────── 11. Colapsar/expandir sidebar ────────
    banner('↔️ Colapsar e reabrir sidebar');
    const collapseToggle = page
      .locator('button')
      .filter({ hasText: /^[<>]$/ })
      .first();
    if (await collapseToggle.isVisible({ timeout: 1500 }).catch(() => false)) {
      await step(page, '⬅️', 'Colapsando sidebar');
      await collapseToggle.click();
      await page.waitForTimeout(600);
      await shot(page, 'sidebar-colapsada');

      await step(page, '➡️', 'Reabrindo sidebar');
      await collapseToggle.click();
      await page.waitForTimeout(600);
    }

    // ──────── 12. Deletar um node ────────
    banner('🗑️ Deletar um node');
    const lastNode = page.locator('.react-flow__node').last();
    if (await lastNode.isVisible({ timeout: 1500 }).catch(() => false)) {
      await step(page, '🎯', 'Selecionando último node');
      await lastNode.click();
      await page.waitForTimeout(400);
      await step(page, '⌫', 'Pressionando Backspace para deletar');
      await page.keyboard.press('Backspace');
      await page.waitForTimeout(500);
      await shot(page, 'node-deletado');
    }

    // ──────── 13. Mermaid preview (por último, pois bloqueia o canvas) ────────
    banner('📊 Preview Mermaid');
    await ensureNoOverlay(page);
    const mermaidBtn = page.getByRole('button', { name: /Mermaid/i }).first();
    if (await mermaidBtn.isVisible({ timeout: 1500 }).catch(() => false)) {
      await step(page, '📊', 'Abrindo preview Mermaid');
      await mermaidBtn.click();
      const mermaidTitle = page.getByText(/Visualizacao Mermaid|Visualização Mermaid/i).first();
      await mermaidTitle.waitFor({ state: 'visible', timeout: 4000 }).catch(() => {});
      await page.waitForTimeout(800);
      await shot(page, 'mermaid');

      await step(page, '❌', 'Fechando modal Mermaid');
      const closeX = page.locator('button:has(svg.lucide-x)').last();
      if (await closeX.isVisible({ timeout: 800 }).catch(() => false)) {
        await closeX.click();
      } else {
        await page.mouse.click(10, 10);
      }
      await mermaidTitle.waitFor({ state: 'hidden', timeout: 3000 }).catch(() => {});
    }

    // ──────── Final ────────
    banner('✅ Tour concluído');
    const finalCount = await page.locator('.react-flow__node').count();
    console.log(`   ${c.green}${c.bold}Passos executados: ${stepCounter}${c.reset}`);
    console.log(`   ${c.green}Nodes no canvas final: ${finalCount}${c.reset}`);
    console.log(`   ${c.dim}Screenshots em: ${SCREENSHOT_DIR}${c.reset}\n`);

    await shot(page, 'final');
    await page.waitForTimeout(1500);
  });
});
