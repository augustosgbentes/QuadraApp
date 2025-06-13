package com.unifor.quadraapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unifor.quadraapp.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }

    val cadastroState by viewModel.cadastroState.collectAsState()

    LaunchedEffect(cadastroState.isSuccess) {
        if (cadastroState.isSuccess) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Cadastro",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = matricula,
            onValueChange = { newValue ->

                if (newValue.all { it.isDigit() } && newValue.length <= 7) {
                    matricula = newValue
                }
            },
            label = { Text("Matrícula (7 dígitos)") },
            placeholder = { Text("Ex: 1234567") },
            isError = matricula.isNotEmpty() && matricula.length != 7,
            supportingText = {
                if (matricula.isNotEmpty() && matricula.length != 7) {
                    Text(
                        text = "A matrícula deve ter exatamente 7 dígitos",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (nome.isNotEmpty() && email.isNotEmpty() &&
                    senha.isNotEmpty() && matricula.length == 7) {
                    viewModel.cadastrar(nome, email, senha, matricula)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cadastroState.isLoading &&
                    nome.isNotEmpty() &&
                    email.isNotEmpty() &&
                    senha.isNotEmpty() &&
                    matricula.length == 7
        ) {
            if (cadastroState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Cadastrar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Já tem conta? Faça login")
        }

        cadastroState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}