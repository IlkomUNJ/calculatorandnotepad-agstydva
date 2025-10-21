package com.example.tugasmokom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tugasmokom.ui.theme.TugasMokomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TugasMokomTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val notesViewModel: NotesViewModel = viewModel()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }

        composable("calculator") {
            val viewModel: CalculatorViewModel = viewModel()
            Calculator(viewModel = viewModel, navController = navController)
        }

        composable("texteditor_dashboard") {
            DashboardTextEditorScreen(
                navController = navController,
                viewModel = notesViewModel
            )
        }

        composable("texteditor") {
            TextEditorScreen(
                navController = navController,
                viewModel = notesViewModel
            )
        }
    }
}
