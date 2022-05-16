package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizData(
    @SerializedName("courseId")
    var courseId: Int? = 61,
    @SerializedName("lectureId")
    var lectureId: Int? = null,
    @SerializedName("quizId")
    var quizId: Int? = null,
    @SerializedName("sectionId")
    var sectionId: Int? = 270,
    @SerializedName("list")
    var list: ArrayList<QuizQuestionData>? = null
) : Parcelable