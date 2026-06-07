# Desafio Codificar - Sistema de Chamados

Sistema web para cadastrar, atribuir, acompanhar, atualizar e excluir chamados. O projeto possui:

- Backend REST em Java 26, Spring Boot, Spring Data JPA e PostgreSQL.
- Frontend em React, TypeScript e Vite.
- Docker Compose para executar o backend e o PostgreSQL.
- Swagger/OpenAPI para explorar e testar os endpoints.

## Tecnologias e ferramentas utilizadas

### Backend

- **Java 26:** linguagem principal da aplicação.
- **Spring Boot 4:** estrutura e configuração da API.
- **Spring Web MVC:** criação dos controllers e endpoints REST.
- **Spring Data JPA:** acesso aos dados por meio dos repositórios.
- **Hibernate:** mapeamento e persistência das entidades no banco.
- **Jakarta Validation e Hibernate Validator:** validação dos dados, incluindo nome obrigatório e CPF válido.
- **Lombok:** redução de código repetitivo nas entidades, serviços e controllers.
- **Gradle e Gradle Wrapper:** gerenciamento de dependências, testes e build do backend.

### Frontend

- **React 19:** construção da interface por componentes.
- **TypeScript:** tipagem dos dados e das operações realizadas pela interface.
- **Vite 8:** servidor de desenvolvimento e processo de build do frontend.
- **Lucide React:** biblioteca de ícones utilizada na interface.
- **CSS:** estilização responsiva da aplicação.
- **ESLint:** análise estática e padronização do código frontend.
- **npm:** gerenciamento das dependências e scripts do frontend.

### Banco de dados e infraestrutura

- **PostgreSQL 17:** armazenamento dos responsáveis e chamados.
- **Docker:** criação de uma imagem executável do backend.
- **Docker Compose:** execução integrada da API e do PostgreSQL.
- **Variáveis de ambiente (`.env`):** configuração local de portas, banco de dados e credenciais sem expor dados sensíveis no repositório.

### Testes, qualidade e documentação

- **JUnit 5:** execução dos testes automatizados do backend.
- **Mockito:** criação de mocks para testes unitários dos serviços.
- **Spring Boot Test:** suporte aos testes da aplicação Spring.
- **JaCoCo:** geração e verificação da cobertura de testes.
- **Swagger UI, OpenAPI e Springdoc:** documentação e teste interativo dos endpoints.
- **Git e GitHub:** versionamento e compartilhamento do código-fonte.

### Justificativa das escolhas

Escolhi **React com Vite** para o frontend porque recentemente participei de um bootcamp focado nessas tecnologias. Atualmente, essa é a combinação com a qual estou mais familiarizado para desenvolver interfaces web. Eu sinto uma facilidade muito grande para interagir com aplicações usando React, provavelmente por ter sido acompanhado por um profissional durante o bootcamp para esclarecer minhas dúvidas.

Escolhi **Java e o ecossistema Spring** para o backend porque Java é minha linguagem principal e a tecnologia com a qual tenho maior domínio. Com Java, Spring Boot e seus frameworks complementares, consigo implementar todas as partes necessárias do sistema, incluindo API REST, regras de negócio, validação, persistência, tratamento de erros, testes e documentação. Essa escolha também permitiu manter uma arquitetura organizada em controllers, services, repositories e entities, utilizei de padrões como SOLID e Design Patterns também.

## Estrutura do projeto

```text
DesafioCodificar/
├── Backend/   # API REST, regras de negócio, persistência e Docker Compose
└── Frontend/  # Interface web React
```

## Pré-requisitos

### Para executar sem Docker

- Java JDK 26.
- PostgreSQL instalado e em execução.
- Node.js compatível com Vite 8 e npm.

Não é necessário instalar o Gradle, pois o backend inclui o Gradle Wrapper.

### Para executar com Docker

- Docker Desktop ou Docker Engine com Docker Compose.
- Node.js e npm para executar o frontend.

O Docker Compose atual executa o backend e o PostgreSQL. O frontend é iniciado separadamente com Vite.

## Executar sem Docker

### 1. Preparar o PostgreSQL

Com o PostgreSQL em execução, crie o usuário e o banco usados pelo ambiente local:

```sql
CREATE USER desafio WITH PASSWORD 'desafio';
CREATE DATABASE desafio_codificar OWNER desafio;
```

