#!/bin/bash
# Runner do teste OpenWebUI - navegacao + APIs leves
# Uso: ./run.sh [smoke|carga|relatorio]
#
# JMeter: usa $JMETER se definido, senao procura no PATH.
#   export JMETER=/opt/apache-jmeter-5.6.3/bin/jmeter
# Credenciais: obrigatorias via ambiente (nao versionar senha).
#   export OWUI_EMAIL="..." OWUI_PASSWORD="..."
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JMETER="${JMETER:-$(command -v jmeter || true)}"
if [ -z "$JMETER" ]; then
  echo "ERRO: JMeter nao encontrado. Defina JMETER=/caminho/para/bin/jmeter ou adicione ao PATH." >&2
  exit 1
fi
JMX="$SCRIPT_DIR/openwebui-navegacao.jmx"
PROPS="$SCRIPT_DIR/user.properties"
MODO="${1:-smoke}"

if [ -z "${OWUI_EMAIL:-}" ] || [ -z "${OWUI_PASSWORD:-}" ]; then
  echo "ERRO: defina OWUI_EMAIL e OWUI_PASSWORD antes de rodar." >&2
  exit 1
fi

case "$MODO" in
  smoke)
    echo "== SMOKE: 2 threads, 1 loop =="
    "$JMETER" -n -t "$JMX" -q "$PROPS" \
      -l "$SCRIPT_DIR/smoke.jtl" -j "$SCRIPT_DIR/smoke.log" -e -o "$SCRIPT_DIR/report-smoke" -f \
      -Jhost=localhost -Jport=8080 -Jprotocol=http \
      -Jthreads=2 -Jramp=5 -Jloops=1 -Jthink_time=300 \
      -Jemail="$OWUI_EMAIL" -Jpassword="$OWUI_PASSWORD"
    ;;
  carga)
    echo "== CARGA: 50 threads, 10 loops (~500 amostras por sampler) =="
    "$JMETER" -n -t "$JMX" -q "$PROPS" \
      -l "$SCRIPT_DIR/carga.jtl" -j "$SCRIPT_DIR/carga.log" -e -o "$SCRIPT_DIR/report-carga" -f \
      -Jhost=localhost -Jport=8080 -Jprotocol=http \
      -Jthreads=50 -Jramp=50 -Jloops=10 -Jthink_time=300 \
      -Jemail="$OWUI_EMAIL" -Jpassword="$OWUI_PASSWORD"
    ;;
  relatorio)
    # Regera HTML a partir de um JTL existente: ./run.sh relatorio carga.jtl report-carga
    JTL="${2:-carga.jtl}"
    OUT="${3:-report-carga}"
    echo "== Relatorio de $JTL =="
    "$JMETER" -g "$SCRIPT_DIR/$JTL" -q "$PROPS" -o "$SCRIPT_DIR/$OUT"
    ;;
  *)
    echo "Uso: $0 [smoke|carga|relatorio]"
    exit 1
    ;;
esac
