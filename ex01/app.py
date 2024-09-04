from Triangulo import Triangulo

def main():
    try:
        print("Digite os lados do triângulo")
        a = int(input("Lado a: "))
        b = int(input("Lado b: "))
        c = int(input("Lado c: "))
        triangulo = Triangulo(a, b, c)
        print(triangulo.tipo_triangulo())
        
    except Exception:
        print("Ocorreu um erro")

if __name__ == "__main__":
    main()