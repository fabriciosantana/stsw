from flask import Flask, request, render_template_string

app = Flask(__name__)

@app.route('/')
def index():
    q = request.args.get('q', '')
    # refletindo diretamente o parâmetro para simular uma página vulnerável a reflected XSS
    return render_template_string(f"""
        <html>
          <head><title>Demo XSS</title></head>
          <body>
            <h1>Busca</h1>
            <form method="get">
              <input name="q" value="{q}">
              <button type="submit">Pesquisar</button>
            </form>
            <p>Resultado: {q}</p>
          </body>
        </html>
    """)

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)
