# Privacidade e gestão da conta — 21/09/2026

## Comportamento entregue
- Em cada toque em Entrar no Android, o diálogo exige novo aceite dos Termos e ciência da Política. Cancelar não autentica. Os documentos completos podem ser abertos antes de aceitar.
- Login Android envia versaoTermos=2026-09-21.1. Após autenticação correta, o servidor registra usuário, versão, data e SHA-256 dos documentos. Versão diferente retorna 409.
- Clientes web antigos ainda podem autenticar sem versaoTermos; o popup obrigatório foi implementado no Android. O backend não deve ser apresentado como imposição universal de consentimento.
- Meu perfil, nas telas iniciais de paciente e profissional, abre os documentos e ações de privacidade.
- POST /minha-conta/exportacao exige JWT e senha atual; exporta somente o titular autenticado em ZIP com JSON e PDFs de prescrições disponíveis. Paciente: cadastro, aceites, diário tradicional e Chat confirmado, relatórios, consultas e prescrições. Profissional: cadastro e aceites próprios, sem dados de pacientes. Senhas, hashes de senha e tokens não são exportados.
- POST /minha-conta/encerramento exige JWT e senha atual. Registra data e protocolo de solicitação de eliminação, desativa/bloqueia a conta e limpa o token de notificações. Impede novo login, uso de JWT anterior e geração de novos resumos para essa conta.
- Encerramento NÃO apaga fisicamente registros, PDFs, backups ou dados em provedores. Não cancela consultas automaticamente. A interface explica isso antes da confirmação.
- O pedido fica persistido em usuario.encerrada_em e usuario.protocolo_eliminacao; não é enviado email automaticamente.

## Operação necessária
A equipe MindCare deve acompanhar os protocolos registrados, confirmar o recebimento ao titular por canal apropriado e analisar a eliminação por categoria de dado e eventual obrigação de conservação. O canal é mindcare.diary@gmail.com. O status atual não acompanha a conclusão da análise; a eliminação física/anônima e sua comprovação ainda precisam de procedimento administrativo específico. Nunca tratar encerramento como comprovante de eliminação.
Não atribuir prazo de guarda universal nem apagar documentos clínicos indiscriminadamente. Definir o responsável legal/controlador antes de publicação para usuários reais. AWS continua sendo planejamento, não infraestrutura entregue. Aceite dos termos não substitui a definição de bases legais ou consentimentos específicos para dados sensíveis.

## Instalação e teste
1. Manter PostgreSQL ligado e reiniciar o backend com as variáveis de ambiente habituais.
2. No ambiente local com ddl-auto=update, Hibernate adiciona campos/tabela. Em produção aplicar migração revisada antes de iniciar; não depender de atualização automática.
3. Instalar APK atualizado. Entrar, cancelar o popup e confirmar que não entra. Reabrir e marcar as duas opções; repetir após sair.
4. Em Meu perfil, abrir os dois documentos. Informar senha atual, exportar ZIP e conferir somente os próprios dados, incluindo registros salvos nos dois modos.
5. Usar UMA CONTA DESCARTÁVEL para encerrar. Confirmar protocolo e verificar que não consegue mais autenticar. Os dados continuam retidos para análise.
6. Senha incorreta não deve exportar nem encerrar a conta.

## Validação e limites
Oito novos testes backend cobrem isolamento da exportação, PDFs com nomes seguros, senha inválida, protocolo/retensão, versão/hash do aceite, exportação profissional, rejeição de JWT anterior e novo login após encerramento.
Suíte backend: 139 testes, 133 passaram, 5 erros preexistentes em AgendamentoServiceTest/ClinicaServiceTest, 1 ignorado.
O APK é de desenvolvimento; não representa certificação jurídica, auditoria de segurança ou publicação em produção.
A exportação atual é síncrona e montada em memória; deve ser adaptada para volumes grandes antes de ampliar uso.
