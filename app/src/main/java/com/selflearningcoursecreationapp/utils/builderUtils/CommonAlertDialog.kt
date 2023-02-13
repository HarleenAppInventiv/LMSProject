package com.selflearningcoursecreationapp.utils.builderUtils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.LayoutSuccessBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.isNullOrNegative
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView

class CommonAlertDialog {

    companion object {
        fun builder(context: Context): Builder {
            return Builder(context)
        }
    }

    class Builder(private var context: Context) {

        private var title: String? = context.getString(R.string.successful)
        private var description: String? = ""
        private var spannedText: SpannableString? = null
        private var positiveBtnText: String? = context.getString(R.string.okay)
        private var negativeBtnText: String? = context.getString(R.string.cancel)
        private var icon: Int? = R.drawable.ic_checked_logo
        private var theme: Int? = R.style.DialogTransparent
        private var isCancellable: Boolean = true
        private var hideNegativeBtn: Boolean = false
        private var hidePositiveBtn: Boolean = false
        private var isPositiveCapsOn: Boolean = true
        private var isNegativeCapsOn: Boolean = true
        private var themeIconColor: Boolean = false
        private var type: Int? = null
        private var positiveBgColor: Int? = null
        private var iconMainColor: Int? = null
        private var iconSecondaryColor: Int? = null
        private var positiveTextColor: Int? = null
        private var positiveStrokeColor: Int? = null
        private var negativeBgColor: Int? = null
        private var negativeTextColor: Int? = null
        private var negativeStrokeColor: Int? = null
        private var isCloseIconEnabled: Boolean = false
        private var onClick: (isPositive: Boolean) -> Unit = {}


        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun type(type: Int): Builder {
            this.type = type
            return this
        }

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun spannedText(spannedText: SpannableString): Builder {
            this.spannedText = spannedText
            return this
        }

        fun positiveBtnText(positiveBtnText: String): Builder {
            this.positiveBtnText = positiveBtnText
            return this
        }

        fun negativeBtnText(negativeBtnText: String): Builder {
            this.negativeBtnText = negativeBtnText
            return this
        }

        fun hideNegativeBtn(isHide: Boolean = false): Builder {
            this.hideNegativeBtn = isHide
            return this
        }

        fun isCloseIconEnabled(isCloseIconEnabled: Boolean = false): Builder {
            this.isCloseIconEnabled = isCloseIconEnabled
            return this
        }

        fun hidePositiveBtn(isHide: Boolean = false): Builder {
            this.hidePositiveBtn = isHide
            return this
        }

        fun icon(icon: Int?): Builder {
            this.icon = icon
            return this
        }

        fun notCancellable(b: Boolean = false): Builder {
            this.isCancellable = b
            return this
        }

        fun getCallback(callback: (isPositive: Boolean) -> Unit): Builder {
            onClick = callback
            return this
        }

        fun getCloseButtonCallback(isVisible: Boolean): Builder {
            this.isCloseIconEnabled = isVisible
            return this
        }

        fun setPositiveInCaps(isCapsOn: Boolean = true): Builder {
            isPositiveCapsOn = isCapsOn
            return this
        }

        fun setNegativeInCaps(isCapsOn: Boolean = true): Builder {

            isNegativeCapsOn = isCapsOn
            return this
        }

        fun setVectorIconColor(mainColor: Int? = -1, secondaryColor: Int? = -1): Builder {
            iconMainColor = mainColor
            iconSecondaryColor = secondaryColor
            return this
        }

        fun setThemeIconColor(themeIconColor: Boolean = false): Builder {
            this.themeIconColor = themeIconColor
            return this
        }


        fun setTheme(theme: Int): Builder {
            this.theme = theme
            return this
        }

        fun setPositiveButtonTheme(
            bgColor: Int = -1,
            textColor: Int = -1,
            strokeColor: Int = -1
        ): Builder {
            positiveBgColor = bgColor
            positiveTextColor = textColor
            positiveStrokeColor = strokeColor
            return this
        }

