📌 Visão Geral
API RESTful desenvolvida em Spring Boot para cadastro, autenticação e gerenciamento de usuários, seguindo boas práticas de arquitetura e segurança.

Principais funcionalidades:
✔ Cadastro de usuários com envio de e-mail de confirmação
✔ Autenticação via JWT (JSON Web Token)
✔ Consulta paginada de usuários com filtro por nome
✔ Atualização e exclusão de usuários
✔ Testes unitários e de integração

🛠 Tecnologias Utilizadas
Java 17

Spring Boot 3 (Web, Security, Data JPA, Validation)

PostgreSQL (Banco de dados)

Lombok (Redução de boilerplate)

MapStruct (Mapeamento de DTOs)

JUnit 5 + Mockito (Testes unitários)

Swagger/OpenAPI (Documentação da API)

Docker (Containerização)

MailHog (Simulador de e-mail para desenvolvimento)

🚀 Como Executar
Pré-requisitos
Docker e Docker Compose instalados

WSL 2 (se estiver no Windows)

Java 21

1. Via Docker (Recomendado)
   bash
# Clone o repositório
git clone <repo-url>
cd <diretorio-do-projeto>

# Inicie os containers
docker-compose up -d --build
Serviços disponíveis:

API: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui.html

MailHog (E-mails simulados): http://localhost:8025

PostgreSQL: localhost:5432 (usuário: postgres, senha: postgres)

2. Localmente (Sem Docker)
   bash
# Configure o application.yml com suas credenciais do PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/vmtech_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Execute a aplicação
mvn spring-boot:run