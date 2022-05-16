package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizSettings(
    @SerializedName("courseId")
    var courseId: Int? = null,
    @SerializedName("lectureId")
    var lectureId: Int? = null,
    @SerializedName("quizId")
    var quizId: Int? = null,
    @SerializedName("sectionId")
    var sectionId: Int? = null,
    @SerializedName("freezeContent")
    var freezeContent: Boolean? = null,
    @SerializedName("makeQuizMandatory")
    var makeQuizMandatory: Boolean? = null,

    @Transient
    var totalQues: Int = 0

) : BaseObservable(), Parcelable {
    @SerializedName("markOfCorrectAns")
    var markOfCorrectAns: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @SerializedName("numberOfQuesToDisplay")
    var numberOfQuesToDisplay: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.quesCountValid)
            notifyPropertyChanged(BR.dataEntered)
        }

    @SerializedName("passingCriteria")
    var passingCriteria: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @SerializedName("quizName")
    var quizName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @SerializedName("totalAssesmentTime")
    var totalAssesmentTime: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @Transient
    @get:Bindable
    var quesCountValid: Boolean = true
        get() {
            return numberOfQuesToDisplay ?: 0 <= totalQues
        }

    @Transient
    @get:Bindable
    var dataEntered: Boolean = false
        get() {
            return !quizName.isNullOrEmpty() && quesCountValid && !markOfCorrectAns.isNullOrZero() && !numberOfQuesToDisplay.isNullOrZero()
                    && !totalAssesmentTime.isNullOrZero() && !passingCriteria.isNullOrZero()
        }

}