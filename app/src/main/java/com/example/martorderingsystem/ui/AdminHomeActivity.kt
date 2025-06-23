// FILE: AdminHomeActivity.kt
package com.example.martorderingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.martorderingsystem.ui.RegisterScreen
import com.example.martorderingsystem.ui.theme.MartOrderingSystemTheme

class AdminHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MartOrderingSystemTheme {
                AdminHomeScreen()
            }
        }
    }
}