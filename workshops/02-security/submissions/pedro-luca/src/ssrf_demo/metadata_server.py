from http.server import BaseHTTPRequestHandler, HTTPServer

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        # responde com o token para qualquer requisição
        self.send_response(200)
        self.send_header('Content-type', 'text/plain')
        self.end_headers()
        self.wfile.write(b"SECRET_TOKEN=super_secret_value")

if __name__ == '__main__':
    server = HTTPServer(('0.0.0.0', 80), Handler)
    print('Metadata-like server running on http://0.0.0.0:80/')
    server.serve_forever()
