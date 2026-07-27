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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.designsystem.childTestId
import com.cinemateca.features.designsystem.testId

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
    testId: String? = null,
) {
    val loadingDescription = stringResource(R.string.details_loading)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SkeletonBackground)
            .navigationBarsPadding()
            .testId(testId)
            .semantics {
                contentDescription = loadingDescription
            },
    ) {
        LoadingHero(
            onBackClick = onBackClick,
            testId = testId.childTestId("hero"),
        )
        LoadingTitle(testId = testId.childTestId("title"))
        LoadingActions(testId = testId.childTestId("actions"))
        HorizontalDivider(color = CinematecaColors.Outline)
        LoadingBody(testId = testId.childTestId("body"))
    }
}

@Composable
private fun LoadingHero(
    onBackClick: () -> Unit,
    testId: String? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(SkeletonColor)
            .testId(testId),
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 19.dp, top = 12.dp),
        ) {
            GlassIconButton(
                contentDescription = stringResource(R.string.action_back),
                onClick = onBackClick,
                testId = testId.childTestId("back"),
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
private fun LoadingTitle(
    testId: String? = null,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(9.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 19.dp,
                top = 19.dp,
                end = 19.dp,
                bottom = 9.5.dp,
            )
            .testId(testId),
    ) {
        SkeletonBlock(
            modifier = Modifier
                .width(255.dp)
                .height(24.dp),
            shape = RoundedCornerShape(8.dp),
            testId = testId.childTestId("line.0"),
        )
        SkeletonBlock(
            modifier = Modifier
                .width(170.dp)
                .height(19.dp),
            shape = RoundedCornerShape(8.dp),
            testId = testId.childTestId("line.1"),
        )
    }
}

@Composable
private fun LoadingActions(
    testId: String? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 19.dp,
                top = 14.dp,
                end = 19.dp,
                bottom = 15.dp,
            )
            .testId(testId),
    ) {
        repeat(2) { index ->
            SkeletonBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                testId = testId.childTestId("button.$index"),
            )
        }
    }
}

@Composable
private fun LoadingBody(
    testId: String? = null,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(19.dp),
        modifier = Modifier
            .padding(19.dp)
            .testId(testId),
    ) {
        LoadingStats(testId = testId.childTestId("stats"))
        LoadingTags(testId = testId.childTestId("tags"))
        LoadingPromotionalMaterials(
            testId = testId.childTestId("promotional"),
        )
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(57.dp),
            shape = RoundedCornerShape(19.dp),
            testId = testId.childTestId("youtube"),
        )
    }
}

@Composable
private fun LoadingStats(
    testId: String? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testId(testId),
    ) {
        repeat(3) { index ->
            SkeletonBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(76.dp),
                shape = RoundedCornerShape(14.dp),
                testId = testId.childTestId("card.$index"),
            )
        }
    }
}

@Composable
private fun LoadingTags(
    testId: String? = null,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(9.5.dp),
        modifier = Modifier.testId(testId),
    ) {
        SkeletonBlock(
            modifier = Modifier
                .width(95.dp)
                .height(14.dp),
            shape = RoundedCornerShape(5.dp),
            testId = testId.childTestId("label"),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(9.5.dp),
        ) {
            listOf(80.dp, 64.dp, 72.dp, 56.dp).forEachIndexed { index, width ->
                SkeletonBlock(
                    modifier = Modifier
                        .width(width)
                        .height(29.dp),
                    shape = RoundedCornerShape(50),
                    testId = testId.childTestId("tag.$index"),
                )
            }
        }
    }
}

@Composable
private fun LoadingPromotionalMaterials(
    testId: String? = null,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(9.5.dp),
        modifier = Modifier.testId(testId),
    ) {
        SkeletonBlock(
            modifier = Modifier
                .width(133.dp)
                .height(14.dp),
            shape = RoundedCornerShape(5.dp),
            testId = testId.childTestId("label"),
        )
        repeat(3) { index ->
            LoadingPromotionalCard(
                testId = testId.childTestId("card.$index"),
            )
        }
    }
}

@Composable
private fun LoadingPromotionalCard(
    testId: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(
                color = CinematecaColors.Surface,
                shape = RoundedCornerShape(14.dp),
            )
            .padding(14.dp)
            .testId(testId),
    ) {
        SkeletonBlock(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(10.dp),
            testId = testId.childTestId("thumbnail"),
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
                testId = testId.childTestId("line.0"),
            )
            SkeletonBlock(
                modifier = Modifier
                    .width(87.dp)
                    .height(14.dp),
                shape = RoundedCornerShape(5.dp),
                testId = testId.childTestId("line.1"),
            )
        }
    }
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier,
    shape: Shape,
    testId: String? = null,
) {
    Box(
        modifier = modifier.background(
            color = SkeletonColor,
            shape = shape,
        ).testId(testId),
    )
}
