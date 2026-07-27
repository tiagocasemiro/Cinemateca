package com.cinemateca.features.trailers.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import com.cinemateca.features.designsystem.childTestId
import com.cinemateca.features.designsystem.testId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.designsystem.CinematecaTheme
import com.cinemateca.features.trailers.home.HomeSortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeSortBottomSheet(
    selectedOption: HomeSortOption,
    onOptionSelected: (HomeSortOption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    testId: String? = null,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CinematecaColors.SurfaceElevated,
        contentColor = CinematecaColors.OnBackground,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        shape = RoundedCornerShape(
            topStart = 19.dp,
            topEnd = 19.dp,
        ),
        tonalElevation = 0.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(
                        top = 14.dp,
                        bottom = 10.dp,
                    )
                    .size(
                        width = 48.dp,
                        height = 5.dp,
                    )
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape,
                    ),
            )
        },
        modifier = modifier.testId(testId),
    ) {
        Text(
            text = stringResource(R.string.sort_title),
            color = CinematecaColors.SecondaryText,
            fontSize = 17.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
        )

        HomeSortOption.entries.forEach { option ->
            SortOptionRow(
                option = option,
                isSelected = option == selectedOption,
                onClick = {
                    onOptionSelected(option)
                },
                testId = testId.childTestId(
                    "option.${option.name.lowercase()}",
                ),
            )
        }

        Spacer(
            modifier = Modifier
                .height(38.dp)
                .navigationBarsPadding(),
        )
    }
}

@Composable
private fun SortOptionRow(
    option: HomeSortOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testId: String? = null,
) {
    val optionLabel = stringResource(option.labelResource)
    val optionDescription = if (isSelected) {
        stringResource(R.string.option_selected, optionLabel)
    } else {
        optionLabel
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .height(57.dp)
            .testId(testId)
            .clickable(
                role = Role.RadioButton,
                onClick = onClick,
            )
            .semantics {
                role = Role.RadioButton
                contentDescription = optionDescription
            }
            .padding(horizontal = 24.dp),
    ) {
        Text(
            text = optionLabel,
            color = if (isSelected) {
                CinematecaColors.Primary
            } else {
                Color.White.copy(alpha = 0.8f)
            },
            fontSize = 17.sp,
            lineHeight = 24.sp,
            fontWeight = if (isSelected) {
                FontWeight.Medium
            } else {
                FontWeight.Normal
            },
        )

        if (isSelected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(19.dp)
                    .background(
                        color = CinematecaColors.Primary,
                        shape = CircleShape,
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

@Preview(
    name = "Home - Ordenação",
    showBackground = true,
    widthDp = 378,
    heightDp = 300,
)
@Composable
private fun HomeSortBottomSheetPreview() {
    CinematecaTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CinematecaColors.SurfaceElevated)
                .padding(top = 14.dp),
        ) {
            Text(
                text = stringResource(R.string.sort_title),
                color = CinematecaColors.SecondaryText,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            HomeSortOption.entries.forEach { option ->
                SortOptionRow(
                    option = option,
                    isSelected = option == HomeSortOption.MostRecent,
                    onClick = {},
                )
            }
        }
    }
}
