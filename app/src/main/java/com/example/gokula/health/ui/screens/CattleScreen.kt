package com.example.gokula.health.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gokula.health.data.Cow
import com.example.gokula.health.viewmodel.CowViewModel
import com.example.gokula.health.viewmodel.GokulaViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CattleScreen() {

    val ctx = LocalContext.current
    val vm: CowViewModel = viewModel(
        factory = GokulaViewModelFactory(ctx.applicationContext as android.app.Application)
    )

    val cows by vm.cows.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cattle Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAdd = true },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Add Cow") },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF1F8E9))
                .padding(16.dp)
        ) {

            LazyColumn {

                items(cows) { cow ->

                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // 🐄 IMAGE / ICON
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!cow.photoUri.isNullOrEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(cow.photoUri),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.Pets,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {

                                Text(
                                    cow.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF2E7D32)
                                )

                                Spacer(Modifier.height(4.dp))

                                Text("Tag: ${cow.earTagId}", color = Color.Gray)
                                Text("Breed: ${cow.breed}", color = Color.Gray)

                                Text(
                                    "DOB: ${
                                        SimpleDateFormat(
                                            "dd MMM yyyy",
                                            Locale.getDefault()
                                        ).format(Date(cow.dateOfBirth))
                                    }",
                                    color = Color.Gray
                                )
                            }

                            IconButton(onClick = { vm.delete(cow) }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddCowDialog(
            onDismiss = { showAdd = false },
            onSave = {
                vm.add(it)
                showAdd = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCowDialog(
    onDismiss: () -> Unit,
    onSave: (Cow) -> Unit
) {

    val ctx = LocalContext.current

    var tag by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var dobText by remember { mutableStateOf("2022-01-01") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var showPicker by remember { mutableStateOf(false) }

    val photoFile = remember {
        File(ctx.cacheDir, "cow_${System.currentTimeMillis()}.jpg")
    }

    val cameraUri: Uri = FileProvider.getUriForFile(
        ctx,
        ctx.packageName + ".provider",
        photoFile
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) photoUri = cameraUri.toString()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { photoUri = it.toString() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFFFFFFFF),

        title = {
            Text(
                "Add Cow",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF2E7D32)
            )
        },

        text = {
            Column {

                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Ear Tag ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Breed") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = dobText,
                    onValueChange = { dobText = it },
                    label = { Text("DOB (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(14.dp))

                // 📸 PHOTO BUTTON
                Button(
                    onClick = { showPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Select Photo", color = Color.White)
                }

                // 📷 IMAGE PREVIEW
                if (photoUri != null) {
                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },

        confirmButton = {
            TextButton(onClick = {

                val dob = runCatching {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(dobText)?.time
                }.getOrNull() ?: System.currentTimeMillis()

                if (tag.isNotBlank() && name.isNotBlank()) {
                    onSave(
                        Cow(
                            earTagId = tag,
                            name = name,
                            breed = breed,
                            dateOfBirth = dob,
                            photoUri = photoUri
                        )
                    )
                }

            }) {
                Text("Save", color = Color(0xFF2E7D32))
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )

    // 📷 PICKER DIALOG
    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("Select Image") },
            text = {
                Column {

                    TextButton(onClick = {
                        showPicker = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("Upload from Gallery")
                    }

                    TextButton(onClick = {
                        showPicker = false
                        try {
                            cameraLauncher.launch(cameraUri)
                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Camera not available", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Take Photo (Camera)")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}