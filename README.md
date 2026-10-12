### Instruções para executar a aplicação usando banco de dados Oracle 26ai: 
- Utilizar branch feature/oracle-db
- Instalar banco de dados Oracle SQL 26ai ou subir uma imagem Docker do Oracle 26ai, utilizando o docker-compose.yaml, que se encontra na raiz do projeto
- Cadastrar schema 'MINDCARE' no banco de dados Oracle SQL
- Connection string: jdbc:oracle:thin:@oracle-26ai:1521/FREEPDB1, schema: MINDCARE, password: 12345678
- Rodar mvn clean install para baixar as dependências Maven
- Configurar variáveis de ambiente (no Jenkins, essas variáveis precisam ser cadastradas como credentials):
  - DB_PASSWORD = 12345678
  - OPEN_AI_API_KEY
  - MINDCARE_DATA_KEY
- Criar a procedure e a trigger no banco de dados Oracle SQL, que foram desenvolvidas em linguagem PL/SQL. Elas se encontram na pasta /src/main/resources/plsql:
  - carrega_ultimos_registros_diarios.sql (procedure)
  - registra_alerta_nivel_humor_paciente.sql (trigger)
- Para testar os endpoints da aplicação no Postman, siga o seguinte passo:
    - Importe como collection o arquivo Mindcare Diary.postman_collection.json, que se encontra na pasta /src/main/resources
- Rodar a aplicação via IDE ou utilizando o comando:
```bash
mvn spring-boot:run
```
