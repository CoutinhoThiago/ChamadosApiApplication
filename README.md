# API REST de Gerenciamento de Chamados

Trabalho 01 — **Linguagens para Aplicação Comercial**  
**Professor:** Joselito Júnior  
**Tecnologias:** Java 17, Spring Boot, Spring MVC, Maven e JSON  
**Porta padrão:** `8080`

## 1. Sobre o projeto

Esta aplicação disponibiliza uma API REST para gerenciar chamados de suporte técnico. Por meio de requisições HTTP, é possível consultar chamados, pesquisar por diferentes critérios, identificar atendimentos críticos, visualizar um resumo por status e cadastrar novas solicitações.

A implementação utiliza uma lista (`ArrayList`) para manter os chamados em memória. Ao iniciar, a API carrega dez registros fictícios, distribuídos entre diferentes setores, prioridades e status. Não há persistência em banco de dados, autenticação ou camadas de Service e Repository, conforme o escopo do trabalho.

## 2. Tecnologias e ferramentas

- **Java 17:** linguagem utilizada no desenvolvimento.
- **Spring Boot 4.1.2:** inicialização e configuração da aplicação.
- **Spring Web MVC:** criação dos endpoints e tratamento das requisições HTTP.
- **Maven e Maven Wrapper:** gerenciamento de dependências e execução do projeto.
- **JSON:** formato utilizado para receber e devolver os dados da API.
- **Springdoc OpenAPI / Swagger UI:** documentação interativa dos endpoints.

## 3. Modelo de dados

Cada chamado é representado por um objeto `Chamado`, com os seguintes atributos:

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | `Long` | Identificador único do chamado. |
| `titulo` | `String` | Descrição resumida do problema. |
| `solicitante` | `String` | Pessoa que abriu o chamado. |
| `setor` | `String` | Departamento de origem da solicitação. |
| `prioridade` | `Prioridade` | Nível de prioridade do atendimento. |
| `status` | `StatusChamado` | Situação atual do chamado. |

### Valores aceitos

**Prioridade:** `BAIXA`, `MEDIA` ou `ALTA`.  
**Status:** `ABERTO`, `EM_ANDAMENTO` ou `RESOLVIDO`.

Os valores de prioridade e status são representados por enums Java. Nas requisições, podem ser informados em letras maiúsculas ou minúsculas; a aplicação normaliza esses valores antes da validação.

### Dados iniciais

A aplicação inicia com os seguintes dez chamados fictícios:

| ID | Título | Setor | Prioridade | Status |
|---:|---|---|---|---|
| 1 | Computador não liga | Financeiro | ALTA | ABERTO |
| 2 | Impressora sem conexão | RH | MEDIA | EM_ANDAMENTO |
| 3 | Acesso ao sistema bloqueado | TI | ALTA | EM_ANDAMENTO |
| 4 | Internet lenta no setor | Comercial | MEDIA | ABERTO |
| 5 | Instalação do pacote Office | Administrativo | BAIXA | RESOLVIDO |
| 6 | Monitor com tela piscando | Financeiro | MEDIA | RESOLVIDO |
| 7 | Falha no acesso ao e-mail | RH | ALTA | RESOLVIDO |
| 8 | Teclado não funciona | TI | BAIXA | ABERTO |
| 9 | Sistema financeiro indisponível | Financeiro | ALTA | ABERTO |
| 10 | Configuração de impressora | Comercial | BAIXA | EM_ANDAMENTO |

## 4. Como executar a aplicação

### Pré-requisitos

- JDK 17 ou superior instalado e configurado no `PATH`.
- Acesso à internet na primeira execução para que o Maven Wrapper baixe o Maven e as dependências do projeto.

Confira a instalação do Java com:

```bash
java -version
```

### Inicialização

Abra o terminal na pasta raiz do projeto, onde está localizado o arquivo `pom.xml`.

**Windows (PowerShell ou Prompt de Comando):**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux ou macOS:**

```bash
./mvnw spring-boot:run
```

Caso o Maven já esteja instalado, também é possível iniciar com:

```bash
mvn spring-boot:run
```

Quando a aplicação iniciar, a API estará disponível em:

```text
http://localhost:8080
```

Para encerrar a execução, pressione `Ctrl + C` no terminal.

### Compilar e executar os testes automatizados

Com o Maven Wrapper, execute:

**Windows:**

```powershell
.\mvnw.cmd clean test
```

