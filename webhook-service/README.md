# Payment Webhook Service

Microsserviço webhook que recebe requisições de pagamento e as encaminha para o serviço de processamento, mantendo um histórico persistente no PostgreSQL.

## Pré-requisitos

- Java 17
- Maven
- Docker e Docker Compose (para rodar o PostgreSQL)
- PostgreSQL (se preferir instalar localmente em vez de usar Docker)

## Configuração do Banco de Dados

### Usando Docker (recomendado)

1. Inicie o PostgreSQL usando Docker Compose:
```bash
docker-compose up -d
```

### Instalação Manual do PostgreSQL

1. Instale o PostgreSQL
2. Crie um banco de dados chamado `webhook`:
```sql
CREATE DATABASE webhook;
```

## Configuração da Aplicação

O arquivo `application.properties` já está configurado para conectar ao PostgreSQL em:
- Host: localhost
- Porta: 5432
- Banco: webhook
- Usuário: postgres
- Senha: postgres

Se necessário, ajuste estas configurações em `src/main/resources/application.properties`.

## Executando a Aplicação

1. Certifique-se que o PostgreSQL está rodando
2. Execute a aplicação:
```bash
mvn spring-boot:run
```

## Endpoints

### 1. Receber Pagamento (Async)

Endpoint que recebe um novo pagamento e o armazena para processamento posterior.

```bash
curl -X POST http://localhost:8080/webhook/payment \
  -H "Content-Type: application/json" \
  -d '{
    "id": "evt_123",
    "amount": 150.50,
    "currency": "BRL",
    "payer": {
      "document": "123.456.789-00",
      "name": "João Silva",
      "email": "joao@example.com"
    }
  }'
```

Resposta de Sucesso (202 Accepted):
```json
{
  "webhookAccepted": true,
  "forwarded": false,
  "message": "Payment queued for processing",
  "forwardedResponse": null
}
```

### 2. Consultar Próximo Pagamento

Retorna o pagamento mais antigo com status "RECEIVED" que ainda não foi processado.

```bash
curl -X GET http://localhost:8080/webhook/payment/next
```

Resposta de Sucesso (200 OK):
```json
{
  "id": 1,
  "eventId": "evt_123",
  "amount": 150.50,
  "currency": "BRL",
  "payerDocument": "123.456.789-00",
  "status": "RECEIVED",
  "requestPayload": "...",
  "createdAt": "2023-10-29T10:30:00Z"
}
```

### 3. Atualizar Status do Pagamento

Atualiza o status de um pagamento específico e tenta encaminhá-lo para o serviço de pagamentos.

```bash
curl -X PATCH http://localhost:8080/webhook/payment/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "FORWARDED"
  }'
```

Resposta de Sucesso (200 OK):
```json
{
  "webhookAccepted": true,
  "forwarded": true,
  "message": "Payment evt_123 status updated to FORWARDED",
  "forwardedResponse": {
    "status": "OK",
    "id": "pay_987"
  }
}
```

Resposta Quando Não Encontrado (404 Not Found)

## Estrutura do Banco

A tabela `payment_history` registra:
- ID do evento
- Valor e moeda
- Nome do pagador
- Payload completo da requisição
- Status (RECEIVED, FORWARDED, FAILED)
- Resposta do serviço de pagamentos
- Data de criação/atualização

## Configuração do Serviço de Pagamentos

Configure a URL do serviço de pagamentos em `application.properties`:
```properties
payments.service.url=http://seu-servico-pagamentos/api/pay
```

## Monitoramento

Consulte o histórico de pagamentos diretamente no PostgreSQL:
```sql
SELECT * FROM payment_history ORDER BY created_at DESC;
```