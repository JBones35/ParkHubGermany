package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.theme.*

val stundenOptionen = (0..23).toList()
val minutenOptionen = listOf(0, 30)
val minutenLabels = mapOf(0 to ":00", 30 to ":30")

// Dialog zur Uhrzeitauswahl über ein Stunden-Dropdown und Minuten-Chips
// (volle und halbe Stunde). Zeigt die aktuelle Auswahl groß über den
// Eingabeelementen an.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucheTimePickerDialog(
    show: Boolean,
    title: String,
    selectedHour: Int,
    selectedMinute: Int,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK", color = ParkHubGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = Gray)
            }
        },
        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "%02d:%02d".format(selectedHour, selectedMinute),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParkHubGreen
                )

                Spacer(modifier = Modifier.height(16.dp))

                var expandedStunde by androidx.compose.runtime.remember {
                    androidx.compose.runtime.mutableStateOf(false)
                }
                ExposedDropdownMenuBox(
                    expanded = expandedStunde,
                    onExpandedChange = { expandedStunde = it }
                ) {
                    OutlinedTextField(
                        value = "%02d Uhr".format(selectedHour),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Stunde") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStunde)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ParkHubGreen,
                            focusedLabelColor = ParkHubGreen
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStunde,
                        onDismissRequest = { expandedStunde = false }
                    ) {
                        stundenOptionen.forEach { stunde ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "%02d Uhr".format(stunde),
                                        color = if (selectedHour == stunde)
                                            ParkHubGreen
                                        else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (selectedHour == stunde)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onHourSelected(stunde)
                                    expandedStunde = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Minute",
                    fontSize = 12.sp,
                    color = Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    minutenOptionen.forEach { minute ->
                        FilterChip(
                            selected = selectedMinute == minute,
                            onClick = { onMinuteSelected(minute) },
                            label = {
                                Text(
                                    text = minutenLabels[minute] ?: ":00",
                                    fontWeight = if (selectedMinute == minute)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ParkHubGreenContainer,
                                selectedLabelColor = ParkHubGreen
                            )
                        )
                    }
                }
            }
        }
    )
}