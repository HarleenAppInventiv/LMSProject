package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.util.AttributeSet
import com.richpath.RichPathView

class LMSRichView : RichPathView {
    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context)
    }


    private fun initView(context: Context) {

        findRichPathByName("primary_path")?.fillColor = ThemeUtils.getAppColor(context)

        findRichPathByName("tint_path")?.fillColor = ThemeUtils.getTintColor(context)
        findRichPathByName("tint_path")?.fillAlpha = 0.5f

        findRichPathByName("dark_path")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("dark_path1")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("light_path")?.fillColor = ThemeUtils.getTintColor(context)
//        findRichPathByName("light_path")?.fillAlpha = 0.95f
        findRichPathByName("light_path1")?.fillColor = ThemeUtils.getTintColor(context)
        findRichPathByName("light_path1")?.fillAlpha = 0.99f


    }
}