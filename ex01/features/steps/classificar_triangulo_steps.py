from behave import Given, When, Then
from Triangulo import Triangulo

@Given("os lados do triângulo {a}, {b}, {c}")
def step_given_lados_do_triangulo(context, a, b, c):
    context.a = a
    context.b = b
    context.c = c

@When("eu classifico o triângulo")
def step_when_classifico_o_triangulo(context):
    triangulo = Triangulo(context.a, context.b, context.c)
    context.resultado = triangulo.tipo_triangulo()

@Then("o resultado deve ser {esperado}")
def set_then_o_resultado_deve_ser(context, esperado):
    assert context.resultado == esperado, f"Esperado {esperado}, mas obteve {context.resultado}"
