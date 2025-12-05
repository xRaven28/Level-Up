package com.example.labx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.labx.data.local.AppDatabase
import com.example.labx.data.local.PreferenciasManager
import com.example.labx.data.local.ProductoInicializador
import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.ui.navigation.NavGraph
import com.example.labx.ui.theme.LevelUpTheme
import com.example.labx.ui.viewmodel.ProductoViewModel
import com.example.labx.ui.viewmodel.ProductoViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        ProductoInicializador.inicializarProductos(applicationContext)

        val productoRepository = ProductoRepositoryImpl(database.productoDao())
        val preferenciasManager = PreferenciasManager(applicationContext)

        setContent {

            LevelUpTheme {

                val navController = rememberNavController()

                val productoViewModel: ProductoViewModel = viewModel(
                    factory = ProductoViewModelFactory(productoRepository)
                )

                NavGraph(
                    navController = navController,
                    productoRepository = productoRepository,
                    preferenciasManager = preferenciasManager,
                    productoViewModel = productoViewModel
                )
            }
        }
    }
}
