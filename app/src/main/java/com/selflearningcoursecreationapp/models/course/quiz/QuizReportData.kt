package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizReportData(
    @SerializedName("percentageScored")
    var percentageScored: Int? = null,
    @SerializedName("quizPassed")
    var quizPassed: Boolean? = null
) : Parcelable