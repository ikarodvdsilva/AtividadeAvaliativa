# EnviroCrime

Sistema de denúncias ambientais com API REST e aplicativo Android.

## Estrutura do Projeto

O projeto está dividido em duas partes principais:

1. `api/` - API REST em Python com FastAPI
2. `app/` - Aplicativo Android

## API REST

### Requisitos

- Python 3.8+
- pip (gerenciador de pacotes Python)

### Instalação

1. Navegue até o diretório da API:
```bash
cd api
```

2. Crie um ambiente virtual (recomendado):
```bash
python -m venv venv
source venv/bin/activate  # No Windows: venv\Scripts\activate
```

3. Instale as dependências:
```bash
pip install -r requirements.txt
```

### Executando a API

1. Ative o ambiente virtual (se ainda não estiver ativo):
```bash
source venv/bin/activate  # No Windows: venv\Scripts\activate
```

2. Execute o servidor:
```bash
uvicorn main:app --reload
```

A API estará disponível em `http://localhost:8000`

## Aplicativo Android

### Requisitos

- Android Studio Arctic Fox ou superior
- JDK 11 ou superior
- Android SDK 21+

### Configuração

1. Abra o projeto no Android Studio
2. Aguarde a sincronização do Gradle
3. Configure o endereço da API no arquivo `app/src/main/java/br/com/ikaro/atividadeavaliativa/api/ApiClient.java`:
   - Para emulador: `http://10.0.2.2:8000/`
   - Para dispositivo físico: `http://<seu-ip-local>:8000/`

### Executando o Aplicativo

1. Conecte um dispositivo Android ou inicie um emulador
2. Clique em "Run" no Android Studio
3. Aguarde a instalação e inicialização do aplicativo

## Funcionalidades

### API

- Autenticação de usuários
- Gerenciamento de usuários
- CRUD de relatórios
- Upload e gerenciamento de imagens
- Estatísticas e contagens

### Aplicativo Android

- Login e registro de usuários
- Criação e visualização de relatórios
- Upload de imagens
- Visualização de mapas
- Estatísticas
- Gerenciamento de usuários (admin)

## Segurança

- Autenticação via JWT
- Senhas criptografadas com bcrypt
- Validação de permissões
- HTTPS (em produção)

## Desenvolvimento

### API

- FastAPI para a API REST
- SQLAlchemy para ORM
- SQLite para banco de dados
- Pydantic para validação de dados

### Android

- MVVM como padrão de arquitetura
- Retrofit para chamadas de API
- LiveData para observáveis
- ViewModel para gerenciamento de estado
- Glide para carregamento de imagens
- OpenStreetMap para mapas 