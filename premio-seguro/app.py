from Seguro import Seguro

def main():
    try:
        idade = int(input("Digite a idade do segurado: "))
        pontos = int(input("Quantos pontos tem na carteira? "))
        
        s = Seguro(idade, pontos)

        resultado = s.calcular_premio()

        print(f"Prêmio: {resultado['valor_premio']}, Pode segurar: {resultado['pode_ser_segurado']}")

    except ValueError as e:
        print("Ocorreu um erro: " + str(e))
    except Exception as e:
        print("Ocorreu um erro: " + str(e))

if __name__ == "__main__":
    main()