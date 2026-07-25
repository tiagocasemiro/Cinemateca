# Visão geral da arquitetura

Adotar MVVM, Clean Architecture e Jetpack Compose como definições principais:

- MVVM (Model-View-ViewModel)
- Clean Architecture
- Jetpack Compose

## Camadas

### UI Layer

- Compose
- ViewModel
- State/UI State

### Domain Layer

- UseCases
- Regras de negócio

### Data Layer

- Repository
- Remote API
- Banco local
- Cache

## Fluxo simplificado

```text
UI (Compose Screen)
↓
ViewModel
↓
UseCase
↓
Repository (API / Database)

```

## Stack de referência

- Kotlin
- Jetpack Compose
- Coroutines
- Flow / StateFlow
- Koin
- Retrofit
- Room
- Navigation Compose
- JUnit
- Konsist
- Robolectric
- coil
- Gson
- Paging 3
- Media3