**Linux ou macOS:**

```bash
./mvnw clean test
```

## 5. Endpoints da API

Todos os endpoints utilizam o caminho base `/chamados`.

| Método | Endpoint | Função |
|---|---|---|
| `GET` | `/chamados` | Lista todos os chamados. Também aceita filtros opcionais. |
| `GET` | `/chamados/{id}` | Consulta um chamado pelo ID. |
| `GET` | `/chamados/setor?nome=financeiro` | Filtra os chamados por setor. |
| `GET` | `/chamados/prioridade?nivel=alta` | Filtra os chamados por prioridade. |
| `GET` | `/chamados/busca?texto=impressora` | Pesquisa pelo texto contido no título. |
| `GET` | `/chamados/criticos` | Lista chamados de prioridade alta que ainda não foram resolvidos. |
| `GET` | `/chamados/resumo` | Retorna as quantidades por status e o total geral. |
| `POST` | `/chamados` | Cadastra um novo chamado. |

### 5.1 Listar todos e aplicar filtros opcionais

**GET `/chamados`**

Sem parâmetros, retorna todos os chamados. O endpoint também aceita os parâmetros opcionais:

- `setor`: corresponde ao setor informado.
- `prioridade`: aceita `BAIXA`, `MEDIA` ou `ALTA`.
- `texto`: procura um trecho no título do chamado.

Exemplos:

```http
GET http://localhost:8080/chamados
GET http://localhost:8080/chamados?setor=financeiro
GET http://localhost:8080/chamados?prioridade=alta
GET http://localhost:8080/chamados?texto=impressora
GET http://localhost:8080/chamados?setor=financeiro&prioridade=alta&texto=sistema
```

Os filtros podem ser combinados. Quando mais de um parâmetro é enviado, somente os chamados que atendem simultaneamente a todos os critérios são retornados. No último exemplo, a resposta contém o chamado de ID `9`.

### 5.2 Consultar chamado por ID

**GET `/chamados/{id}`**

Exemplo:

```http
GET http://localhost:8080/chamados/1
```

Resposta esperada — `200 OK`:

```json
{
  "id": 1,
  "titulo": "Computador não liga",
  "solicitante": "Ana Souza",
  "setor": "Financeiro",
  "prioridade": "ALTA",
  "status": "ABERTO"
}
```

Quando o ID não existe, a API retorna `404 Not Found` com uma mensagem de erro em JSON.

### 5.3 Filtrar por setor

**GET `/chamados/setor?nome={setor}`**

Exemplo:

```http
GET http://localhost:8080/chamados/setor?nome=financeiro
```

Retorna os chamados do setor informado. A comparação do nome do setor não diferencia letras maiúsculas de minúsculas. Com os dados iniciais, a consulta por `financeiro` retorna os IDs `1`, `6` e `9`.

### 5.4 Filtrar por prioridade

**GET `/chamados/prioridade?nivel={prioridade}`**

Exemplo:

```http
GET http://localhost:8080/chamados/prioridade?nivel=alta
```

Retorna os chamados que possuem a prioridade informada. Com os dados iniciais, a prioridade `alta` retorna os IDs `1`, `3`, `7` e `9`. Uma prioridade diferente de `BAIXA`, `MEDIA` ou `ALTA` resulta em `400 Bad Request`.

### 5.5 Buscar pelo título

**GET `/chamados/busca?texto={termo}`**

Exemplo:

```http
GET http://localhost:8080/chamados/busca?texto=impressora
```

A pesquisa procura o texto informado em qualquer trecho do título e não diferencia maiúsculas de minúsculas. Com os dados iniciais, o exemplo retorna os chamados de IDs `2` e `10`.

### 5.6 Listar chamados críticos

**GET `/chamados/criticos`**

Retorna somente os chamados que atendem às duas condições ao mesmo tempo:

1. Prioridade igual a `ALTA`.
2. Status diferente de `RESOLVIDO`.

Com os dados iniciais, são retornados os chamados de IDs `1`, `3` e `9`. O chamado de ID `7` não aparece, pois já está resolvido.

### 5.7 Consultar resumo

**GET `/chamados/resumo`**

Retorna a quantidade de chamados em cada status e o total geral. Com os dez registros iniciais, a resposta é:

```json
{
  "abertos": 4,
  "em_andamento": 3,
  "resolvidos": 3,
  "total": 10
}
```

