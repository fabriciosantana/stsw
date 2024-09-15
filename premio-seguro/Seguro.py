class Seguro:
    def __init__(self, idade_motorista, pontos_motorista):
        self.idade_motorista = idade_motorista
        self.pontos_motorista = pontos_motorista
        self.taxa_base = 500

    def calcular_premio(self):
        valor_premio = 0
        multiplicador_idade = 0
        desconto_motorista_seguro = 0
        pode_ser_segurado = True

        if self.idade_motorista < 16:
            pode_ser_segurado = False
        elif self.idade_motorista < 25:
            multiplicador_idade = 2.8
            if self.pontos_motorista < 1:
                desconto_motorista_seguro = 50
        elif self.idade_motorista < 35:
            multiplicador_idade = 1.8
            if self.pontos_motorista < 3:
                desconto_motorista_seguro = 50
        elif self.idade_motorista < 45:
            multiplicador_idade = 1.0
            if self.pontos_motorista < 5:
                desconto_motorista_seguro = 100
        elif self.idade_motorista < 60:
            multiplicador_idade = 0.8
            if self.pontos_motorista < 7:
                desconto_motorista_seguro = 150
        elif self.idade_motorista < 100:
            multiplicador_idade = 1.5
            if self.pontos_motorista < 5:
                desconto_motorista_seguro = 200
        else:
            pode_ser_segurado = False

        if self.pontos_motorista > 12:
            pode_ser_segurado = False

        valor_premio = self.taxa_base * multiplicador_idade - desconto_motorista_seguro

        return {"valor_premio": valor_premio, "pode_ser_segurado": pode_ser_segurado}


