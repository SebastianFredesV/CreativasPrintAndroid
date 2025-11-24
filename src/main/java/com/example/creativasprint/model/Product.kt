package com.example.creativasprint.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("precio") val precio: Double,
    @SerializedName("color") val color: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("imagen") val imagen: ProductImage, // Cambiar de String a ProductImage
    @SerializedName("categoria") val categoria: String,
    @SerializedName("stock") val stock: Int,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: Long? = null,
    @SerializedName("updated_at") val updatedAt: Long? = null
) {
    // Función helper para obtener la URL de la imagen fácilmente
    fun getImageUrl(): String {
        return imagen.url
    }

    // Función para verificar si el producto es válido (filtra el primer producto vacío)
    fun isValid(): Boolean {
        return nombre.isNotEmpty() && precio > 0 && isActive
    }
}