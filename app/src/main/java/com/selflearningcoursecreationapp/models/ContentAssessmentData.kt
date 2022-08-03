package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentAssessmentQuestionAndDuration(
    @SerializedName("noOfQuesToBeDisplayed")
    var noOfQuesToBeDisplayed: Int? = null,
    @SerializedName("totalDuration")
    var totalDuration: Int? = null,

    @SerializedName("attemptLeft")
    var attemptLeft: Int? = null,

    @SerializedName("assessmentMandatory", alternate = ["AssessmentMandatory"])
    var AssessmentMandatory: Boolean? = null,
) : Parcelable

@Parcelize
data class ContentUserAssessmentLatestReport(
    @SerializedName("assessmentId")
    var assessmentId: Int? = null,
    @SerializedName("attemptedId")
    var attemptedId: Int? = null,
    @SerializedName("courseId")
    var courseId: Int? = null,
) : Parcelable


@Parcelize
data class ContentAssessmentData(
    @SerializedName("contentAssessmentQuestionAndDuration")
    var contentAssessmentQuestionAndDuration: ContentAssessmentQuestionAndDuration? = null,
    @SerializedName("contentUserAssessmentLatestReport")
    var contentUserAssessmentLatestReport: ContentUserAssessmentLatestReport? = null,
) : Parcelable

