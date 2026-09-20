# MIA: backend e guardrails

O backend oferece mensagens com proteção em camadas e persistência de um registro
textual revisado pelo paciente. A conversa bruta não é persistida. Voz, batch e
dashboard não fazem parte desta etapa.

## Contrato

`POST /mia/mensagens`, com o JWT de um paciente ativo e não bloqueado:

```json
{"mensagem":"Hoje caminhei e me senti bem."}
```

Resposta de exemplo (a pergunta pode variar dentro do catálogo):

```json
{
  "assistente":"MIA",
  "papel":"Assistente de registro",
  "mensagem":"Gostaria de contar um pouco mais sobre isso?",
  "fallback":false
}
```

O corpo da mensagem aceita de 1 a 4000 caracteres. Não envia nome de usuário,
ID de paciente ou histórico. A autenticação usa o filtro JWT existente, inclusive
o principal `Optional<Usuario>` que ele fornece. Outros perfis, pacientes inativos
ou bloqueados recebem 403. Sem autenticação, o filtro existente rejeita o acesso.
Mensagens vazias, ausentes ou maiores que o limite recebem 400.
Respostas de sucesso usam `Cache-Control: no-store`.

## Quatro camadas

1. `MiaIntentClassifier`: normaliza texto e identifica intenções por regras locais;
   risco de autoagressão/violência tem prioridade. Mensagens não reconhecidas
   permanecem incertas até a classificação semântica.
2. `MiaSafeResponses`: intenções restritas retornam templates fixos sem chamar IA.
3. `MiaAiService`: reutiliza o `ChatModel` já configurado pelo Spring AI, sem
   advisors de relatório, pesquisa vetorial, ferramentas ou cliente HTTP adicional.
   Instruções de sistema e texto do usuário são mensagens separadas. O modelo
   apenas classifica a intenção e seleciona o identificador de uma pergunta neutra.
4. `MiaResponseValidator`: exige JSON estrito, exatamente dois campos e valores
   do catálogo. Rejeita campos extras, duplicados, saída extensa, texto adicional
   e identificadores desconhecidos. Nunca devolve prosa gerada: resolve os códigos
   para textos previamente definidos no servidor.

Situações sensíveis recebem mensagem fixa de segurança; não existe triagem clínica,
monitoramento humano ou acionamento automático de emergência. O indicador interno
de intenção não é devolvido como rótulo clínico ao paciente.

Falhas do provedor, saídas inválidas ou saturação retornam mensagem fixa com
`fallback: true`. Há no máximo quatro chamadas de IA concorrentes por instância.
São herdados os timeouts/retries do cliente existente. Esta entrega não muda
configurações sensíveis; um timeout operacional adequado e limites por usuário
devem ser definidos antes de ampliar o uso.

## Privacidade e limites

Não há gravação da conversa bruta, áudio ou novos embeddings pelo chat.
O texto confirmado é salvo no diário, conforme descrito abaixo.
Somente o texto da mensagem não resolvida localmente é enviado ao provedor já
configurado. O aplicativo deve informar esse processamento externo ao paciente.
Isso não implica ausência de retenção pelo provedor: depende do contrato e das
configurações da conta. Não são enviados automaticamente dados cadastrais.

Os novos serviços não registram conteúdo, prompts, respostas do provedor, JWTs ou
exceções que possam conter esses dados. O DTO de entrada omite o conteúdo no
`toString`. A instrumentação externa ao módulo também precisa evitar captura de
corpos. Problemas de logs e autorização em outros endpoints identificados na
análise inicial continuam fora desta entrega.

As regras são conservadoras: uma menção factual a medicamento pode receber a
resposta fixa de medicamento. Classificação semântica pode errar; o catálogo
limita o que sai, mas não garante detecção de toda situação sensível. Não há
memória entre chamadas de mensagens. A persistência ocorre somente quando o
paciente confirma o texto e o cliente chama o endpoint de registros.

## Testes e execução

Requisitos do backend existente: Java 21, Maven (ou wrapper), PostgreSQL/pgvector
e integrações já configuradas. Nenhuma dependência foi adicionada.

```text
mvn test
mvn -Dtest=MiaIntentClassifierTest,MiaResponseValidatorTest,MiaServiceTest,MiaAiServiceTest,MiaControllerTest test
```

