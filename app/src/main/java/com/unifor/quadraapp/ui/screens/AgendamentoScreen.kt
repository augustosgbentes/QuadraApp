package com.unifor.quadraapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.window.Dialog
import com.unifor.quadraapp.R

data class QuadraInfo(
    val id: String,
    val nome: String,
    val descricao: String,
    val icone: String,
    val corFundo: Color,
    val imagemRes: Int
)

data class HorarioDisponivel(
    val horario: String,
    val periodo: String,
    val vagasOcupadas: Int,
    val vagasTotal: Int = 10
) {
    val disponivel: Boolean get() = vagasOcupadas < vagasTotal
    val percentualOcupacao: Float get() = vagasOcupadas.toFloat() / vagasTotal.toFloat()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendamentoScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetalhes: (String) -> Unit
) {
    val uniforBlue = Color(0xFF2563EB)
    var showHorariosDialog by remember { mutableStateOf(false) }
    var quadraSelecionadaParaHorarios by remember { mutableStateOf<QuadraInfo?>(null) }


    val quadras = listOf(
        QuadraInfo(
            id = "futsal",
            nome = "Quadra de Futsal",
            descricao = "Quadra oficial de futsal com piso de madeira",
            icone = "⚽",
            corFundo = Color(0xFFE8B4A6),
            imagemRes = R.drawable.quadra_futsal
        ),
        QuadraInfo(
            id = "basquete",
            nome = "Quadra de Basquete",
            descricao = "Quadra oficial de basquete com piso de madeira e tabelas regulamentares",
            icone = "🏀",
            corFundo = Color(0xFF8B7355),
            imagemRes = R.drawable.quadra_basquete
        ),
        QuadraInfo(
            id = "volei",
            nome = "Quadra de Vôlei",
            descricao = "Quadra oficial de vôlei com rede regulamentar",
            icone = "🏐",
            corFundo = Color(0xFF6B8E6B),
            imagemRes = R.drawable.quadra_volei
        )
    )

    val todosHorarios = listOf(

        HorarioDisponivel("07:00", "Manhã", 2),
        HorarioDisponivel("08:00", "Manhã", 5),
        HorarioDisponivel("09:00", "Manhã", 8),
        HorarioDisponivel("10:00", "Manhã", 3),
        HorarioDisponivel("11:00", "Manhã", 7),
        HorarioDisponivel("12:00", "Manhã", 4),


        HorarioDisponivel("13:00", "Tarde", 6),
        HorarioDisponivel("14:00", "Tarde", 9),
        HorarioDisponivel("15:00", "Tarde", 10),
        HorarioDisponivel("16:00", "Tarde", 2),
        HorarioDisponivel("17:00", "Tarde", 5),
        HorarioDisponivel("18:00", "Tarde", 8),


        HorarioDisponivel("19:00", "Noite", 4),
        HorarioDisponivel("20:00", "Noite", 7),
        HorarioDisponivel("21:00", "Noite", 6),
        HorarioDisponivel("22:00", "Noite", 3)
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
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Escolha a Quadra",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(quadras) { quadra ->
                QuadraCard(
                    quadra = quadra,
                    onVerHorarios = {
                        quadraSelecionadaParaHorarios = quadra
                        showHorariosDialog = true
                    },
                    onReservar = {
                        onNavigateToDetalhes(quadra.nome)
                    }
                )
            }
        }
    }


    if (showHorariosDialog && quadraSelecionadaParaHorarios != null) {
        VerHorariosDialog(
            quadra = quadraSelecionadaParaHorarios!!,
            horarios = todosHorarios,
            onDismiss = {
                showHorariosDialog = false
                quadraSelecionadaParaHorarios = null
            }
        )
    }
}

@Composable
fun QuadraCard(
    quadra: QuadraInfo,
    onVerHorarios: () -> Unit,
    onReservar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = quadra.imagemRes),
                    contentDescription = "Imagem da ${quadra.nome}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quadra.icone,
                            style = TextStyle(fontSize = 24.sp),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Unifor Sports",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }


            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quadra.icone,
                        style = TextStyle(fontSize = 20.sp),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = quadra.nome,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = quadra.descricao,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = onVerHorarios
                    ) {
                        Text(
                            text = "Ver Horários",
                            style = TextStyle(
                                color = Color(0xFFE91E63),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }


                    Button(
                        onClick = onReservar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "RESERVAR",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerHorariosDialog(
    quadra: QuadraInfo,
    horarios: List<HorarioDisponivel>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quadra.icone,
                        style = TextStyle(fontSize = 20.sp),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Horários Disponíveis",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = quadra.nome,
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    val horariosPorPeriodo = horarios.groupBy { it.periodo }

                    horariosPorPeriodo.forEach { (periodo, horariosDoPerio) ->
                        item {
                            PeriodoHorariosSection(
                                periodo = periodo,
                                horarios = horariosDoPerio
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    )
                ) {
                    Text("Fechar")
                }
            }
        }
    }
}

@Composable
fun PeriodoHorariosSection(
    periodo: String,
    horarios: List<HorarioDisponivel>
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

    Column {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = icone,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = periodo,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
        }


        horarios.forEach { horario ->
            HorarioItem(horario = horario, cor = cor)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HorarioItem(
    horario: HorarioDisponivel,
    cor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (horario.disponivel) cor.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = horario.horario,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (horario.disponivel) Color.Black else Color.Gray
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            when {
                                !horario.disponivel -> Color.Red
                                horario.percentualOcupacao > 0.7f -> Color(0xFFFF9800)
                                else -> Color.Green
                            },
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (horario.disponivel)
                        "${horario.vagasOcupadas}/${horario.vagasTotal} pessoas"
                    else
                        "Lotado",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = if (horario.disponivel) Color.Gray else Color.Red,
                        fontWeight = if (!horario.disponivel) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}