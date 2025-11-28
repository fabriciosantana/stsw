#!/bin/bash
# Script de Demonstração do Trivy focado apenas na varredura de Imagens de Contêiner.
# Executa exemplos de SUCESSO (Imagem Limpa) e FALHA (Imagem Vulnerável).

# Configuração de cores para o terminal (Para melhor visualização na apresentação)
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # Sem cor

echo -e "${BLUE}========================================================"
echo -e "       TRIVY: DEMONSTRAÇÃO DE VARREDURA DE IMAGENS      "
echo -e "========================================================${NC}"
echo ""

# --- 1. CASO DE USO 1: SUCESSO (Imagem Limpa - Alpine mais recente) ---
echo -e "${GREEN}--- [1/2] SUCESSO: Varredura de Imagem Atualizada ---${NC}"
echo -e "${YELLOW}Alvo: alpine:latest (Esperamos zero vulnerabilidades CRITICAL/HIGH).${NC}"
echo "--------------------------------------------------------"

# O comando usa --severity para filtrar e focar apenas no que é mais importante.
trivy image --severity HIGH,CRITICAL alpine:latest

# Verifica o código de saída (0 = OK)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Resultado: PASS. Nenhuma vulnerabilidade CRITICAL/HIGH encontrada. Imagem segura.${NC}"
else
    echo -e "${RED}Resultado: FALHA INESPERADA. Imagem atualizada deve estar limpa.${NC}"
fi

echo ""
echo -e "${BLUE}========================================================${NC}"
echo ""


# --- 2. CASO DE USO 2: FALHA (Imagem Vulnerável - Nginx Antigo) ---
echo -e "${RED}--- [2/2] FALHA: Varredura de Imagem Antiga ---${NC}"
echo -e "${YELLOW}Alvo: nginx:1.21.6-alpine (Contém vulnerabilidades conhecidas).${NC}"
echo "--------------------------------------------------------"

# Este comando irá listar as falhas e, em um pipeline de CI/CD, falharia o processo.
trivy image --severity HIGH,CRITICAL nginx:1.21.6-alpine

# Verifica o código de saída (geralmente != 0 = FALHA se encontrar problemas)
if [ $? -ne 0 ]; then
    echo -e "${RED}Resultado: FALHA. Vulnerabilidades CRITICAL/HIGH ENCONTRADAS. ${NC}"
    echo -e "${YELLOW}Destaque: Observe a coluna 'Fixed Version', que informa como corrigir o problema.${NC}"
else
    echo -e "${GREEN}Resultado: SUCESSO INESPERADO. A imagem deveria ter falhas.${NC}"
fi

echo ""
echo -e "${BLUE}========================================================${NC}"
echo -e "              DEMONSTRAÇÃO COMPLETA                     "
echo -e "========================================================${NC}"