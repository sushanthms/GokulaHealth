package com.example.gokula.health.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gokula.health.GokulaApp
import com.example.gokula.health.data.MilkEntry
import com.example.gokula.health.ui.components.CowPicker
import com.example.gokula.health.viewmodel.CowViewModel
import com.example.gokula.health.viewmodel.GokulaViewModelFactory
import com.example.gokula.health.viewmodel.MilkViewModel
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilkScreen() {

    val ctx = LocalContext.current
    val app = ctx.applicationContext as GokulaApp

    val cowVm: CowViewModel = viewModel(factory = GokulaViewModelFactory(app))
    val milkVm: MilkViewModel = viewModel(factory = GokulaViewModelFactory(app))

    val cows by cowVm.cows.collectAsState()
    var selected by remember { mutableStateOf(cows.firstOrNull()) }

    LaunchedEffect(cows) { if (selected == null) selected = cows.firstOrNull() }

    val entries by (selected?.let { milkVm.entries(it.id) } ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    val avg by (selected?.let { milkVm.monthlyAverage(it.id) } ?: flowOf(null))
        .collectAsState(initial = null)

    var morning by remember { mutableStateOf("") }
    var evening by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Milk Diary")
                        Text(
                            "User: ${app.currentFarmerName}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
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

            // 🔷 Average Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.WaterDrop, null, tint = Color(0xFF4CAF50))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Avg: ${avg?.let { "%.2f L/day".format(it) } ?: "—"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 🔷 INPUT SECTION CARD
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Button(
                        onClick = {
                            val c = selectedDate
                            DatePickerDialog(
                                ctx,
                                { _, y, m, d -> c.set(y, m, d) },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))
                    ) {
                        Text("Select Date", color = Color.White)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text("Selected: ${dateFormat.format(selectedDate.time)}")

                    Spacer(Modifier.height(12.dp))

                    Row {
                        OutlinedTextField(
                            value = morning,
                            onValueChange = {
                                // allow only numbers + decimal
                                if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                    morning = it
                                }
                            },
                            label = { Text("Morning (L)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )

                        Spacer(Modifier.width(8.dp))

                        OutlinedTextField(
                            value = evening,
                            onValueChange = {
                                if (it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                    evening = it
                                }
                            },
                            label = { Text("Evening (L)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val cow = selected ?: return@Button
                            milkVm.add(
                                MilkEntry(
                                    cowId = cow.id,
                                    date = selectedDate.timeInMillis,

                                    // ✅ send -1 if empty (means "no change")
                                    morningLitres = if (morning.isBlank()) -1.0 else morning.toDouble(),
                                    eveningLitres = if (evening.isBlank()) -1.0 else evening.toDouble()
                                )
                            )
                            morning = ""
                            evening = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))
                    ) {
                        Text("Save Milk", color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Entries", style = MaterialTheme.typography.titleMedium)

            LazyColumn {

                // 🔥 SORT: latest first
                items(entries.sortedByDescending { it.date }) { e ->

                    val formattedDate = dateFormat.format(Date(e.date))

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

                            Column(Modifier.weight(1f)) {

                                // 📅 DATE (NEW)
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Spacer(Modifier.height(4.dp))

                                // 🥛 TOTAL
                                Text(
                                    "${"%.2f".format(e.totalLitres)} L",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                // 🔍 DETAILS
                                Text(
                                    "M ${e.morningLitres} + E ${e.eveningLitres}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            TextButton(onClick = { milkVm.delete(e) }) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}