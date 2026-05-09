package com.example.gokula.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gokula.health.data.Vaccination
import com.example.gokula.health.ui.components.CowPicker
import com.example.gokula.health.viewmodel.CowViewModel
import com.example.gokula.health.viewmodel.GokulaViewModelFactory
import com.example.gokula.health.viewmodel.VaccinationViewModel
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen() {

    val ctx = LocalContext.current
    val app = ctx.applicationContext as android.app.Application

    val cowVm: CowViewModel = viewModel(factory = GokulaViewModelFactory(app))
    val vacVm: VaccinationViewModel = viewModel(factory = GokulaViewModelFactory(app))

    val cows by cowVm.cows.collectAsState()

    var selected by remember { mutableStateOf(cows.firstOrNull()) }
    LaunchedEffect(cows) { if (selected == null) selected = cows.firstOrNull() }

    // ✅ FIX: filter by selected cow
    val items by (selected?.let { vacVm.getByCow(it.id) } ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    var vaccine by remember { mutableStateOf("") }
    var dateText by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(Date(System.currentTimeMillis() + 60_000))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaccination") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF1F8E9))
                .padding(16.dp)
        ) {

            CowPicker(cows, selected) { selected = it }

            Spacer(Modifier.height(12.dp))

            // 🔷 INPUT CARD
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = vaccine,
                        onValueChange = { vaccine = it },
                        label = { Text("Vaccine name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Schedule time (yyyy-MM-dd HH:mm)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val cow = selected ?: return@Button

                            val time = runCatching {
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                    .parse(dateText)?.time
                            }.getOrNull() ?: return@Button

                            if (vaccine.isNotBlank()) {

                                vacVm.schedule(
                                    Vaccination(
                                        cowId = cow.id,
                                        vaccineName = vaccine,
                                        scheduledAt = time
                                    ),
                                    ctx   // ✅ VERY IMPORTANT FIX
                                )

                                vaccine = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))
                    ) {
                        Text("Schedule Reminder", color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Upcoming", style = MaterialTheme.typography.titleMedium)

            LazyColumn {

                // 🔥 Latest first
                items(items.sortedByDescending { it.scheduledAt }) { v ->

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Filled.Vaccines,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50)
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(v.vaccineName)

                                Text(
                                    SimpleDateFormat(
                                        "dd MMM yyyy HH:mm",
                                        Locale.getDefault()
                                    ).format(Date(v.scheduledAt))
                                )
                            }

                            Row {
                                if (!v.done) {
                                    TextButton(onClick = { vacVm.markDone(v) }) {
                                        Text("Done")
                                    }
                                }
                                TextButton(onClick = { vacVm.delete(v) }) {
                                    Text("Delete", color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}