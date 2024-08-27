from Triangulo import Triangulo

def main():
    try:
        # Coletar os lados do triângulo
        a = int(input("Digite o valor do lado a: "))
        b = int(input("Digite o valor do lado b: "))
        c = int(input("Digite o valor do lado c: "))

        # Criar uma instância da classe Triangulo
        triangulo = Triangulo(a, b, c)

        # Determinar o tipo do triângulo e exibir o resultado
        resultado = triangulo.tipo_triangulo()
        print(f"O triângulo é: {resultado}")

    except ValueError:
        print("Por favor, insira valores inteiros válidos.")
    except Exception as e:
        print(f"Ocorreu um erro: {e}")

if __name__ == "__main__":
    main()
