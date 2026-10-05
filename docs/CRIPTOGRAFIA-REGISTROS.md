# Criptografia dos registros — implantação e limites

## O que foi implementado

O backend cifra antes de gravar e decifra ao ler, tanto no JDBC quanto no JPA.
O aplicativo, a exportação e o relatório recebem o texto autorizado em formato legível.
Não é criptografia de ponta a ponta nem TDE do Oracle.

| Tabela | Campos protegidos em novas colunas CLOB com sufixo `_enc` |
|---|---|
| registro_diario | pontos_positivos, dificuldades_desafios, texto_confirmado |
| relatorio_semanal | observacoes, recomendacoes, relatorio_ia, resumo |

AES-256-GCM pela biblioteca JCE do Java: nonce aleatório de 12 bytes por gravação,
tag de 128 bits, envelope `mc1:` + Base64 e contexto autenticado específico de cada
campo. A chave de 32 bytes vem de `MINDCARE_DATA_KEY`. Não há chave padrão em produção,
geração automática ao iniciar ou retorno de texto aberto quando a descriptografia falha.
O contexto distingue campos, mas não vincula o envelope a um ID individual: não protege
contra troca de envelopes válidos entre linhas do mesmo campo por alguém com escrita no banco.

Datas, identificadores, origem, humor, contadores, dados cadastrais, consultas e
prescrições/PDFs **não** foram cifrados por esta entrega. Humor e metadados também
exigem controle de acesso e proteção da infraestrutura. HTTPS, discos, backups,
logs históricos e segredos em produção precisam de configuração independente.
O backend e o provedor de IA ainda processam o texto legível quando o recurso é usado.

## Compatibilidade

- Histórico: filtros de data/humor/origem continuam no SQL. Busca textual percorre
  somente candidatos do paciente autenticado, decifra em memória e aplica o texto
  literal antes da paginação. Não há índice ou coluna auxiliar com relatos abertos.
  Custo da busca textual cresce com os candidatos; filtrar o período ajuda. O stream
  limita memória e fecha o cursor, mas não elimina o custo de percorrer os registros.
- Relatório semanal: usa os relatos dos últimos sete dias em memória. Não grava
  narrativas/embeddings no cache vetorial nem imprime os relatos nos logs. Contextos
  acima de 120 mil caracteres são recusados explicitamente, sem resumo parcial silencioso.
- A function Oracle que retorna `SELECT *` continua utilizável: o mapper JDBC lê
  as colunas `_enc`. Rotinas de humor não mudaram. Consultas diretas aos campos antigos
  passam a retornar NULL; quem precisa do texto deve passar pelo backend autorizado.
- Exportação ZIP continua legível para o titular; não é um arquivo criptografado.
- Backend anterior e cargas SQL antigas não devem gravar no mesmo banco após a migração.

## Chave e recuperação

Há três segredos diferentes: senha do banco, chave da OpenAI e chave de dados.
Não substitua a chave de dados após gravar registros. Perder a chave torna os textos
irrecuperáveis. Guarde uma cópia recuperável em gerenciador de segredos, separada do
backup do banco. Não envie a chave por chat nem a coloque no GitHub.

O auxiliar PowerShell usa DPAPI do usuário Windows para guardar a chave fora do
repositório, em `%LOCALAPPDATA%/MindCare/secrets/record-key.dpapi`. Essa cópia depende
do usuário/máquina Windows: copiar apenas esse arquivo para outro computador não é
um plano de recuperação. Em outro ambiente, injete **a mesma chave** pelo gerenciador
de segredos. O formato atual não faz rotação automática nem usa AWS KMS; uma rotação
exige ferramenta controlada que leia com a chave antiga e regrave com a nova.

Na pasta do backend, criar uma única vez para um banco ainda não criptografado:

```powershell
& .\scripts\Load-DataKey.ps1 -Create
```

Nas próximas sessões, carregar a chave existente no terminal que iniciará o backend:

