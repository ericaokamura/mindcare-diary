### Instruções para executar a aplicação: 
- Utilizar branch feature/oracle-db
- Instalar banco de dados Oracle SQL 26ai ou subir uma imagem Docker do Oracle 26ai, utilizando o docker-compose.yaml, que se encontra na raiz do projeto
- Cadastrar schema 'MINDCARE' no banco de dados Oracle SQL
- Connection string: jdbc:oracle:thin:@localhost:1521/FREEPDB1, schema: MINDCARE, password: 12345678
- Rodar mvn clean install para baixar as dependências Maven
- Configurar variáveis de ambiente: 
  - DB_PASSWORD = 12345678
  - OPEN_AI_API_KEY
- Rodar a aplicação