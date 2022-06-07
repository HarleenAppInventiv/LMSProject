package com.selflearningcoursecreationapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.selflearningcoursecreationapp.databinding.DialogRequestFailedBinding

class CommonErrorDialog {

    companion object {
        fun builder(context: Context): Builder {
            return Builder(context)
        }
    }

    class Builder(val context: Context) {

        private var image = -1
        private var mainHeading = ""
        private var subHeading = ""
        private var buttonText = ""
        private var isCancellable = false
        private var onClick: (isPositive: Boolean) -> Unit = {}

        fun configureErrorDialog(
            image: Int,
            mainHeading: String,
            subHeading: String,
            buttonText: String,
            isCancellable: Boolean
        ): Builder {
            this@Builder.image = image
            this@Builder.mainHeading = mainHeading
            this@Builder.subHeading = subHeading
            this@Builder.buttonText = buttonText
            this@Builder.isCancellable = isCancellable
            return this
        }

        fun getCallback(callback: (isPositive: Boolean) -> Unit): Builder {
            onClick = callback
            return this
        }

        fun build(): AlertDialog {
            val alertDialog =
                AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                    .create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            alertDialog.setCancelable(isCancellable)
            val binding =
                DialogRequestFailedBinding.inflate(LayoutInflater.from(context), null, false)
            alertDialog.show()
            alertDialog.setContentView(binding.root)

            binding.apply {
                Glide.with(context).load(image)
                    .into(ivError)

                tvErrorHeading.text = mainHeading
                tvErrorSubHeading.text = subHeading
                btnSubmit.text = buttonText

                btnSubmit.setOnClickListener {
                    onClick(false)
                    alertDialog.dismiss()
                }
            }

            return alertDialog
        }

    }

}