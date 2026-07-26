package com.cinemateca.features.trailers.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.cinemateca.features.designsystem.CinematecaColors

private val SkeletonColor = CinematecaColors.SurfaceElevated.copy(alpha = 0.53f)
private val SkeletonBackground = Brush.linearGradient(
    colors = listOf(
        Color(0xFF111118),
        Color(0xFF0D0D18),
    ),
)

@Composable
internal fun TrailerDetailsLoading(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SkeletonBackground)
            .navigationBarsPadding()
            .semantics {
                contentDescription = "Carregando detalhes do trailer"
            },
    ) {
        LoadingHero(onBackClick = onBackClick)
        LoadingTitle()
        LoadingActions()
        HorizontalDivider(color = CinematecaColors.Outline)
        LoadingBody()
    }
}

@Composable
private fun LoadingHero(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(SkeletonColor),
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 19.dp, top = 12.dp),
        ) {
            GlassIconButton(
                contentDescription = "Voltar",
                onClick = onBackClick,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.55f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun LoadingTitle() {
    Column(
        verticalArrangement = Arrangement.spacedBy(9.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 19.dp,
                top = 19.dp,
                end = 19.dp,
                bottom = 9.5.dp,
            ),
    ) {
        SkeletonBlock(
            modifier = Modifier
                .width(255.dp)
                .height(24.dp),
            shape = RoundedCornerShape(8.dp),
        )
        SkeletonBlock(
            modifier = Modifier
                .width(170.dp)
                .height(19.dp),
            shape = RoundedCornerShape(8.dp),
        )
    }
}

@Composable
private fun LoadingActions() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 19.dp,
                top = 14.dp,
                end = 19.dp,
                bottom = 15.dp,
            ),
    ) {
        repeat(2) {
            SkeletonBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
            )
        }
    }
}

@Composable
private fun LoadingBody() {
    Column(
        verticalArrangement = Arrangement.spacedBy(19.dp),
        modifier = Modifier.padding(19.dp),
    ) {
        LoadingStats()
        LoadingTags()
        LoadingPromotionalMaterials()
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(57.dp),
            shape = RoundedCornerShape(19.dp),
        )
    }
}

@Composable
private fun LoadingStats() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        repeat(3) {
            SkeletonBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(76.dp),
                shape = RoundedCornerShape(14.dp),
            )
        }
    }
}

@Composable
private fun LoadingTags() {
    Column(
        verticalArrangement = Arrangement.spacedBy(9.5.dp),
    ) {
        SkeletonBlock(
            modifier = Modifier
                .width(95.dp)
                .height(14.dp),
            shape = RoundedCornerShape(5.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(9.5.dp),
        ) {
            listOf(80.dp, 64.dp, 72.dp, 56.dp).forEach { width ->
                SkeletonBlock(
                    modifier = Modifier
                        .width(width)
                        .height(29.dp),
                    shape = RoundedCornerShape(50),
                )
            }
        }
    }
}

@Composable
private fun LoadingPromotionalMaterials() {
    Column(
        verticalArrangement = Arrangement.spacedBy(9.5.dp),
    ) {
        SkeletonBlock(
            modifier = Modifier
                .width(133.dp)
                .height(14.dp),
            shape = RoundedCornerShape(5.dp),
        )
        repeat(3) {
            LoadingPromotionalCard()
        }
    }
}

@Composable
private fun LoadingPromotionalCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(
                color = CinematecaColors.Surface,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(14.dp),
    ) {
        SkeletonBlock(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(10.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.padding(top = 2.dp),
        ) {
            SkeletonBlock(
                modifier = Modifier
                    .width(150.dp)
                    .height(17.dp),
                shape = RoundedCornerShape(5.dp),
            )
            SkeletonBlock(
                modifier = Modifier
                    .width(87.dp)
                    .height(14.dp),
                shape = RoundedCornerShape(5.dp),
            )
        }
    }
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier,
    shape: Shape,
) {
    Box(
        modifier = modifier.background(
            color = SkeletonColor,
            shape = shape,
        ),
    )
}
