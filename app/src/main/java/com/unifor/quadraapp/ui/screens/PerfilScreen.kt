package com.unifor.quadraapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.unifor.quadraapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val uniforBlue = Color(0xFF2563EB)
    val userState by userViewModel.userState.collectAsState()
    val alterarSenhaState by userViewModel.alterarSenhaState.collectAsState()

    var showAlterarSenhaDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAlterarFotoDialog by remember { mutableStateOf(false) }

    // Launcher para selecionar imagem da galeria
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { userViewModel.uploadFoto(it) }
    }

    // Dados do usuário
    val user = userState.user
    val nomeUsuario = user?.nome ?: "Carregando..."
    val matriculaUsuario = user?.matricula ?: "Carregando..."
    val emailUsuario = user?.email ?: "Carregando..."
    val fotoUrl = user?.fotoUrl

    // Observar sucesso na alteração de senha
    LaunchedEffect(alterarSenhaState.isSuccess) {
        if (alterarSenhaState.isSuccess) {
            showAlterarSenhaDialog = false
            userViewModel.clearErrors()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header azul
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
            colors = CardDefaults.cardColors(containerColor = uniforBlue)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Meu Perfil",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Seção Foto do Perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "📸",
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Foto do Perfil",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }

                    // Foto do usuário
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, uniforBlue, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!fotoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = fotoUrl,
                                contentDescription = "Foto do usuário",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Foto do usuário",
                                tint = Color.Gray,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        // Loading indicator se estiver fazendo upload
                        if (userState.isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão Alterar Foto
                    TextButton(
                        onClick = { showAlterarFotoDialog = true },
                        enabled = !userState.isLoading
                    ) {
                        Text(
                            text = "Alterar Foto",
                            style = TextStyle(
                                color = Color(0xFFE91E63),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Seção Informações Pessoais
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Título
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "ℹ️",
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Informações Pessoais",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }

                    // Informações
                    InfoItem(label = "Nome:", valor = nomeUsuario)
                    InfoItem(label = "Matrícula:", valor = matriculaUsuario)
                    InfoItem(label = "E-mail:", valor = emailUsuario)
                }
            }

            // Seção Configurações
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Título
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "⚙️",
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Configurações",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }

                    // Opções
                    ConfiguracaoItem(
                        icone = Icons.Default.Lock,
                        texto = "Alterar Senha",
                        onClick = { showAlterarSenhaDialog = true }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    ConfiguracaoItem(
                        icone = Icons.Default.ExitToApp,
                        texto = "Sair",
                        onClick = { showLogoutDialog = true },
                        corTexto = Color.Red
                    )
                }
            }

            // Mostrar erros
            userState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    // Dialog Alterar Senha
    if (showAlterarSenhaDialog) {
        AlterarSenhaDialog(
            alterarSenhaState = alterarSenhaState,
            onDismiss = {
                showAlterarSenhaDialog = false
                userViewModel.clearErrors()
            },
            onConfirmar = { senhaAtual, novaSenha ->
                userViewModel.alterarSenha(senhaAtual, novaSenha)
            }
        )
    }

    // Dialog Logout
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirmar = {
                userViewModel.logout() // Faz logout no Firebase
                showLogoutDialog = false
                onLogout() // Navega para login e limpa pilha
            }
        )
    }

    // Dialog Alterar Foto
    if (showAlterarFotoDialog) {
        AlterarFotoDialog(
            onDismiss = { showAlterarFotoDialog = false },
            onEscolherGaleria = {
                imagePickerLauncher.launch("image/*")
                showAlterarFotoDialog = false
            }
        )
    }
}

@Composable
fun InfoItem(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        )
        Text(
            text = if (valor == "Carregando...") valor else valor.take(30),
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray
            )
        )
    }
}

@Composable
fun ConfiguracaoItem(
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String,
    onClick: () -> Unit,
    corTexto: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = Color(0xFFFFB74D),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = texto,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = corTexto
                )
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun AlterarSenhaDialog(
    alterarSenhaState: com.unifor.quadraapp.ui.viewmodel.UserState,
    onDismiss: () -> Unit,
    onConfirmar: (String, String) -> Unit
) {
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Alterar Senha",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = senhaAtual,
                    onValueChange = { senhaAtual = it },
                    label = { Text("Senha atual") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !alterarSenhaState.isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = novaSenha,
                    onValueChange = { novaSenha = it },
                    label = { Text("Nova senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !alterarSenhaState.isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = { confirmarSenha = it },
                    label = { Text("Confirmar nova senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !alterarSenhaState.isLoading
                )

                // Mostrar erro
                alterarSenhaState.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !alterarSenhaState.isLoading
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (novaSenha == confirmarSenha && senhaAtual.isNotEmpty() && novaSenha.length >= 6) {
                                onConfirmar(senhaAtual, novaSenha)
                            }
                        },
                        enabled = !alterarSenhaState.isLoading &&
                                senhaAtual.isNotEmpty() &&
                                novaSenha.isNotEmpty() &&
                                novaSenha == confirmarSenha &&
                                novaSenha.length >= 6
                    ) {
                        if (alterarSenhaState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Alterar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirmar: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sair do App",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Tem certeza que deseja sair?",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = onConfirmar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Sair")
                    }
                }
            }
        }
    }
}

@Composable
fun AlterarFotoDialog(
    onDismiss: () -> Unit,
    onEscolherGaleria: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Alterar Foto",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Escolha uma opção:",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onEscolherGaleria,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Escolher da Galeria")
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}