package com.cinemateca.features.trailers.details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.trailers.details.PromotionalVideoUiModel
import com.cinemateca.features.trailers.details.TrailerDetailsUiModel

private val ContentPadding = 19.dp
private val CardShape = RoundedCornerShape(14.dp)
private val ActionShape = RoundedCornerShape(14.dp)

@Composable
fun TrailerDetailsBackground(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinematecaColors.Background),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            error = ColorPainter(CinematecaColors.Background),
            fallback = ColorPainter(CinematecaColors.Background),
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    radius = 32.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded,
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.80f)),
        )
    }
}

@Composable
fun TrailerDetailsContent(
    details: TrailerDetailsUiModel,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onYouTubeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        TrailerHero(
            details = details,
            onBackClick = onBackClick,
            onShareClick = onShareClick,
        )
        Text(
            text = details.title,
            color = Color.White,
            fontSize = 19.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = ContentPadding,
                top = 14.dp,
                end = ContentPadding,
                bottom = 10.dp,
            ),
        )
        ActionButtons(
            details = details,
            onFavoriteClick = onFavoriteClick,
            onWatchlistClick = onWatchlistClick,
        )
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.07f),
            modifier = Modifier.padding(top = 14.dp),
        )
        DetailsBody(
            details = details,
            onYouTubeClick = onYouTubeClick,
        )
    }
}

@Composable
private fun TrailerHero(
    details: TrailerDetailsUiModel,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
    ) {
        AsyncImage(
            model = details.thumbnailUrl,
            contentDescription = "Imagem de ${details.title}",
            contentScale = ContentScale.Crop,
            error = ColorPainter(Color.White.copy(alpha = 0.05f)),
            fallback = ColorPainter(Color.White.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.55f),
                        0.4f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.5f),
                    ),
                ),
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = ContentPadding, vertical = 12.dp),
        ) {
            GlassIconButton(
                contentDescription = "Voltar",
                onClick = onBackClick,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(21.dp),
                )
            }
            GlassIconButton(
                contentDescription = "Compartilhar trailer",
                onClick = onShareClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 10.dp),
        ) {
            Text(
                text = details.topBadge,
                color = Color.White,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            )
        }
    }
}

@Composable
private fun GlassIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(43.dp)
            .clip(CircleShape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(8.dp)
                .background(Color.Black.copy(alpha = 0.48f)),
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics {
                    this.contentDescription = contentDescription
                },
        ) {
            content()
        }
    }
}

@Composable
private fun ActionButtons(
    details: TrailerDetailsUiModel,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ContentPadding),
    ) {
        DetailActionButton(
            text = if (details.isFavorite) "Favoritado" else "Favoritar",
            iconResource = if (details.isFavorite) {
                R.drawable.figma_heart_selected
            } else {
                R.drawable.figma_heart
            },
            isSelected = details.isFavorite,
            selectedContainerColor = CinematecaColors.FavoriteSelectedSurface,
            selectedBorderColor = CinematecaColors.FavoriteSelectedOutline,
            onClick = onFavoriteClick,
            modifier = Modifier.weight(1f),
        )
        DetailActionButton(
            text = if (details.isWatchlisted) "Na Lista" else "Quero Assistir",
            iconResource = if (details.isWatchlisted) {
                R.drawable.figma_ticket_selected
            } else {
                R.drawable.figma_ticket
            },
            isSelected = details.isWatchlisted,
            selectedContainerColor = CinematecaColors.WatchlistSelectedSurface,
            selectedBorderColor = CinematecaColors.WatchlistSelectedOutline,
            onClick = onWatchlistClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailActionButton(
    text: String,
    iconResource: Int,
    isSelected: Boolean,
    selectedContainerColor: Color,
    selectedBorderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) {
            selectedContainerColor
        } else {
            Color(0xFF3A3A50)
        },
        shape = ActionShape,
        border = BorderStroke(
            1.dp,
            if (isSelected) {
                selectedBorderColor
            } else {
                Color(0xFF55556A)
            },
        ),
        modifier = modifier
            .height(50.dp)
            .semantics {
                selected = isSelected
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize(),
        ) {
            Icon(
                painter = painterResource(iconResource),
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Unspecified,
                modifier = Modifier.size(17.dp),
            )
            Spacer(modifier = Modifier.width(9.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DetailsBody(
    details: TrailerDetailsUiModel,
    onYouTubeClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(19.dp),
        modifier = Modifier.padding(
            start = ContentPadding,
            top = 19.dp,
            end = ContentPadding,
            bottom = 10.dp,
        ),
    ) {
        StatsRow(details)
        TagsSection(tags = details.tags)
        TextSection(
            label = "DESCRIÇÃO DO TRAILER",
            text = details.description,
        )
        PromotionalMaterials(
            videos = details.promotionalVideos,
        )
        Button(
            onClick = onYouTubeClick,
            enabled = details.youtubeVideoId != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                disabledContainerColor = Color.Red.copy(alpha = 0.35f),
            ),
            shape = RoundedCornerShape(19.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(57.dp),
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(19.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Assistir no YouTube",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun StatsRow(details: TrailerDetailsUiModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        StatCard(
            label = "VISUALIZAÇÕES",
            value = details.views,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = "VÍDEOS",
            value = details.videoCount,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = "PUBLICADO",
            value = details.published,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = Color(0xFF13131A).copy(alpha = 0.9f),
        shape = CardShape,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        modifier = modifier.height(96.dp),
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                lineHeight = 15.sp,
                letterSpacing = 0.25.sp,
                maxLines = 1,
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun TagsSection(tags: List<String>) {
    Column {
        SectionLabel(text = "TAGS DO VÍDEO")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 9.dp),
        ) {
            items(
                items = tags,
                key = { it },
            ) { tag ->
                Surface(
                    color = CinematecaColors.Primary.copy(alpha = 0.1f),
                    shape = CircleShape,
                    border = BorderStroke(
                        1.dp,
                        CinematecaColors.Primary.copy(alpha = 0.28f),
                    ),
                ) {
                    Text(
                        text = tag,
                        color = CinematecaColors.Primary,
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 6.dp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun TextSection(
    label: String,
    text: String,
) {
    Column {
        SectionLabel(text = label)
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            lineHeight = 27.sp,
            modifier = Modifier.padding(top = 9.dp),
        )
    }
}

@Composable
private fun PromotionalMaterials(
    videos: List<PromotionalVideoUiModel>,
) {
    Column {
        SectionLabel(text = "MATERIAIS PROMOCIONAIS")
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            videos.forEach { video ->
                PromotionalVideoCard(video = video)
            }
        }
    }
}

@Composable
private fun PromotionalVideoCard(
    video: PromotionalVideoUiModel,
) {
    Surface(
        color = Color(0xFF13131A).copy(alpha = 0.9f),
        shape = CardShape,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(73.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = ColorPainter(Color.White.copy(alpha = 0.05f)),
                    fallback = ColorPainter(Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = video.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = video.subtitle,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.4f),
        fontSize = 10.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
    )
}

@Composable
fun TrailerDetailsError(
    message: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.padding(24.dp),
    ) {
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                onClick = onBackClick,
                color = CinematecaColors.ButtonSurface,
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = "Voltar",
                    color = Color.White,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 12.dp,
                    ),
                )
            }
            Surface(
                onClick = onRetry,
                color = CinematecaColors.Primary,
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = "Tentar novamente",
                    color = Color.White,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 12.dp,
                    ),
                )
            }
        }
    }
}
