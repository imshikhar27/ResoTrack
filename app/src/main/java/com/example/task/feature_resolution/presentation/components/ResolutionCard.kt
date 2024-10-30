package com.example.task.feature_resolution.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.task.feature_resolution.domain.ResolutionRetrieve

@Composable
fun ResolutionCard(resolution: ResolutionRetrieve) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "UID: ${resolution.uid}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            // Resolution Text
            Text(
                text = resolution.resolutionText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Display Image if available
            if (resolution.images.isNotEmpty()) {
                AsyncImage(
                    model = resolution.images,
                    contentDescription = "Resolution Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Resolution Rating Calculation
            val rating = if (resolution.peopleApproved > 0) {
                resolution.score.toDouble() / resolution.peopleApproved
            } else {
                0.0
            }

            // Display Rating
            Text(
                text = "Rating: %.2f".format(rating),
                style = MaterialTheme.typography.bodyLarge, // Updated for Material 3
                color = MaterialTheme.colorScheme.primary // Updated for Material 3
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewResolutionCard() {
    val sampleResolution = ResolutionRetrieve(
        rid = "sampleRid",
        uid = "sampleUid",
        resolutionText = "This is a sample resolution text for preview purposes.",
        images = "https://example.com/sample_image.jpg",
        score = 10,
        peopleApproved = 2
    )

    ResolutionCard(resolution = sampleResolution)
}
