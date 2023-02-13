package com.selflearningcoursecreationapp.utils.builderUtils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.LocaleSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import java.util.*

class SpanUtils {

    companion object {
        fun with(context: Context, message: String): Builder {
            return Builder(context, message)
        }
    }

    class Builder(private var context: Context, private var message: String) {
        private var isBold: Boolean = false
        private var attrColor: Int = R.attr.primaryTextColor
        private var color: Int? = null
        private var themeColor: Int? = null
        private var startPos: Int = 0
        private var isUnderline: Boolean = false
        private var endPos: Int = message.length
        private var size: Float = 1f
        private var onClick: () -> Unit = {}

        fun isBold(isBold: Boolean = true): Builder {
            this.isBold = isBold
            return this
        }

        fun setSpannedTextSize(size: Float = 1f): Builder {
            this.size = size
            return this
        }

        fun isUnderline(): Builder {
            isUnderline = true
            return this
        }

        fun themeColor(): Builder {
            themeColor = ThemeUtils.getAppColor(context)
            return this
        }

        fun textColor(color: Int? = null): Builder {
            this.color = color ?: ThemeUtils.getPrimaryTextColor(context)
            return this
        }


        fun startPos(pos: Int): Builder {
            startPos = pos
            return this
        }

        fun endPos(pos: Int): Builder {
            endPos = pos
            return this
        }

        fun getCallback(callback: () -> Unit): Builder {
            onClick = callback
            return this
        }

        fun getSpanString(): SpannableString {
            val ss = SpannableString(message)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    onClick()
                }

                @SuppressLint("ResourceType")
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = isUnderline
                    if (!themeColor.isNullOrZero()) {
                        ds.color = themeColor!!
                    } else if (!color.isNullOrZero())
                        ds.color = color!!
                    else {
                        val typedValue = TypedValue()
                        val theme = context.theme
                        theme.resolveAttribute(attrColor, typedValue, true)
                        ds.color = ContextCompat.getColor(context, typedValue.resourceId)
//                        val fontName = ThemeUtils.getFontName(SelfLearningApplication.fontId)
//                        val font = Typeface.createFromAsset(context.assets, "fonts/edu_test.ttf")
//
//                        ds.typeface= font

                    }
                    if (isBold) {
                        ds.typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.resources.getFont(

                                ThemeUtils.getFont(
                                    SelfLearningApplication.fontId,
                                    ThemeConstants.FONT_BOLD
                                )
                            )
                        } else {
                            ResourcesCompat.getFont(
                                context,
                                ThemeUtils.getFont(
                                    SelfLearningApplication.fontId,
                                    ThemeConstants.FONT_BOLD
                                )
                            )
                        }
                    }
                }
            }

            if (isBold) {
                ss.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startPos,
                    endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            ss.setSpan(LocaleSpan(Locale.getDefault()), 0, ss.length, 0)
            ss.setSpan(RelativeSizeSpan(size), startPos, endPos, 0)

            ss.setSpan(clickableSpan, startPos, endPos, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            return ss
        }

    }
}