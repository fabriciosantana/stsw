# Controle de execução e evidências — P2-12

| Parte | Situação | Evidência |
|---|---|---|
| Explicações conceituais, prevenção e incidente real | Documentado com referências | `P2-12-ssrf.md` |
| Simulação vulnerável e protegida, **somente localhost** | **Executado: 4/4 verificações passaram** | `resultado-lab-local.txt` |
| Código da simulação | Disponível e compilação Python testada | `lab/ssrf_lab.py` |
| Arquivo de requisição de exemplo SSRFmap | Preparado | `lab/request-ssrfmap.txt` |
| Download, instalação e uso efetivo do SSRFmap | **Ainda não comprovado neste documento** | Nenhum log da ferramenta anexado inicialmente; execução depende de ambiente com acesso ao projeto SSRFmap |
| Exploração no OWASP Juice Shop / PortSwigger | **Fora do escopo de evidências deste PR** | Pertence à atividade prática I2-07, PR #256 |

**Restrições:** os servidores do laboratório são vinculados a `127.0.0.1`, com alvos limitados às portas de teste. Nenhum serviço ou dado real de terceiros foi consultado.

**Comando repetível:** `python3 lab/ssrf_lab.py --demo`. Os resultados devem ser registrados na máquina que efetivamente executá-lo, não simulados.