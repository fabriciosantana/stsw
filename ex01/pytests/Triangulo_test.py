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

    @pytest.mark.parametrize("a,b,c,expected", [
        (1, 1, 1, True),
        (200, 200, 200, True),
        (0, 1, 1, False),
        (201, 1, 1, False),
        (1, 0, 1, False),
        (1, 201, 1, False),
        (1, 1, 0, False),
        (1, 1, 201, False),
        (100, 100, 100, True),
        (2, 1, 1, False),
        (199, 1, 1, False),
        (200, 1, 1, False),
        (100, 99, 2, True),
        (100, 100, 1, True),
        (100, 99, 98, True)
    ])
    def test_triangulo_boundary_values(self, a, b, c, expected):
        if expected:
            t = Triangulo(a, b, c)
            assert t.eh_valido() == expected
        else:
            t = Triangulo(a, b, c)
            assert t.eh_valido() == False
    @pytest.mark.parametrize("a,b,c,expected_type", [
        (5, 5, 5, "Equilátero"),
        (100, 100, 100, "Equilátero"),
        (5, 5, 3, "Isósceles"),
        (3, 5, 5, "Isósceles"),
        (5, 3, 5, "Isósceles"),
        (3, 4, 5, "Escaleno"),
        (5, 7, 9, "Escaleno"),
        (1, 1, 3, "Não é um triângulo"),
        (1, 3, 1, "Não é um triângulo"),
        (3, 1, 1, "Não é um triângulo"),
        (0, 5, 5, "Fora do intervalo"),
        (201, 5, 5, "Fora do intervalo"),
        (5, 0, 5, "Fora do intervalo"),
        (5, 201, 5, "Fora do intervalo"),
        (5, 5, 0, "Fora do intervalo"),
        (5, 5, 201, "Fora do intervalo"),
    ])
    def test_triangulo_equivalence_classes(self, a, b, c, expected_type):
        if expected_type in ["Equilátero", "Isósceles", "Escaleno"]:
            t = Triangulo(a, b, c)
            assert t.tipo_triangulo() == expected_type
        else:
            t = Triangulo(a, b, c)
            assert t.tipo_triangulo() == expected_type

    def test_triangulo_decision_coverage(self):
        t1 = Triangulo(3, 4, 5)
        assert t1.eh_valido() == True

        t = Triangulo(1, 1, 3)
        assert t.tipo_triangulo() == "Não é um triângulo"

        t = Triangulo(0, 5, 5)
        assert t.tipo_triangulo() == "Fora do intervalo"

        t = Triangulo(201, 5, 5)
        assert t.tipo_triangulo() == "Fora do intervalo"
        t2 = Triangulo(5, 5, 5)
        assert t2.tipo_triangulo() == "Equilátero"

        t3 = Triangulo(5, 5, 3)
        assert t3.tipo_triangulo() == "Isósceles"

        t4 = Triangulo(3, 4, 5)
        assert t4.tipo_triangulo() == "Escaleno"

    def test_triangulo_condition_coverage(self):
        t1 = Triangulo(100, 100, 100)
        assert t1.eh_valido() == True

        t2 = Triangulo(50, 60, 70)
        assert t2.eh_valido() == True

        t = Triangulo(0, 50, 50)
        assert t.eh_valido() == False
        
        t = Triangulo(201, 50, 50)
        assert t.eh_valido() == False

        t = Triangulo(1, 1, 3)
        assert t.eh_valido() == False

        t3 = Triangulo(5, 5, 5)
        assert t3.tipo_triangulo() == "Equilátero"

        t4 = Triangulo(5, 5, 3)
        assert t4.tipo_triangulo() == "Isósceles"

        t5 = Triangulo(5, 3, 5)
        assert t5.tipo_triangulo() == "Isósceles"

        t6 = Triangulo(3, 5, 5)
        assert t6.tipo_triangulo() == "Isósceles"

        t7 = Triangulo(3, 4, 5)
        assert t7.tipo_triangulo() == "Escaleno"

    

                