        fun setNegativeButtonTheme(
            bgColor: Int = -1,
            textColor: Int = -1,
            strokeColor: Int = -1
        ): Builder {
            negativeBgColor = bgColor
            negativeTextColor = textColor
            negativeStrokeColor = strokeColor
            return this
        }


        fun build(): AlertDialog {
            val alertDialog =
                AlertDialog.Builder(context, R.style.DialogTransparent).create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding = LayoutSuccessBinding.inflate(LayoutInflater.from(context), null, false)



            alertDialog.show()
            alertDialog.setContentView(binding.root)
            alertDialog.setCancelable(isCancellable)

            binding.apply {
                tvTitle.text = title
                tvTitle.visibleView(!title.isNullOrEmpty())
                btnPositive.visibleView(!hidePositiveBtn)
                btnNegative.visibleView(!hideNegativeBtn)
                btnPositive.text = positiveBtnText
                btnNegative.text = negativeBtnText


                icon?.let {
                    if (!iconMainColor.isNullOrNegative() || !iconSecondaryColor.isNullOrNegative() || themeIconColor) {
                        imageView2.setVectorDrawable(it)
                    } else {
                        imageView2.setImageResource(it)
//
                    }
                    imageView2.visible()

                } ?: kotlin.run {
                    imageView2.gone()
                }
                if (isCloseIconEnabled) {
                    icClose.visible()
                }

                btnPositive.setOnClickListener {
                    btnPositive.isClickable = false
                    onClick(true)
                    alertDialog.dismiss()
                }
                btnNegative.setOnClickListener {
                    btnNegative.isClickable = false
                    onClick(false)
                    alertDialog.dismiss()
                }

                icClose.setOnClickListener {
                    alertDialog.dismiss()
                }

            }

            if (!positiveBgColor.isNullOrNegative()) {

                ViewCompat.setBackgroundTintList(
                    binding.btnPositive,
                    ColorStateList.valueOf(ContextCompat.getColor(context, positiveBgColor!!))
                )

            }

            if (!positiveTextColor.isNullOrNegative()) {
                binding.btnPositive.setTextColor(
                    ContextCompat.getColor(
                        context,
                        positiveTextColor!!
                    )
                )
            }
            if (!positiveStrokeColor.isNullOrNegative()) {
                binding.btnPositive.strokeWidth =
                    context.resources.getDimensionPixelOffset(R.dimen._1sdp)
                binding.btnPositive.strokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, positiveStrokeColor!!))
            }

            if (!negativeTextColor.isNullOrNegative()) {
                binding.btnNegative.setTextColor(
                    ContextCompat.getColor(
                        context,
                        negativeTextColor!!
                    )
                )
            }
            if (!negativeStrokeColor.isNullOrNegative()) {
                binding.btnNegative.strokeWidth =
                    context.resources.getDimensionPixelOffset(R.dimen._1sdp)
                binding.btnNegative.strokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, negativeStrokeColor!!))
            }

            if (!negativeBgColor.isNullOrNegative()) {

                ViewCompat.setBackgroundTintList(
                    binding.btnNegative,
                    ColorStateList.valueOf(ContextCompat.getColor(context, negativeBgColor!!))
                )

            }

            binding.btnPositive.isAllCaps = isPositiveCapsOn
            binding.btnNegative.isAllCaps = isNegativeCapsOn
            if (!iconMainColor.isNullOrNegative()) {
                binding.imageView2.setCustomColor(iconMainColor!!)
            }
            if (!iconSecondaryColor.isNullOrNegative()) {
                binding.imageView2.setSecondaryColor(iconSecondaryColor!!)
            }


            binding.tvMsg.apply {
                visible()
                if (!description.isNullOrEmpty()) {
                    text = description
                } else if (!spannedText.isNullOrEmpty()) {
                    text = spannedText
                    movementMethod = LinkMovementMethod.getInstance()
                    highlightColor =
                        ContextCompat.getColor(context, android.R.color.transparent)

                } else {
                    gone()
                }
            }


            return alertDialog
        }
    }
}