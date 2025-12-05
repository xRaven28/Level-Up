package com.example.labx.data.repository

import com.example.labx.data.local.dao.UsuarioDao
import com.example.labx.data.local.entity.UsuarioEntity

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    suspend fun registrarUsuario(usuario: UsuarioEntity): Long {
        return usuarioDao.insertarUsuario(usuario)
    }

    suspend fun obtenerUsuarioPorId(id: Long): UsuarioEntity? {
        return usuarioDao.obtenerUsuarioPorId(id)
    }

    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity? {
        return usuarioDao.obtenerUsuarioPorEmail(email)
    }

    suspend fun obtenerUsuarioPorCodigo(codigo: String): UsuarioEntity? {
        return usuarioDao.obtenerUsuarioPorCodigo(codigo)
    }

    suspend fun obtenerUsuarios(): List<UsuarioEntity> {
        return usuarioDao.obtenerUsuarios()
    }

    suspend fun eliminarUsuario(usuario: UsuarioEntity) {
        usuarioDao.eliminarUsuario(usuario)
    }
}
