class Triangulo:
    def __init__(self, a, b, c):
        self.a = int(a)
        self.b = int(b)
        self.c = int(c)

    def eh_valido(self):
        # Verifica se os lados podem formar um triângulo
        return (self.a + self.b > self.c) and (self.a + self.c > self.b) and (self.b + self.c > self.a)

    def tipo_triangulo(self):
        if not self.eh_valido():
            return "Não é um triângulo"
        if self.a == self.b == self.c:
            return "Equilátero"
        elif self.a == self.b or self.a == self.c or self.b == self.c:
            return "Isósceles"
        else:
            return "Escaleno"
