# Como testar esta entrega

## Pastas corretas
Backend: C:\Users\sayur\Documents\Codex\2026-09-16\como\work\evolucao-backend
Mobile: C:\Users\sayur\Documents\Codex\2026-09-16\como\work\evolucao-mobile
Ambas estão na branch feature/evolucao-mindcare. As pastas originais do OneDrive não receberam estas alterações.
Os arquivos backend.patch e mobile.patch permitem transportar somente esta entrega para cópias compatíveis dos repositórios.

## Preparação
1. Abra o Docker Desktop e o emulador. O Android deve chegar à tela inicial.
2. Disponibilize o Oracle e o esquema da feature/oracle-db com os scripts da colega. PostgreSQL não atende a esta branch.
3. Configure DB_PASSWORD e OPEN_AI_API_KEY no terminal, sem gravar credenciais no código.
4. Inicie o backend da pasta evolucao-backend. A configuração de desenvolvimento herdada aponta para localhost:1521/FREEPDB1, usuário MINDCARE. Confirme os valores do seu ambiente.
5. Instale mindcare-evolucao-debug.apk no emulador e use uma conta de teste ativa. O app acessa 10.0.2.2:8080.
6. Abra o relatório EVOLUCAO-PARTE-1.md para seguir os cenários da demonstração.

## Testes de integração do backend
O perfil test herdado usa MINDCARE_TEST em localhost:1522/FREEPDB1 e ddl-auto=create-drop.
Esse perfil deve apontar EXCLUSIVAMENTE para um esquema descartável de testes; ele cria/remove tabelas.
Não mude o perfil test para usar banco de usuários reais.
Os testes unitários de histórico usam H2 local em modo Oracle e não dependem desse banco.

## Situação verificada
- Mobile: 23 testes unitários aprovados; APK e APK instrumentado compilados.
- Backend: 145 testes aprovados de 145, zero falhas/erros, após disponibilizar Oracle e o esquema MINDCARE_TEST.
- Emulador emulator-5556 disponível: 6 testes instrumentados aprovados em 14,34 segundos. APK atualizado instalado. Capturas do Chat e histórico verificadas com dados sintéticos.
- O Oracle de testes tornou-se acessível após configuração do container/esquema; a suíte completa foi executada com sucesso em 04/10/2026.
- Keystore, isolamento de rascunhos, Chat, termos e histórico testados no emulador. A consulta paginada foi validada em H2 e no Oracle real. Ainda falta a demonstração integrada com login e backend de desenvolvimento: os testes de interface utilizam repositórios simulados.
- As limitações acima devem constar no relatório até a validação integrada; não significam certificação de produção.

## Sem instalação sobre versão antiga do servidor
O aplicativo novo exige o endpoint /meu-diario/historico e documentos versão 2026-10-04.1.
Atualize backend e APK juntos. Se o backend antigo for usado, login/termos e histórico podem falhar.
