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

Atualize o estado de loading esse layout https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=2-8&t=h9eFD3oIOp74GJK6-4

$android-app-architecture Adicione detecção de queda de internet, mesmo quando o device continua ligado a rede. Ao abrir o app sem conexão exiba a esta tela https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=3-136&t=h9eFD3oIOp74GJK6-4

Ao clicar no botão mais recentes, Exiba um botom sheet como no layout https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=5-248&t=h9eFD3oIOp74GJK6-4. Após seleção de uma nova forma de ordenar, o texto Mais Recentes ao lado do icone de ordenação será substituido pela nova frma de ordenar.

Ao clicar em um dos filtros "Todos", "Em Cartaz", "Lançamentos", "Em Breve", Selecione o botão clicado e aplique o filtro na listagem.

Considerenado que um filme fique 3 semanas em cartas e que a data de lancamento do trailer acontece 1 mes antes do lancamento do filme. Atulaize os filtros "Todos", "Em Cartaz", "Lançamentos", "Em Breve" baseando se na data de publicação do trailer

O campo de busca com placeholder "Buscar filmes..." deve ser construido com um input. Ao digitar cada letra no input a listagem deve ser filtrada pelo titulo do filme com o termo digitado.

Confira o layout implementado com o layout projetado no figma url: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=7-1092&t=h9eFD3oIOp74GJK6-4 . Se necessario faca os ajustes visuais.

$android-app-architecture ao clicar nos botões "Favoritar" ou "Quero assistir", os botões devem ficar selecionados por filme, como no layout do figma url: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=7-1226&t=h9eFD3oIOp74GJK6-4. Crie 2 tabelas no banco local usando room, para salvar os "favoritos" e os "quero assistir". Ao reiniciar o app esta informação deve ser preservada.

$android-app-architecture a implementação do room com a consulta a banco local ficou no modulo app, enquanto a implementação do acesso a dados remotos ficou no nodulo networking. Crie um modulo para implementação de acesso a dados locais, parecido com o que ja acontece com o acesso a dados remotos. Atualize a skill .github/skills/architecture/SKILL.md e seus arquivos de referencia para sempre criarem acesso local a dados em modulo proprio, seguindo as demais orientações da skill.

Crie a tela de detalhe do trailer clicado. A tela possui scroll com isso vou passar 2 layouts com o topo da tela e a base da tela, no meio tem o conteudo que ficou nas 2 partes. No fundo da tela tem a mesma imagem usada no topo, esticada, aplicado blur e colocado uma mascara preta com 80% de transparencia, a imagem com blur + mascara com transparencia ficam no fundo da tela por tras do conteudo. O background dos botões de compartilhar e voltar tem uma transparencia levemente escura com blur. Figma urls, topo: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=9-1645&t=h9eFD3oIOp74GJK6-4 e base: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=9-1830&t=h9eFD3oIOp74GJK6-4

Atualize as cores do botão de favorito e querro assistir quando selecionados. use o layout do figma url: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=9-1986&t=h9eFD3oIOp74GJK6-4

Na pasta /home/tiagocasemiro/Imagens/cinemateca_launcher_icons voce vai encontrar todos os icones para adicionar ao app. Adicione o icone com todas as configurações recomentadas

Atualize o estado de loading da tela de detalhes https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=13-2171&t=h9eFD3oIOp74GJK6-4

Cire o icone usando Android vector drawable com esse layout https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=14-2254&t=h9eFD3oIOp74GJK6-4. Adicione bordas da cor Color(0xFF4D8EFF)

No meu celular sansumg o icone fica com as bordas cortadas, pq no sansung os icones tem um formato diferente. Em alguns celulares o formato é redondo. ajuste o icone de launch para funcionar bem em todos os formatos

Adicione uma borda azul igual a do app/src/main/res/drawable/ic_launcher.xml no icone app/src/main/res/drawable/ic_launcher_round_background.xml

os 2 icones da pasta app/src/main/res/mipmap estão apresentando erro

Os 2 drawables da pasta mipmap estão dando erro. todos os icones redondos das pastas mipmap estão quadrados.
