package com.selflearningcoursecreationapp.extensions

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.blurHash.blurPlaceHolder


@SuppressLint("CheckResult")
fun ImageView.loadImage(url: String?, placeholder: Int?, index: Int) {
    when {
        url.isNullOrEmpty() -> {
            placeholder?.let { setImageResource(it) }
            when (index % 4) {
                0 -> {
                    this.setBackgroundColor(ContextCompat.getColor(context, R.color.red_light))
                }
                1 -> {
                    this.setBackgroundColor(ContextCompat.getColor(context, R.color.violet_light))

                }
                2 -> {
                    this.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_light))

                }
                3 -> {
                    this.setBackgroundColor(ContextCompat.getColor(context, R.color.green_light))

                }
                else -> {
                    this.setBackgroundColor(ContextCompat.getColor(context, R.color.red_light))

                }
            }

        }
        url.startsWith("http") -> {
            Glide.with(context).load(url)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .also {
                    if (!placeholder.isNullOrZero()) {
                        if (placeholder != null) {
                            it.placeholder(placeholder)
                        }
                        it.error(placeholder)
                    }
                }
                .into(this)
        }
        else -> {
            setImageURI(Uri.parse(url))
        }

    }

}


fun ImageView.loadGif(gif: Int) {
    Glide.with(context).asGif().load(gif).into(this)
}


@SuppressLint("CheckResult")
fun ImageView.loadImage(url: String?, placeholder: Int?) {
    when {
        url.isNullOrEmpty() -> {
            placeholder?.let { setImageResource(it) }
        }
        url.startsWith("http") -> {
            Glide.with(context).load(url)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .also {
                    if (!placeholder.isNullOrZero()) {
                        if (placeholder != null) {
                            it.placeholder(placeholder)
                        }
                        it.error(placeholder)
                    }
                }
                .into(this)
        }
        else -> {
            setImageURI(Uri.parse(url))
        }

    }

}

@SuppressLint("CheckResult")
fun ImageView.loadImage(url: String?, placeholder: Int?, keyHash: String?) {
    if (keyHash.isNullOrEmpty()) {
        loadImage(url, placeholder)
    } else {
        when {
            url.isNullOrEmpty() -> {
                placeholder?.let { setImageResource(it) }
            }
            url.startsWith("http") -> {
                Glide.with(context).load(url)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .also {
                        if (!placeholder.isNullOrZero()) {
                            placeholder?.let { place -> it.placeholder(place) }
                            it.error(placeholder)
                        }
                    }
                    .blurPlaceHolder(keyHash, this)
                    { requestBuilder ->
                        requestBuilder.into(this)
                    }
//                .into(this)
            }
            else -> {
                setImageURI(Uri.parse(url))
            }
        }
    }

}


fun ImageView.loadImage(drawable: Int?) {

    drawable?.let { setImageResource(it) }


}