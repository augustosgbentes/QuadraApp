package com.unifor.quadraapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unifor.quadraapp.data.model.Agendamento
import com.unifor.quadraapp.ui.viewmodel.AgendamentoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeusAgendamentosScreen(
    onNavigateBack: () -> Unit,
    agendamentoViewModel: AgendamentoViewModel = viewModel()
) {
    val uniforBlue = Color(0xFF2563EB)
    var showCancelDialog by remember { mutableStateOf(false) }
    var agendamentoParaCancelar by remember { mutableStateOf<Agendamento?>(null) }

    val agendamentoState by agendamentoViewModel.agendamentoState.collectAsState()

    // Recarregar agendamentos quando a tela for exibida
    LaunchedEffect(Unit) {
        agendamentoViewModel.carregarAgendamentos()
    }

    // Mostrar mensagens de sucesso/erro
    LaunchedEffect(agendamentoState.mensagemSucesso) {
        if (agendamentoState.mensagemSucesso != null) {
            // Limpar mensagem após 3 segundos
            kotlinx.coroutines.delay(3000)
            agendamentoViewModel.clearMessages()
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
                    text = "Meus Agendamentos",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Botão de refresh
                IconButton(
                    onClick = { agendamentoViewModel.carregarAgendamentos() },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar"
                    )
                }
            }
        }

        // Mostrar loading
        if (agendamentoState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = uniforBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Carregando agendamentos...",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                }
            }
        }
        // Mostrar mensagem de erro
        else if (agendamentoState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "❌",
                        style = TextStyle(fontSize = 48.sp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = agendamentoState.errorMessage!!,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { agendamentoViewModel.carregarAgendamentos() },
                        colors = ButtonDefaults.buttonColors(containerColor = uniforBlue)
                    ) {
                        Text("Tentar Novamente")
                    }
                }
            }
        }
        // Lista vazia
        else if (agendamentoState.agendamentos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📅",
                        style = TextStyle(fontSize = 48.sp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Você ainda não tem agendamentos",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = "Faça sua primeira reserva na tela de agendamentos!",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { agendamentoViewModel.carregarAgendamentos() },
                        colors = ButtonDefaults.buttonColors(containerColor = uniforBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Atualizar")
                    }
                }
            }
        }
        // Lista com agendamentos
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mostrar mensagem de sucesso se houver
                if (agendamentoState.mensagemSucesso != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                        ) {
                            Text(
                                text = "✅ ${agendamentoState.mensagemSucesso}",
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // Header com contador
                item {
                    Text(
                        text = "Total: ${agendamentoState.agendamentos.size} agendamento(s)",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(agendamentoState.agendamentos) { agendamento ->
                    AgendamentoCard(
                        agendamento = agendamento,
                        onCancelar = {
                            agendamentoParaCancelar = agendamento
                            showCancelDialog = true
                        }
                    )
                }
            }
        }
    }

    // Dialog de cancelamento
    if (showCancelDialog && agendamentoParaCancelar != null) {
        CancelarAgendamentoDialog(
            agendamento = agendamentoParaCancelar!!,
            onDismiss = {
                showCancelDialog = false
                agendamentoParaCancelar = null
            },
            onConfirmar = {
                agendamentoViewModel.cancelarAgendamento(agendamentoParaCancelar!!.id)
                showCancelDialog = false
                agendamentoParaCancelar = null
            }
        )
    }
}

@Composable
fun AgendamentoCard(
    agendamento: Agendamento,
    onCancelar: () -> Unit
) {
    val corStatus = when (agendamento.status) {
        "Confirmado" -> Color(0xFF4CAF50)
        "Recusado" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val iconeQuadra = when {
        agendamento.quadra.contains("Futsal") -> "⚽"
        agendamento.quadra.contains("Basquete") -> "🏀"
        agendamento.quadra.contains("Vôlei") -> "🏐"
        else -> "🏟️"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header do card com ícone e status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = iconeQuadra,
                        style = TextStyle(fontSize = 20.sp),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = agendamento.quadra,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }

                // Badge do status
                Card(
                    colors = CardDefaults.cardColors(containerColor = corStatus),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = agendamento.status,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Informações do agendamento
            AgendamentoInfo(
                label = "📅 Data e Horário:",
                valor = agendamento.dataHora
            )

            AgendamentoInfo(
                label = "⏱️ Duração:",
                valor = agendamento.duracao
            )

            AgendamentoInfo(
                label = "👤 Responsável:",
                valor = agendamento.nomeUsuario
            )

            // ID do agendamento (para debug)
            if (agendamento.id.isNotEmpty()) {
                AgendamentoInfo(
                    label = "🔗 ID:",
                    valor = agendamento.id.take(8) + "..."
                )
            }

            // Botão cancelar (apenas para agendamentos confirmados)
            if (agendamento.status == "Confirmado") {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cancelar",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Mensagem para agendamentos recusados
            if (agendamento.status == "Recusado") {
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = "❌ Este agendamento foi recusado. Entre em contato com a administração para mais informações.",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color(0xFFC62828),
                            lineHeight = 16.sp
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AgendamentoInfo(
    label: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        )
        Text(
            text = valor,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Black
            )
        )
    }
}

@Composable
fun CancelarAgendamentoDialog(
    agendamento: Agendamento,
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
                // Ícone de aviso
                Text(
                    text = "⚠️",
                    style = TextStyle(fontSize = 48.sp),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Cancelar Agendamento",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Tem certeza que deseja cancelar este agendamento?",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Detalhes do agendamento
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "📍 ${agendamento.quadra}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "📅 ${agendamento.dataHora}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Manter",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onConfirmar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Cancelar",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}