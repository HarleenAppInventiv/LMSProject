package com.selflearningcoursecreationapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.FrameLayout
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.android.synthetic.main.dialog_progress.*

class ProgressDialog(private var mContext: Context, private var message: String?) :
    Dialog(mContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.run {
            setBackgroundDrawable(null)
            setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.CENTER)
//            attributes?.windowAnimations = R.style.DialogBounceAnimation
        }
        tv_title.visibleView(!message.isNullOrEmpty())
        tv_title.text = message ?: ""
        pb_loading.progressTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(mContext))
        pb_loading.progressTintMode = PorterDuff.Mode.ADD
        pb_loading.indeterminateTintList = ColorStateList.valueOf(ThemeUtils.getAppColor(mContext))
        pb_loading.indeterminateTintMode = PorterDuff.Mode.SRC_IN

    }

    fun setLoadingMessage(message: String?) {
        tv_title.visibleView(!message.isNullOrEmpty())
        tv_title.text = message ?: ""
    }
}