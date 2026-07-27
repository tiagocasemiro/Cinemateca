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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.designsystem.UiText
import com.cinemateca.features.designsystem.asString
import com.cinemateca.features.designsystem.childTestId
import com.cinemateca.features.designsystem.testId
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
    testId: String? = null,
) {
    val searchDescription = stringResource(R.string.search_movies)
    val clearSearchDescription = stringResource(R.string.clear_search)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 19.dp,
                top = 5.dp,
                end = 19.dp,
                bottom = 14.dp,
            )
            .testId(testId),
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
                    text = stringResource(R.string.app_name),
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
                        contentDescription = pluralStringResource(
                            R.plurals.favorite_count,
                            favoriteCount,
                            favoriteCount,
                        ),
                        testId = testId.childTestId("favorite_count"),
                    )
                }
                if (watchlistCount > 0) {
                    SelectionCountBadge(
                        count = watchlistCount,
                        drawableResource = R.drawable.figma_ticket_selected,
                        color = CinematecaColors.Primary,
                        contentDescription = pluralStringResource(
                            R.plurals.watchlist_count,
                            watchlistCount,
                            watchlistCount,
                        ),
                        testId = testId.childTestId("watchlist_count"),
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
                        .testId(testId.childTestId("search"))
                        .semantics {
                            contentDescription = searchDescription
                        },
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = stringResource(
                                        R.string.search_movies_hint,
                                    ),
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
                        contentDescription = clearSearchDescription,
                        modifier = Modifier
                            .size(14.dp)
                            .testId(testId.childTestId("clear_search"))
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
    testId: String? = null,
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = PillShape,
        border = BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.2f),
        ),
        modifier = modifier
            .testId(testId)
            .semantics {
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
    testId: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CinematecaColors.Background)
            .testId(testId),
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
                val optionLabel = stringResource(option.labelResource)
                FilterChip(
                    text = optionLabel,
                    selected = option == selectedFilter,
                    onClick = {
                        onFilterClick(option)
                    },
                    testId = testId.childTestId(
                        "filter.${option.name.lowercase()}",
                    ),
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
                text = movieCount?.let { count ->
                    pluralStringResource(
                        R.plurals.movie_count,
                        count,
                        count,
                    )
                } ?: stringResource(R.string.not_available_symbol),
                color = CinematecaColors.TertiaryText,
                fontSize = 11.sp,
                lineHeight = 17.sp,
            )

            val sortDescription = stringResource(
                R.string.sort_movies,
                sortOptionLabel,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .testId(testId.childTestId("sort"))
                    .clickable(
                        role = Role.Button,
                        onClick = onSortClick,
                    )
                    .semantics {
                        contentDescription = sortDescription
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
    testId: String? = null,
) {
    val loadingDescription = stringResource(R.string.loading_movies)

    LazyColumn(
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = loadingDescription
            }
            .testId(testId),
    ) {
        items(
            count = 4,
            key = { index -> index },
        ) { index ->
            SkeletonMovieCard(
                testId = testId.childTestId("movie.$index"),
            )
        }
    }
}

@Composable
private fun SkeletonMovieCard(
    modifier: Modifier = Modifier,
    testId: String? = null,
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
            .testId(testId)
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
                    testId = testId.childTestId("line.0"),
                )
                SkeletonLine(
                    widthFraction = 0.5f,
                    testId = testId.childTestId("line.1"),
                )
                SkeletonLine(
                    widthFraction = 0.67f,
                    testId = testId.childTestId("line.2"),
                )
                Spacer(modifier = Modifier.height(0.75.dp))
                SkeletonLine(
                    widthFraction = 1f,
                    testId = testId.childTestId("line.3"),
                )
                SkeletonLine(
                    widthFraction = 0.8f,
                    testId = testId.childTestId("line.4"),
                )
            }
        }
    }
}

