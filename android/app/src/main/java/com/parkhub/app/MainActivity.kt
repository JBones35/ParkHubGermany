package com.parkhub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.parkhub.app.data.AppDatabase
import com.parkhub.app.data.DatabaseSeeder
import com.parkhub.app.ui.theme.ParkHubTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            DatabaseSeeder(db).seedIfEmpty()
        }
        setContent {
            ParkHubTheme {
                MainScreen()
            }
        }
    }
}