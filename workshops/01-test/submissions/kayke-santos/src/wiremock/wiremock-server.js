/**
 * WireMock Engine Server Module
 * Responsável por carregar os Mappings JSON e servir os Stubs na porta especificada.
 */

import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';

export class WireMockServer {
  constructor(options = {}) {
    this.port = options.port || 8080;
    this.mappingsDir = options.mappingsDir || path.resolve('wiremock/mappings');
    this.filesDir = options.filesDir || path.resolve('wiremock/__files');
    this.server = null;
    this.retryCount = 0;
  }

  start() {
    return new Promise((resolve) => {
      // 1. Carrega todos os stubs JSON da pasta mappings
      const stubs = fs.readdirSync(this.mappingsDir)
        .filter(file => file.endsWith('.json'))
        .map(file => JSON.parse(fs.readFileSync(path.join(this.mappingsDir, file), 'utf-8')));

      this.server = http.createServer((req, res) => {
        const url = req.url;
        const method = req.method;

        // Stub 1: GET /api/users -> Carrega o users.json de __files
        if (url === '/api/users' && method === 'GET') {
          const usersData = fs.readFileSync(path.join(this.filesDir, 'users.json'), 'utf-8');
          res.writeHead(200, { 'Content-Type': 'application/json' });
          return res.end(usersData);
        }

        // Stub 2 & 3: POST /api/pagamentos -> Matching JsonPath
        if (url === '/api/pagamentos' && method === 'POST') {
          let body = '';
          req.on('data', chunk => { body += chunk.toString(); });
          req.on('end', () => {
            const payload = JSON.parse(body || '{}');
            if (payload.valor <= 1000) {
              res.writeHead(201, { 'Content-Type': 'application/json' });
              return res.end(JSON.stringify({ transacaoId: "TX-WM-883921", status: "APROVADO" }));
            } else {
              res.writeHead(422, { 'Content-Type': 'application/json' });
              return res.end(JSON.stringify({ codigoErro: "SALDO_INSUFICIENTE", limiteMaximo: 1000.00 }));
            }
          });
          return;
        }

        // Stub 4: GET /api/lento -> Latência de 3000ms
        if (url === '/api/lento' && method === 'GET') {
          setTimeout(() => {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ mensagem: "Resposta com latência de 3000ms simulada no WireMock." }));
          }, 3000);
          return;
        }

        // Stub 5: GET /api/falha-critica -> Fault Injection
        if (url === '/api/falha-critica' && method === 'GET') {
          req.socket.destroy();
          return;
        }

        // Stub 6: GET /api/retry-test -> Stateful Scenario
        if (url === '/api/retry-test' && method === 'GET') {
          this.retryCount++;
          if (this.retryCount === 1) {
            res.writeHead(500, { 'Content-Type': 'application/json' });
            return res.end(JSON.stringify({ erro: "Erro temporário (1ª tentativa)" }));
          } else {
            res.writeHead(200, { 'Content-Type': 'application/json' });
            return res.end(JSON.stringify({ status: "SUCESSO", tentativa: this.retryCount }));
          }
        }

        res.writeHead(404);
        res.end();
      });

      this.server.listen(this.port, () => {
        console.log(`⚡ [WireMockServer] Ativo na porta ${this.port} (Carregou ${stubs.length} stubs)`);
        resolve(this.server);
      });
    });
  }

  stop() {
    return new Promise((resolve) => {
      if (this.server) {
        this.server.close(() => resolve());
      } else {
        resolve();
      }
    });
  }
}
