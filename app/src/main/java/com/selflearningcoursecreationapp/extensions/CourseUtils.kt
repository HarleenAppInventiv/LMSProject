package com.selflearningcoursecreationapp.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.customViews.LMSMaterialButton
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView

fun Int.getMediaType(showGreen: Boolean = true): Pair<Int, Int> {
    return when (this) {
        MediaType.VIDEO -> {
            Pair(
                R.drawable.ic_video_icon, R.string.video
            )
        }
        MediaType.AUDIO -> {
            Pair(R.drawable.ic_audio_icon, R.string.audio)

        }
        MediaType.DOC -> {
            Pair(R.drawable.ic_text_icon, R.string.doc)
        }
        MediaType.TEXT -> {
            Pair(R.drawable.ic_docx_icon, R.string.text)

        }
        else -> {
            Pair(if (showGreen) R.drawable.ic_quiz_green else R.drawable.ic_quiz, R.string.quiz)
        }
    }
}

fun TextView.setComplexityLevel(type: Int?) {
    when (type) {
        ComplexityLevel.ADVANCED -> {
            setTextColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
            backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.offWhite_f6f6f6))
        }
        ComplexityLevel.BEGINNER -> {
            setTextColor(ContextCompat.getColor(context, R.color.accent_color_2FBF71))
            backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.offWhite_f6f6f6))

        }
        ComplexityLevel.INTERMEDIATE -> {
            setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
            backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_tint_color))

        }
    }
}

fun TextView.setPriceColor(type: Int?) {
    when (type) {
        CourseType.FREE -> {
            setTextColor(ContextCompat.getColor(context, R.color.black))

        }
        CourseType.PAID -> {
            setTextColor(ContextCompat.getColor(context, R.color.accent_color_2FBF71))


        }
        CourseType.RESTRICTED -> {
            setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
        }
        CourseType.REWARD_POINTS -> {
            setTextColor(ContextCompat.getColor(context, R.color.primaryColor))
        }
    }
}

fun LMSMaterialButton.setCourseButton(
    type: Int?,
    userCourseStatus: Int? = 0,
    paymentStatus: Int? = 0,
    type2: Int? = 0,
) {
    text = context.getButtonText(type, userCourseStatus, type2)
    icon = when (userCourseStatus) {
        CourseStatus.ENROLLED -> {
            ContextCompat.getDrawable(context, R.drawable.ic_resume)
        }
        else -> {
            null
        }
    }
    when (paymentStatus) {
        PaymentStatus.IN_PROGRESS -> {
            text = context.getString(R.string.resume)
            setBtnEnabled(false)
            isEnabled = false
        }
        else -> {
            setBtnEnabled(true)
            isEnabled = true

        }
    }
}

fun Context.getButtonText(type: Int?, userCourseStatus: Int? = 0, type2: Int?): String {
    if (userCourseStatus == 1) {
        return getString(R.string.resume)
    } else {
        return when (type) {
            CourseType.FREE -> {
                getString(R.string.enroll_now)
            }
            CourseType.PAID -> {
                getString(R.string.buy_now)

            }
            CourseType.RESTRICTED -> {
                if (type2 != 1) {
                    getString(R.string.unlock_now)
                } else {
                    getString(R.string.unlock_the_now)

                }
            }
            CourseType.REWARD_POINTS -> {
                getString(R.string.enroll_now)

            }
            else -> {
                getString(R.string.enroll_now)

            }
        }
    }
}


fun Long?.getReviewCount(showBracket: Boolean = true): String {

    val review = when {
        isNullOrZero() -> "0"
        this!! < 1000 -> "$this"
        Math.abs(this / 1000000000) > 1 -> {

            if (this.rem(1000000000) != 0L) {
                (this / 1000000000).toString() + "." + this.rem(1000000000).div(10000000) + "b"
            } else {
                (this / 1000000000).toString() + "b"
            }
        }
        Math.abs(this / 1000000) > 1 -> {

            if (this.rem(1000000) != 0L) {
                (this / 1000000).toString() + "." + this.rem(1000000).div(10000) + "m"
            } else {
                (this / 1000000).toString() + "m"
            }
        }
        Math.abs(this / 1000) > 1 -> {
            if (this.rem(1000) != 0L) {
                (this / 1000).toString() + "." + this.rem(1000).div(100) + "k"
            } else {
                (this / 1000).toString() + "k"
            }


        }
        else -> "0"

    }

    return if (showBracket) {
        "($review)"
    } else {
        review
    }
}

fun TabLayout.setCustomTabs(list: ArrayList<String>) {

    for (i in 0 until list.size) {
        getTabAt(i)?.setCustomView(R.layout.tab_custom_view)
        (getTabAt(i)?.customView as LMSTextView).text = list[i]
    }
}

fun TextView.setLimitedName(name: String?) {
    if (name.isNullOrEmpty())
        text = ""
    else if (name.length > 12) {
        var str = name.substring(0, 12)
        text = "${str}..."
    } else {
        text = name
    }
}


fun MaterialToolbar.scrollHandling(totalCount: Int, currentCount: Int) {
    if (currentCount >= totalCount) {
        val percentValue = currentCount.toFloat().div(totalCount).toFloat()

        val colorValue = 2.55.times(1.minus(percentValue)).times(100)
        val colorTransValue = 2.55.times(percentValue).times(100)
        val color = Color.rgb(colorValue.toInt(), colorValue.toInt(), colorValue.toInt())

        val whiteColor = Color.rgb(255, 255, 255)
        val blackColor = Color.rgb(36, 36, 36)

        val whiteToolbarColor = String.format(
            "#%02x%02x%02x%02x",
            colorTransValue.toInt(),
            whiteColor.red,
            whiteColor.green,

            whiteColor.blue
        )

        val blackTitleColor = String.format(
            "#%02x%02x%02x%02x",
            colorTransValue.toInt(),
            blackColor.red,
            blackColor.green,
            blackColor.blue
        )
        setTitleTextColor(Color.parseColor(blackTitleColor))
        setNavigationIconTint(color)
        setBackgroundColor(
            Color.parseColor(whiteToolbarColor)
        )


        for (i in 0 until menu.size()) {
            menu?.getItem(i)?.icon?.colorFilter = PorterDuffColorFilter(
                color,
                PorterDuff.Mode.SRC_IN
            )
        }
    }

}