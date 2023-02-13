package com.selflearningcoursecreationapp.extensions

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.customViews.LMSMaterialButton
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

fun Int.getMediaType(lectureContentType: String? = ""): Pair<Int, Int> {
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
            when (lectureContentType) {
                "application/msword" -> {
                    return Pair(R.drawable.ic_docx_icon, R.string.doc)
                }
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
                    return Pair(R.drawable.ic_docx_icon, R.string.docx)
                }
                "application/vnd.ms-powerpoint" -> {
                    return Pair(R.drawable.ic_ppt, R.string.ppt)
                }
                "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> {
                    return Pair(R.drawable.ic_ppt, R.string.pptx)
                }
                else -> {
                    return Pair(R.drawable.ic_docx_icon, R.string.doc)
                }
            }
//
        }
//
        MediaType.TEXT -> {
            Pair(R.drawable.ic_text, R.string.text)
        }
        else -> {
            Pair(/*if (showGreen)*/ R.drawable.ic_quiz_green /*else R.drawable.ic_quiz*/,
                R.string.quiz
            )
        }
    }
}

fun Int?.isLectureInProcessing(): Boolean {
    return when (this) {
        LectureStatus.CONTENT_PROCESSING, LectureStatus.JOB_CREATED, LectureStatus.JOB_PROCESSING_START, LectureStatus.JOB_FINISHED, LectureStatus.LINK_GENERATED -> {
            true
        }
        else -> false
    }
}

fun Int?.isMediaLecture(): Boolean {
    return when (this) {
        MediaType.VIDEO, MediaType.AUDIO, MediaType.DOC -> {
            true
        }
        else -> false
    }
}

fun Int?.isLectureFailed(): Boolean {
    return when (this) {
        LectureStatus.ERRORED, LectureStatus.IN_PROCESS, LectureStatus.CANCELLED -> {
            true
        }
        else -> false
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

fun Button.setCreateCourseText(type: Int?) {
    when (type) {
        CreatedCourseStatus.BLOCKED -> {
            gone()
        }
        CreatedCourseStatus.DRAFT -> {
            isEnabled = true
            text = context.getString(R.string.edit_course)
        }
        CreatedCourseStatus.INPROCESS -> {
            text = context.getString(R.string.in_progress)
            isEnabled = false
            if (ThemeUtils.isViOn()) setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )

        }

        CreatedCourseStatus.SUBMIT -> {
            text = context.getString(R.string.under_review)
            isEnabled = false
            if (ThemeUtils.isViOn()) setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )
        }
        CreatedCourseStatus.APPROVAL -> {
            text = context.getString(R.string.approved)
            isEnabled = false
            if (ThemeUtils.isViOn()) setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )

        }
        CreatedCourseStatus.PUBLISHED -> {
            text = context.getString(R.string.published)
            isEnabled = false
            if (ThemeUtils.isViOn()) setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )

        }
        CreatedCourseStatus.REJECTED -> {
            text = context.getString(R.string.rejected)
            isEnabled = false
            if (ThemeUtils.isViOn()) setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )

        }
        CreatedCourseStatus.PARTIALREJECTED -> {
            isEnabled = true
            text = context.getString(R.string.edit_course)

        }
        CreatedCourseStatus.UNDER_PROCESSING -> {
            isEnabled = false
            text = context.getString(R.string.under_processing)

        }
        CreatedCourseStatus.PARTIAL_SUBMITTED -> {
            isEnabled = false
            setText(context.getString(R.string.under_processing))

        }
    }
}

fun LMSMaterialButton.setCourseButton(
    type: Int?,
    userCourseStatus: Int? = 0,
    paymentStatus: Int? = 0,
    type2: Int? = 0,
    status: Int? = 1
) {
    text = context.getButtonText(type, userCourseStatus, type2, status)
    icon = when (userCourseStatus) {
        CourseStatus.IN_PROGRESS -> {
            ContextCompat.getDrawable(context, R.drawable.ic_resume)
        }
        else -> {
            null
        }
    }

    text = when (userCourseStatus) {

        CourseStatus.IN_PROGRESS -> {
            context.getString(R.string.resume)
        }
        CourseStatus.ENROLLED -> {
            context.getString(R.string.start)
        }
        CourseStatus.COMPELETD -> {
            context.getString(R.string.completed)
        }
        else -> {
            context.getButtonText(type, userCourseStatus, type2, status)
        }
    }
    setBtnEnabled(true)
    isEnabled = true

//    when (paymentStatus) {
//        PaymentStatus.IN_PROGRESS, PaymentStatus.SUCCESS -> {
//            text = context.getString(R.string.resume)
////            setBtnEnabled(false)
////            isEnabled = false
//            setBtnEnabled(true)
//            isEnabled = true
//        }
//        else -> {
//            setBtnEnabled(true)
//            isEnabled = true
//
//        }
//    }
}

fun Context.getButtonText(
    type: Int?,
    userCourseStatus: Int? = 0,
    type2: Int?,
    status: Int?
): String {
    if (userCourseStatus == 1) {
        return if (status == 0) getString(R.string.start) else getString(R.string.resume)
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

fun TabLayout.setCustomTabsFixed(list: ArrayList<String>) {

    for (i in 0 until list.size) {
        getTabAt(i)?.setCustomView(R.layout.tab_custom_view_fixed_tab)
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


fun MaterialToolbar.scrollHandling(totalCount: Int, currentCount: Int, offsetValue: Int) {
//    if (currentCount >= totalCount) {
//        val percentValue = currentCount.toFloat().div(totalCount).toFloat()
//
//        val colorValue = 2.55.times(1.minus(percentValue)).times(100)
//        val colorTransValue = 2.55.times(percentValue).times(100)
    var color: Int/* = Color.rgb(colorValue.toInt(), colorValue.toInt(), colorValue.toInt())*/

//        val whiteColor = Color.rgb(255, 255, 255)
//        val blackColor = Color.rgb(36, 36, 36)

    var whiteToolbarColor: Int/* = Color.parseColor(String.format(
            "#%02x%02x%02x%02x",
            colorTransValue.toInt(),
            whiteColor.red,
            whiteColor.green,

            whiteColor.blue
        ))*/

    var blackTitleColor: Int /*= Color.parseColor(String.format(
            "#%02x%02x%02x%02x",
            colorTransValue.toInt(),
            blackColor.red,
            blackColor.green,
            blackColor.blue
        ))*/



    if (currentCount >= offsetValue) {
        color = ContextCompat.getColor(context, R.color.white)
        whiteToolbarColor = ContextCompat.getColor(context, android.R.color.transparent)
        blackTitleColor = ContextCompat.getColor(context, android.R.color.transparent)


    } else {
        color = ContextCompat.getColor(context, R.color.text_color_black_131414)
        whiteToolbarColor = ContextCompat.getColor(context, R.color.white)
        blackTitleColor = context.getAttrResource(R.attr.toolbarTitleColor)


    }

    setTitleTextColor(blackTitleColor)
    setNavigationIconTint(color)
    setBackgroundColor(
        whiteToolbarColor
    )
    for (i in 0 until menu.size()) {
        menu?.getItem(i)?.icon?.colorFilter = PorterDuffColorFilter(
            color,
            PorterDuff.Mode.SRC_IN
        )
    }

//    }

}


