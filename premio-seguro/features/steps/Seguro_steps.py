from behave import given, when, then
from Seguro import Seguro

@given("que o motorista tem {idade} anos")
def step_given_motorista_tem_idade(context, idade):
    context.idade = int(idade)

@given("o motorista possui {pontos} pontos na carteira")
def step_given_motorista_tem_pontos(context, pontos):
    context.pontos = int(pontos)

@when("eu calcular o prêmio do seguro")
def step_when_calcular_premio(context):
    seguro = Seguro(context.idade, context.pontos)
    context.resultado = seguro.calcular_premio()

@then('o resultado deve ser {esperado}')
def step_then_resultado_deve_ser_nao_seguravel(context, esperado):
    print(context.resultado['pode_ser_segurado'])
    print(esperado)
    esperado_bool = True if esperado == 'True' else False
    assert context.resultado['pode_ser_segurado'] == esperado_bool, f"Esperado {esperado_bool}, mas obteve {context.resultado['pode_ser_segurado']}"
