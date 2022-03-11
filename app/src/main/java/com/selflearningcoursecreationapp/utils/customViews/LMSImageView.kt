package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.models.AppThemeFile

class LMSImageView: AppCompatImageView {

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        initView(context, attrs, defStyle)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyle: Int? = 0) {
        val themeAttrs = context.obtainStyledAttributes(
            attrs, R.styleable.LMSImageView,
            defStyle!!, 0
        )

        val changeBgType =
            themeAttrs.getInt(R.styleable.LMSImageView_changeBackgroundType, ThemeConstants.TYPE_NONE)
        when(changeBgType)
        {
            ThemeConstants.TYPE_TINT->{

                imageTintList= ColorStateList.valueOf(ThemeUtils.getAppColor(context))
                imageTintMode=PorterDuff.Mode.MULTIPLY
            }
            ThemeConstants.TYPE_TINT_SRC->{

                imageTintList= ColorStateList.valueOf(ThemeUtils.getAppColor(context))
                imageTintMode=PorterDuff.Mode.SRC_IN
            }
            ThemeConstants.TYPE_BACKGROUND->{
                setBackgroundColor(ThemeUtils.getAppColor(context))
            }
        }

themeAttrs.recycle()
    }

}