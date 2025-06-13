# Projeto VMTech - API de Gestão de Usuários

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.5-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

API RESTful para gestão de usuários com autenticação JWT, desenvolvida como teste técnico para vaga de Desenvolvedor
Java.

### Tecnologias utilizadas

- Java 21
- Spring Boot 3x
- Spring Web, Spring Security, Spring Data JPA, Spring Validation
- JWT (JSON Web Token) para autenticação e autorização
- H2 (banco em memória para testes)
- JUnit 5 + Mockito + MockMvc
- OpenAPI/Swagger para documentação de endpoints REST

---

## Endpoints principais

### Autenticação

- `POST /vmtech/auth/login` - Autentica o usuário e retorna um token JWT
- `POST /vmtech/auth/logout` - Invalida o token JWT atual

## Fluxo Básico

1. Autentique-se via `/auth/login` para obter o token JWT
2. Use o token no header `Authorization: Bearer <token>`
3. Acesse endpoints protegidos
4. Invalide o token via `/auth/logout` quando necessário

### Usuários

- `POST /vmtech/users/createUser` - Cria novo usuário
- `PUT /vmtech/users/{id}` - Atualiza dados de um usuário existente
- `GET /vmtech/users` - Lista paginada de usuários (com filtro opcional por nome)
- `GET /vmtech/users/{id}` - Consulta por ID
- `DELETE /vmtech/users/{id}` - Remove um usuário do sistema

---

## Testes unitários e integração

### Estrutura de Testes

Os testes foram divididos em:

- `UserControllerTest`: valida o comportamento REST (HTTP status, resposta JSON)
- `UserServiceTest`: cobre a lógica de negócio, interação com repositório e email
- `VmTesteTecnicoApplicationTests`: teste de inicialização do contexto Spring Boot

### Cenários cobertos

- Criação de usuário com dados válidos e com e-mail duplicado
- Falha no envio de email com rollback da operação
- Atualização de dados com troca de e-mail e senha
- Busca de usuário por ID (sucesso e erro)
- Exclusão de usuário (com email de confirmação)
- Listagem paginada com e sem filtro
- Validação de formato de email
- Conversão correta entre `User` e `UserDTO`

### Motivação

- Os métodos de negócio são testados para garantir a integridade dos dados e consistência em casos de exceção.
- O controlador foi testado com MockMvc para garantir contratos REST corretos (status, JSON).
- A lógica de rollback em caso de erro no envio de e-mail foi coberta para mostrar preocupação com atomicidade.

---

## Pré-requisitos

- Java 21 JDK instalado
- Maven (ou usar o wrapper incluído)
- Opcional: IDE como IntelliJ IDEA ou VS Code

## Possibilidades de frontend

### React

**Prós:**

- Grande comunidade e ecossistema
- Facilidade de integração com REST APIs
- Flexível e performático com React Query e Hooks

**Contras:**

- Curva de aprendizado com gerenciamento de estado (Redux, Zustand, etc.)
- Exige configuração inicial se não usar Create React App ou Vite

## 🖥️ Possibilidades para Frontend

### <img src="https://angular.io/assets/images/logos/angular/angular.svg" width="20" height="20"> Angular

#### ✅ Vantagens
- **Arquitetura bem definida** - MVC claro com serviços, componentes e módulos
- **Ferramentas integradas** - CLI poderosa incluindo testes (Karma/Jasmine)
- **RxJS** - Excelente para chamadas HTTP reativas
- **Material UI** - Componentes prontos seguindo guidelines do Google

#### ⚠️ Desafios
- Curva de aprendizado acentuada (injetores, decorators, zones)
- Verbosidade no código

#### 💻 Exemplo de Implementação
```typescript
// user.service.ts
@Injectable({ providedIn: 'root' })
export class UserService {
    constructor(private http: HttpClient) {}

    getUsers(page: number, filter?: string): Observable<PaginatedResponse<User>> {
        return this.http.get<PaginatedResponse<User>>('/vmtech/users', {
            params: { page, size: 10, ...(filter && { nome: filter }) }
        });
    }
}
```
    
<img src="https://vuejs.org/images/logo.png" width="20" height="20"> Vue 3 (Composition API)
✅ Vantagens
Sintaxe intuitiva - Single-file components (.vue)

Alta performance - Virtual DOM otimizado

Pinia - Gerenciamento de estado simplificado

Vite - Build tool ultra-rápido

⚠️ Desafios
Mudanças frequentes entre versões

Menor presença em grandes corporações

💻 Exemplo de Implementação

```vue
<template>
<div>
    <table v-if="users.length">
    <!-- Lista de usuários -->
</table>
</div>
</template>

<script setup>
import { ref } from 'vue';

const users = ref([]);
const page = ref(1);

const loadUsers = async () => {
    const { data } = await axios.get('/vmtech/users', {
        params: { page: page.value, size: 10 }
    });
    users.value = data.content;
};
</script>
```
🚀 Como Rodar o Projeto
bash
    ./mvnw clean install
    ./mvnw spring-boot:run
```

Acesse a documentação Swagger: `http://localhost:8080/swagger-ui.html`

---

## Observações finais

- O projeto está preparado para execução com perfil `test`, utilizando H2
- Possui separação de responsabilidades e boas práticas de arquitetura
- JWT é utilizado com blacklist para logout seguro
- Pronto para extensão futura com autenticação via roles e integração com frontend moderno

