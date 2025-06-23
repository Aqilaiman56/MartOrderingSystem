// FILE: UserHomeActivity.kt
package com.example.martorderingsystem.ui.Activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.martorderingsystem.ui.UserHomeScreen
import com.example.martorderingsystem.ui.theme.MartOrderingSystemTheme

class UserHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MartOrderingSystemTheme {
                UserHomeScreen()
            }
        }
    }
}
