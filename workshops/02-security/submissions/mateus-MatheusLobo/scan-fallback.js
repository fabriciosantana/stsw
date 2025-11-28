const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');

console.log('🔍 Iniciando scan de vulnerabilidades...\n');

const reportDir = path.join(__dirname, 'report');
if (!fs.existsSync(reportDir)) {
  fs.mkdirSync(reportDir, { recursive: true });
}

// Tenta usar Dependency-Check primeiro
const isWindows = process.platform === 'win32';
const dependencyCheckCmd = isWindows ? 'dependency-check.bat' : 'dependency-check.sh';

let useDependencyCheck = false;
try {
  if (isWindows) {
    execSync('where dependency-check.bat', { stdio: 'ignore' });
  } else {
    execSync('which dependency-check.sh', { stdio: 'ignore' });
  }
  useDependencyCheck = true;
} catch (error) {
  console.log('⚠️  Dependency-Check não encontrado. Usando npm audit...\n');
}

if (useDependencyCheck) {
  const nvdApiKey = process.env.NVD_API_KEY;
  const skipUpdate = process.env.SKIP_NVD_UPDATE === 'true';
  let command = `${dependencyCheckCmd} --project "vulnerable-app" --scan ./ --format HTML --format JSON --out ./report`;
  
  if (skipUpdate) {
    command += ` --noupdate`;
    console.log('⚠️  Usando dados locais (sem atualizar do NVD)...\n');
  } else if (nvdApiKey) {
    command += ` --nvdApiKey ${nvdApiKey} --nvdApiDelay 12000`;
  }
  
  try {
    console.log('📦 Tentando Dependency-Check...\n');
    execSync(command, { encoding: 'utf-8', stdio: 'inherit' });
    console.log('\n✅ Scan concluído com Dependency-Check!');
    console.log('📄 Relatórios gerados:');
    console.log('   - HTML: ./report/dependency-check-report.html');
    console.log('   - JSON: ./report/dependency-check-report.json');
    process.exit(0);
  } catch (error) {
    console.log('\n⚠️  Dependency-Check falhou. Usando npm audit como alternativa...\n');
  }
}

// Fallback: usa npm audit
console.log('📦 Executando npm audit...\n');

// npm audit retorna exit code 1 quando há vulnerabilidades (isso é normal!)
// Então capturamos o output mesmo com erro
let auditJson = '';
try {
  auditJson = execSync('npm audit --json', { encoding: 'utf-8', stdio: 'pipe' });
} catch (error) {
  // Exit code 1 é esperado quando há vulnerabilidades
  auditJson = error.stdout || error.output[1] || '{}';
}

// Salva o relatório JSON
if (auditJson) {
  fs.writeFileSync(path.join(reportDir, 'npm-audit-report.json'), auditJson);
}

// Mostra resumo no console (pode dar erro, mas mostra as vulnerabilidades)
try {
  execSync('npm audit', { stdio: 'inherit' });
} catch (error) {
  // Ignora erro - exit code 1 é esperado quando há vulnerabilidades
}

console.log('\n✅ Scan concluído com npm audit!');
console.log('📄 Relatório JSON: ./report/npm-audit-report.json');
console.log('💡 Para ver detalhes, execute: npm audit');

