class Triangulo():
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c


    def tipo_triangulo(self):
        if self.eh_valido():
            if self.a == self.b == self.c:
                return "Equilátero"
            elif self.a == self.b or self.a == self.c or self.b == self.c:
                return "Isósceles"
            else:
                return "Escanelo"
        else:
            return "Não é um triângulo"
   
    def eh_valido(self):
        if (self.a >= 1 and self.a <= 200) and (self.b >= 1 and self.b <= 200) and (self.c >= 1 and self.c <= 200):
            if(self.a + self.b > self.c and self.a + self.c > self.b and self.b + self.c > self.a):
                return True
            else:
                print('Os lados fornecidos não formam um triângulo')
        else:
            print('Valores fora do intervalo')