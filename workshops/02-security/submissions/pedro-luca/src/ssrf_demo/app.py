from flask import Flask, request, render_template_string
import requests

app = Flask(__name__)

@app.route('/')
def index():
    return """
    <html>
      <head><title>SSRF Demo</title></head>
      <body>
        <h1>SSRF Demo</h1>
        <form method="get" action="/fetch">
          <input name="url" placeholder="http://example.com">
          <button type="submit">Fetch</button>
        </form>
      </body>
    </html>
    """

@app.route('/fetch')
def fetch():
    target = request.args.get('url', '')
    try:
        # A aplicação faz uma requisição HTTP para a URL informada pelo usuário
        resp = requests.get(target, timeout=3)
        content = resp.text[:1000]
    except Exception as e:
        content = f"ERROR: {e}"
    return render_template_string("""
        <html>
          <head><title>Resultado</title></head>
          <body>
            <h2>Requisição para: {{ target }}</h2>
            <pre>{{ content }}</pre>
            <a href="/">Voltar</a>
          </body>
        </html>
    """, target=target, content=content)

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)
