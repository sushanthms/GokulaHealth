package com.example.gokula.health.ui.screens

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.gokula.health.ui.components.CowPicker
import com.example.gokula.health.viewmodel.CowViewModel
import com.example.gokula.health.viewmodel.GokulaViewModelFactory
import com.example.gokula.health.viewmodel.MilkViewModel
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YieldScreen() {

    val ctx = LocalContext.current
    val app = ctx.applicationContext as android.app.Application

    val cowVm: CowViewModel = viewModel(factory = GokulaViewModelFactory(app))
    val milkVm: MilkViewModel = viewModel(factory = GokulaViewModelFactory(app))

    val cows by cowVm.cows.collectAsState()
    var selected by remember { mutableStateOf(cows.firstOrNull()) }

    LaunchedEffect(cows) {
        if (selected == null) selected = cows.firstOrNull()
    }

    val entries by (selected?.let { milkVm.entries(it.id) } ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yield Analysis") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                    titleContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color(0xFFF1F8E9))
                .padding(16.dp)
        ) {

            CowPicker(cows, selected) { selected = it }

            Spacer(Modifier.height(12.dp))

            // 🐄 Selected Cow
            selected?.let {
                Text(
                    text = "Cow: ${it.name}",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(Modifier.height(12.dp))

            // 📊 Aggregate data
            val byDay = entries.groupBy { it.date }
                .mapValues { it.value.sumOf { e -> e.totalLitres } }
                .toSortedMap()
                .entries.toList()
                .takeLast(30)

            val avg = if (byDay.isNotEmpty()) byDay.map { it.value }.average() else 0.0

            // 📊 Average Card
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Average Yield")
                    Text(
                        "%.2f L/day".format(avg),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            val chartEntries = byDay.mapIndexed { i, e ->
                Entry(i.toFloat(), e.value.toFloat())
            }

            val labels = byDay.map {
                dateFormat.format(Date(it.key))
            }

            // 📈 Chart
            AndroidView(
                factory = { context ->
                    LineChart(context).apply {
                        description.isEnabled = false
                        axisRight.isEnabled = false
                    }
                },
                update = { chart ->

                    val dataSet = LineDataSet(chartEntries, "Milk Yield").apply {
                        color = Color.GREEN
                        valueTextColor = Color.BLACK
                        lineWidth = 2f
                        circleRadius = 4f
                    }

                    chart.data = LineData(dataSet)

                    // ✅ FIX X-AXIS LABELS (DATES)
                    chart.xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        setDrawGridLines(false)

                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val i = value.toInt()
                                return labels.getOrNull(i) ?: ""
                            }
                        }
                    }

                    chart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            if (chartEntries.isEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("No data yet")
            }
        }
    }
}