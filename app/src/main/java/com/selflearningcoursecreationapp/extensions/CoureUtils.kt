package com.selflearningcoursecreationapp.extensions

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE

fun Int.getMediaType(): Pair<Int, Int> {
    return when (this) {
        MEDIA_TYPE.VIDEO -> {
            Pair(
                R.drawable.ic_video_icon, R.string.video
            )
        }
        MEDIA_TYPE.AUDIO -> {
            Pair(R.drawable.ic_audio_icon, R.string.audio)

        }
        MEDIA_TYPE.DOC -> {
            Pair(R.drawable.ic_text_icon, R.string.doc)
        }
        MEDIA_TYPE.TEXT -> {
            Pair(R.drawable.ic_docx_icon, R.string.text)

        }
        else -> {
            Pair(R.drawable.ic_quiz, R.string.quiz)
        }
    }
}