package com.example.gokula.health.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gokula.health.data.Cow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CowPicker(cows: List<Cow>, selected: Cow?, onSelect: (Cow) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selected?.let { "${it.name} (${it.earTagId})" } ?: "Select cow",
            onValueChange = {},
            label = { Text("Cow") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            cows.forEach { cow ->
                DropdownMenuItem(
                    text = { Text("${cow.name} — ${cow.earTagId}") },
                    onClick = { onSelect(cow); expanded = false }
                )
            }
        }
    }
}
