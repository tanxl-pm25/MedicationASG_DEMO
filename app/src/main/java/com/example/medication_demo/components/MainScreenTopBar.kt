package com.example.medication_demo.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreenTopBar(
    title: String,
    rightIcon: ImageVector,
    rightIconDescription: String,
    onRightIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleStartPadding: Dp = 0.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 18.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        MainScreenActionIcon(
            icon = rightIcon,
            contentDescription = rightIconDescription,
            onClick = onRightIconClick
        )
    }
}