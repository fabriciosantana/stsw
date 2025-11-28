const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');

console.log('🔍 Iniciando scan de vulnerabilidades com OWASP Dependency-Check...\n');

// Detecta o sistema operacional para usar o comando correto
const isWindows = process.platform === 'win32';
const dependencyCheckCmd = isWindows ? 'dependency-check.bat' : 'dependency-check.sh';

// Função para verificar se o comando existe
function checkDependencyCheckInstalled() {
  try {
    if (isWindows) {
      execSync('where dependency-check.bat', { stdio: 'ignore' });
      return true;
    } else {
      execSync('which dependency-check.sh', { stdio: 'ignore' });
      return true;
    }
  } catch (error) {
    return false;
  }
}

// Verifica se está instalado
if (!checkDependencyCheckInstalled()) {
  console.error('❌ OWASP Dependency-Check não está instalado ou não está no PATH.\n');
  console.log('📥 Para instalar no Windows:');
  console.log('   1. Baixe de: https://github.com/jeremylong/DependencyCheck/releases');
  console.log('   2. Extraia o ZIP em um diretório (ex: C:\\tools\\dependency-check)');
  console.log('   3. Adicione ao PATH:');
  console.log('      $env:Path += ";C:\\tools\\dependency-check\\bin"');
  console.log('\n   Ou instale via Chocolatey:');
  console.log('   choco install dependency-check\n');
  console.log('📖 Veja mais detalhes no README.md\n');
  process.exit(1);
}

// Cria o diretório de relatório se não existir
const reportDir = path.join(__dirname, 'report');
if (!fs.existsSync(reportDir)) {
  fs.mkdirSync(reportDir, { recursive: true });
}

// Verifica se há API key do NVD (altamente recomendado)
const nvdApiKey = process.env.NVD_API_KEY;
const skipUpdate = process.env.SKIP_NVD_UPDATE === 'true';
let command = `${dependencyCheckCmd} --project "vulnerable-app" --scan ./ --format HTML --format JSON --out ./report`;

let useApiKey = false;
if (skipUpdate) {
  // Usa dados locais sem tentar atualizar
  command += ` --noupdate`;
  console.log('⚠️  Modo: Usando apenas dados locais (sem atualizar do NVD)');
  console.log('💡 Isso pode resultar em dados desatualizados, mas permite o scan funcionar.\n');
} else if (nvdApiKey) {
  command += ` --nvdApiKey ${nvdApiKey} --nvdApiDelay 12000`;
  useApiKey = true;
  console.log('🔑 Usando NVD API Key...');
  console.log('💡 Se a API key foi recém-criada, pode levar alguns minutos para ativar.');
  console.log('💡 Se receber erro 403, tente: $env:SKIP_NVD_UPDATE="true"; node scan.js\n');
} else {
  console.log('⚠️  NVD API Key não encontrada!');
  console.log('📝 Tentando usar dados locais (se existirem)...');
  console.log('\n🔑 Para melhor resultado, obtenha uma API key gratuita:');
  console.log('   1. Acesse: https://nvd.nist.gov/developers/request-an-api-key');
  console.log('   2. Preencha o formulário (nome e email)');
  console.log('   3. Você receberá a chave por email');
  console.log('   4. ⚠️  IMPORTANTE: Clique no link de ativação no email!');
  console.log('   5. Defina no PowerShell:');
  console.log('      $env:NVD_API_KEY="sua-chave-aqui"');
  console.log('\n💡 Tentando executar sem API key (pode falhar se não houver dados locais)...\n');
}

try {
  console.log('📦 Executando Dependency-Check...');
  console.log('⏳ Isso pode levar alguns minutos...\n');

  // Executa o comando
  const output = execSync(
    command,
    {
      encoding: 'utf-8',
      stdio: 'inherit' // Mostra a saída em tempo real
    }
  );

  console.log('\n✅ Scan concluído com sucesso!');
  console.log('📄 Relatórios gerados:');
  console.log('   - HTML: ./report/dependency-check-report.html');
  console.log('   - JSON: ./report/dependency-check-report.json');
  console.log('\n💡 Para visualizar melhor o JSON, execute:');
  console.log('   node view-report.js');

} catch (error) {
  console.error('\n❌ Erro ao executar Dependency-Check:');
  console.error(error.message);
  
  // Verifica se o erro é relacionado à API key
  const errorOutput = error.message || error.stdout || error.stderr || '';
  if ((errorOutput.includes('403') || errorOutput.includes('404')) && useApiKey) {
    console.log('\n🔴 Problema com a NVD API Key:');
    console.log('   1. A API key pode precisar de alguns minutos para ativar após a criação');
    console.log('   2. Verifique se você clicou no link de ativação no email do NVD');
    console.log('   3. Tente executar sem atualizar o NVD (usa dados locais):');
    console.log('      $env:SKIP_NVD_UPDATE="true"');
    console.log('      node scan.js');
    console.log('   4. Ou aguarde alguns minutos e tente novamente com a API key');
  } else if (errorOutput.includes('403') || errorOutput.includes('404')) {
    console.log('\n🔴 Erro ao atualizar dados do NVD:');
    console.log('   Tente executar sem atualizar (usa dados locais):');
    console.log('   $env:SKIP_NVD_UPDATE="true"');
    console.log('   node scan.js');
  }
  
  // Mesmo com erro, verifica se o relatório foi gerado
  const reportPath = path.join(reportDir, 'dependency-check-report.html');
  const reportJsonPath = path.join(reportDir, 'dependency-check-report.json');
  
  if (fs.existsSync(reportPath)) {
    console.log('\n⚠️  Relatório parcial pode estar disponível em:');
    console.log('📄 ./report/dependency-check-report.html');
  } else if (fs.existsSync(reportJsonPath)) {
    console.log('\n⚠️  Relatório JSON gerado (mas HTML falhou):');
    console.log('📄 ./report/dependency-check-report.json');
  } else {
    console.log('\n❌ Nenhum relatório foi gerado porque o Dependency-Check falhou.');
    console.log('\n💡 Alternativas:');
    console.log('   1. Use o scan-fallback.js que usa npm audit:');
    console.log('      node scan-fallback.js');
    console.log('   2. Ou execute npm audit diretamente:');
    console.log('      npm audit --json > report/npm-audit-report.json');
    console.log('   3. Depois visualize com:');
    console.log('      node view-report.js');
  }
  
  process.exit(1);
}
