package com.parkhub.app.ui.components.flotte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.ui.theme.StatusAktiv
import com.parkhub.app.ui.theme.StatusWartung
import com.parkhub.app.ui.theme.StatusBesetzt

// Kompaktes, abgerundetes Badge mit Punkt-Symbol, Label und farblicher
// Kennzeichnung je nach Fahrzeug-Status. Wird in FahrzeugItem verwendet.
@Composable
fun StatusBadge(status: FahrzeugStatus, modifier: Modifier = Modifier) {
    val (text, color) = when (status) {
        FahrzeugStatus.FREI -> Pair("● FREI", StatusAktiv)
        FahrzeugStatus.WARTUNG -> Pair("● Wartung", StatusWartung)
        FahrzeugStatus.BESETZT -> Pair("● Besetzt", StatusBesetzt)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}