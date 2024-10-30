package com.example.task.feature_resolution.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.task.feature_resolution.presentation.ResolutionEvent
import com.example.task.feature_resolution.presentation.ResolutionViewModel
import com.example.task.objects.ResolutionFireStoreObject

@Composable
fun UpdateResolution(
    cid: String,
    uid: String,
    resolutionViewModel: ResolutionViewModel,
    navController: NavController
) {
    // Flow to get resolutions
    val resolutionsFlow =
        ResolutionFireStoreObject.resolutionFireStore.getResolution(cid, listOf(uid))
    val resolutions by resolutionsFlow.collectAsState(initial = emptyList())

    // State for dropdown and image selection
    var selectedResolutionId by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Update Resolution", style = MaterialTheme.typography.titleLarge)

        // Dropdown menu for resolutions
        Box {
            Text(
                text = selectedResolutionId ?: "Select a Resolution",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                resolutions.forEach { resolution ->
                    DropdownMenuItem(
                        text = { Text(resolution.resolutionText) },
                        onClick = {
                            selectedResolutionId = resolution.rid
                            expanded = false
                        }
                    )
                }
            }
        }

        // Image selection composable
        Button(onClick = {
            // Open gallery to select image
            // Use a launcher or activity result for image selection
            // selectedImageUri = picked image Uri
        }) {
            Text("Select Image")
        }

        // Display selected image if available
        selectedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )
        }

        // Button to update resolution with selected image
        Button(
            onClick = {
                selectedResolutionId?.let { rid ->
                    selectedImageUri?.let { uri ->
                        resolutionViewModel.event(ResolutionEvent.ResolutionUpdateImage(cid, uid, rid, uri))
                        navController.navigate("CommunityScreen")
                    }
                }
            },
            enabled = selectedResolutionId != null && selectedImageUri != null
        ) {
            Text("Update Resolution")
        }
    }
}
