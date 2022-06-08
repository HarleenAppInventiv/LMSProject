package com.selflearningcoursecreationapp.extensions

import android.content.Context
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE

fun ArrayList<ChildModel>.getLessonCount(context: Context): ArrayList<String> {
    val lessonList = arrayListOf<String>()
    groupingBy { it.mediaType }.eachCount().forEach {
        val stringId = when (it.key) {
            MEDIA_TYPE.VIDEO ->
                R.plurals.video_quantity

            MEDIA_TYPE.AUDIO -> R.plurals.audio_quantity
            MEDIA_TYPE.DOC -> R.plurals.doc_quantity
            MEDIA_TYPE.TEXT -> R.plurals.text_quantity
            MEDIA_TYPE.QUIZ -> R.plurals.quiz_quantity
            else -> 0
        }
        if (!stringId.isNullOrZero()) {
            lessonList.add(context.getQuantityString(stringId, it.value))
        }
    }

    return lessonList
}