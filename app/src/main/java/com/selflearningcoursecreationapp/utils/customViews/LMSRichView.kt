package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes

class LMSRichView : com.selflearningcoursecreationapp.utils.richView.RichPathView {


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
        findRichPathByName("primary_path1")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("primary_path2")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("primary_path3")?.fillColor = ThemeUtils.getAppColor(context)

        findRichPathByName("tint_path")?.fillColor = ThemeUtils.getTintColor(context)
        findRichPathByName("tint_path")?.fillAlpha = 0.3f
        findRichPathByName("primary_tint")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("primary_tint")?.fillAlpha = 0.99f

        findRichPathByName("stroke_tint")?.strokeColor = ThemeUtils.getTintColor(context)

        findRichPathByName("dark_path")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("dark_primary")?.fillColor =
            ThemeUtils.getDarkColor(ThemeUtils.getAppColor(context))
        findRichPathByName("dark_primary1")?.fillColor =
            ThemeUtils.getDarkColor(ThemeUtils.getAppColor(context))
        findRichPathByName("dark_path1")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("dark_path2")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("dark_path3")?.fillColor = ThemeUtils.getAppColor(context)
        findRichPathByName("light_path")?.fillColor = ThemeUtils.getTintColor(context)
        findRichPathByName("light_path2")?.fillColor = ThemeUtils.getTintColor(context)
//        findRichPathByName("light_path")?.fillAlpha = 0.95f
        findRichPathByName("light_path1")?.fillColor = ThemeUtils.getTintColor(context)
        findRichPathByName("light_path1")?.fillAlpha = 0.99f


    }

    override fun setVectorDrawable(@DrawableRes resId: Int) {
        super.setVectorDrawable(resId)
        initView(context)
    }


    fun setCustomColor(mainColor: Int) {
        findRichPathByName("primary_path")?.fillColor = mainColor
        findRichPathByName("primary_path1")?.fillColor = mainColor
        findRichPathByName("primary_path2")?.fillColor = mainColor
        findRichPathByName("primary_path3")?.fillColor = mainColor
        findRichPathByName("dark_path")?.fillColor = mainColor
        findRichPathByName("dark_path1")?.fillColor = mainColor
        findRichPathByName("dark_path2")?.fillColor = mainColor
        findRichPathByName("dark_path3")?.fillColor = mainColor

    }

    fun setSecondaryColor(secondaryColor: Int) {
        findRichPathByName("tint_path")?.fillColor = secondaryColor
        findRichPathByName("tint_path")?.fillAlpha = 0.3f
        findRichPathByName("stroke_tint")?.strokeColor = secondaryColor
        findRichPathByName("light_path")?.fillColor = secondaryColor
        findRichPathByName("light_path2")?.fillColor = secondaryColor
//        findRichPathByName("light_path")?.fillAlpha = 0.95f
        findRichPathByName("light_path1")?.fillColor = secondaryColor
        findRichPathByName("light_path1")?.fillAlpha = 0.99f


    }
}