Caso utilize outros dados de acesso, altere o arquivo `Backend/.env`.

### 2. Configurar o backend

O arquivo local `Backend/.env` possui a configuração padrão:

```properties
DB_URL=jdbc:postgresql://localhost:5432/desafio_codificar
DB_USERNAME=desafio
DB_PASSWORD=desafio
JPA_DDL_AUTO=update
```

Esse arquivo é carregado automaticamente pelo Spring quando o backend é iniciado dentro da pasta `Backend`. Ele está ignorado pelo Git para evitar o compartilhamento de credenciais.

Para recriá-lo a partir do modelo:

```powershell
Copy-Item Backend/.env.example Backend/.env
```

### 3. Iniciar o backend

No Windows:

```powershell
cd Backend
.\gradlew.bat bootRun
```

No Linux ou macOS:

```bash
cd Backend
./gradlew bootRun
```

Após a inicialização:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Na primeira execução, o Hibernate cria ou atualiza as tabelas e o sistema cadastra automaticamente os responsáveis João, Ayesha e Frida.

### 4. Iniciar o frontend

Abra outro terminal:

```powershell
cd Frontend
npm install
npm run dev
```

Acesse a URL exibida pelo Vite, normalmente http://localhost:5173.

Durante o desenvolvimento, o Vite encaminha requisições iniciadas por `/api` para `http://localhost:8080`.

## Executar com Docker

### 1. Configurar as variáveis

Na raiz do projeto:

```powershell
Copy-Item Backend/.env.example Backend/.env
```

Edite `Backend/.env` e defina uma senha em `POSTGRES_PASSWORD`. O Docker Compose utiliza as variáveis `POSTGRES_*`, `APP_PORT` e `JPA_DDL_AUTO`.

### 2. Iniciar backend e banco

```powershell
cd Backend
docker compose up --build -d
```

Verifique os containers:

```powershell
docker compose ps
docker compose logs -f api
```

O backend estará disponível em http://localhost:8080 e o PostgreSQL na porta `5432`, considerando os valores padrão do `.env`.

### 3. Iniciar o frontend

Em outro terminal:

```powershell
cd Frontend
npm install
npm run dev
```

Acesse http://localhost:5173.

### 4. Encerrar os containers

```powershell
cd Backend
docker compose down
```

Para também apagar os dados persistidos do PostgreSQL:

```powershell
docker compose down -v
```

## Variáveis de ambiente do backend

| Variável | Padrão | Finalidade |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/desafio_codificar` | Conexão JDBC usada na execução local |
| `DB_USERNAME` | `desafio` | Usuário usado pela aplicação |
| `DB_PASSWORD` | `desafio` | Senha usada pela aplicação |
| `JPA_DDL_AUTO` | `update` | Estratégia de atualização do schema |
| `APP_PORT` | `8080` | Porta externa da API no Docker |
| `POSTGRES_PORT` | `5432` | Porta externa do PostgreSQL no Docker |
| `POSTGRES_DB` | `desafio_codificar` | Banco criado pelo container |
| `POSTGRES_USER` | `desafio` | Usuário criado pelo container |
| `POSTGRES_PASSWORD` | definida no `.env` | Senha criada pelo container |

## Modelo de dados

### Chamado

| Campo | Tipo/valores | Observação |
| --- | --- | --- |
| `id` | UUID | Gerado automaticamente |
| `titulo` | texto, até 100 caracteres | Obrigatório no banco |
| `descricao` | texto, até 500 caracteres | Obrigatório no banco |
| `prioridade` | `ALTA`, `MEDIA`, `BAIXA` | Obrigatório |
| `status` | `Aberto`, `EmAndamento`, `Resolvido`, `Fechado` | Obrigatório |
| `responsavel` | objeto responsável | Responsável vinculado ao chamado |
| `createdDate` | data/hora | Gerado automaticamente |
| `lastModifiedDate` | data/hora | Atualizado automaticamente |

### Responsável

| Campo | Tipo | Observação |
| --- | --- | --- |
| `id` | UUID | Gerado automaticamente |
| `nome` | texto | Obrigatório |
| `cpf` | texto | CPF válido, obrigatório e único |
| `chamados` | lista | Chamados vinculados ao responsável |

## Endpoints

Os endpoints de listagem aceitam:

- `page`: página iniciando em `0`.
- `size`: quantidade de itens.
- `sortBy`: campo usado na ordenação.
- `direction`: `asc` ou `desc`.
- `page=-1&size=-1`: retorna todos os registros em uma única página.

### Chamados

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/chamados` | Lista chamados com paginação e ordenação |
| `GET` | `/chamados/{id}` | Busca um chamado pelo UUID |
| `POST` | `/chamados` | Cria um chamado |
| `PUT` | `/chamados/{id}` | Atualiza um chamado existente |
| `DELETE` | `/chamados/{id}` | Exclui um chamado fechado |

