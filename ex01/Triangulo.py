class Triangulo():
    def __init__(self, a, b, c):
        try:
            self.a = int(a)
            self.b = int(b)        
            self.c = int(c)
        except ValueError:
            raise ValueError("Exceção: os lados dos triângulos devem ser um número inteiro.")

    def __str__(self):
        return f"O triângulo é {self.tipo_triangulo()}, pois possui lados {self.a}, {self.b} e {self.c}"

    def tipo_triangulo(self):

        if not self.eh_valido_intervalo_dos_lados():
            return "Fora do intervalo"

        if self.eh_valido():
            if self.a == self.b == self.c:
                return "Equilátero"
            elif self.a == self.b or self.b == self.c or self.a == self.c:
                return "Isósceles"
            else:
                return "Escaleno"
        else:
            return "Não é um triângulo"
    
    def eh_valido(self):
        return (self.a + self.b > self.c and
                self.b + self.c > self.a and
                self.c + self.a > self.b)
    
    def eh_valido_intervalo_dos_lados(self):
        return ((self.a >= 1 and self.a <= 200) and
                (self.b >= 1 and self.b <= 200) and
                (self.c >= 1 and self.c <= 200))