```powershell
& .\scripts\Load-DataKey.ps1
```

O script não imprime a chave e recusa sobrescrever uma existente. Faça o backup
recuperável do segredo antes de migrar dados que não possam ser perdidos.

## Migração dos registros existentes

1. Pare todos os backends/escritores. Faça backup do banco e confirme a recuperação
   da chave. Execute primeiro sobre uma cópia do banco. Backups antigos continuam
   contendo textos abertos e devem receber proteção/retencão apropriadas.
2. Gere o JAR com `mvn package` (ou Maven instalado no seu computador). A suíte usa
   `MINDCARE_TEST` descartável com `create-drop`: nunca aponte os testes ao banco real.
3. Carregue a chave de dados. Configure o endereço/usuário **do banco que deseja migrar**
   e `DB_PASSWORD` no terminal. Exemplos de estrutura, substituindo o ambiente:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:oracle:thin:@localhost:1521/FREEPDB1'
$env:SPRING_DATASOURCE_USERNAME = 'MINDCARE'
$dbSecret = Read-Host 'Senha do banco' -AsSecureString
$env:DB_PASSWORD = ([PSCredential]::new('db', $dbSecret)).GetNetworkCredential().Password
java -jar .\target\mindcare-diary-0.0.1-SNAPSHOT.jar --migrate-record-encryption
```

Esse comando não inicia HTTP nem chama IA. Ele adiciona colunas CLOB, bloqueia as
tabelas no Oracle, cifra/verifica cada texto, limpa os campos antigos e confirma
uma transação. Também reconhece a antiga coluna `relatorioia`. Se encontrar textos
divergentes entre cópias, falha e reverte os dados. DDL Oracle faz commit implícito:
as colunas adicionadas podem permanecer após falha, sem perda dos textos antigos.
É possível executar novamente; envelopes existentes válidos não são cifrados duas vezes.

Se existir conteúdo em `SPRING_AI_VECTORS`, o comando recusa concluir até você
revisar esse cache derivado. Depois de confirmar o backup e que é o cache de relatos
do MindCare, autorize sua limpeza e repita:

```powershell
$env:MINDCARE_PURGE_LEGACY_VECTORS = 'true'
java -jar .\target\mindcare-diary-0.0.1-SNAPSHOT.jar --migrate-record-encryption
Remove-Item Env:MINDCARE_PURGE_LEGACY_VECTORS
```

Somente esse cache é apagado; registros e relatórios são preservados cifrados.
Isso não apaga cópias históricas em backups/redo/undo/discos. Mantenha a aplicação
antiga parada: não há compatibilidade para reintroduzir gravações em texto aberto.

4. Inicie a nova versão, no mesmo terminal e com as demais configurações já usadas:

```powershell
java -jar .\target\mindcare-diary-0.0.1-SNAPSHOT.jar
```

Na inicialização, o backend verifica ausência de texto legado/cache e autentica os
envelopes existentes com a chave. Falha antes de aceitar requisições se a verificação
não passar. Essa varredura pode aumentar o tempo de início em bancos grandes.

## Verificação e entrega

- Testes de AES: Unicode/texto longo, nonce diferente, nulo/vazio, chave ausente/errada,
  adulteração e troca de campo.
- Migração: limpeza dos campos antigos, execução repetida, rollback, cache vetorial
  e nome legado do relatório.
- Oracle real de testes: persistência JDBC e JPA com CLOB longo, leitura legível,
  conteúdo bruto cifrado, migração/reexecução e pesquisa.
- Exportação, idempotência do Chat e geração de relatórios continuam testadas.
- Suíte atual: 153 testes aprovados. Também foi realizada migração do ambiente local, com preservação da conta e dos três registros e conferência de login/histórico. A implementação não ativa
  retroativamente criptografia em um banco de desenvolvimento/produção não migrado.

Referências de implementação: [OWASP Cryptographic Storage](https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html)
e [Java 21 Cipher](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/Cipher.html).
