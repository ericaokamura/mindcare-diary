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
- Criar os objetos PL/SQL no banco de dados Oracle SQL, conectado ao schema MINDCARE. Eles se encontram na pasta /src/main/resources/plsql:
  - instalar_packages.sql: instala os packages abaixo na ordem de dependência (rodar a partir da pasta plsql)
    - PKG_MINDCARE_COMUM: códigos de erro, busca de paciente/profissional e utilitários de data, reutilizados pelos demais
    - PKG_MINDCARE_DIARIO: CARREGA_ULTIMOS_REGISTROS_DIARIOS (antes uma function avulsa), média de humor, cálculo do estado do paciente e REGISTRA_ALERTA_NIVEL_HUMOR_PACIENTE
    - PKG_MINDCARE_AGENDA: validação de horário, horários livres, agendamento, atendimento, cancelamento e taxa de comparecimento
    - PKG_MINDCARE_CLINICA: faturamento e receita líquida do mês (por id ou CNPJ), resumo do dashboard e agenda da clínica
  - registra_alerta_nivel_humor_paciente.sql (trigger)
  - exemplos_packages.sql: demonstração de todos os packages com dados próprios, finalizada com ROLLBACK
- Para testar os endpoints da aplicação no Postman, siga o seguinte passo:
    - Importe como collection o arquivo Mindcare Diary.postman_collection.json, que se encontra na pasta /src/main/resources
- Rodar a aplicação via IDE ou utilizando o comando:
```bash
mvn spring-boot:run
```