Os testes novos simulam a IA e o token para não enviar dados a serviços externos.
O teste MVC executa o filtro de autenticação real com dependências simuladas.
O teste de contexto preexistente depende das configurações/infraestrutura do
projeto; falhas dele devem ser reportadas separadamente, sem trocar credenciais.

Para teste manual com backend configurado, envie a mensagem usando seu JWT de
paciente, sem copiá-lo para documentação ou logs. Confira relato normal, pedido de
conselho, diagnóstico, medicamento, opinião, assunto externo e situação sensível.
Confira também 400 para mensagem inválida, 403 para outro perfil e resposta fixa
quando o provedor não estiver disponível. Use apenas relatos fictícios nos testes.

## Explicação acadêmica

A MIA é uma assistente de registro. Ela recebe uma mensagem e faz perguntas
neutras para ajudar a pessoa a contar o seu dia. Não oferece diagnósticos,
aconselhamento ou tratamentos. Os guardrails combinam regras locais, respostas
fixas, instruções restritas e validação final por catálogo. O paciente revisa suas
próprias falas e confirma o texto que será salvo como registro do diário.

## Persistência do registro confirmado

`POST /mia/registros`, usando o mesmo JWT de paciente:

```json
{
  "idRequisicao": "0afc7e20-3210-4ec0-8b04-7a52047f04c0",
  "textoConfirmado": "Hoje caminhei e conversei com uma amiga.",
  "nivelHumor": "BOM"
}
```

O UUID deve ser gerado uma vez por confirmação e reutilizado em novas tentativas
com o mesmo corpo. O servidor retorna 200 com o registro tanto na primeira gravação
quanto numa repetição idêntica. Mesmo identificador com outro texto/humor retorna
409. O identificador é limitado ao paciente; outro paciente não acessa o registro
de terceiros. Texto vazio, acima de 20000 caracteres ou humor inválido retorna 400.
Humor omitido/vazio vira `SEM_DEFINICAO`, sem inferência clínica.

`MiaRegistroService` executa uma transação, bloqueia a linha do paciente durante
o salvamento e consulta a chave de idempotência antes de inserir. A entidade
`RegistroDiario` recebeu `textoConfirmado` (`TEXT`), `origem` (`TRADITIONAL`/`CHAT`)
e `idRequisicao` (`UUID`), com unicidade por paciente/requisição. Paciente, data e
origem vêm do servidor. Não são criadas tabelas duplicadas de diários ou mensagens.

A migração PostgreSQL aditiva está em `docs/sql/001_mia_registro.sql`. Ela não foi
executada em banco do usuário. A configuração existente `ddl-auto=update` também
atualiza o esquema quando a aplicação inicia; o script permite preparar e revisar
a alteração explicitamente antes disso. Não foi introduzido Flyway/Liquibase nem
modificado `application.properties`.

O endpoint existente `GET /registrosDiarios/{nomeUsuario}` inclui os novos campos
e exige o próprio paciente ou profissional vinculado. O cadastro tradicional
exige o próprio paciente, ignora propriedade/data/origem enviadas e continua
retornando corpo vazio. Os DTOs de diário não copiam senha nem token do paciente.

O texto confirmado fica disponível na mesma entidade e nos DTOs dos registros.
O processamento por IA de relatórios, que atualmente lê os campos tradicionais,
não foi alterado nesta etapa e precisa de adaptação posterior para esse novo campo.
As limitações preexistentes dos outros endpoints/relatórios continuam fora deste
escopo; a autorização implementada aqui protege os endpoints de diário e MIA.

## Testes de persistência

Foi adicionada apenas a dependência H2 em escopo de teste. Testes JPA verificam
gravação, leitura, compatibilidade com diários tradicionais, isolamento por paciente
e concorrência de requisições idênticas. H2 não substitui a verificação da migração
e das transações no PostgreSQL do ambiente real. Os testes não usam credenciais
nem serviços externos.

Referências: [Spring AI ChatModel](https://docs.spring.io/spring-ai/reference/1.0/api/chatmodel.html)
e [OpenAI Safety best practices](https://developers.openai.com/api/docs/guides/safety-best-practices).