@Composable
private fun SkeletonLine(
    widthFraction: Float,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 14.dp,
    testId: String? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .testId(testId),
    )
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testId: String? = null,
) {
    val chipDescription = stringResource(
        if (selected) {
            R.string.filter_selected
        } else {
            R.string.filter_movies_by
        },
        text,
    )

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
            .testId(testId)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = chipDescription
                role = Role.Button
                this.selected = selected
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
    testId: String? = null,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 14.dp,
            top = 14.dp,
            end = 14.dp,
            bottom = 38.dp,
        ),
        modifier = modifier
            .fillMaxSize()
            .testId(testId),
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
                testId = testId.childTestId("movie.${trailer.id}"),
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
    testId: String? = null,
) {
    val favoriteLabel = stringResource(R.string.action_favorite)
    val watchlistLabel = stringResource(R.string.action_watchlist)

    Surface(
        onClick = onClick,
        color = CinematecaColors.Surface,
        shape = CardShape,
        border = BorderStroke(1.dp, CinematecaColors.Outline),
        modifier = modifier
            .fillMaxWidth()
            .height(197.dp)
            .testId(testId),
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
                    contentDescription = stringResource(
                        R.string.movie_poster_description,
                        trailer.title,
                    ),
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
                        testId = testId.childTestId("genres"),
                    )
                    MovieMetadata(
                        drawableResource = R.drawable.figma_calendar,
                        text = trailer.published,
                        color = Color.White.copy(alpha = 0.4f),
                        testId = testId.childTestId("published"),
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                MovieActionButton(
                    text = favoriteLabel,
                    drawableResource = if (trailer.isFavorite) {
                        R.drawable.figma_heart_selected
                    } else {
                        R.drawable.figma_heart
                    },
                    isSelected = trailer.isFavorite,
                    selectedContainerColor = CinematecaColors.FavoriteSelectedSurface,
                    selectedBorderColor = CinematecaColors.FavoriteSelectedOutline,
                    onClick = onFavoriteClick,
                    modifier = Modifier.weight(1f),
                    testId = testId.childTestId("favorite"),
                )
                MovieActionButton(
                    text = watchlistLabel,
                    drawableResource = if (trailer.isWatchlisted) {
                        R.drawable.figma_ticket_selected
                    } else {
                        R.drawable.figma_ticket
                    },
                    isSelected = trailer.isWatchlisted,
                    selectedContainerColor = CinematecaColors.WatchlistSelectedSurface,
                    selectedBorderColor = CinematecaColors.WatchlistSelectedOutline,
                    onClick = onWatchClick,
                    modifier = Modifier.weight(1f),
                    testId = testId.childTestId("watchlist"),
                )
            }
        }
    }
}

@Composable
private fun MovieMetadata(
    drawableResource: Int,
    text: UiText,
    color: Color,
    modifier: Modifier = Modifier,
    testId: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .testId(testId),
    ) {
        FigmaIcon(
            drawableResource = drawableResource,
            contentDescription = null,
            modifier = Modifier.size(11.dp),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text.asString(),
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
    selectedContainerColor: Color,
    selectedBorderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testId: String? = null,
) {
    val selectedDescription = stringResource(R.string.action_selected, text)

    Surface(
        onClick = onClick,
        color = if (isSelected) {
            selectedContainerColor
        } else {
            CinematecaColors.ActionButtonSurface
        },
        shape = PillShape,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                selectedBorderColor
            } else {
                CinematecaColors.ActionButtonOutline
            },
        ),
        modifier = modifier
            .height(36.dp)
            .testId(testId)
            .semantics {
                selected = isSelected
                contentDescription = if (isSelected) {
                    selectedDescription
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
            Icon(
                painter = painterResource(drawableResource),
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Unspecified,
                modifier = Modifier.size(12.dp),
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = text,
                color = if (isSelected) {
                    Color.White
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
    testId: String? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(24.dp)
            .testId(testId),
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
            modifier = Modifier.testId(testId.childTestId("retry")),
        ) {
            Text(text = stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun FigmaIcon(
    drawableResource: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    testId: String? = null,
) {
    Image(
        painter = painterResource(drawableResource),
        contentDescription = contentDescription,
        contentScale = ContentScale.FillBounds,
        modifier = modifier.testId(testId),
    )
}
