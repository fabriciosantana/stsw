from behave import given, when, then
from Triangulo import Triangulo

@given('os lados do triângulo {a}, {b}, {c}')
def step_given_os_lados(context, a, b, c):
    try:
        context.triangulo = Triangulo(a, b, c)
    except ValueError as e:
        context.error = str(e)

@when('eu classifico o triângulo')
def step_when_eu_classifico(context):
    if hasattr(context, 'triangulo'):
        context.resultado = context.triangulo.tipo_triangulo()
    else:
        context.resultado = context.error

@then('o resultado deve ser "{esperado}"')
def step_then_o_resultado_deve_ser(context, esperado):
    assert context.resultado == esperado, f'Esperado: {esperado}, Obtido: {context.resultado}'

@then('o sistema deve disparar uma exception')
def step_then_o_sistema_deve_disparar_exception(context):
    assert "Os lados devem ser números inteiros" in context.resultado