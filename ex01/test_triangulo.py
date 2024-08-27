import unittest
from triangulo import classifica_triangulo

class TestClassificaTriangulo(unittest.TestCase):

    def test_equilatero(self):
        self.assertEqual(classifica_triangulo(5, 5, 5), "Equilátero")

    def test_isosceles(self):
        self.assertEqual(classifica_triangulo(5, 5, 3), "Isósceles")

    def test_escaleno(self):
        self.assertEqual(classifica_triangulo(5, 4, 3), "Escaleno")

    def test_nao_triangulo(self):
        self.assertEqual(classifica_triangulo(1, 2, 3), "Não é um triângulo")

    def test_lados_invalidos(self):
        self.assertEqual(classifica_triangulo(-5, 0, 5), "Lados inválidos")

if __name__ == '__main__':
    unittest.main()
