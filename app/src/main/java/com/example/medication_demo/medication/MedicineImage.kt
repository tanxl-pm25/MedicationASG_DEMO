package com.example.medication_demo.medication

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.medication_demo.R

@Composable
fun MedicineImage(
    presetImageRes: Int?,
    galleryImageUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    imageSize: Dp = 34.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            galleryImageUri != null -> {
                AsyncImage(
                    model = galleryImageUri,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            presetImageRes != null -> {
                Image(
                    painter = painterResource(presetImageRes),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(imageSize)
                )
            }

            else -> {
                Icon(
                    painter = painterResource(
                        R.drawable.pill_24dp_1f1f1f_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(imageSize)
                )
            }
        }
    }
}