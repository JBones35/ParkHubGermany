package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.parkhub.app.model.SucheTab
import com.parkhub.app.ui.theme.*

@Composable
fun SucheTabRow(
    tabs: List<SucheTab>,
    selectedView: Int,
    onViewSelected: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        tabs.forEachIndexed { index, tab ->
            SegmentedButton(
                selected = selectedView == index,
                onClick = { onViewSelected(index) },
                shape = SegmentedButtonDefaults.itemShape(index, tabs.size),
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = ParkHubGreenContainer,
                    activeContentColor = ParkHubGreen,
                    activeBorderColor = ParkHubGreen,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor = Gray,
                    inactiveBorderColor = Gray
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = tab.label,
                        fontWeight = if (selectedView == index)
                            FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}