// FILE: ui/LoginScreen.kt
package com.example.martorderingsystem.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.martorderingsystem.ui.theme.MartOrderingSystemTheme
import com.example.martorderingsystem.AdminHomeActivity
import com.example.martorderingsystem.UserHomeActivity

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Mart Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (username == "admin" && password == "admin") {
                context.startActivity(Intent(context, AdminHomeActivity::class.java))
            } else {
                context.startActivity(Intent(context, UserHomeActivity::class.java))
            }
        }) {
            Text("Login")
        }
    }
}