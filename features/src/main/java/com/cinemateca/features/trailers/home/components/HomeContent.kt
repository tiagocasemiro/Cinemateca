package com.cinemateca.features.trailers.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.trailers.home.HomeFilterOption
import com.cinemateca.features.trailers.home.HomeTrailerItemUiModel

private val CardShape = RoundedCornerShape(14.dp)
private val PosterShape = RoundedCornerShape(10.dp)
private val PillShape = RoundedCornerShape(50)

@Composable
internal fun HomeHeader(
    searchQuery: String,
    favoriteCount: Int,
    watchlistCount: Int,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 19.dp,
                top = 5.dp,
                end = 19.dp,
                bottom = 14.dp,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = CinematecaColors.Primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        CinematecaColors.Primary.copy(alpha = 0.25f),
                    ),
                    modifier = Modifier.size(34.dp),
                ) {
                    FigmaIcon(
                        drawableResource = R.drawable.figma_logo,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(9.dp),
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Cinemateca",
                    color = CinematecaColors.OnBackground,
                    fontSize = 18.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                if (favoriteCount > 0) {
                    SelectionCountBadge(
                        count = favoriteCount,
                        drawableResource = R.drawable.figma_heart_selected,
                        color = CinematecaColors.Favorite,
                        contentDescription = "$favoriteCount favoritos",
                    )
                }
                if (watchlistCount > 0) {
                    SelectionCountBadge(
                        count = watchlistCount,
                        drawableResource = R.drawable.figma_ticket_selected,
                        color = CinematecaColors.Primary,
                        contentDescription =
                            "$watchlistCount quero assistir",
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            color = CinematecaColors.SurfaceElevated,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CinematecaColors.Outline),
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 15.dp),
            ) {
                FigmaIcon(
                    drawableResource = R.drawable.figma_search,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = CinematecaColors.OnBackground,
                        fontSize = 17.sp,
                        lineHeight = 24.sp,
                    ),
                    cursorBrush = SolidColor(CinematecaColors.Primary),
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = "Buscar filmes"
                        },
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Buscar filmes...",
                                    color = CinematecaColors.TertiaryText,
                                    fontSize = 17.sp,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
                if (searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(10.dp))
                    FigmaIcon(
                        drawableResource = R.drawable.figma_close,
                        contentDescription = "Limpar busca",
                        modifier = Modifier
                            .size(14.dp)
                            .clickable(
                                role = Role.Button,
                                onClick = {
                                    onSearchQueryChange("")
                                },
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionCountBadge(
    count: Int,
    drawableResource: Int,
    color: Color,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = PillShape,
        border = BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.2f),
        ),
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(
                horizontal = 10.5.dp,
                vertical = 3.5.dp,
            ),
        ) {
            FigmaIcon(
                drawableResource = drawableResource,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
            )
            Text(
                text = count.toString(),
                color = color,
                fontSize = 10.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
internal fun HomeFilters(
    movieCount: Int?,
    sortOptionLabel: String,
    selectedFilter: HomeFilterOption,
    onFilterClick: (HomeFilterOption) -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CinematecaColors.Background),
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 19.dp,
                vertical = 12.dp,
            ),
        ) {
            items(
                items = HomeFilterOption.entries,
                key = HomeFilterOption::name,
            ) { option ->
                FilterChip(
                    text = option.label,
                    selected = option == selectedFilter,
                    onClick = {
                        onFilterClick(option)
                    },
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 19.dp,
                    end = 19.dp,
                    bottom = 12.dp,
                ),
        ) {
            Text(
                text = movieCount?.let { "$it filmes" } ?: "—",
                color = CinematecaColors.TertiaryText,
                fontSize = 11.sp,
                lineHeight = 17.sp,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(
                        role = Role.Button,
                        onClick = onSortClick,
                    )
                    .semantics {
                        contentDescription =
                            "Ordenar filmes: $sortOptionLabel"
                        role = Role.Button
                    },
            ) {
                FigmaIcon(
                    drawableResource = R.drawable.figma_sort,
                    contentDescription = null,
                    modifier = Modifier.size(11.dp),
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = sortOptionLabel,
                    color = CinematecaColors.Primary,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.width(5.dp))
                FigmaIcon(
                    drawableResource = R.drawable.figma_chevron,
                    contentDescription = null,
                    modifier = Modifier.size(11.dp),
                )
            }
        }

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.06f),
            thickness = 1.dp,
        )
    }
}

@Composable
internal fun HomeLoadingContent(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Carregando filmes"
            }
            .testTag("home_loading"),
    ) {
        items(
            count = 4,
            key = { index -> index },
        ) {
            SkeletonMovieCard()
        }
    }
}

@Composable
private fun SkeletonMovieCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        color = CinematecaColors.Surface,
        shape = CardShape,
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.05f),
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(164.dp)
            .graphicsLayer {
                alpha = 0.71f
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(15.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(95.dp)
                    .height(133.dp)
                    .clip(PosterShape)
                    .background(Color.White.copy(alpha = 0.1f)),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(9.5.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp),
            ) {
                SkeletonLine(
                    widthFraction = 0.75f,
                    height = 19.dp,
                )
                SkeletonLine(widthFraction = 0.5f)
                SkeletonLine(widthFraction = 0.67f)
                Spacer(modifier = Modifier.height(0.75.dp))
                SkeletonLine(widthFraction = 1f)
                SkeletonLine(widthFraction = 0.8f)
            }
        }
    }
}

