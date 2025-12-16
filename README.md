# jp_bank
Sistema que tem o objetivo de simulas o funcionamento de um banco. Para rodar a aplicação é necessário ter o java, maven e algum sistema de banco de dados relacional instalado, no caso do programa, o que está sendo utilizado é o mySQL. Além disso, também sera necessário atualizar no application.properties (em src/main/resources/resources) as informações referêntes ao endereço, usuário e senha do banco de dados.
No momento, ele é composto por duas pastas principais:
- jp_bank_back: Pasta com o aplicativo Spring responsável pelo backend.
- jp_bank_front: Pasta com os arquivos html, css e javascripts. No momento ainda é bem provisória.

Abaixo, imagens exemplificando o front e uma breve explicação dos módulos que estão no backend.

## Funções
No momento, o sistema tem as seguintes funções:
- Resgistro;
- Login com autenticação JWT;
- Transferência Interna;
- Simulação de cãmbio;
- Extrato improvisado.

## Imagens
Algumas imagens do front improvisado:

#### Login
<img width="1901" height="985" alt="image" src="https://github.com/user-attachments/assets/f995a1c8-4be5-462c-ad57-7007320e0927" />

#### Registro (página provisória)
<img width="1902" height="973" alt="image" src="https://github.com/user-attachments/assets/2ec22bdf-40ec-4e30-b867-3aed895a386a" />

#### Página do Usuário
<img width="1903" height="985" alt="image" src="https://github.com/user-attachments/assets/b41b8aeb-5e01-4356-8ebb-24a393fcdfdf" />

#### Transferência Interna
<img width="1903" height="988" alt="image" src="https://github.com/user-attachments/assets/33e4da6d-a965-45d7-8f1f-dcca634ec927" />

#### Câmbio (o usuário escolhe uma das opções, o front pega o valor da moeda através de uma API pública e apresenta ao usuário, que então pode clicar em comprar)
<img width="1898" height="984" alt="image" src="https://github.com/user-attachments/assets/dfc93d2e-b616-4abc-b7d7-bbab3c77461c" />

<img width="1898" height="979" alt="image" src="https://github.com/user-attachments/assets/d3f7ef37-0e39-4139-803c-ed6b20e3026c" />

#### Página dos cartões/fatura de crédito
<img width="1903" height="983" alt="image" src="https://github.com/user-attachments/assets/ca428ad5-b206-4c60-9581-b414c8e7921d" />

## Módulos Backend

### auth
Pasta contendo os arquivos referentes a autenticação do usuário, criação do token jwt.
Endpoints:  
- /auth/login : Responsável pelo login;
- /auth/register : Responsável pelo registro público de novos usuários;


### config
Pasta contendo os arquivos referentes as configurações de segurança do aplicativo. por exemplo, marca quais autenticações cada endpoint pede e cria o FilterChain.


### domain/user
Pasta contendo os arquivos referentes ao usuário. Possui a entidade BankUser, que guarda as informações do usuário.
Endpoints:
- /user/me : retorna informações selecionadas do usuário;
- /user/verify-password: recebe uma senha e retorna se bate com a do usuário. Usada para confirmar operações.


### domain/transactions
Pasta contendo os arquivos referentes as transações. Possui as entidades que guardam as informações sobre as transações e operações realizadas.
Endpoints:
- /api/transactions : cria a transação;
- /api/transactions/{id} : retorna a transação com o id;
- /api/transactions/user/{userId} : retorna as transações referentes ao usuário com o userId;


### domain/internalTransfer
Pasta contendo os arquivos referntes a transferencia interna.
Endpoints:
- /internal-transfer: faz as validações, realiza a transferência e cria a transação;


### domain/exchange
Pasta contendo os arquivos referentes ao câmbio.
Endpoints:
- /exchange/buy : faz as validações, realiza o câmbio e cria a transação;

### domain/creditcard
Pasta contendo os arquivos referetens a criação e utilização de cartões de crédito e gerenciamento da fatura.
Endpoints:
- /creditcard/createcard : cria o cartão de crédito par o usuário; (Ainda não está presente no frontend)
- /creditcard/buy : faz as validações e realiza a compra no cartão de crédito;
- /creditcard/usercards : retorna informações do cartão do usuário autenticado;
- /creditcard/invoiceNotClosed : retorna a soma dos gastos do usuário durnte o período da fatura atual;
- /creditcard/purchases : retorna os gastos de crédito do usuário;
- /creditcard/invoice : retorna a fatura do usuário, caso já esteja fechada;

### scheduler
Pasta contendo o arquivo referente ao agendamento da criação da fatura dos usuários. Ele roda uma vez por dia.


