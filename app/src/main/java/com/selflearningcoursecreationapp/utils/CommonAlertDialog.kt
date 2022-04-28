package com.selflearningcoursecreationapp.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.LayoutSuccessBinding
import com.selflearningcoursecreationapp.extensions.gone
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
        private var positiveBtnText: String? = context.getString(R.string.ok)
        private var negativeBtnText: String? = context.getString(R.string.cancel)
        private var icon: Int? = R.drawable.ic_success
        private var theme: Int? = R.style.DialogTransparent
        private var isCancellable: Boolean = true
        private var hideNegativeBtn: Boolean = false
        private var onClick: (isPositive: Boolean) -> Unit = {}


        fun title(title: String): Builder {
            this.title = title
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

        fun icon(icon: Int?): Builder {
            this.icon = icon
            return this
        }

        fun notCancellable(): Builder {
            this.isCancellable = false
            return this
        }

        fun getCallback(callback: (isPositive: Boolean) -> Unit): Builder {
            onClick = callback
            return this
        }

        fun setTheme(theme: Int): Builder {
            this.theme = theme
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
                btnPositive.visibleView(!positiveBtnText.isNullOrEmpty())
                btnNegative.visibleView(!hideNegativeBtn)
                btnPositive.text = positiveBtnText
                btnNegative.text = negativeBtnText


                icon?.let {
                    imageView2.setImageResource(it)
                    imageView2.visible()

                } ?: kotlin.run {
                    imageView2.gone()
                }

                btnPositive.setOnClickListener {
                    onClick(true)
                    alertDialog.dismiss()
                }
                btnNegative.setOnClickListener {
                    onClick(false)
                    alertDialog.dismiss()
                }

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