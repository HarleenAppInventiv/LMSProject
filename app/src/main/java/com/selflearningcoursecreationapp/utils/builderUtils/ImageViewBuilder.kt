package com.selflearningcoursecreationapp.utils.builderUtils

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.extensions.isNullOrNegative
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.utils.blurHash.blurPlaceHolder
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class ImageViewBuilder {
    companion object {
        fun builder(imageView: ImageView): Builder {
            return Builder(imageView)
        }
    }

    class Builder(private var imageView: ImageView) {
        private var context = imageView.context
        private var imageUrl: String? = null
        private var placeHolderImage: Int? = null
        private var indexPosition: Int? = null
        private var isViOn: Boolean? = false
        private var gifFile: Int? = null
        private var blurHash: String? = null
        private var tintPrimary: Boolean? = null

        fun setImageUrl(url: String?): Builder {
            this.imageUrl = url
            return this
        }

        fun placeHolder(placeHolder: Int?): Builder {
            this.placeHolderImage = placeHolder
            return this
        }

        fun primaryTint(applyPrimaryTint: Boolean? = false): Builder {
            this.tintPrimary = applyPrimaryTint
            return this
        }

        fun colorIndex(position: Int?): Builder {
            this.indexPosition = position
            return this
        }

//        fun isViModeOn(isOn: Boolean? = false): Builder {
//            this.isViOn = isOn
//            return this
//        }

        fun loadGif(gif: Int?): Builder {
            this.gifFile = gif

            return this
        }

        fun blurhash(blurhash: String?): Builder {
            this.blurHash = blurhash
            return this

        }

        fun loadImage() {
            if (!indexPosition.isNullOrNegative()) {
                when (indexPosition!! % 4) {
                    0 -> {
                        imageView.setBackgroundColor(
                            context.getAttrResource(
                                R.attr.colorVariant_LightPink
                            )
                        )
                    }
                    1 -> {
                        imageView.setBackgroundColor(
                            context.getAttrResource(
                                R.attr.colorVariant_LightPurple
                            )
                        )

                    }
                    2 -> {
                        imageView.setBackgroundColor(
                            context.getAttrResource(
                                R.attr.colorVariant_LightYellow
                            )
                        )

                    }
                    3 -> {
                        imageView.setBackgroundColor(
                            context.getAttrResource(
                                R.attr.colorVariant_LightGreen
                            )
                        )

                    }
                    else -> {
                        imageView.setBackgroundColor(
                            context.getAttrResource(R.attr.colorVariant_LightPink)
                        )

                    }
                }
            }

            if (!gifFile.isNullOrZero()) {
                Glide.with(context).asGif().load(gifFile).into(imageView)
            } else if (!imageUrl.isNullOrEmpty()) {
                if (imageUrl!!.startsWith("http")) {


                    Glide.with(context).load(imageUrl)
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .apply {
                            if (!placeHolderImage.isNullOrZero()) {
                                placeholder(placeHolderImage!!)

                                error(placeHolderImage)
                            }


                            if (!blurHash.isNullOrEmpty()) {
                                blurPlaceHolder(blurHash!!, imageView) { requestBuilder ->
                                    requestBuilder.into(imageView)
                                }
                            } else {
                                into(imageView)
                            }
                        }
//                    .into(imageView)
                } else {
                    imageView.setImageURI(Uri.parse(imageUrl))
                }

            } else {
                placeHolderImage?.let { imageView.setImageResource(it) }
            }

            if (tintPrimary == true) {
                imageView.imageTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(context))
                imageView.imageTintMode = PorterDuff.Mode.SRC_IN
            }

//            if (isViOn == true) {
//                val colorMatrix = ColorMatrix()
//                colorMatrix.setSaturation(0.0f)
//                val filter = ColorMatrixColorFilter(colorMatrix)
//                imageView.colorFilter = filter
//            }
        }
    }
}