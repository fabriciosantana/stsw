from flask import Flask, render_template, request
from Triangulo import Triangulo

app = Flask(__name__)

@app.route("/", methods=["GET", "POST"])
def main():
    result = None
    if request.method == "POST":
        try:
            side_a = int(request.form["side_a"])
            side_b = int(request.form["side_b"])
            side_c = int(request.form["side_c"])

            triangulo = Triangulo(side_a, side_b, side_c)
            result = triangulo.tipo_triangulo()
        except ValueError:
            result = "Por favor, insira valores numéricos válidos."

    return render_template("index.html", result=result)

if __name__ == "__main__":
    app.run(debug=True)
