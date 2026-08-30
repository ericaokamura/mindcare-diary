# Testes unitários - MindCare Diary

Estes testes foram escritos para a camada `services` do repositório `mindcare-diary`.

## Dependências

O `pom.xml` atual já possui `spring-boot-starter-test`, que fornece JUnit 5, Mockito e AssertJ.

## Execução

```bash
mvn test
```

Para executar somente os testes:

```bash
mvn -Dtest=*ServiceTest test
```

## Observação importante

`ConsultaService.atualizarConsulta()` atualmente chama `save()` e, logo depois, lança
`ConsultaNaoEncontradaException` mesmo quando a consulta existe. Um teste de sucesso
para esse método deve ser adicionado depois de corrigir o fluxo para retornar
normalmente após o `save()`.

`PushNotificationService` usa `FirebaseMessaging.getInstance()` estaticamente. Por isso,
o teste foi deixado como contrato/disabled para não introduzir uma dependência adicional
sem necessidade. Se quiser mockar Firebase estaticamente, adicione `mockito-inline` e
substitua o teste por `Mockito.mockStatic(...)`.
