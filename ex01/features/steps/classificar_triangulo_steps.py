from behave import Given, When, Then
from Triangulo import Triangulo

@Given("os lados do triângulo {a:d}, {b:d}, {c:d}")
def step_given_lados_do_triangulo(context, a, b, c):
    try:
        context.triangulo = Triangulo(a, b, c)
    except ValueError as e:
        context.value_error = e

@When("eu classifico o triângulo")
def step_when_classifico_o_triangulo(context):
    if hasattr(context, 'value_error'):
        context.resultado = "Fora do intervalo"
    else:
        context.resultado = context.triangulo.tipo_triangulo()

@Then("o resultado deve ser {esperado}")
def step_then_o_resultado_deve_ser(context, esperado):
    assert context.resultado == esperado, f"Esperado {esperado}, mas obteve {context.resultado}"
