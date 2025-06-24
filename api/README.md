# EnviroCrime API

API REST para o sistema EnviroCrime, desenvolvida com FastAPI e SQLite.

## Requisitos

- Python 3.8+
- pip (gerenciador de pacotes Python)

## Instalação

1. Crie um ambiente virtual (recomendado):
```bash
python -m venv venv
source venv/bin/activate  # No Windows: venv\Scripts\activate
```

2. Instale as dependências:
```bash
pip install -r requirements.txt
```

## Executando a API

1. Ative o ambiente virtual (se ainda não estiver ativo):
```bash
source venv/bin/activate  # No Windows: venv\Scripts\activate
```

2. Execute o servidor:
```bash
uvicorn main:app --reload
```

A API estará disponível em `http://localhost:8000`

## Documentação

- Documentação Swagger UI: `http://localhost:8000/docs`
- Documentação ReDoc: `http://localhost:8000/redoc`

## Endpoints Principais

### Autenticação
- POST `/token` - Login e obtenção de token

### Usuários
- POST `/users/` - Criar novo usuário
- GET `/users/me/` - Obter dados do usuário atual
- GET `/users/` - Listar usuários (apenas admin)

### Relatórios
- POST `/reports/` - Criar novo relatório
- GET `/reports/` - Listar relatórios
- GET `/reports/{report_id}` - Obter detalhes de um relatório
- PUT `/reports/{report_id}` - Atualizar relatório

### Imagens
- POST `/reports/{report_id}/images/` - Adicionar imagem ao relatório
- GET `/reports/{report_id}/images/` - Listar imagens do relatório

## Segurança

- Todas as rotas (exceto login e registro) requerem autenticação via token JWT
- Senhas são armazenadas com hash bcrypt
- Tokens expiram após 30 minutos
- Validação de permissões para operações administrativas 