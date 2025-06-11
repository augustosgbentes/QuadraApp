package com.unifor.quadraapp.data.model

data class Agendamento(
    val id: String = "",
    val userId: String = "",
    val nomeUsuario: String = "",
    val dataHora: String = "",
    val quadra: String = "",
    val duracao: String = "",
    val status: String = "Confirmado"
)