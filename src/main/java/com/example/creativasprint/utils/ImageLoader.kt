package com.example.creativasprint.utils

import android.widget.ImageView
import coil.load
import com.example.creativasprint.R

object ImageLoader {
    fun loadImage(imageView: ImageView, url: String) {
        imageView.load(url) {
            crossfade(true)
            placeholder(R.drawable.ic_image_placeholder)
            error(R.drawable.ic_image_placeholder)
        }
    }
}