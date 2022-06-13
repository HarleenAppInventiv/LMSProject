package com.selflearningcoursecreationapp.extensions

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


fun ImageView.loadImage(url: String?, placeholder: Int?) {
    if (url.isNullOrEmpty()) {
        placeholder?.let { setImageResource(it) }
    } else if (url.startsWith("http")) {
        Glide.with(context).load(url).apply {
            skipMemoryCache(false)
            diskCacheStrategy(DiskCacheStrategy.ALL)
            if (!placeholder.isNullOrZero()) {
                placeholder(placeholder!!)
                error(placeholder)
            }
        }
            .into(this)
    } else {
        setImageURI(Uri.parse(url))
    }

}

fun ImageView.loadImage(drawable: Int?) {

    drawable?.let { setImageResource(it) }


}