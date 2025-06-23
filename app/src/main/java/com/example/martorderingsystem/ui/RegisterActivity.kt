// FILE: RegisterActivity.kt
package com.example.martorderingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.martorderingsystem.ui.RegisterScreen
import com.example.martorderingsystem.ui.theme.MartOrderingSystemTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MartOrderingSystemTheme {
                RegisterScreen()
            }
        }
    }
}
