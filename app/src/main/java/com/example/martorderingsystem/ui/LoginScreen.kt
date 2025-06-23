// FILE: ui/LoginScreen.kt
package com.example.martorderingsystem.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.martorderingsystem.ui.Activity.AdminHomeActivity
import com.example.martorderingsystem.data.AppDatabase
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.martorderingsystem.ui.Activity.RegisterActivity
import com.example.martorderingsystem.ui.Activity.UserHomeActivity

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val userDao = db.userDao()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Mart Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                val user = userDao.getUser(username, password)
                if (user != null) {
                    val intent = if (user.isAdmin) {
                        Intent(context, AdminHomeActivity::class.java)
                    } else {
                        Intent(context, UserHomeActivity::class.java)
                    }
                    context.startActivity(intent)
                } else {
                    errorMessage = "Invalid username or password"
                }
            }
        }) {
            Text("Login")
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Register Now",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.clickable {
                context.startActivity(Intent(context, RegisterActivity::class.java))
            }
        )
    }
}