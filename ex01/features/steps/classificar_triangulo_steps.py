from behave import given, when, then
from Triangulo import Triangulo

@given('um triângulo com lados {lado_a}, {lado_b}, {lado_c}')
def step_given_triangulo(context, lado_a, lado_b, lado_c):
    context.triangulo = Triangulo(int(lado_a), int(lado_b), int(lado_c))

@when('classifico o triângulo')
def step_when_classifico_triangulo(context):
    context.resultado = context.triangulo.tipo_triangulo()

@then('o tipo de triângulo deve ser "{resultado_esperado}"')
def step_then_tipo_triangulo(context, resultado_esperado):
    assert context.resultado == resultado_esperado
