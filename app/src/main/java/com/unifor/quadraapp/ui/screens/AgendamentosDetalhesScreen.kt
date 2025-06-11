package com.unifor.quadraapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unifor.quadraapp.ui.viewmodel.AgendamentoViewModel
import java.text.SimpleDateFormat
import java.util.*

data class HorarioInfo(
    val horario: String,
    val periodo: String,
    val vagasOcupadas: Int,
    val vagasTotal: Int = 10
) {
    val disponivel: Boolean get() = vagasOcupadas < vagasTotal
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentoDetalhesScreen(
    quadraNome: String,
    onNavigateBack: () -> Unit,
    agendamentoViewModel: AgendamentoViewModel = viewModel()
) {
    var dataSelecionada by remember { mutableStateOf<Int?>(null) }
    var mesAtual by remember { mutableStateOf(Calendar.getInstance()) }
    var showHorariosDialog by remember { mutableStateOf(false) }
    var periodoSelecionado by remember { mutableStateOf("") }
    var showConfirmacaoDialog by remember { mutableStateOf(false) }
    var horarioSelecionado by remember { mutableStateOf<HorarioInfo?>(null) }

    val agendamentoState by agendamentoViewModel.agendamentoState.collectAsState()
    val uniforBlue = Color(0xFF2563EB)

    // Observar sucesso no agendamento
    LaunchedEffect(agendamentoState.isSuccess) {
        if (agendamentoState.isSuccess) {
            showConfirmacaoDialog = false
            agendamentoViewModel.clearMessages()
            onNavigateBack() // Volta para a tela anterior após sucesso
        }
    }

    // Horários organizados por período
    val todosHorarios = listOf(
        // Manhã (07:00 - 12:00)
        HorarioInfo("07:00", "Manhã", 2),
        HorarioInfo("08:00", "Manhã", 5),
        HorarioInfo("09:00", "Manhã", 8),
        HorarioInfo("10:00", "Manhã", 3),
        HorarioInfo("11:00", "Manhã", 7),
        HorarioInfo("12:00", "Manhã", 4),

        // Tarde (13:00 - 18:00)
        HorarioInfo("13:00", "Tarde", 6),
        HorarioInfo("14:00", "Tarde", 9),
        HorarioInfo("15:00", "Tarde", 10), // Lotado
        HorarioInfo("16:00", "Tarde", 2),
        HorarioInfo("17:00", "Tarde", 5),
        HorarioInfo("18:00", "Tarde", 8),

        // Noite (19:00 - 22:00)
        HorarioInfo("19:00", "Noite", 4),
        HorarioInfo("20:00", "Noite", 7),
        HorarioInfo("21:00", "Noite", 6),
        HorarioInfo("22:00", "Noite", 3)
    )

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

                Column {
                    Text(
                        text = "Agendar Quadra",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = quadraNome,
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }

        // Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Calendário
            CalendarioCustom(
                mesAtual = mesAtual,
                dataSelecionada = dataSelecionada,
                onDataSelecionada = { data ->
                    dataSelecionada = data
                },
                onMesAnterior = {
                    mesAtual.add(Calendar.MONTH, -1)
                    mesAtual = mesAtual.clone() as Calendar
                },
                onProximoMes = {
                    mesAtual.add(Calendar.MONTH, 1)
                    mesAtual = mesAtual.clone() as Calendar
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Seção Horários Disponíveis
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Horários Disponíveis",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (dataSelecionada == null) {
                Text(
                    text = "Selecione uma data no calendário",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
            } else {
                // Períodos do dia
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PeriodoCard(
                        periodo = "Manhã",
                        horario = "07:00 - 12:00",
                        icone = "🌅",
                        cor = Color(0xFFFFB74D),
                        quantidadeHorarios = todosHorarios.count { it.periodo == "Manhã" },
                        onClick = {
                            periodoSelecionado = "Manhã"
                            showHorariosDialog = true
                        }
                    )

                    PeriodoCard(
                        periodo = "Tarde",
                        horario = "13:00 - 18:00",
                        icone = "☀️",
                        cor = Color(0xFFFF8A65),
                        quantidadeHorarios = todosHorarios.count { it.periodo == "Tarde" },
                        onClick = {
                            periodoSelecionado = "Tarde"
                            showHorariosDialog = true
                        }
                    )

                    PeriodoCard(
                        periodo = "Noite",
                        horario = "19:00 - 22:00",
                        icone = "🌙",
                        cor = Color(0xFF9575CD),
                        quantidadeHorarios = todosHorarios.count { it.periodo == "Noite" },
                        onClick = {
                            periodoSelecionado = "Noite"
                            showHorariosDialog = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Mostrar erro se houver
            agendamentoState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = "❌ $error",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botão Confirmar Agendamento
            Button(
                onClick = {
                    if (dataSelecionada != null && horarioSelecionado != null) {
                        showConfirmacaoDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7986CB)
                ),
                shape = RoundedCornerShape(24.dp),
                enabled = dataSelecionada != null && horarioSelecionado != null && !agendamentoState.isLoading
            ) {
                if (agendamentoState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = if (horarioSelecionado != null)
                            "CONFIRMAR AGENDAMENTO (${horarioSelecionado!!.horario})"
                        else
                            "SELECIONE UM HORÁRIO",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    // Dialog de Horários do Período
    if (showHorariosDialog) {
        HorariosPeriodoDialog(
            periodo = periodoSelecionado,
            horarios = todosHorarios.filter { it.periodo == periodoSelecionado },
            onHorarioSelecionado = { horario ->
                horarioSelecionado = horario
                showHorariosDialog = false
            },
            onDismiss = { showHorariosDialog = false }
        )
    }

    // Dialog de Confirmação
    if (showConfirmacaoDialog) {
        ConfirmacaoDialog(
            quadraNome = quadraNome,
            data = formatarData(dataSelecionada ?: 0, mesAtual),
            horario = horarioSelecionado?.horario ?: "",
            isLoading = agendamentoState.isLoading,
            onConfirmar = {
                val dataFormatada = formatarData(dataSelecionada ?: 0, mesAtual)
                agendamentoViewModel.criarAgendamento(
                    quadraNome = quadraNome,
                    data = dataFormatada,
                    horario = horarioSelecionado?.horario ?: "",
                    duracao = "1 hora"
                )
            },
            onDismiss = { showConfirmacaoDialog = false }
        )
    }
}

// Função para formatar a data
fun formatarData(dia: Int, calendario: Calendar): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val cal = calendario.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, dia)
    return formatter.format(cal.time)
}

@Composable
fun CalendarioCustom(
    mesAtual: Calendar,
    dataSelecionada: Int?,
    onDataSelecionada: (Int) -> Unit,
    onMesAnterior: () -> Unit,
    onProximoMes: () -> Unit
) {
    val formatter = SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR"))
    val diasSemana = listOf("D", "S", "T", "Q", "Q", "S", "S")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header do mês
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMesAnterior) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Mês anterior"
                    )
                }

                Text(
                    text = formatter.format(mesAtual.time).replaceFirstChar { it.uppercase() },
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                IconButton(onClick = onProximoMes) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Próximo mês"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dias da semana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                diasSemana.forEach { dia ->
                    Text(
                        text = dia,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid dos dias
            val calendar = mesAtual.clone() as Calendar
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val primeiroDiaMes = calendar.get(Calendar.DAY_OF_WEEK) - 1
            val diasNoMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(240.dp)
            ) {
                // Espaços vazios antes do primeiro dia
                items(primeiroDiaMes) {
                    Spacer(modifier = Modifier.size(40.dp))
                }

                // Dias do mês
                items(diasNoMes) { dia ->
                    val diaAtual = dia + 1
                    val isSelected = dataSelecionada == diaAtual

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF00BCD4) else Color.Transparent
                            )
                            .clickable {
                                onDataSelecionada(diaAtual)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = diaAtual.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodoCard(
    periodo: String,
    horario: String,
    icone: String,
    cor: Color,
    quantidadeHorarios: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icone,
                    style = TextStyle(fontSize = 24.sp),
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = periodo,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = horario,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$quantidadeHorarios horários",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Ver horários",
                    tint = cor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun HorariosPeriodoDialog(
    periodo: String,
    horarios: List<HorarioInfo>,
    onHorarioSelecionado: (HorarioInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val icone = when (periodo) {
        "Manhã" -> "🌅"
        "Tarde" -> "☀️"
        "Noite" -> "🌙"
        else -> "🕐"
    }

    val cor = when (periodo) {
        "Manhã" -> Color(0xFFFFB74D)
        "Tarde" -> Color(0xFFFF8A65)
        "Noite" -> Color(0xFF9575CD)
        else -> Color.Gray
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = icone,
                        style = TextStyle(fontSize = 20.sp),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Horários - $periodo",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Lista de horários
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(horarios) { horario ->
                        HorarioSelecionavelCard(
                            horario = horario,
                            cor = cor,
                            onClick = {
                                if (horario.disponivel) {
                                    onHorarioSelecionado(horario)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão Fechar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("Fechar")
                }
            }
        }
    }
}

@Composable
fun HorarioSelecionavelCard(
    horario: HorarioInfo,
    cor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = horario.disponivel) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                !horario.disponivel -> Color.Gray.copy(alpha = 0.3f)
                horario.vagasOcupadas > 7 -> Color(0xFFFF9800).copy(alpha = 0.2f) // Laranja
                else -> cor.copy(alpha = 0.1f)
            }
        ),
        border = if (horario.disponivel) BorderStroke(1.dp, cor.copy(alpha = 0.3f)) else null,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = horario.horario,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (horario.disponivel) Color.Black else Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Indicador visual de ocupação
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            when {
                                !horario.disponivel -> Color.Red
                                horario.vagasOcupadas > 7 -> Color(0xFFFF9800) // Laranja
                                else -> Color.Green
                            },
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = if (horario.disponivel)
                        "${horario.vagasOcupadas}/${horario.vagasTotal}"
                    else
                        "Lotado",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = if (horario.disponivel) Color.Gray else Color.Red,
                        fontWeight = if (!horario.disponivel) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
fun ConfirmacaoDialog(
    quadraNome: String,
    data: String,
    horario: String,
    isLoading: Boolean,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
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
                    text = "Confirmar Agendamento",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📍 Quadra: $quadraNome",
                            style = TextStyle(fontSize = 14.sp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = "📅 Data: $data",
                            style = TextStyle(fontSize = 14.sp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = "🕐 Horário: $horario",
                            style = TextStyle(fontSize = 14.sp),
                            modifier = Modifier.padding(bottom = 4.dp)
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
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = onConfirmar,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }
}