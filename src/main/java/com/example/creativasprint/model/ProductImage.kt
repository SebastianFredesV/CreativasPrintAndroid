package com.example.creativasprint.model

import com.google.gson.annotations.SerializedName

data class ProductImage(
    @SerializedName("access") val access: String,
    @SerializedName("path") val path: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("size") val size: Int,
    @SerializedName("mime") val mime: String,
    @SerializedName("url") val url: String,
    @SerializedName("meta") val meta: ImageMeta? = null
)

data class ImageMeta(
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)