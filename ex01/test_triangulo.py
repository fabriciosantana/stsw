import pytest
from Triangulo import Triangulo

def test_equilatero():
    triangulo = Triangulo(5, 5, 5)
    assert triangulo.tipo_triangulo() == "Equilátero"

def test_isosceles():
    triangulo = Triangulo(5, 5, 8)
    assert triangulo.tipo_triangulo() == "Isósceles"

def test_escaleno():
    triangulo = Triangulo(3, 4, 5)
    assert triangulo.tipo_triangulo() == "Escaleno"

def test_nao_triangulo():
    triangulo = Triangulo(1, 2, 3)
    assert triangulo.tipo_triangulo() == "Não é um triângulo"

def test_lados_fora_intervalo():
    triangulo = Triangulo(201, 10, 10)
    assert triangulo.tipo_triangulo() == "Não é um triângulo"

def test_lados_negativos():
    triangulo = Triangulo(-1, 10, 10)
    assert triangulo.tipo_triangulo() == "Não é um triângulo"
