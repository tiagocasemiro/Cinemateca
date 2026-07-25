# Repository

## Conteúdo

- [Responsabilidades](#responsabilidades)
- [Convenções obrigatórias](#convenções-obrigatórias)
- [Fluxo de dados remoto](#fluxo-de-dados-remoto)
- [Uso dos assets](#uso-dos-assets)
- [Automação](#automação)
- [Checklist de Repository](#checklist-de-repository)
- [Origens dos dados](#origens-dos-dados)
- [Tecnologias de referência](#tecnologias-de-referência)

## Responsabilidades

- Abstrair a origem dos dados.
- Decidir entre API, cache e banco local.
- Converter os modelos recebidos da fonte de dados em modelos de domínio.
- Traduzir respostas HTTP e exceções de infraestrutura para o tipo `Result` do domínio.

## Convenções obrigatórias

Todo Repository deve cumprir as seguintes regras:

1. Ser declarado como `interface` no módulo de domínio.
2. Terminar o nome exatamente com o sufixo `Repository`.
3. Estar no pacote
   `com.example.app.domain.<feature>.repository`.
4. Declarar o contrato da fonte remota na interface aninhada `Remote`.
5. Expor operações remotas como funções `suspend`.
6. Retornar `Result<T>` do domínio, sem expor `retrofit2.Response`, DTOs ou
   exceções de infraestrutura.
7. Implementar o contrato remoto no módulo de network com uma classe cujo nome
   termine em `RemoteImpl`, no pacote de adapters.
8. Executar a chamada remota e a extração da resposta dentro de `fetchData`.
9. Registrar a implementação no módulo de injeção de dependências vinculando-a
   ao respectivo contrato `Repository.Remote`.

Usar as seguintes estruturas de pacote:

```text
com.example.app.domain.<feature>.repository
com.example.app.networking.adapter
```

### Exemplo válido

```kotlin
package com.example.app.domain.user.repository

interface UserRepository {
    interface Remote {
        suspend fun findUser(id: String): Result<User>
    }
}
```

A implementação correspondente deve cumprir o contrato:

```kotlin
package com.example.app.networking.adapter

class UserRemoteImpl(
    private val gateway: UserGateway
) : UserRepository.Remote {

    override suspend fun findUser(id: String): Result<User> {
        return fetchData {
            gateway.findUser(id).extractData()
        }
    }
}
```

E deve ser registrada pelo tipo do contrato:

```kotlin
factory<UserRepository.Remote> {
    UserRemoteImpl(gateway = get())
}
```

## Fluxo de dados remoto

Adotar o seguinte fluxo:

```text
Retrofit Gateway
↓ Response<ResponseDto>, Response<List<ResponseDto>> ou Response<Unit>
Remote Repository
↓ fetchData { response.extract...() }
Result<DomainModel>
↓
UseCase / ViewModel
```

## Uso dos assets

Usar os arquivos de `assets` como templates de implementação, não apenas como
referência conceitual. Copiar o conjunto correspondente para o projeto e
preservar os packages `com.example.app` declarados nos arquivos.

Para implementar o fluxo remoto de Repository, usar em conjunto:

| Asset | Destino e uso obrigatório |
| --- | --- |
| `assets/usecase/domain/Result.kt` | Copiar para o módulo de domínio. Usar `Result`, `Success`, `Failure`, `Loading` e `Error` nos contratos e consumidores. |
| `assets/repository/DomainMapper.kt` | Copiar para a infraestrutura de Repository. Fazer DTOs de resposta com dados implementarem `DomainMapperResponse`. |
| `assets/repository/FetchData.kt` | Copiar para a infraestrutura de Repository. Envolver chamadas remotas e extração de respostas com `fetchData`. |
| `assets/repository/NetworkResult.kt` | Copiar para a infraestrutura de Repository. Converter respostas Retrofit com `extractData`, `extractList`, `extractNoData` ou `processData`. |

Não recriar manualmente essas classes quando o asset correspondente puder ser
copiado. Se o projeto já possuir uma implementação equivalente, comparar o
contrato existente com o asset e adaptar sem manter duas abstrações concorrentes.

## Automação

Executar os scripts a partir da raiz da skill.

### Instalar assets

Usar `install_assets.py` para copiar os assets, criar os diretórios dos módulos e
substituir `com.example.app` pelo pacote selecionado.

Simular a instalação do conjunto de Repository:

```bash
python3 scripts/install_assets.py repository \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --dry-run
```

Após revisar os caminhos, repetir sem `--dry-run`. O grupo `repository` instala
`Result.kt`, `DomainMapper.kt`, `FetchData.kt` e `NetworkResult.kt`. O script
recusa sobrescrever arquivos. Usar `--force` somente após comparar a
implementação existente e confirmar explicitamente a substituição.

### Gerar contrato e implementação remota

Usar o subcomando `repository` para criar o contrato no domínio e a implementação
`RemoteImpl` no módulo de networking:

```bash
python3 scripts/scaffold_architecture.py repository \
  --target-root /caminho/do/projeto \
  --base-package com.example.app \
  --feature user \
  --name User \
  --operation findUser \
  --parameter "id: String" \
  --result-type User \
  --import com.example.app.domain.user.User \
  --dry-run
```

O comando gera:

```text
domain/.../domain/user/repository/UserRepository.kt
networking/.../networking/adapter/UserRemoteImpl.kt
```

Repetir sem `--dry-run` para gravar. Em seguida:

1. Injetar o Gateway em `UserRemoteImpl`.
2. Substituir o `TODO` pela chamada Retrofit e pela extensão `extract...`
   adequada.
3. Criar ou adaptar os DTOs com `DomainMapperResponse`.
4. Registrar `UserRepository.Remote` na DI.
5. Formatar e compilar os módulos afetados.

Usar `--parameter` e `--import` mais de uma vez quando necessário. O gerador não
altera Gateway, módulo de DI ou arquivos existentes.

### 1. Definir o contrato do Repository no domínio

O contrato exposto para as demais camadas deve retornar
`com.example.app.domain.Result`, sem expor `retrofit2.Response` ou DTOs da API:

```kotlin
interface UserRepository {
    interface Remote {
        suspend fun findUser(id: String): Result<User>
        suspend fun findAll(): Result<List<User>>
        suspend fun update(user: User): Result<Unit>
    }
}
```

O módulo de domínio não deve depender do módulo de network. A implementação remota,
por outro lado, pode depender dos tipos do domínio para cumprir esse contrato.

### 2. Mapear respostas com `DomainMapperResponse`

Todo DTO retornado pela API que representa um objeto do domínio deve implementar
`DomainMapperResponse<T>` e realizar a transformação em `mapToDomain()`:

```kotlin
data class UserResponse(
    val id: String,
    val displayName: String
) : DomainMapperResponse<User> {
    override fun mapToDomain(): User {
        return User(
            id = id,
            name = displayName
        )
    }
}
```

O mapper pertence ao DTO de resposta. DTOs usados somente como corpo de requisição
não precisam implementar a interface.

### 3. Converter a `Response` do Retrofit

Selecionar a extensão de acordo com a assinatura do endpoint:

| Retorno do Gateway | Extensão | Retorno do Repository |
| --- | --- | --- |
| `Response<UserResponse>` | `extractData()` | `Result<User>` |
| `Response<List<UserResponse>>` | `extractList()` | `Result<List<User>>` |
| `Response<Unit>` | `extractNoData()` | `Result<Unit>` |
| `Response<Void>` | `processData()` | `Result<Unit>` |

Exemplo de Gateway:

```kotlin
interface UserGateway {
    @GET("users/{id}")
    suspend fun findUser(@Path("id") id: String): Response<UserResponse>

    @GET("users")
    suspend fun findAll(): Response<List<UserResponse>>

    @PUT("users/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Body request: UpdateUserRequest
    ): Response<Unit>
}
```

As extensões aplicam as seguintes regras:

- Resposta HTTP bem-sucedida com corpo: cria `Success`, mapeando objetos e listas
  para o domínio quando necessário.
- Resposta HTTP sem sucesso e com `errorBody`: cria `Failure` a partir do JSON de
  erro e preenche `Error.httpError` com o status HTTP.
- Resposta sem o corpo esperado, inclusive sucesso com corpo nulo, ou falha sem
  `errorBody`: retorna `Failure(null)`, representado por `generalFailure`.
- `extractError()` espera que o corpo de erro possa ser desserializado para
  `Error(code, httpError, title, message)`.

`extractNoData()` e `processData()` têm a mesma saída de domínio, mas atendem tipos
de resposta Retrofit diferentes. Usar a função compatível com a assinatura do
Gateway.

### 4. Executar a chamada com `fetchData`

A implementação remota deve envolver a chamada ao Gateway e a extração da resposta
em `fetchData`:

```kotlin
class UserRemoteImpl(
    private val gateway: UserGateway
) : UserRepository.Remote {

    override suspend fun findUser(id: String): Result<User> {
        return fetchData {
            gateway.findUser(id).extractData()
        }
    }

    override suspend fun findAll(): Result<List<User>> {
        return fetchData {
            gateway.findAll().extractList()
        }
    }

    override suspend fun update(user: User): Result<Unit> {
        return fetchData {
            val request = UpdateUserRequest(name = user.name)
            gateway.update(user.id, request).extractNoData()
        }
    }
}
```

`fetchData`:

- executa todo o bloco em `Dispatchers.IO`;
- preserva o `Result` produzido pelo bloco quando não ocorre exceção;
- converte `ConnectException` em `Failure` com código interno `166` e mensagem de
  falha de conexão;
- converte qualquer outra `Exception` em `Failure` com código interno `266` e
  mensagem de erro inesperado.

O bloco deve incluir tanto a chamada Retrofit quanto `extractData()`,
`extractList()`, `extractNoData()` ou `processData()`. Assim, exceções da chamada,
da leitura do erro e do mapeamento ficam sujeitas ao mesmo tratamento.

### 5. Consumir o `Result` do domínio

`Result<T>` é uma classe selada com os estados:

- `Success<T>(data)`: contém o dado de domínio.
- `Failure(error)`: contém um `Error?`; o erro pode ser nulo no caso de
  `generalFailure`.
- `Loading<T>(data)`: representa carregamento com dado associado. Esse estado está
  definido, mas não é utilizado no fluxo analisado.

É possível tratar os estados com `when`:

```kotlin
when (val result = repository.findUser(id)) {
    is Success -> showUser(result.data)
    is Failure -> showError(result.error?.formattedMessage.orEmpty())
    is Loading -> showLoading()
}
```

Também estão disponíveis as extensões `onSuccess` e `onFailure` do domínio:

```kotlin
repository.findUser(id)
    .onSuccess { user -> showUser(user) }
    .onFailure { error -> showError(error?.formattedMessage.orEmpty()) }
```

`onSuccess` devolve o próprio `Result`, permitindo o encadeamento acima.
`onFailure` encerra o encadeamento e recebe `Error?`.

O tipo `Error` oferece:

- `formattedTitle`: título ou string vazia.
- `formattedMessage`: mensagem ou string vazia.
- `formattedMessageCode`: concatenação de código, título e mensagem.
- `httpError`: status HTTP preenchido durante a extração de uma falha da API.

### Exemplos inválidos

```kotlin
package com.example.app.data

interface UserRepository
```

O nome está correto, mas o contrato não está no pacote de domínio da feature.

```kotlin
package com.example.app.domain.user.repository

interface UserData
```

O pacote está correto, mas o nome não termina com `Repository`.

```kotlin
interface UserRepository {
    interface Remote {
        fun findUser(id: String): Response<UserResponse>
    }
}
```

O contrato remoto não é `suspend`, expõe um tipo do Retrofit e retorna um DTO.

## Checklist de Repository

Antes de considerar um Repository válido, verificar:

- [ ] O contrato é uma `interface` declarada no módulo de domínio.
- [ ] O nome termina exatamente com `Repository`.
- [ ] O package pertence ao caminho
  `com.example.app.domain.<feature>.repository`.
- [ ] O contrato remoto está na interface aninhada `Remote`.
- [ ] As operações remotas são `suspend`.
- [ ] O contrato retorna `Result` do domínio e não expõe `Response` ou DTOs.
- [ ] A implementação está no módulo de network, no pacote de adapters, e seu
  nome termina em `RemoteImpl`.
- [ ] DTOs de resposta com dados implementam `DomainMapperResponse`.
- [ ] A extensão `extract...` corresponde ao tipo retornado pelo Gateway.
- [ ] A chamada remota e a extração da resposta estão dentro de `fetchData`.
- [ ] A implementação está registrada na injeção de dependências pelo tipo
  `Repository.Remote`.
- [ ] O consumidor trata `Failure.error` como anulável.

## Origens dos dados

- Remote API
- Banco local
- Cache

## Tecnologias de referência

- Retrofit
- Room
