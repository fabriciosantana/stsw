from Triangulo import Triangulo

def main():
    try:
        a = int(input('Digite o lado A: '))
        b = int(input('Digite o lado B: '))
        c = int(input('Digite o lado C: '))

        triangulo = Triangulo(a, b, c)

        print(triangulo.tipo_triangulo())

    except Exception:
        print("Ocorreu um erro")

if __name__ == "__main__":
    main()