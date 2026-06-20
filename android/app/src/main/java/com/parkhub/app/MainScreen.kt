package com.parkhub.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.parkhub.app.ui.screens.FlotteScreen
import com.parkhub.app.ui.screens.SucheScreen
import com.parkhub.app.ui.theme.Gray
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.parkhub.app.ui.screens.rememberSucheUiState


sealed class BottomNavItem(
    val label: String,
    val icon: ImageVector
) {
    object Suchen : BottomNavItem("Suchen", Icons.Filled.Search)
    object Flotte : BottomNavItem("Flotte", Icons.Filled.DirectionsCar)
}

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableStateOf(0) }
    var bottomBarVisible by remember { mutableStateOf(true) }
    val sucheUiState = rememberSucheUiState()

    val items = listOf(
        BottomNavItem.Suchen,
        BottomNavItem.Flotte
    )

    Scaffold(
        bottomBar = {
            if (bottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = Gray,
                                unselectedTextColor = Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        val saveableStateHolder = rememberSaveableStateHolder()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            saveableStateHolder.SaveableStateProvider(selectedItem) {
                when (selectedItem) {
                    0 -> SucheScreen(
                        state = sucheUiState,
                        onDetailScreenChanged = { isDetailScreen ->
                            bottomBarVisible = !isDetailScreen
                        }
                    )
                    1 -> FlotteScreen()
                }
            }
        }
    }
}