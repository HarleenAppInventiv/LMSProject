package com.selflearningcoursecreationapp.extensions

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.LayoutSuccessBinding


fun Context.showAlertDialog(
    title: String? = getString(R.string.successful),
    description: String? = "",
    buttonText: String? = getString(R.string.ok),
    negativeButtonText: String? = null,
    style: Int = R.style.DialogTransparent,
    icon: Int? = R.drawable.ic_success,
    isCancellable: Boolean = true,
    spanText: SpannableString? = null,
    onClick: (isPositive: Boolean) -> Unit
): AlertDialog {


    val alertDialog = AlertDialog.Builder(this, style).create()
    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alertDialog.window?.setLayout(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val binding = LayoutSuccessBinding.inflate(LayoutInflater.from(this), null, false)
    alertDialog.show()
    alertDialog.setContentView(binding.root)
    alertDialog.setCancelable(isCancellable)

    binding.tvTitle.text = title



    binding.tvTitle.visibleView(!title.isNullOrEmpty())

    binding.btnPositive.visibleView(!buttonText.isNullOrEmpty())
    binding.btnNegative.visibleView(!negativeButtonText.isNullOrEmpty())

    binding.btnPositive.text = buttonText
    binding.btnNegative.text = negativeButtonText
    icon?.let {
        binding.imageView2.setImageResource(it)
        binding.imageView2.visible()

    } ?: kotlin.run {
        binding.imageView2.gone()
    }


    binding.tvMsg.apply {
        visible()
        if (!description.isNullOrEmpty()) {
            text = description
        } else if (!spanText.isNullOrEmpty()) {
            text = spanText
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor =
                ContextCompat.getColor(this@showAlertDialog, android.R.color.transparent)

        } else {
            gone()
        }
    }


    binding.btnPositive.setOnClickListener {
        onClick(true)
        alertDialog.dismiss()
    }
    binding.btnNegative.setOnClickListener {
        onClick(false)
        alertDialog.dismiss()
    }

    return alertDialog
}