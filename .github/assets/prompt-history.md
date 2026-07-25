Após cada prompt, adicione o prompt digitado no arquivo .github/assets/prompt-history.md

Extraia todas as definições de analytcs do arquivo .github/skills/architecture/references/re

› Extraia todas as definições de analytcs do arquivo .github/skills/architecture/references/repository.md para o arquivo .github/skills/architecture/assets/repository/analytics.md. Atualize a referencia de uso no arquivo .github/skills/architecture/SKILL.md

mova o arquivo .github/skills/architecture/assets/repository/analytics.md para o diretorio .github/skills/architecture/references

Crie a sessão ## Conteúdo com links locais para o arquivo .github/skills/architecture/references/repository.md

Crie no diretorio rais um app Android chamado Cinemateca apenas com a MainActivity. Deixe a activity em branco.

Compile e execute o app  no device usando o adb

Renomeie o pacote do app de com.example.cinemateca para com.cinemateca; Remova qualquer referencia a example do app

Adicione o pacote da aplicação ao manifest

A activity MainActivity está dando erro no manifest. Corrija

$android-app-architecture adicione todas as dependencias listada na skill ao projeto

Crie a camada de repository

Integre o app Android Cinemateca com estes endpoints da KinoCheck:

GET /trailers/trending — filmes e trailers em alta.
GET /trailers/latest — lançamentos e trailers recentes.
GET /trailers — filtros por gênero, categoria e idioma.
GET /movies?id={id} — detalhes, trailer, vídeos oficiais e recomendações.

Também permita buscar detalhes usando:

tmdb_id
imdb_id

Crie os usecases para disponibilisar para as view model todos os dados recebidos por repository

$android-app-architecture Implemente a view model da home consumindo a listagem o usecase de trending. Deixe a viewmodel preparada para receber a tela com jetpack compose

implemente o layout da tela de home, use o mcp do figma para ler o layout da url: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=1-411&t=h9eFD3oIOp74GJK6-4
