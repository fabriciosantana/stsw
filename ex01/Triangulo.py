class Triangulo():
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

    def tipo_triangulo(self):
        self.eh_valido()
        
        if self.a == self.b == self.c:
            return "Equilátero"
        elif self.a == self.b or self.a == self.c or self.b == self.c:
            return "Isósceles"
        else:
            return "Escaleno"
    
    def eh_valido(self):
        if self.a + self.b <= self.c or self.a + self.c <= self.b or self.b + self.c <= self.a:
            raise Exception("Os lados informados não formam um triângulo")
        elif self.a <= 0 or self.b <= 0 or self.c <= 0:
            raise Exception("Os lados informados devem ser maiores que zero")
        elif self.a > 200 or self.b > 200 or self.c > 200 or self.a < 1 or self.b < 1 or self.c < 1:
            raise Exception("Os lados informados não podem ser nulos")
        
        return True