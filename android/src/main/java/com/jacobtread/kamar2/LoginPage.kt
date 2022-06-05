package com.jacobtread.kamar2

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import com.jacobtread.kamar2.api.KAMAR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginPage() {
    var address by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginScope = rememberCoroutineScope()
    Column {
        TextField(address, onValueChange = { address = it })
        TextField(username, onValueChange = { username = it })
        TextField(password, onValueChange = { password = it })
        Button(onClick = {
            loginScope.launch(Dispatchers.IO) {
                KAMAR.address = address
                val result = KAMAR.authenticate(username, password)
            }
        }) {
            Text("Login")
        }
    }
}