Exemplo de criação ou atualização:

```json
{
  "titulo": "Erro ao acessar o sistema",
  "descricao": "O usuário recebe erro ao tentar realizar login.",
  "prioridade": "ALTA",
  "status": "Aberto",
  "responsavel": {
    "id": "UUID_DO_RESPONSAVEL"
  }
}
```

Exemplo de listagem:

```http
GET /chamados?page=0&size=10&sortBy=createdDate&direction=desc
```

### Responsáveis

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/responsaveis` | Lista responsáveis com seus chamados |
| `GET` | `/responsaveis/{id}` | Busca um responsável pelo UUID |
| `GET` | `/responsaveis/atribuir` | Retorna o UUID do responsável com menos chamados |
| `POST` | `/responsaveis` | Cria um responsável |
| `PUT` | `/responsaveis/{id}` | Atualiza um responsável |
| `DELETE` | `/responsaveis/{id}` | Endpoint existente, mas atualmente não remove o responsável |

Exemplo de criação:

```json
{
  "nome": "Maria Silva",
  "cpf": "52998224725"
}
```

## Regras de negócio

1. O sistema cadastra João, Ayesha e Frida automaticamente ao iniciar. Se já existirem, os registros são reaproveitados.
2. A atribuição automática seleciona o responsável com a menor quantidade de chamados vinculados.
3. Um chamado somente pode ser excluído quando seu status é `Fechado`.
4. Ao tentar excluir um chamado inexistente, a API retorna erro de regra de negócio.
5. Ao buscar ou atualizar recursos inexistentes, a API retorna erro.
6. O CPF do responsável deve ser válido e único.
7. A opção **Concluir** do frontend altera o status do chamado para `Resolvido`; ela não exclui nem fecha o chamado.
8. Para excluir um chamado pela interface, primeiro altere seu status para `Fechado`.

## Como usar o frontend

### Cadastrar um chamado

1. Abra a aba **Novo Chamado**.
2. Informe título, descrição, prioridade e status.
3. Clique em **Avançar**.
4. Selecione um responsável ou clique em **Atribuir Automaticamente**.
5. Clique em **Cadastrar Chamado**.

### Consultar e filtrar chamados

1. Abra a aba **Visualizar Chamados**.
2. Utilize a busca por título ou descrição.
3. Combine filtros de status, prioridade e responsável.
4. Clique em **Limpar filtros** para exibir todos novamente.

### Ver detalhes, editar e concluir

1. Clique em **Ver Detalhes** para consultar dados, UUID e datas.
2. Clique em **Editar** para alterar título, descrição, responsável, prioridade ou status.
3. Em chamados `Aberto` ou `EmAndamento`, clique em **Concluir** para alterar o status para `Resolvido`.

### Excluir um chamado

1. Edite o chamado e altere seu status para `Fechado`.
2. Abra **Ver Detalhes**.
3. Clique em **Excluir Chamado**.
4. Confirme a exclusão permanente.

## Respostas de erro

Erros tratados pela API seguem este formato:

```json
{
  "timestamp": "2026-06-07T14:30:00",
  "status": 400,
  "error": "Bussines Rule Violantion",
  "message": "Somente chamados fechados podem ser deletados",
  "path": "/chamados/UUID"
}
```

Os principais códigos retornados são:

- `400 Bad Request`: violação de regra de negócio.
- `404 Not Found`: recurso não encontrado.
- `500 Internal Server Error`: erro não tratado ou falha interna.

## Testes e build

Backend:

```powershell
cd Backend
.\gradlew.bat test
.\gradlew.bat build
```

Frontend:

```powershell
cd Frontend
npm run lint
npm run build
```
