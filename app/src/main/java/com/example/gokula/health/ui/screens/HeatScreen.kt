package com.example.gokula.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gokula.health.data.HeatCycle
import com.example.gokula.health.ui.components.CowPicker
import com.example.gokula.health.viewmodel.CowViewModel
import com.example.gokula.health.viewmodel.GokulaViewModelFactory
import com.example.gokula.health.viewmodel.HeatViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatScreen() {

    val ctx = LocalContext.current
    val app = ctx.applicationContext as android.app.Application

    val cowVm: CowViewModel = viewModel(factory = GokulaViewModelFactory(app))
    val heatVm: HeatViewModel = viewModel(factory = GokulaViewModelFactory(app))

    val cows by cowVm.cows.collectAsState()
    val allCycles by heatVm.cycles.collectAsState()

    var selected by remember { mutableStateOf(cows.firstOrNull()) }

    LaunchedEffect(cows) {
        if (selected == null) selected = cows.firstOrNull()
    }

    // ✅ FILTER BY SELECTED COW
    val cycles = allCycles.filter { it.cowId == selected?.id }

    var dateText by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
    }

    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Heat Cycle") },
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

            // 🐄 Cow selector
            CowPicker(cows, selected) { selected = it }

            Spacer(Modifier.height(12.dp))

            // 📦 Input card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Last heat date (yyyy-MM-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // ✅ FIXED BUTTON
                    Button(
                        onClick = {
                            val cow = selected ?: return@Button

                            val time = runCatching {
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .parse(dateText)?.time
                            }.getOrNull()

                            if (time != null) {
                                heatVm.add(
                                    HeatCycle(
                                        cowId = cow.id,
                                        lastHeatDate = time
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))
                    ) {
                        Text("Save Heat Date", color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("History", style = MaterialTheme.typography.titleMedium)

            LazyColumn {

                items(cycles) { h ->

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
                                Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50)
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text("Last: ${fmt.format(Date(h.lastHeatDate))}")
                                Text("Next: ${fmt.format(Date(h.nextExpected))}")
                            }

                            TextButton(onClick = { heatVm.delete(h) }) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}