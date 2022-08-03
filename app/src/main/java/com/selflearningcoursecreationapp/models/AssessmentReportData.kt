package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AssessmentReportData(
    @SerializedName("assessmentId")
    var assessmentId: Int? = null,
    @SerializedName("assessmentPassed")
    var assessmentPassed: Boolean? = null,
    @SerializedName("attemptedBy")
    var attemptedBy: Int? = null,
    @SerializedName("attemptedId")
    var attemptedId: Int? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("noOfQuesDisplayed")
    var noOfQuesDisplayed: Int? = null,
    @SerializedName("percentageScored")
    var percentageScored: Int? = null,
    @SerializedName("totalCorrectAnswer")
    var totalCorrectAnswer: Int? = null,
    @SerializedName("totalPoints")
    var totalPoints: Int? = null,
    @SerializedName("totalTimeTaken")
    var totalTimeTaken: Int? = null,
    @SerializedName("totalWrongAnswer")
    var totalWrongAnswer: Int? = null
) : Parcelable