package com.example.labx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.labx.data.local.AppDatabase
import com.example.labx.data.local.PreferenciasManager
import com.example.labx.data.local.ProductoInicializador
import com.example.labx.ui.navigation.NavGraph
import com.example.labx.ui.theme.LevelUpTheme
import com.example.labx.ui.viewmodel.CarritoViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDatabase.getDatabase(applicationContext)
        ProductoInicializador.inicializarProductos(applicationContext)
        val preferenciasManager = PreferenciasManager(applicationContext)

        setContent {
            LevelUpTheme {

                val navController = rememberNavController()

                // ✅ ESTE ES EL ÚNICO CARRITO DEL SISTEMA
                val carritoViewModel: CarritoViewModel = viewModel()

                NavGraph(
                    navController = navController,
                    preferenciasManager = preferenciasManager,
                    carritoViewModel = carritoViewModel
                )
            }
        }
    }
}
