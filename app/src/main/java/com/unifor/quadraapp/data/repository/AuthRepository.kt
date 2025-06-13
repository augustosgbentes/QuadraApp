package com.unifor.quadraapp.data.repository

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.unifor.quadraapp.data.model.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance().reference

    suspend fun cadastrarUsuario(
        nome: String,
        email: String,
        senha: String,
        matricula: String
    ): Result<String> {
        return try {
            Log.d("AuthRepository", "Tentando cadastrar usuário: $email")

            val result = auth.createUserWithEmailAndPassword(email, senha).await()
            val userId = result.user?.uid ?: throw Exception("Erro ao criar usuário")

            Log.d("AuthRepository", "Usuário criado com ID: $userId")

            val user = User(
                id = userId,
                nome = nome,
                email = email,
                matricula = matricula,
                fotoUrl = ""
            )

            database.child("usuarios").child(userId).setValue(user).await()
            Log.d("AuthRepository", "Dados do usuário salvos no database")

            Result.success("Usuário cadastrado com sucesso!")
        } catch (e: FirebaseAuthException) {
            val errorMessage = when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Este email já está em uso"
                "ERROR_WEAK_PASSWORD" -> "Senha muito fraca (mínimo 6 caracteres)"
                "ERROR_INVALID_EMAIL" -> "Email inválido"
                else -> "Erro no cadastro: ${e.message}"
            }
            Log.e("AuthRepository", "Erro no cadastro: $errorMessage")
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro inesperado no cadastro: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun loginUsuario(emailOuMatricula: String, senha: String): Result<String> {
        return try {
            Log.d("AuthRepository", "Tentando fazer login: $emailOuMatricula")

            var emailParaLogin = emailOuMatricula


            if (!emailOuMatricula.contains("@")) {
                if (emailOuMatricula.length != 7 || !emailOuMatricula.all { it.isDigit() }) {
                    throw Exception("Matrícula deve ter exatamente 7 dígitos")
                }

                Log.d("AuthRepository", "Login com matrícula detectado: $emailOuMatricula")


                val snapshot = database.child("usuarios")
                    .orderByChild("matricula")
                    .equalTo(emailOuMatricula)
                    .get().await()

                Log.d("AuthRepository", "Snapshot existe: ${snapshot.exists()}")

                if (snapshot.exists()) {
                    val userData = snapshot.children.first().getValue(User::class.java)
                    emailParaLogin = userData?.email ?: throw Exception("Email não encontrado")
                    Log.d("AuthRepository", "Email encontrado: $emailParaLogin")
                } else {
                    throw Exception("Matrícula não encontrada")
                }
            }


            val result = auth.signInWithEmailAndPassword(emailParaLogin, senha).await()
            val user = result.user

            if (user != null) {
                Log.d("AuthRepository", "Login realizado com sucesso!")
                Result.success("Login realizado com sucesso!")
            } else {
                Result.failure(Exception("Erro ao fazer login"))
            }
        } catch (e: FirebaseAuthException) {
            val errorMessage = when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> "Usuário não encontrado"
                "ERROR_WRONG_PASSWORD" -> "Senha incorreta"
                "ERROR_INVALID_EMAIL" -> "Email inválido"
                "ERROR_USER_DISABLED" -> "Usuário desabilitado"
                "ERROR_TOO_MANY_REQUESTS" -> "Muitas tentativas. Tente novamente mais tarde"
                "ERROR_INVALID_CREDENTIAL" -> "Credenciais inválidas"
                else -> "Erro no login: ${e.message}"
            }
            Log.e("AuthRepository", "Erro no login: $errorMessage")
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro inesperado no login: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun buscarDadosUsuario(userId: String): Result<User> {
        return try {
            Log.d("AuthRepository", "Buscando dados do usuário: $userId")

            val snapshot = database.child("usuarios").child(userId).get().await()
            val user = snapshot.getValue(User::class.java)

            if (user != null) {
                Log.d("AuthRepository", "Dados do usuário encontrados: ${user.nome}")
                Result.success(user)
            } else {
                Log.e("AuthRepository", "Dados do usuário não encontrados")
                Result.failure(Exception("Dados do usuário não encontrados"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao buscar dados do usuário: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun alterarSenha(senhaAtual: String, novaSenha: String): Result<String> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não autenticado")
            val email = user.email ?: throw Exception("Email não encontrado")

            Log.d("AuthRepository", "Alterando senha do usuário: $email")

            val credential = EmailAuthProvider.getCredential(email, senhaAtual)
            user.reauthenticate(credential).await()

            user.updatePassword(novaSenha).await()

            Log.d("AuthRepository", "Senha alterada com sucesso")
            Result.success("Senha alterada com sucesso!")
        } catch (e: FirebaseAuthException) {
            val errorMessage = when (e.errorCode) {
                "ERROR_WRONG_PASSWORD" -> "Senha atual incorreta"
                "ERROR_WEAK_PASSWORD" -> "Nova senha muito fraca (mínimo 6 caracteres)"
                "ERROR_REQUIRES_RECENT_LOGIN" -> "É necessário fazer login novamente"
                else -> "Erro ao alterar senha: ${e.message}"
            }
            Log.e("AuthRepository", "Erro ao alterar senha: $errorMessage")
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro inesperado ao alterar senha: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun atualizarFotoUsuario(userId: String, novaFotoUrl: String): Result<String> {
        return try {
            Log.d("AuthRepository", "Atualizando foto do usuário: $userId")

            database.child("usuarios").child(userId).child("fotoUrl").setValue(novaFotoUrl).await()

            Log.d("AuthRepository", "Foto atualizada com sucesso")
            Result.success("Foto atualizada com sucesso!")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erro ao atualizar foto: ${e.message}")
            Result.failure(e)
        }
    }

    fun logout() {
        Log.d("AuthRepository", "Fazendo logout")
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    fun isUserLoggedIn(): Boolean {
        val user = auth.currentUser
        Log.d("AuthRepository", "Usuário logado: ${user != null}")
        return user != null
    }
}