from Triangulo import Triangulo
import pytest

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
        t = Triangulo(201,2,3)
        assert t.tipo_triangulo() == "Fora do intervalo"
    
    def test_eh_valido(self):
        t = Triangulo(1,1,1)
        assert t.eh_valido() == True
        t = Triangulo(1,1,3)
        assert t.eh_valido() == False
    
    def test_eh_valido_intervalo_dos_lados(self):
        t = Triangulo(1,1,1)
        assert t.eh_valido_intervalo_dos_lados() == True
        t = Triangulo(201,1,1)
        assert t.eh_valido_intervalo_dos_lados() == False
        
        try:
            t = Triangulo("a",1,1)
            assert False
        except ValueError:
            assert True
        

    @pytest.mark.parametrize("a,b,c,expected", [(1,1,1, True)])
    def test_eh_valido2(self, a, b, c, expected):
        t = Triangulo(a,b,c)
        assert t.eh_valido() == expected


