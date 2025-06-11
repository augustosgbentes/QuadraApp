package com.unifor.quadraapp.data.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.unifor.quadraapp.data.model.Agendamento
import kotlinx.coroutines.tasks.await

class AgendamentoRepository {
    private val database = FirebaseDatabase.getInstance().reference

    suspend fun criarAgendamento(agendamento: Agendamento): Result<String> {
        return try {
            Log.d("AgendamentoRepository", "Iniciando criação de agendamento...")
            Log.d("AgendamentoRepository", "Agendamento: $agendamento")

            // Gerar ID único para o agendamento
            val agendamentoId = database.child("agendamentos").push().key
            if (agendamentoId == null) {
                Log.e("AgendamentoRepository", "Erro: Não foi possível gerar ID para o agendamento")
                throw Exception("Erro ao gerar ID do agendamento")
            }

            Log.d("AgendamentoRepository", "ID gerado: $agendamentoId")

            // Criar agendamento com ID
            val agendamentoComId = agendamento.copy(id = agendamentoId)
            Log.d("AgendamentoRepository", "Agendamento com ID: $agendamentoComId")

            // Salvar no Firebase
            Log.d("AgendamentoRepository", "Salvando no Firebase...")
            database.child("agendamentos").child(agendamentoId)
                .setValue(agendamentoComId).await()

            Log.d("AgendamentoRepository", "Agendamento salvo com sucesso no Firebase!")
            Result.success("Agendamento criado com sucesso!")

        } catch (e: Exception) {
            Log.e("AgendamentoRepository", "Erro ao criar agendamento: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun buscarAgendamentosUsuario(userId: String): List<Agendamento> {
        return try {
            Log.d("AgendamentoRepository", "Buscando agendamentos para usuário: $userId")

            val snapshot = database.child("agendamentos")
                .orderByChild("userId")
                .equalTo(userId)
                .get().await()

            Log.d("AgendamentoRepository", "Snapshot obtido. Existe: ${snapshot.exists()}")
            Log.d("AgendamentoRepository", "Número de children: ${snapshot.childrenCount}")

            val agendamentos = snapshot.children.mapNotNull { dataSnapshot ->
                try {
                    val agendamento = dataSnapshot.getValue(Agendamento::class.java)
                    Log.d("AgendamentoRepository", "Agendamento encontrado: $agendamento")
                    agendamento
                } catch (e: Exception) {
                    Log.e("AgendamentoRepository", "Erro ao converter agendamento: ${e.message}")
                    null
                }
            }

            Log.d("AgendamentoRepository", "Total de agendamentos encontrados: ${agendamentos.size}")
            agendamentos

        } catch (e: Exception) {
            Log.e("AgendamentoRepository", "Erro ao buscar agendamentos: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun cancelarAgendamento(agendamentoId: String): Result<String> {
        return try {
            Log.d("AgendamentoRepository", "Cancelando agendamento: $agendamentoId")

            database.child("agendamentos").child(agendamentoId).removeValue().await()

            Log.d("AgendamentoRepository", "Agendamento cancelado com sucesso!")
            Result.success("Agendamento cancelado com sucesso!")

        } catch (e: Exception) {
            Log.e("AgendamentoRepository", "Erro ao cancelar agendamento: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun atualizarStatusAgendamento(agendamentoId: String, novoStatus: String): Result<String> {
        return try {
            Log.d("AgendamentoRepository", "Atualizando status do agendamento $agendamentoId para: $novoStatus")

            database.child("agendamentos").child(agendamentoId).child("status")
                .setValue(novoStatus).await()

            Log.d("AgendamentoRepository", "Status atualizado com sucesso!")
            Result.success("Status atualizado com sucesso!")

        } catch (e: Exception) {
            Log.e("AgendamentoRepository", "Erro ao atualizar status: ${e.message}", e)
            Result.failure(e)
        }
    }
}