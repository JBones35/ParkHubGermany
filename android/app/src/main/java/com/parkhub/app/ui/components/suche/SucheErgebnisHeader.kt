package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.screens.Sortierung
import com.parkhub.app.ui.screens.sortierOptionen
import com.parkhub.app.ui.theme.*

@Composable
fun SucheErgebnisHeader(
    anzahl: Int,
    showSortieren: Boolean,
    onSortierenClick: () -> Unit,
    onSortierenDismiss: () -> Unit,
    selectedSortierung: Sortierung,
    onSortierungSelected: (Sortierung) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$anzahl Stellplätze gefunden",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Box {
            TextButton(onClick = onSortierenClick) {
                Text(
                    text = "Sortieren ▾",
                    color = ParkHubGreen,
                    fontWeight = FontWeight.Medium
                )
            }
            DropdownMenu(
                expanded = showSortieren,
                onDismissRequest = onSortierenDismiss
            ) {
                sortierOptionen.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label,
                                color = if (selectedSortierung == option)
                                    ParkHubGreen
                                else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selectedSortierung == option)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = { onSortierungSelected(option) },
                        leadingIcon = {
                            Text(
                                text = if (option.aufsteigend) "↑" else "↓",
                                color = if (selectedSortierung == option)
                                    ParkHubGreen else Gray
                            )
                        }
                    )
                }
            }
        }
    }
}