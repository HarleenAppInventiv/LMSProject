package com.selflearningcoursecreationapp.extensions

import android.content.Context
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.MediaType

fun ArrayList<ChildModel>.getLessonCount(context: Context): ArrayList<String> {
    val lessonList = arrayListOf<String>()
    groupingBy { it.mediaType }.eachCount().forEach {
        val stringId = when (it.key) {
            MediaType.VIDEO ->
                R.plurals.video_quantity

            MediaType.AUDIO -> R.plurals.audio_quantity
            MediaType.DOC -> R.plurals.doc_quantity
            MediaType.TEXT -> R.plurals.text_quantity
            MediaType.QUIZ -> R.plurals.quiz_quantity
            else -> 0
        }
        if (!stringId.isNullOrZero()) {
            lessonList.add(context.getQuantityString(stringId, it.value))
        }
    }

    return lessonList
}