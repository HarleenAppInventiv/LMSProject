package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.models.AppThemeFile
import com.selflearningcoursecreationapp.utils.FONT_CONSTANT

object ThemeUtils {

    var appThemeData: AppThemeFile? = null

    init {
        try {

            appThemeData =
                Gson().fromJson(SelfLearningApplication.themeFile, AppThemeFile::class.java)
        } catch (e: Exception) {
            showException(e)

        }

    }

    fun getAppThemeFile(): AppThemeFile? {
        try {

            return Gson().fromJson(SelfLearningApplication.themeFile, AppThemeFile::class.java)
        } catch (e: Exception) {
            showException(e)
            return null
        }
    }

    fun getAppColor(context: Context): Int {
        return if (!getAppThemeFile()?.themeColor.isNullOrEmpty())
            Color.parseColor(getAppThemeFile()?.themeColor)
        else ContextCompat.getColor(context, R.color.blue)
    }

    fun getPrimaryBgColor(context: Context): Int {
        return if (!getAppThemeFile()?.primaryBgColor.isNullOrEmpty())
            Color.parseColor(getAppThemeFile()?.primaryBgColor)
        else ContextCompat.getColor(context, R.color.white)
    }

    fun getSecondaryBgColor(context: Context): Int {
        return if (!getAppThemeFile()?.secondaryBgColor.isNullOrEmpty())
            Color.parseColor(getAppThemeFile()?.secondaryBgColor)
        else ContextCompat.getColor(context, R.color.intro_btn_bg_color_f5f5f5)
    }

    fun getBtnTextColor(context: Context): Int {
        return if (!getAppThemeFile()?.btnTextColor.isNullOrEmpty())
            Color.parseColor(getAppThemeFile()?.btnTextColor)
        else ContextCompat.getColor(context, R.color.blue)
    }

    fun getTintColor(context: Context): Int {
        return if (!getAppThemeFile()?.themeTint.isNullOrEmpty())
            Color.parseColor(getAppThemeFile()?.themeTint)
        else ContextCompat.getColor(context, R.color.blue_tint_color)
    }

    fun getPrimaryTextColor(context: Context): Int {
        return if (!getAppThemeFile()?.primaryTextColor.isNullOrEmpty())
            Color.parseColor(getAppThemeFile()?.primaryTextColor)
        else ContextCompat.getColor(context, R.color.text_color_black_131414)
    }


    fun getFont(fontId: Int, styleType: Int): Int {
        return when (fontId) {

            FONT_CONSTANT.WORK_SANS -> {
                when (styleType) {
                    ThemeConstants.FONT_MEDIUM -> {
                        R.font.worksans_medium
                    }
                    ThemeConstants.FONT_SEMI_BOLD -> {
                        R.font.worksans_semibold
                    }
                    ThemeConstants.FONT_BOLD -> {
                        R.font.worksans_bold
                    }
                    else -> {
                        R.font.worksans_regular
                    }
                }
            }
            FONT_CONSTANT.ROBOTO -> {
                val fontArray = listOf(
                    R.font.roboto_regular,
                    R.font.roboto_medium,
                    R.font.roboto_bold,
                    R.font.roboto_black
                )
                fontArray[styleType - 1]
            }

            else -> {
                val fontArray = listOf(
                    R.font.ibm_regular,
                    R.font.ibm_medium,
                    R.font.ibm_semibold,
                    R.font.ibm_bold
                )
                fontArray[styleType - 1]
            }
        }

    }
}