package com.unifor.quadraapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.unifor.quadraapp.R
import com.unifor.quadraapp.ui.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    onNavigateToAgendamento: () -> Unit,
    onNavigateToMeusAgendamentos: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onLogout: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val uniforBlue = Color(0xFF2563EB)
    val userState by userViewModel.userState.collectAsState()

    // Dados do usuário atual ou dados padrão
    val nomeUsuario = userState.user?.nome ?: "Carregando..."
    val matriculaUsuario = userState.user?.matricula ?: "Carregando..."
    val fotoUrl = userState.user?.fotoUrl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header fixo azul
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
                // Foto do usuário
                Card(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                                tint = uniforBlue,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nome e matrícula
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = nomeUsuario,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = matriculaUsuario,
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    )
                }

                // Logo da faculdade
                Card(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.unifor_logo),
                            contentDescription = "Logo Universidade de Fortaleza",
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        // Conteúdo principal
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Reservar Quadra
            item {
                MenuCard(
                    title = "Reservar Quadra",
                    icon = "🏟️",
                    imageContent = {
                        // Simulando imagem da quadra esportiva
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Color(0xFFE8B4A6),
                                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏟️ Quadra Poliesportiva",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            )
                        }
                    },
                    onClick = onNavigateToAgendamento
                )
            }

            // Card Minhas Reservas
            item {
                MenuCard(
                    title = "Minhas Reservas",
                    icon = "📋",
                    imageContent = {
                        // Simulando imagem de pessoas na quadra
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Color(0xFF8B7355),
                                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👥 Seus Agendamentos",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            )
                        }
                    },
                    onClick = onNavigateToMeusAgendamentos
                )
            }

            // Card Meu Perfil
            item {
                MenuCard(
                    title = "Meu Perfil",
                    icon = "👤",
                    imageContent = {
                        // Simulando imagem de perfil
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Color(0xFF6B8E6B),
                                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚙️ Configurações",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            )
                        }
                    },
                    onClick = onNavigateToPerfil
                )
            }
        }

        // Mostrar erro se houver
        userState.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    text = "Erro: $error",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: String,
    imageContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Imagem do card
            imageContent()

            // Título do card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
            }
        }
    }
}