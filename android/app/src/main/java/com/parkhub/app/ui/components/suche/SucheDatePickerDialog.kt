package com.parkhub.app.ui.components.suche

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.parkhub.app.ui.theme.*

// Dialog zur Datumsauswahl, eingebettet in Material3s DatePickerDialog.
// Gibt das gewählte Datum als Millisekunden-Timestamp über onConfirm zurück.
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
            }) {
                Text("OK", color = ParkHubGreen)
            }
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