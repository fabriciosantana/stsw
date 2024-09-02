from Triangulo import Triangulo

class TestTriangulo():
    
    def test_tipo_triangulo(self):
        t = Triangulo(1,1,1)
        assert  t.tipo_triangulo() == "Equilátero"
        t = Triangulo(1,2,2)
        assert t.tipo_triangulo() == "Isósceles"
        t = Triangulo(4,2,3)
        assert t.tipo_triangulo() == "Escaleno"
        t = Triangulo(1,2,3)
        assert t.tipo_triangulo() == "Não é um triângulo"
    
    def test_eh_valido(self):
        t = Triangulo(1,1,1)
        assert t.eh_valido() == True
    
    def test_eh_valido_intervalo_dos_lados(self):
        t = Triangulo(1,1,1)
        assert t.eh_valido_intervalo_dos_lados() == True
