package com.parkhub.app.ui.components.suche

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.parkhub.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucheDatePickerDialog(
    show: Boolean,
    state: DatePickerState,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    if (!show) return
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onConfirm(it) }
            }) { Text("OK", color = ParkHubGreen) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = Gray)
            }
        }
    ) {
        DatePicker(
            state = state,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = ParkHubGreen,
                todayDateBorderColor = ParkHubGreen
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucheTimePickerDialog(
    show: Boolean,
    state: TimePickerState,
    title: String,
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
            TimePicker(
                state = state,
                colors = TimePickerDefaults.colors(
                    selectorColor = ParkHubGreen,
                    timeSelectorSelectedContainerColor = ParkHubGreenContainer,
                    timeSelectorSelectedContentColor = ParkHubGreen
                )
            )
        }
    )
}