import pytest
from Seguro import Seguro

@pytest.mark.parametrize("idade, pontos, esperado", [
    (15, 1, False),
    (16, 1, True),   
    (25, 0, True),   
    (99, 0, True),  
    (100, 0, False),
])
def test_boundary_values(idade, pontos, esperado):
    s = Seguro(idade, pontos)
    resultado = s.calcular_premio()
    assert resultado['pode_ser_segurado'] == esperado

@pytest.mark.parametrize("idade, pontos, esperado", [
    (18, 0, True),    
    (35, 5, True),    
    (60, 10, True),   
    (110, 0, False),  
])
def test_equivalence_class_partitioning(idade, pontos, esperado):
    s = Seguro(idade, pontos)
    resultado = s.calcular_premio()
    assert resultado['pode_ser_segurado'] == esperado

@pytest.mark.parametrize("idade, pontos, esperado", [
    (20, 0, 1350),   
    (30, 2, 850),    
    (40, 4, 400),    
])
def test_decision_table(idade, pontos, esperado):
    s = Seguro(idade, pontos)
    resultado = s.calcular_premio()
    assert resultado['valor_premio'] == esperado

def test_decision_coverage():
    s = Seguro(24, 0)  
    resultado = s.calcular_premio()
    assert resultado['valor_premio'] > 0

    s = Seguro(45, 6)  
    resultado = s.calcular_premio()
    assert resultado['valor_premio'] > 0

def test_condition_coverage():
    s = Seguro(18, 1)  
    resultado = s.calcular_premio()
    assert resultado['pode_ser_segurado'] is True

    s = Seguro(55, 8) 
    resultado = s.calcular_premio()
    assert resultado['pode_ser_segurado'] is True

def test_statement_coverage():
    s = Seguro(23, 1)  
    resultado = s.calcular_premio()
    assert resultado['valor_premio'] > 0
    assert resultado['pode_ser_segurado'] is True




