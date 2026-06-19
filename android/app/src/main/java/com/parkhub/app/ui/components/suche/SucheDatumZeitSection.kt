package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.parkhub.app.ui.theme.*

// Zeigt Datum sowie Start- und Endzeit als nicht editierbare, aber
// klickbare Felder an. Ein Klick öffnet jeweils den entsprechenden
// Picker-Dialog, die eigentliche Werteingabe passiert dort.
@Composable
fun SucheDatumZeitSection(
    datum: String,
    onDatumClick: () -> Unit,
    uhrzeitStart: String,
    onUhrzeitStartClick: () -> Unit,
    uhrzeitEnd: String,
    onUhrzeitEndClick: () -> Unit
) {
    OutlinedTextField(
        value = datum,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDatumClick() },
        label = { Text("Datum") },
        placeholder = { Text("Datum wählen") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = ParkHubGreen
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = Gray,
            disabledLabelColor = Gray,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledLeadingIconColor = ParkHubGreen
        ),
        enabled = false
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = uhrzeitStart,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .weight(1f)
                .clickable { onUhrzeitStartClick() },
            label = { Text("Von") },
            placeholder = { Text("09:00") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Gray,
                disabledLabelColor = Gray,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
            enabled = false
        )
        OutlinedTextField(
            value = uhrzeitEnd,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .weight(1f)
                .clickable { onUhrzeitEndClick() },
            label = { Text("Bis") },
            placeholder = { Text("11:00") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Gray,
                disabledLabelColor = Gray,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
            enabled = false
        )
    }
}