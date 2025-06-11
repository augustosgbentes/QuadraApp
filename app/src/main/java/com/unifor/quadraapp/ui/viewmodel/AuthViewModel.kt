package com.unifor.quadraapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unifor.quadraapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow(AuthState())
    val loginState: StateFlow<AuthState> = _loginState

    private val _cadastroState = MutableStateFlow(AuthState())
    val cadastroState: StateFlow<AuthState> = _cadastroState

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _loginState.value = AuthState(isLoading = true)

            try {
                val result = repository.loginUsuario(email.trim(), senha)

                if (result.isSuccess) {
                    _loginState.value = AuthState(isSuccess = true)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Erro no login"
                    _loginState.value = AuthState(
                        errorMessage = "Erro no login: $errorMsg"
                    )
                }
            } catch (e: Exception) {
                _loginState.value = AuthState(
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun cadastrar(nome: String, email: String, senha: String, matricula: String) {
        viewModelScope.launch {
            _cadastroState.value = AuthState(isLoading = true)

            try {
                val result = repository.cadastrarUsuario(nome, email.trim(), senha, matricula)

                if (result.isSuccess) {
                    _cadastroState.value = AuthState(isSuccess = true)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Erro no cadastro"
                    _cadastroState.value = AuthState(
                        errorMessage = "Erro no cadastro: $errorMsg"
                    )
                }
            } catch (e: Exception) {
                _cadastroState.value = AuthState(
                    errorMessage = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        repository.logout()
        // Reset states
        _loginState.value = AuthState()
        _cadastroState.value = AuthState()
    }

    // Função para limpar mensagens de erro
    fun clearErrors() {
        _loginState.value = _loginState.value.copy(errorMessage = null)
        _cadastroState.value = _cadastroState.value.copy(errorMessage = null)
    }
}
