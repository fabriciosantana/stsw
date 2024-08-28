from behave import Given, When, Then
from Triangulo import Triangulo

@Given("os lados do triângulo {a}, {b}, {c}")
def step_given_lados_do_triangulo(context, a, b, c):
    try:
        context.triangulo = Triangulo(a, b, c)
    except ValueError as e:
        context.value_error = e

@When("eu classifico o triângulo")
def step_given_lados_do_triangulo(context):
     context.resultado = context.triangulo.tipo_triangulo()

@Then("o resultado deve ser {esperado}")
def set_then_o_resultado_deve_ser(context, esperado):
    assert context.resultado == esperado, f"Esperado {esperado}, mas obteve {context.resultado}"

@Then("o sistema deve disparar exception")
def step_then_dispara_exception(context):
    assert isinstance(context.value_error, ValueError)