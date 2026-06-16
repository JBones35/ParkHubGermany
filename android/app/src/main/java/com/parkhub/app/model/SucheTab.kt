package com.parkhub.app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Map
import androidx.compose.ui.graphics.vector.ImageVector

data class SucheTab(val label: String, val icon: ImageVector)

val tabs = listOf(
    SucheTab("Karte", Icons.Outlined.Map),
    SucheTab("Liste", Icons.AutoMirrored.Outlined.FormatListBulleted)
)