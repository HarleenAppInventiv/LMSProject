package com.selflearningcoursecreationapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.DialogFileProgressBinding
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class FileProgressDialog(private var mContext: Context, private var message: String?) :
    Dialog(mContext) {
    private lateinit var binding: DialogFileProgressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate<DialogFileProgressBinding>(
            LayoutInflater.from(mContext),
            R.layout.dialog_file_progress,
            null,
            false
        )
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.run {
            setBackgroundDrawable(null)
            setLayout(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
//            attributes?.windowAnimations = R.style.DialogBounceAnimation
        }
//        tv_title.visibleView(!message.isNullOrEmpty())
//        tv_title.text = message ?: ""
        binding.progressBar.progressTintList =
            ColorStateList.valueOf(ThemeUtils.getAppColor(mContext))
        binding.progressBar.progressTintMode = PorterDuff.Mode.ADD
        binding.progressBar.indeterminateTintList =
            ColorStateList.valueOf(ThemeUtils.getAppColor(mContext))
        binding.progressBar.indeterminateTintMode = PorterDuff.Mode.SRC_IN

    }

    fun setLoadingMessage(message: String?) {
//        tv_title.visibleView(!message.isNullOrEmpty())
//        tv_title.text = message ?: ""
    }
}