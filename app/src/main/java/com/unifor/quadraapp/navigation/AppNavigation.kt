package com.unifor.quadraapp.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.unifor.quadraapp.ui.screens.*
import com.unifor.quadraapp.ui.viewmodel.AgendamentoViewModel

@Composable
fun AppNavigation(navController: NavHostController) {

    val agendamentoViewModel: AgendamentoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("cadastro")
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("cadastro") {
            CadastroScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToAgendamento = {
                    navController.navigate("agendamento")
                },
                onNavigateToMeusAgendamentos = {
                    navController.navigate("meus_agendamentos")
                },
                onNavigateToPerfil = {
                    navController.navigate("perfil")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("agendamento") {
            AgendamentoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetalhes = { quadraNome ->
                    navController.navigate("agendamento_detalhes/$quadraNome")
                }
            )
        }

        composable("agendamento_detalhes/{quadraNome}") { backStackEntry ->
            val quadraNome = backStackEntry.arguments?.getString("quadraNome") ?: ""
            AgendamentoDetalhesScreen(
                quadraNome = quadraNome,
                onNavigateBack = {
                    navController.popBackStack()
                },
                agendamentoViewModel = agendamentoViewModel
            )
        }

        composable("meus_agendamentos") {
            MeusAgendamentosScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                agendamentoViewModel = agendamentoViewModel
            )
        }

        composable("perfil") {
            PerfilScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}