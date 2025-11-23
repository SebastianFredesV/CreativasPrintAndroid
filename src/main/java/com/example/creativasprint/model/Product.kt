package com.example.creativasprint.model

data class Product(
    val id: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val color: String = "",
    val descripcion: String = "",
    val imagen: String = "",
    val categoria: String = "",
    val images: List<String> = emptyList(),
    val stock: Int = 0,
    val isActive: Boolean = true
) {
    // Constructor para el JSON que tienes
    constructor(
        nombre: String,
        precio: String,
        color: String,
        descripcion: String,
        imagen: String
    ) : this(
        id = "",
        nombre = nombre,
        precio = precio.toDoubleOrNull() ?: 0.0,
        color = color,
        descripcion = descripcion,
        imagen = imagen,
        categoria = "General"
    )
}