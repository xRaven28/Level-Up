package com.example.labx.data.local.dao

import androidx.room.*
import com.example.labx.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE codigoPropio = :codigo LIMIT 1")
    suspend fun obtenerUsuarioPorCodigo(codigo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioEntity>

    @Delete
    suspend fun eliminarUsuario(usuario: UsuarioEntity)
}
