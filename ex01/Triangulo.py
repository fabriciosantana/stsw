class Triangulo():
    def __init__(self, a, b, c):
        try:
            self.a = int(a)
            self.b = int(b)
            self.c = int(c)
        except:
            raise ValueError("Os lados devem ser números inteiros")
        
    def tipo_triangulo(self):

        if not self.intervalo_valido():
            return "Fora do Intervalo"

        if self.lados_validos():
            if self.a == self.b and self.b == self.c:
                return "Equilátero"
            elif self.a == self.b or self.a == self.c or self.b == self.c:
                return "Isósceles"
            else:
                return "Escaleno"
        else:
            return "Não é um triângulo"
    
    def lados_validos(self):
        return (self.a + self.b > self.c) and (self.a + self.c > self.b) and (self.b + self.c > self.a)
        
    def intervalo_valido(self):
        return (self.a <= 200 and self.b <=200 and self.c <= 200)