package com.example.creativasprint.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("precio") val precio: Double,
    @SerializedName("color") val color: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("imagen") val imagen: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("stock") val stock: Int,
    @SerializedName("is_active") val isActive: Boolean = true
)
