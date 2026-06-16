package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.parkhub.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucheFahrzeugDropdown(
    selectedFahrzeugTyp: String,
    dropdownExpanded: Boolean,
    onDropdownChange: (Boolean) -> Unit,
    onFahrzeugTypSelected: (String) -> Unit,
    fahrzeugTypen: List<String>
) {
    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = onDropdownChange
    ) {
        OutlinedTextField(
            value = selectedFahrzeugTyp,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text("Fahrzeugtyp") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = ParkHubGreen
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ParkHubGreen,
                focusedLabelColor = ParkHubGreen
            )
        )
        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { onDropdownChange(false) }
        ) {
            fahrzeugTypen.forEach { typ ->
                DropdownMenuItem(
                    text = { Text(typ) },
                    onClick = { onFahrzeugTypSelected(typ) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            tint = if (selectedFahrzeugTyp == typ) ParkHubGreen else Gray
                        )
                    }
                )
            }
        }
    }
}