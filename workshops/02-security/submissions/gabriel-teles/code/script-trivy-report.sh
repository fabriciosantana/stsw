#!/bin/bash
# Script de Demonstração do Trivy: Varredura de Imagens com Geração de Relatórios JSON.

# Configuração de cores para o terminal
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # Sem cor

REPORT_DIR="./trivy_reports"
mkdir -p $REPORT_DIR # Cria o diretório para os relatórios

echo -e "${BLUE}========================================================"
echo -e "  TRIVY: DEMONSTRAÇÃO COM GERAÇÃO DE RELATÓRIOS JSON    "
echo -e "========================================================${NC}"
echo -e "${YELLOW}Os relatórios serão salvos em: ${REPORT_DIR}${NC}"
echo ""

# --- 1. CASO DE USO 1: SUCESSO (Imagem Limpa - Alpine mais recente) ---
TARGET_IMAGE_CLEAN="alpine:latest"
REPORT_CLEAN="${REPORT_DIR}/report_alpine.json"
echo -e "${GREEN}--- [1/2] SUCESSO: Varredura e Geração de Relatório (Imagem Limpa) ---${NC}"
echo -e "${YELLOW}Alvo: ${TARGET_IMAGE_CLEAN} | Output: ${REPORT_CLEAN}${NC}"
echo "--------------------------------------------------------"

# Comando: Varre a imagem, filtra por HIGH/CRITICAL, define o formato JSON e salva no arquivo.
trivy image --severity HIGH,CRITICAL --format json --output $REPORT_CLEAN $TARGET_IMAGE_CLEAN

# Verifica o código de saída
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Resultado: SUCESSO. Relatório gerado com sucesso.${NC}"
else
    echo -e "${RED}Resultado: FALHA INESPERADA na execução do Trivy.${NC}"
fi

echo ""
echo -e "${BLUE}========================================================${NC}"
echo ""


# --- 2. CASO DE USO 2: FALHA (Imagem Vulnerável - Nginx Antigo) ---
TARGET_IMAGE_VULN="nginx:1.21.6-alpine"
REPORT_VULN="${REPORT_DIR}/report_nginx.json"
echo -e "${RED}--- [2/2] FALHA: Varredura e Geração de Relatório (Imagem Antiga) ---${NC}"
echo -e "${YELLOW}Alvo: ${TARGET_IMAGE_VULN} | Output: ${REPORT_VULN}${NC}"
echo "--------------------------------------------------------"

# Comando: Varre a imagem, filtra por HIGH/CRITICAL, define o formato JSON e salva no arquivo.
trivy image --severity HIGH,CRITICAL --format json --output $REPORT_VULN $TARGET_IMAGE_VULN

# Verifica o código de saída (geralmente != 0 = FALHA se encontrar problemas)
if [ $? -ne 0 ]; then
    echo -e "${RED}Resultado: FALHA. Vulnerabilidades CRITICAL/HIGH ENCONTRADAS. Relatório gerado.${NC}"
else
    echo -e "${GREEN}Resultado: SUCESSO INESPERADO (a imagem deveria ter falhas).${NC}"
fi

echo ""
echo -e "${YELLOW}--- Visualizando o relatório de falha (${REPORT_VULN}) ---${NC}"
echo -e "${YELLOW}O formato JSON é ideal para integração CI/CD/Dashboards.${NC}"
echo "--------------------------------------------------------"

# Usa o `jq` para formatar e mostrar apenas as chaves principais do relatório para demonstração
# (Assumindo que `jq` está instalado no ambiente de demonstração)
if command -v jq &> /dev/null
then
    jq '.Results[].Vulnerabilities[] | {VulnerabilityID, Severity, PkgName, InstalledVersion, FixedVersion}' $REPORT_VULN | head -n 15
else
    echo -e "${RED}AVISO: 'jq' não encontrado. Exibindo as primeiras linhas do JSON bruto...${NC}"
    head -n 20 $REPORT_VULN
fi

echo -e "${BLUE}========================================================${NC}"
echo -e "              DEMONSTRAÇÃO COMPLETA                     "
echo -e "========================================================${NC}"