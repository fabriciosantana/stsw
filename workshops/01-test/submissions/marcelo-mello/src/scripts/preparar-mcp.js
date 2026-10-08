// Instala o Chromium usado pela versão do Playwright embutida no @playwright/mcp.

const { spawnSync } = require('child_process');
const path = require('path');

const pastaMcp = path.dirname(require.resolve('@playwright/mcp/package.json'));
const pacote = require.resolve('playwright/package.json', { paths: [pastaMcp] });
const cli = path.join(path.dirname(pacote), require(pacote).bin.playwright);

console.log(`Instalando o Chromium usado pelo Playwright MCP (Playwright ${require(pacote).version})...`);
const resultado = spawnSync(process.execPath, [cli, 'install', 'chromium'], { stdio: 'inherit' });
process.exit(resultado.status ?? 1);
