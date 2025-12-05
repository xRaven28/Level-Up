package com.example.labx.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreCompleto: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val anioNacimiento: Int,
    val esDuoc: Boolean,
    val codigoPropio: String,
    val codigoReferido: String?,
    val puntosLevelUp: Int = 0,
    val nivel: Int = 1
)

