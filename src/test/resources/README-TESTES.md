## Dependências

O `pom.xml` atual já possui `spring-boot-starter-test`, que fornece JUnit 5, Mockito e AssertJ.

## Execução

```bash
mvn test
```

### Instruções para rodar os testes unitários:
- Utilizar branch feature/oracle-db
- Subir imagem Docker do Oracle 26ai, utilizando o docker-compose.yaml, que se encontra na pasta /src/test/resources
- Cadastrar schema 'MINDCARE_TEST' no banco de dados Oracle SQL 26ai
- Connection string: jdbc:oracle:thin:@localhost:1522/FREEPDB1, schema: MINDCARE_TEST, password: 12345678
- Executar os testes
