import sys
import os
import pytest
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from Triangulo import Triangulo

class TestTriangulo():
    def test_tipo_triangulo(self):
        t = Triangulo(1,1,1)
        assert t.tipo_triangulo() == "Equilátero"
        t = Triangulo(1,100,100)
        assert t.tipo_triangulo() == "Isósceles"
        t = Triangulo(100,199,200)
        assert t.tipo_triangulo() == "Escaleno"
        t = Triangulo(1,2,200)
        assert t.tipo_triangulo() == "Não é um triângulo"
        
        with pytest.raises(ValueError, match="Somente números inteiros entre 1 e 200."):
            Triangulo(0, 2, 201)

        try:
            t = Triangulo("a",1,1)
            assert False
        except ValueError:
            assert True
    
    def test_eh_valido(self):
        t = Triangulo(1,1,1)
        assert t.eh_valido() == True
        t = Triangulo(1,1,3)
        assert t.eh_valido() == False

    @pytest.mark.parametrize("a,b,c,expected", [(1, 1, 1, True),(1, 100, 100, True),(100, 199, 200, True),(1, 2, 200, False)])
    def test_eh_valido2(self, a, b, c, expected):
        t = Triangulo(a, b, c)
        assert t.eh_valido() == expected

    