### 5.8 Cadastrar um novo chamado

**POST `/chamados`**

O cadastro recebe um objeto JSON no corpo da requisição. Os campos `titulo`, `solicitante`, `setor` e `prioridade` são obrigatórios. O campo `status` é opcional e, quando não informado, recebe automaticamente o valor `ABERTO`.

Exemplo de requisição:

```http
POST http://localhost:8080/chamados
Content-Type: application/json
```

```json
{
  "titulo": "Notebook não inicia",
  "solicitante": "Marcos Silva",
  "setor": "Suporte",
  "prioridade": "ALTA"
}
```

Resposta esperada na primeira execução desse cadastro — `201 Created`:

```json
{
  "id": 11,
  "titulo": "Notebook não inicia",
  "solicitante": "Marcos Silva",
  "setor": "Suporte",
  "prioridade": "ALTA",
  "status": "ABERTO"
}
```

A aplicação gera o ID automaticamente e retorna o novo chamado. Se os dados obrigatórios estiverem ausentes ou se a prioridade/status informado for inválido, a API retorna `400 Bad Request` com uma mensagem em JSON.

## 6. Roteiro para reproduzir os testes

Os testes abaixo podem ser realizados pelo Swagger UI, Postman, Insomnia ou `curl`. Para que os IDs e as quantidades coincidam com os resultados indicados, reinicie a aplicação antes de executar a sequência e faça o cadastro do teste 9 apenas uma vez.

| Nº | Requisição | Resultado esperado | O que o teste demonstra |
|---:|---|---|---|
| 1 | `GET /chamados` | `200 OK`, lista com 10 chamados. | Consulta geral dos dados iniciais. |
| 2 | `GET /chamados/1` | `200 OK`, chamado de ID 1. | Consulta individual por identificador. |
| 3 | `GET /chamados/setor?nome=financeiro` | `200 OK`, IDs 1, 6 e 9. | Filtro por setor sem distinção de caixa. |
| 4 | `GET /chamados/prioridade?nivel=alta` | `200 OK`, IDs 1, 3, 7 e 9. | Filtro por prioridade. |
| 5 | `GET /chamados/busca?texto=impressora` | `200 OK`, IDs 2 e 10. | Busca parcial no título. |
| 6 | `GET /chamados/busca?texto=celular` | `200 OK`, lista vazia (`[]`). | Pesquisa sem correspondência. |
| 7 | `GET /chamados/criticos` | `200 OK`, IDs 1, 3 e 9. | Combinação de prioridade alta e status não resolvido. |
| 8 | `GET /chamados/resumo` | `200 OK`, 4 abertos, 3 em andamento, 3 resolvidos e 10 no total. | Contagem dos chamados por status. |
| 9 | `POST /chamados` | `201 Created`, novo chamado com ID 11. | Cadastro e geração automática de ID. |
| 10 | `GET /chamados/11` | `200 OK`, retorna o chamado criado no teste anterior. | Confirmação de que o cadastro foi armazenado na lista. |
| 11 | `GET /chamados/999` | `404 Not Found`, mensagem de chamado não encontrado. | Consulta de um identificador inexistente. |

### Comandos `curl` correspondentes

Execute os comandos abaixo em sequência, com a aplicação em execução:

```bash
curl -i "http://localhost:8080/chamados"
curl -i "http://localhost:8080/chamados/1"
curl -i "http://localhost:8080/chamados/setor?nome=financeiro"
curl -i "http://localhost:8080/chamados/prioridade?nivel=alta"
curl -i "http://localhost:8080/chamados/busca?texto=impressora"
curl -i "http://localhost:8080/chamados/busca?texto=celular"
curl -i "http://localhost:8080/chamados/criticos"
curl -i "http://localhost:8080/chamados/resumo"
```

Cadastro e verificação do registro:

```bash
curl -i -X POST "http://localhost:8080/chamados" \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Notebook não inicia","solicitante":"Marcos Silva","setor":"Suporte","prioridade":"ALTA"}'

curl -i "http://localhost:8080/chamados/11"
curl -i "http://localhost:8080/chamados/999"
```

**Evidências para a entrega:** salve capturas de tela das requisições e respectivas respostas HTTP/JSON. Em cada evidência, procure deixar visíveis o método, a URL, o status HTTP e o corpo da resposta. As capturas podem ser organizadas em uma pasta `docs/evidencias` no repositório e referenciadas nesta seção ou entregues pelo formulário, conforme a orientação do professor. Os resultados descritos nesta tabela são os resultados esperados a partir dos dez registros iniciais; substitua ou complemente esta seção com as capturas das requisições efetivamente executadas.

