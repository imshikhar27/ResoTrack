package com.example.task.feature_home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color

@Composable
fun CommunityCard(
    name: String,
    description: String,
    image: String,
    cid: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onShowCid: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Track menu expansion

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image of the community
            AsyncImage(
                model = image,
                contentDescription = "Community Image",
                modifier = Modifier
                    .size(80.dp)
                    .weight(1f),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Column for name and description
            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Three-dot menu icon
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options Menu"
                )
            }
        }

        // Dropdown menu attached to the IconButton
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "Delete") },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
            DropdownMenuItem(
                text = { Text(text = "Show CID") },
                onClick = {
                    expanded = false
                    onShowCid()
                }
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun CommunityCardPreview() {
//    CommunityCard(
//        name = "Tech Enthusiasts",
//        description = "A community for those who love technology and innovation.",
//        image = "https://example.com/community_image.jpg" // Sample image URL
//    )
//}
