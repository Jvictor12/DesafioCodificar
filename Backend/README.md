# Backend

Backend Spring Boot com PostgreSQL.

## Executar com Docker

Requisito: Docker Desktop ou Docker Engine com Docker Compose.

1. Opcionalmente, copie `.env.example` para `.env` e altere as credenciais.
2. Construa e inicie a API e o banco:

```bash
docker compose up --build
```

A API estará disponível em `http://localhost:8080` e a documentação Swagger
em `http://localhost:8080/swagger-ui.html`.

Para executar em segundo plano:

```bash
docker compose up --build -d
```

Para parar os containers:

```bash
docker compose down
```

Os dados do PostgreSQL são mantidos no volume `postgres_data`. Para também
apagar os dados:

```bash
docker compose down -v
```

## Compartilhar

Envie o repositório para a outra pessoa. Ela não precisa instalar Java,
Gradle ou PostgreSQL; precisa apenas executar `docker compose up --build`.

Para uso fora de um ambiente local, defina uma senha forte em `.env` e não
publique esse arquivo no repositório.