@Composable
private fun SkeletonLine(
    widthFraction: Float,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 14.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White.copy(alpha = 0.1f)),
    )
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = if (selected) {
            CinematecaColors.Primary
        } else {
            Color.White.copy(alpha = 0.06f)
        },
        shape = PillShape,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                CinematecaColors.Primary
            } else {
                Color.White.copy(alpha = 0.08f)
            },
        ),
        modifier = modifier
            .height(31.dp)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = if (selected) {
                    "Filtro $text selecionado"
                } else {
                    "Filtrar por $text"
                }
                role = Role.Button
            },
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 15.dp),
        ) {
            Text(
                text = text,
                color = if (selected) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.5f)
                },
                fontSize = 14.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
internal fun HomeContent(
    trailers: List<HomeTrailerItemUiModel>,
    onTrailerClick: (
        trailerId: String,
        movieId: String,
        resourceType: String,
    ) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onWatchClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 14.dp,
            top = 14.dp,
            end = 14.dp,
            bottom = 38.dp,
        ),
        modifier = modifier.fillMaxSize(),
    ) {
        items(
            items = trailers,
            key = HomeTrailerItemUiModel::id,
        ) { trailer ->
            MovieCard(
                trailer = trailer,
                onClick = {
                    onTrailerClick(
                        trailer.id,
                        trailer.movieId,
                        trailer.resourceType,
                    )
                },
                onFavoriteClick = {
                    onFavoriteClick(trailer.movieId)
                },
                onWatchClick = {
                    onWatchClick(trailer.movieId)
                },
            )
        }
    }
}

@Composable
private fun MovieCard(
    trailer: HomeTrailerItemUiModel,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        color = CinematecaColors.Surface,
        shape = CardShape,
        border = BorderStroke(1.dp, CinematecaColors.Outline),
        modifier = modifier
            .fillMaxWidth()
            .height(197.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp),
            ) {
                AsyncImage(
                    model = trailer.thumbnailUrl,
                    contentDescription = "Pôster de ${trailer.title}",
                    contentScale = ContentScale.Crop,
                    error = ColorPainter(Color.White.copy(alpha = 0.05f)),
                    fallback = ColorPainter(Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier
                        .width(80.dp)
                        .height(116.dp)
                        .clip(PosterShape)
                        .background(Color.White.copy(alpha = 0.05f)),
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp),
                ) {
                    Text(
                        text = trailer.title,
                        color = CinematecaColors.OnBackground,
                        fontSize = 17.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    MovieMetadata(
                        drawableResource = R.drawable.figma_tag,
                        text = trailer.genres,
                        color = CinematecaColors.SecondaryText,
                    )
                    MovieMetadata(
                        drawableResource = R.drawable.figma_calendar,
                        text = trailer.published,
                        color = Color.White.copy(alpha = 0.4f),
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                MovieActionButton(
                    text = "Favoritar",
                    drawableResource = if (trailer.isFavorite) {
                        R.drawable.figma_heart_selected
                    } else {
                        R.drawable.figma_heart
                    },
                    isSelected = trailer.isFavorite,
                    selectedColor = CinematecaColors.Favorite,
                    onClick = onFavoriteClick,
                    modifier = Modifier.weight(1f),
                )
                MovieActionButton(
                    text = "Quero Assistir",
                    drawableResource = if (trailer.isWatchlisted) {
                        R.drawable.figma_ticket_selected
                    } else {
                        R.drawable.figma_ticket
                    },
                    isSelected = trailer.isWatchlisted,
                    selectedColor = CinematecaColors.Primary,
                    onClick = onWatchClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MovieMetadata(
    drawableResource: Int,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        FigmaIcon(
            drawableResource = drawableResource,
            contentDescription = null,
            modifier = Modifier.size(11.dp),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            color = color,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MovieActionButton(
    text: String,
    drawableResource: Int,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) {
            selectedColor.copy(alpha = 0.22f)
        } else {
            CinematecaColors.ButtonSurface
        },
        shape = PillShape,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                selectedColor.copy(alpha = 0.45f)
            } else {
                CinematecaColors.ButtonOutline
            },
        ),
        modifier = modifier
            .height(36.dp)
            .semantics {
                selected = isSelected
                contentDescription = if (isSelected) {
                    "$text selecionado"
                } else {
                    text
                }
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            FigmaIcon(
                drawableResource = drawableResource,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = text,
                color = if (isSelected) {
                    selectedColor
                } else {
                    CinematecaColors.ButtonText
                },
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
internal fun HomeErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(24.dp),
    ) {
        Text(
            text = message,
            color = CinematecaColors.SecondaryText,
            fontSize = 15.sp,
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = CinematecaColors.Primary,
            ),
        ) {
            Text(text = "Tentar novamente")
        }
    }
}

@Composable
private fun FigmaIcon(
    drawableResource: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(drawableResource),
        contentDescription = contentDescription,
        contentScale = ContentScale.FillBounds,
        modifier = modifier,
    )
}