## 7. Documentação interativa — Swagger

Com a aplicação em execução, acesse:

```text
http://localhost:8080/swagger-ui.html
```

A interface Swagger permite visualizar os endpoints, consultar os parâmetros disponíveis, conferir exemplos documentados e executar requisições diretamente pelo navegador.

A especificação OpenAPI em JSON também pode ser consultada em:

```text
http://localhost:8080/v3/api-docs
```

## 8. Análise técnica

### Como o Spring Boot recebe e processa requisições HTTP

Quando a aplicação é iniciada, o Spring Boot configura um servidor web embutido e deixa a API disponível na porta `8080`. Quando um cliente, como o navegador, Postman ou `curl`, envia uma requisição HTTP, o Spring MVC recebe a solicitação e identifica qual método e caminho foram chamados. A partir dessas informações, o framework encaminha a requisição para o método correspondente do Controller. Depois do processamento, a aplicação devolve uma resposta HTTP contendo um código de status e, quando aplicável, um corpo em JSON.

### Qual é a função do Controller

O `ChamadoController` é a classe responsável por expor os endpoints da API. A anotação `@RestController` informa ao Spring que a classe trata requisições REST e que seus retornos devem ser enviados no corpo da resposta. A anotação `@RequestMapping("/chamados")` define o caminho base. As anotações `@GetMapping` e `@PostMapping` associam os métodos Java às operações HTTP.

O Controller recebe parâmetros de rota e de consulta, aplica os filtros, valida os dados enviados no cadastro e devolve os resultados. Neste projeto, ele também manipula diretamente a lista em memória, pois a proposta não exige as camadas Service e Repository.

### Como ocorre a conversão entre objetos Java e JSON

A aplicação utiliza os conversores HTTP integrados ao Spring MVC, com suporte do Jackson, para realizar a comunicação entre Java e JSON. No cadastro, `@RequestBody` indica que o conteúdo JSON enviado pelo cliente deve ser convertido em um objeto `NovoChamadoRequest`. Depois da validação, os dados são utilizados para criar um objeto `Chamado`.

Nas respostas, quando um método retorna um objeto, uma lista ou um mapa Java, o conversor transforma esse conteúdo em JSON para ser enviado ao cliente. Assim, o cliente não precisa conhecer diretamente as classes Java utilizadas internamente pela aplicação.

### Por que os chamados não permanecem após reiniciar a aplicação

Os chamados são armazenados em uma lista `ArrayList<Chamado>`, que existe somente na memória RAM durante a execução do programa. Não há gravação dos dados em arquivos ou em um banco de dados. Quando a aplicação é encerrada, essa memória é liberada e os chamados cadastrados durante a execução são perdidos.

Ao iniciar novamente, o construtor do `ChamadoController` executa o método que carrega os dez chamados fictícios. Por isso, a aplicação volta ao estado inicial a cada reinicialização. O identificador dos novos registros também recomeça após os dez IDs iniciais.

## 9. Organização do código

```text
ChamadosApiApplication/
├── src/
│   ├── main/
│   │   ├── java/com/ChamadosApiApplication/
│   │   │   ├── ChamadosApiApplication.java
│   │   │   ├── config/
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/
│   │   │   │   └── ChamadoController.java
│   │   │   ├── dto/
│   │   │   │   └── NovoChamadoRequest.java
│   │   │   └── model/
│   │   │       ├── Chamado.java
│   │   │       ├── Prioridade.java
│   │   │       └── StatusChamado.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/ChamadosApiApplication/
│           └── ChamadosApiApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 10. Considerações finais

A API implementa operações de consulta, filtros, pesquisa, identificação de chamados críticos, resumo por status e cadastro de novos chamados. O projeto utiliza os recursos do Spring Boot e do Spring MVC para disponibilizar os endpoints REST e trabalhar com dados JSON, mantendo os registros em memória conforme solicitado no enunciado.

**Observação:** a implementação atual não utiliza banco de dados. Embora exista uma dependência H2 declarada no `pom.xml`, o código do gerenciamento de chamados não realiza operações com esse banco; os registros são mantidos exclusivamente na lista em memória.
