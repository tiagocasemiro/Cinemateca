package com.cinemateca.features.trailers.details

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cinemateca.features.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun TrailerDetailsDestination(
    onNavigateBack: () -> Unit,
    viewModel: TrailerDetailsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val youtubeUrl = uiState.details
        ?.youtubeVideoId
        ?.let { videoId -> "https://www.youtube.com/watch?v=$videoId" }

    TrailerDetailsScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBackClick = onNavigateBack,
        onShareClick = {
            youtubeUrl?.let { url ->
                val intent = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${uiState.details?.title.orEmpty()}\n$url",
                        )
                    },
                    context.getString(R.string.action_share_trailer),
                )
                context.startActivity(intent)
            }
        },
        onYouTubeClick = {
            youtubeUrl?.let { url ->
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                )
            }
        },
        onPromotionalVideoClick = { videoId ->
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=$videoId"),
                ),
            )
        },
    )
}
