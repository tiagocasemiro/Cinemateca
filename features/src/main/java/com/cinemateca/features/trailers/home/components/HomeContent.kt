package com.cinemateca.features.trailers.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.trailers.home.HomeTrailerItemUiModel

private val CardShape = RoundedCornerShape(14.dp)
private val PosterShape = RoundedCornerShape(10.dp)
private val PillShape = RoundedCornerShape(50)

@Composable
internal fun HomeHeader(
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

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            color = CinematecaColors.SurfaceElevated,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CinematecaColors.Outline),
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .semantics {
                    contentDescription = "Buscar filmes"
                },
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
                Text(
                    text = "Buscar filmes...",
                    color = CinematecaColors.TertiaryText,
                    fontSize = 17.sp,
                )
            }
        }
    }
}

@Composable
internal fun HomeFilters(
    movieCount: Int,
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
                items = listOf(
                    "Todos",
                    "Em Cartaz",
                    "Lançamentos",
                    "Em Breve",
                ),
            ) { label ->
                FilterChip(
                    text = label,
                    selected = label == "Todos",
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
                text = "$movieCount ${if (movieCount == 1) "filme" else "filmes"}",
                color = CinematecaColors.TertiaryText,
                fontSize = 11.sp,
                lineHeight = 17.sp,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FigmaIcon(
                    drawableResource = R.drawable.figma_sort,
                    contentDescription = null,
                    modifier = Modifier.size(11.dp),
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Mais Recentes",
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
private fun FilterChip(
    text: String,
    selected: Boolean,
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
        modifier = modifier.height(31.dp),
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
    onTrailerClick: (String) -> Unit,
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
                    onTrailerClick(trailer.id)
                },
                onFavoriteClick = {
                    onFavoriteClick(trailer.id)
                },
                onWatchClick = {
                    onWatchClick(trailer.id)
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
        color = CinematecaColors.Surface,
        shape = CardShape,
        border = BorderStroke(1.dp, CinematecaColors.Outline),
        modifier = modifier
            .fillMaxWidth()
            .height(197.dp)
            .clickable(onClick = onClick),
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
                    drawableResource = R.drawable.figma_heart,
                    onClick = onFavoriteClick,
                    modifier = Modifier.weight(1f),
                )
                MovieActionButton(
                    text = "Quero Assistir",
                    drawableResource = R.drawable.figma_ticket,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        color = CinematecaColors.ButtonSurface,
        shape = PillShape,
        border = BorderStroke(1.dp, CinematecaColors.ButtonOutline),
        modifier = modifier.height(36.dp),
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
                color = CinematecaColors.ButtonText,
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
