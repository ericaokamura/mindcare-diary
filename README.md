### Instruções: 
- Utilizar branch master
- Instalar banco de dados PostgreSQL ou subir uma imagem Docker do PostgreSQL utilizando o docker-compose.yaml 
- Criar banco de dados mindcare_db (connection string: jdbc:postgresql://localhost:5432/mindcare_db, username: postgres, password: 12345678)
- Restaurar dados usando o seguinte comando Bash (file:src/main/resources/mindcare_db.dump):
  - pg_restore \
    -U postgres \
    -h localhost \
    -p 5432 \
    -d mindcare_db \
    mindcare_db.dump
- Rodar mvn clean install para baixar as dependências Maven
- Configurar variáveis de ambiente: 
  - DB_PASSWORD = 1234568
  - OPEN_AI_API_KEY
- Rodar a aplicação
