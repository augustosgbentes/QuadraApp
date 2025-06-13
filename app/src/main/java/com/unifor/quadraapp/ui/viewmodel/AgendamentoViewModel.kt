package com.unifor.quadraapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.unifor.quadraapp.data.model.Agendamento
import com.unifor.quadraapp.data.repository.AgendamentoRepository
import com.unifor.quadraapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AgendamentoState(
    val isLoading: Boolean = false,
    val agendamentos: List<Agendamento> = emptyList(),
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val mensagemSucesso: String? = null
)

class AgendamentoViewModel : ViewModel() {
    private val agendamentoRepository = AgendamentoRepository()
    private val authRepository = AuthRepository()

    private val _agendamentoState = MutableStateFlow(AgendamentoState())
    val agendamentoState: StateFlow<AgendamentoState> = _agendamentoState

    init {
        carregarAgendamentos()
    }

    fun carregarAgendamentos() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                _agendamentoState.value = _agendamentoState.value.copy(isLoading = true)

                try {
                    Log.d("AgendamentoViewModel", "Carregando agendamentos para usuário: ${currentUser.uid}")

                    val agendamentos = agendamentoRepository.buscarAgendamentosUsuario(currentUser.uid)

                    Log.d("AgendamentoViewModel", "Agendamentos carregados: ${agendamentos.size}")
                    agendamentos.forEach { agendamento ->
                        Log.d("AgendamentoViewModel", "Agendamento: ${agendamento.quadra} - ${agendamento.dataHora}")
                    }

                    _agendamentoState.value = _agendamentoState.value.copy(
                        agendamentos = agendamentos,
                        isLoading = false,
                        errorMessage = null
                    )
                } catch (e: Exception) {
                    Log.e("AgendamentoViewModel", "Erro ao carregar agendamentos: ${e.message}", e)
                    _agendamentoState.value = _agendamentoState.value.copy(
                        errorMessage = "Erro ao carregar agendamentos: ${e.message}",
                        isLoading = false
                    )
                }
            } else {
                Log.e("AgendamentoViewModel", "Usuário não está logado")
                _agendamentoState.value = _agendamentoState.value.copy(
                    errorMessage = "Usuário não está logado",
                    isLoading = false
                )
            }
        }
    }

    fun criarAgendamento(
        quadraNome: String,
        data: String,
        horario: String,
        duracao: String = "1 hora"
    ) {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                _agendamentoState.value = _agendamentoState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false
                )

                try {
                    Log.d("AgendamentoViewModel", "Iniciando criação de agendamento...")
                    Log.d("AgendamentoViewModel", "Quadra: $quadraNome")
                    Log.d("AgendamentoViewModel", "Data: $data")
                    Log.d("AgendamentoViewModel", "Horário: $horario")
                    Log.d("AgendamentoViewModel", "Usuário ID: ${currentUser.uid}")


                    val userResult = authRepository.buscarDadosUsuario(currentUser.uid)
                    val nomeUsuario = if (userResult.isSuccess) {
                        val user = userResult.getOrNull()
                        Log.d("AgendamentoViewModel", "Nome do usuário encontrado: ${user?.nome}")
                        user?.nome ?: "Usuário"
                    } else {
                        Log.w("AgendamentoViewModel", "Erro ao buscar dados do usuário, usando nome padrão")
                        "Usuário"
                    }

                    val agendamento = Agendamento(
                        userId = currentUser.uid,
                        nomeUsuario = nomeUsuario,
                        dataHora = "$data - $horario",
                        quadra = quadraNome,
                        duracao = duracao,
                        status = "Confirmado"
                    )

                    Log.d("AgendamentoViewModel", "Agendamento criado: $agendamento")

                    val result = agendamentoRepository.criarAgendamento(agendamento)

                    if (result.isSuccess) {
                        Log.d("AgendamentoViewModel", "Agendamento salvo com sucesso no Firebase!")

                        _agendamentoState.value = _agendamentoState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            mensagemSucesso = "Agendamento criado com sucesso!"
                        )


                        carregarAgendamentos()

                    } else {
                        val errorMsg = result.exceptionOrNull()?.message ?: "Erro desconhecido ao criar agendamento"
                        Log.e("AgendamentoViewModel", "Erro ao salvar agendamento: $errorMsg")

                        _agendamentoState.value = _agendamentoState.value.copy(
                            isLoading = false,
                            errorMessage = "Erro ao criar agendamento: $errorMsg"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("AgendamentoViewModel", "Erro inesperado ao criar agendamento: ${e.message}", e)
                    _agendamentoState.value = _agendamentoState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro inesperado: ${e.message}"
                    )
                }
            } else {
                Log.e("AgendamentoViewModel", "Usuário não está logado")
                _agendamentoState.value = _agendamentoState.value.copy(
                    isLoading = false,
                    errorMessage = "Usuário não está logado"
                )
            }
        }
    }

    fun cancelarAgendamento(agendamentoId: String) {
        viewModelScope.launch {
            _agendamentoState.value = _agendamentoState.value.copy(isLoading = true)

            try {
                Log.d("AgendamentoViewModel", "Cancelando agendamento: $agendamentoId")

                val result = agendamentoRepository.cancelarAgendamento(agendamentoId)

                if (result.isSuccess) {
                    Log.d("AgendamentoViewModel", "Agendamento cancelado com sucesso!")


                    val agendamentosAtualizados = _agendamentoState.value.agendamentos.filter {
                        it.id != agendamentoId
                    }

                    _agendamentoState.value = _agendamentoState.value.copy(
                        agendamentos = agendamentosAtualizados,
                        isLoading = false,
                        mensagemSucesso = "Agendamento cancelado com sucesso!"
                    )


                    carregarAgendamentos()

                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Erro ao cancelar agendamento"
                    Log.e("AgendamentoViewModel", "Erro ao cancelar: $errorMsg")

                    _agendamentoState.value = _agendamentoState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao cancelar agendamento: $errorMsg"
                    )
                }
            } catch (e: Exception) {
                Log.e("AgendamentoViewModel", "Erro inesperado ao cancelar: ${e.message}", e)
                _agendamentoState.value = _agendamentoState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro inesperado ao cancelar: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _agendamentoState.value = _agendamentoState.value.copy(
            errorMessage = null,
            mensagemSucesso = null,
            isSuccess = false
        )
    }
}