package com.cinemateca.features.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.CinematecaColors

private val OfflineShape = RoundedCornerShape(50)

@Composable
internal fun OfflineContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    topContent: @Composable BoxScope.() -> Unit = {},
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val topPadding = if (maxHeight >= 360.dp) 76.dp else 0.dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 29.dp,
                    top = topPadding,
                    end = 29.dp,
                    bottom = 24.dp,
                ),
        ) {
            Surface(
                color = CinematecaColors.SurfaceElevated,
                shape = OfflineShape,
                modifier = Modifier
                    .size(76.dp)
                    .semantics {
                        contentDescription = "Sem conexão com a internet"
                    },
            ) {
                Image(
                    painter = painterResource(R.drawable.figma_wifi_off),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                )
            }

            Spacer(modifier = Modifier.height(19.dp))

            Text(
                text = "Sem conexão",
                color = CinematecaColors.OnBackground,
                fontSize = 19.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Verifique sua conexão e tente novamente.",
                color = CinematecaColors.SecondaryText,
                fontSize = 17.sp,
                lineHeight = 24.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                onClick = onRetry,
                color = CinematecaColors.Primary,
                shape = OfflineShape,
                modifier = Modifier.height(44.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                ) {
                    Text(
                        text = "Tentar novamente",
                        color = Color.White,
                        fontSize = 17.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        topContent()
    }
}
