from Triangulo import Triangulo

def main():
    try:
        a = int(input("Informe o lado A: "))
        b = int(input("Informe o lado B: "))
        c = int(input("Informe o lado C: "))
        
        triangulo = Triangulo(a, b, c)
        print(triangulo.tipo_triangulo())

    except Exception as e:
        print(e)

if __name__ == "__main__":
    main()