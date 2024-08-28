from Triangulo import Triangulo

def main():
    try:
        a = int(input("Digite o comprimento do primeiro lado: "))
        b = int(input("Digite o comprimento do segundo lado: "))
        c = int(input("Digite o comprimento do terceiro lado: "))
        
        print(Triangulo(a,b, c))

    except ValueError as e:
        print(str(e))
    except Exception:
        print("Ocorreu um erro")


if __name__ == "__main__":
    main()
