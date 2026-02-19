package com.example.collegeschedule.ui.schedule

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.api.RetrofitInstance
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.utils.getWeekDateRange

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ScheduleScreen() {
    var groups by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Загрузка списка групп при запуске
    LaunchedEffect(Unit) {
        try {
            groups = RetrofitInstance.api.getGroups()
            if (groups.isNotEmpty()) selectedGroup = groups[0]
        } catch (e: Exception) { /* обработка ошибки */ }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Выбор группы:", style = MaterialTheme.typography.labelMedium)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedGroup,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                groups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            selectedGroup = group
                            expanded = false
                        }
                    )
                }
            }
        }

    var schedule by remember {
        mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup.isEmpty()) return@LaunchedEffect
        val start = "2026-01-12"
        val end = "2026-01-17"
        try {
            schedule = RetrofitInstance.api.getSchedule(
                selectedGroup,
                start,
                end
            )
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    when {
        loading -> CircularProgressIndicator()
        error != null -> Text("Ошибка: $error")
        else -> ScheduleList(schedule)
    }
}
    }