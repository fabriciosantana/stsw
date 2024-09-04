import pytest
from Triangulo import Triangulo

class TestTriangulo:
    
    # Caso de Uso 1: Entrada Válida – Triângulo Equilátero
    def test_triangulo_equilatero(self):
        t = Triangulo(5, 5, 5)
        assert t.tipo_triangulo() == "Equilátero"
        assert t.eh_valido() == True
        assert t.eh_valido_intervalo_dos_lados() == True

    # Caso de Uso 2: Entrada Válida – Triângulo Isósceles
    def test_triangulo_isosceles(self):
        t = Triangulo(5, 5, 3)
        assert t.tipo_triangulo() == "Isósceles"
        assert t.eh_valido() == True
        assert t.eh_valido_intervalo_dos_lados() == True

    # Caso de Uso 3: Entrada Válida – Triângulo Escaleno
    def test_triangulo_escaleno(self):
        t = Triangulo(5, 4, 3)
        assert t.tipo_triangulo() == "Escaleno"
        assert t.eh_valido() == True
        assert t.eh_valido_intervalo_dos_lados() == True

    # Caso de Uso 4: Entrada Inválida – Não Forma Triângulo
    def test_triangulo_nao_valido(self):
        t = Triangulo(1, 2, 3)
        assert t.tipo_triangulo() == "Não é um triângulo"
        assert t.eh_valido() == False
        assert t.eh_valido_intervalo_dos_lados() == True

    # Caso de Uso 5: Entrada Inválida – Lados Negativos ou Zero
    def test_lados_invalidos(self):
        with pytest.raises(ValueError):
            Triangulo(-5, 0, 5)

    # Teste de Limite – Valores na fronteira
    def test_valores_extremos(self):
        # Triângulo com lados mínimos válidos
        t = Triangulo(1, 1, 1)
        assert t.tipo_triangulo() == "Equilátero"
        assert t.eh_valido() == True
        assert t.eh_valido_intervalo_dos_lados() == True
        
        # Triângulo com lados máximos válidos
        t = Triangulo(200, 200, 200)
        assert t.tipo_triangulo() == "Equilátero"
        assert t.eh_valido() == True
        assert t.eh_valido_intervalo_dos_lados() == True
        
        # Teste com valores fora do intervalo
        assert Triangulo(201, 1, 1).tipo_triangulo() == "Fora do intervalo"
        assert Triangulo(1, -1, 1).tipo_triangulo() == "Fora do intervalo"
        assert Triangulo(0, 0, 0).tipo_triangulo() == "Fora do intervalo"
