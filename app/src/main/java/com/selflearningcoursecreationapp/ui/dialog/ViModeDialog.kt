package com.selflearningcoursecreationapp.ui.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.DialogViLayoutBinding
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class ViModeDialog : BaseDialog<DialogViLayoutBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.dialog_vi_layout
    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        var colorString = ThemeUtils.getAppColor(baseActivity)
        arguments?.let {
            colorString =
                Color.parseColor(
                    it.getString(
                        "colorString",
                        baseActivity.getString(R.color.primaryColor)
                    )
                )
        }

//        binding.btnClose.backgroundTintList = null
//        binding.imageView2.setCustomColor(colorString)
//        binding.imageView2.setSecondaryColor(
//            Color.parseColor(
//                String.format(
//                    "#%02x%02x%02x%02x",
//                    80,
//                    Color.red(colorString),
//                    Color.green(colorString),
//                    Color.blue(colorString)
//                )
//            )
//        )

//        binding.btnClose.backgroundTintList = ColorStateList.valueOf(colorString)
        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun onApiRetry(apiCode: String) {

    }


}