class Triangulo():
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

    def tipo_triangulo(self):

        if self.eh_valido():
            if self.a == self.b and self.b == self.c:
                return "Equilátero"
            elif self.a == self.b or self.a == self.c or self.b == self.c:
                return "Isósceles"
            else:
                return "Escaleno"
        else:
            return "Não é um triângulo"
    
    def eh_valido(self):
        return (self.a + self.b > self.c) and (self.a + self.c > self.b) and (self.b + self.c > self.a)
        