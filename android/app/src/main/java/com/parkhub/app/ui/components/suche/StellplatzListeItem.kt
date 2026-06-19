package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.parkhub.app.ui.screens.StellplatzMitDetails

// Klickbare Karte für einen Stellplatz in der Listenansicht der Suche.
// Der eigentliche Inhalt kommt aus StellplatzInfo, hier nur die
// Card-Hülle mit Schatten und abgerundeten Ecken.
@Composable
fun StellplatzListeItem(
    stellplatz: StellplatzMitDetails,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            StellplatzInfo(stellplatz = stellplatz)
        }
    }
}