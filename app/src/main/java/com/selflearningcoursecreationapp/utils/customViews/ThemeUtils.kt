package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.extensions.getAttrColor
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.AppThemeFile
import com.selflearningcoursecreationapp.utils.FontConstant

object ThemeUtils {

    private var appThemeData: AppThemeFile? = null

    init {
        try {

            appThemeData =
                Gson().fromJson(SelfLearningApplication.themeFile, AppThemeFile::class.java)
        } catch (e: Exception) {
            showException(e)

        }

    }

    private fun getAppThemeFile(): AppThemeFile? {
        return try {

            Gson().fromJson(SelfLearningApplication.themeFile, AppThemeFile::class.java)
        } catch (e: Exception) {
            showException(e)
            null
        }
    }

    fun getAppColor(context: Context): Int {
        return if (!getAppThemeFile()?.themeColor.isNullOrEmpty() && SelfLearningApplication.isViOn == false)
            Color.parseColor(getAppThemeFile()?.themeColor)
        else ContextCompat.getColor(
            context,
            context.getAttrColor(R.attr.colorPrimary)
        )
    }

    fun getPrimaryBgColor(context: Context): Int {
        return if (!getAppThemeFile()?.primaryBgColor.isNullOrEmpty() && SelfLearningApplication.isViOn == false)
            Color.parseColor(getAppThemeFile()?.primaryBgColor)
        else ContextCompat.getColor(
            context,
            context.getAttrColor(R.attr.screenBackgroundColor)
        )
    }

    fun getSecondaryBgColor(context: Context): Int {
        return if (!getAppThemeFile()?.secondaryBgColor.isNullOrEmpty() && SelfLearningApplication.isViOn == false)
            Color.parseColor(getAppThemeFile()?.secondaryBgColor)
        else ContextCompat.getColor(
            context,
            context.getAttrColor(R.attr.secondaryScreenBgColor)
        )
    }

    fun getBtnTextColor(context: Context): Int {
        return if (!getAppThemeFile()?.btnTextColor.isNullOrEmpty() && SelfLearningApplication.isViOn == false)
            Color.parseColor(getAppThemeFile()?.btnTextColor)
        else ContextCompat.getColor(
            context,
            context.getAttrColor(R.attr.buttonTextColor)
        )
    }

    fun getBtnBgColor(context: Context): Int {
        return if (!getAppThemeFile()?.themeColor.isNullOrEmpty() && SelfLearningApplication.isViOn == false)
            Color.parseColor(getAppThemeFile()?.themeColor)
        else ContextCompat.getColor(
            context,
            context.getAttrColor(R.attr.buttonBackgroundColor)
        )
    }

    fun getTintColor(context: Context): Int {
        return if (!getAppThemeFile()?.themeTint.isNullOrEmpty() && SelfLearningApplication.isViOn == false)
            Color.parseColor(getAppThemeFile()?.themeTint)
        else ContextCompat.getColor(
            context,
            context.getAttrColor(R.attr.primaryTintColor)
        )
    }

    fun getBtnTintColor(context: Context): Int {
        return if (!getAppThemeFile()?.themeTint.isNullOrEmpty() && SelfLearningApplication.isViOn == false)
            Color.parseColor(getAppThemeFile()?.themeTint)
        else ContextCompat.getColor(
            context,
            context.getAttrColor(R.attr.buttonBackgroundTintColor)
        )
    }

    fun getPrimaryTextColor(context: Context): Int {
        return if (!getAppThemeFile()?.primaryTextColor.isNullOrEmpty() && SelfLearningApplication.isViOn == false)
            Color.parseColor(getAppThemeFile()?.primaryTextColor)
        else ContextCompat.getColor(
            context,
            context.getAttrColor(R.attr.primaryTextColor)
        )
    }


    fun getFont(fontId: Int, styleType: Int): Int {
        return when (fontId) {

            FontConstant.WORK_SANS -> {
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
            FontConstant.ROBOTO -> {
                val fontArray = listOf(
                    R.font.roboto_regular,
                    R.font.roboto_medium,
                    R.font.roboto_bold,
                    R.font.roboto_black
                )
                fontArray[styleType - 1]

//                R.font.edu_test
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

    fun getFontName(fontId: Int): Pair<String, String> {

        return when (fontId) {

            FontConstant.WORK_SANS -> {
                Pair(
                    "worksans_regular",
                    "worksans_regular.ttf"
                )

            }
            FontConstant.ROBOTO -> {
                Pair("roboto_regular", "roboto_regular.ttf")
            }

            else -> {
                Pair("ibm_regular", "ibm_regular.ttf")


            }
        }

    }


    fun getDarkColor(colorString: Int): Int {


        var red = (Color.red(colorString))
//        if (red >= 100) {
//            red -= 10
//        } else {
//            red += 10
//        }


        var green = (Color.green(colorString))
        if (green >= 150) {
            green -= 20
        } else {
            green += 20
        }


        var blue = (Color.blue(colorString))
        if (blue <= 235)
            blue += 20
        else blue -= 20





        showLog(
            "COLOR_HEX",
            String.format(
                "#%02x%02x%02x%02x",
                255,
                red,
                green,

                blue
            )
        )
        return Color.parseColor(
            String.format(
                "#%02x%02x%02x%02x",
                255,
                red,
                green,
                blue
            )
        )

    }

    fun getLightestColor(colorString: Int): Int {


        var red = (Color.red(colorString))
//        if (red >= 100) {
//            red -= 10
//        } else {
//            red += 10
//        }


        var green = (Color.green(colorString))
        if (green >= 150) {
            green -= 30
        } else {
            green += 30
        }


        var blue = (Color.blue(colorString))
        if (blue <= 235)
            blue += 30
        else blue -= 30





        showLog(
            "COLOR_HEX",
            String.format(
                "#%02x%02x%02x%02x",
                255,
                red,
                green,

                blue
            )
        )
        return Color.parseColor(
            String.format(
                "#%02x%02x%02x%02x",
                5,
                red,
                green,
                blue
            )
        )

    }

    fun isViOn(): Boolean = SelfLearningApplication.isViOn ?: false
}