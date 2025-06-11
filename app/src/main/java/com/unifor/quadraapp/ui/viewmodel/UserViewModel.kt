package com.unifor.quadraapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.unifor.quadraapp.data.model.User
import com.unifor.quadraapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class UserViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val storage = FirebaseStorage.getInstance().reference

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState

    private val _alterarSenhaState = MutableStateFlow(UserState())
    val alterarSenhaState: StateFlow<UserState> = _alterarSenhaState

    init {
        carregarDadosUsuario()
    }

    fun carregarDadosUsuario() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                _userState.value = UserState(isLoading = true)

                val result = repository.buscarDadosUsuario(currentUser.uid)

                if (result.isSuccess) {
                    _userState.value = UserState(user = result.getOrNull())
                } else {
                    _userState.value = UserState(
                        errorMessage = result.exceptionOrNull()?.message ?: "Erro ao carregar dados"
                    )
                }
            }
        }
    }

    fun alterarSenha(senhaAtual: String, novaSenha: String) {
        viewModelScope.launch {
            _alterarSenhaState.value = UserState(isLoading = true)

            val result = repository.alterarSenha(senhaAtual, novaSenha)

            if (result.isSuccess) {
                _alterarSenhaState.value = UserState(isSuccess = true)
            } else {
                _alterarSenhaState.value = UserState(
                    errorMessage = result.exceptionOrNull()?.message ?: "Erro ao alterar senha"
                )
            }
        }
    }

    fun uploadFoto(imageUri: Uri) {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser ?: return@launch
                _userState.value = _userState.value.copy(isLoading = true)

                // Upload da imagem para o Firebase Storage
                val imageRef = storage.child("fotos_perfil/${currentUser.uid}.jpg")
                val uploadTask = imageRef.putFile(imageUri).await()

                // Obter URL da imagem
                val downloadUrl = imageRef.downloadUrl.await().toString()

                // Atualizar URL no database
                val result = repository.atualizarFotoUsuario(currentUser.uid, downloadUrl)

                if (result.isSuccess) {
                    // Recarregar dados do usuário para mostrar a nova foto
                    carregarDadosUsuario()
                } else {
                    _userState.value = _userState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao salvar foto"
                    )
                }
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao fazer upload da foto: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        repository.logout()
        _userState.value = UserState()
        _alterarSenhaState.value = UserState()
    }

    fun clearErrors() {
        _userState.value = _userState.value.copy(errorMessage = null)
        _alterarSenhaState.value = _alterarSenhaState.value.copy(errorMessage = null, isSuccess = false)
    }
}