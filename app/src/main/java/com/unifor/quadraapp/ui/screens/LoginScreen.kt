package com.unifor.quadraapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unifor.quadraapp.R
import com.unifor.quadraapp.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var emailOuMatricula by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()
    val uniforBlue = Color(0xFF2563EB)

    // Log para debug
    LaunchedEffect(loginState) {
        Log.d("LoginScreen", "Login State: $loginState")
    }

    LaunchedEffect(loginState.isSuccess) {
        if (loginState.isSuccess) {
            Log.d("LoginScreen", "Login bem-sucedido, navegando para home")
            onNavigateToHome()
        }
    }


    LaunchedEffect(Unit) {
        viewModel.clearErrors()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))


        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.unifor_logo),
                contentDescription = "Logo Universidade de Fortaleza",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Universidade",
                style = TextStyle(
                    color = uniforBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "de Fortaleza",
                style = TextStyle(
                    color = uniforBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(80.dp))


        Column {
            UnderlineTextField(
                value = emailOuMatricula,
                onValueChange = { newValue ->
                    emailOuMatricula = newValue
                },
                placeholder = "E-mail ou Matrícula (7 dígitos)",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            UnderlineTextField(
                value = senha,
                onValueChange = { senha = it },
                placeholder = "Senha",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )
        }


        val isValidInput = emailOuMatricula.isNotEmpty() && senha.isNotEmpty() &&
                (emailOuMatricula.contains("@") ||
                        (emailOuMatricula.all { it.isDigit() } && emailOuMatricula.length == 7))

        Spacer(modifier = Modifier.height(24.dp))


        if (emailOuMatricula.isNotEmpty() && !emailOuMatricula.contains("@") &&
            (emailOuMatricula.length != 7 || !emailOuMatricula.all { it.isDigit() })) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    text = "⚠️ A matrícula deve ter exatamente 7 dígitos numéricos",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }


        Button(
            onClick = {
                Log.d("LoginScreen", "Tentando fazer login com: $emailOuMatricula")
                if (emailOuMatricula.isNotEmpty() && senha.isNotEmpty()) {
                    viewModel.login(emailOuMatricula, senha)
                } else {
                    Log.d("LoginScreen", "Email/matrícula ou senha vazios")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = uniforBlue
            ),
            shape = RoundedCornerShape(24.dp),
            enabled = !loginState.isLoading && isValidInput
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "LOGIN",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = onNavigateToRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE5E5E5)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "NÃO É CADASTRADO? CADASTRE-SE AGORA",
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }


        loginState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun UnderlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    )
                }
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}