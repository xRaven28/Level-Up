package com.example.labx.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.labx.data.local.dao.CarritoDao
import com.example.labx.data.local.dao.ProductoDao
import com.example.labx.data.local.dao.UsuarioDao
import com.example.labx.data.local.entity.CarritoEntity
import com.example.labx.data.local.entity.ProductoEntity
import com.example.labx.data.local.entity.UsuarioEntity

/**
 * Database principal de la app
 * Ahora incluye productos y carrito
 * Singleton para una única instancia en toda la app
 *
 * Autor: Prof. Sting Adams Parra Silva
 */
@Database(
    entities = [CarritoEntity::class, ProductoEntity::class, UsuarioEntity::class],
    version = 3, // Incrementado por agregar ProductoEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provee acceso al DAO de carrito
     */
    abstract fun carritoDao(): CarritoDao

    /**
     * Provee acceso al DAO de productos
     */
    abstract fun productoDao(): ProductoDao

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "labx_database"
                )
                    .fallbackToDestructiveMigration()  // recrea BD si cambia versión
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}