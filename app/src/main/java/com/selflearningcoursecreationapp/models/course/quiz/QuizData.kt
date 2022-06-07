package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizData(
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
    @SerializedName("makeQuizMandatory", alternate = ["quizMandatory"])
    var makeQuizMandatory: Boolean? = null,
    @SerializedName("makeAssessmentMandatory", alternate = ["assessmentMandatory"])
    var makeAssessmentMandatory: Boolean? = null,

    @SerializedName("totalQues")
    var totalQues: Int? = null,

    @SerializedName("questions")
    var list: ArrayList<QuizQuestionData>? = null,
    @SerializedName("assessmentId")
    var assessmentId: Int? = null
) : BaseObservable(), Parcelable {
    @SerializedName("markOfCorrectAns")
    var markOfCorrectAns: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @SerializedName("numberOfQuesToDisplay", alternate = ["noOfQuesToBeDisplayed"])
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

    @SerializedName("assessmentName")
    var assessmentName: String? = null

    @SerializedName("quizName")
    var quizName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @SerializedName("totalAssesmentTime", alternate = ["totalDuration"])
    var totalAssesmentTime: Long? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @Transient
    @get:Bindable
    var quesCountValid: Boolean = true
        get() {
            return numberOfQuesToDisplay ?: 0 <= list?.size ?: 0
        }

    @Transient
    @get:Bindable
    var dataEntered: Boolean = false
        get() {
            return !quizName.isNullOrEmpty() && quesCountValid && !markOfCorrectAns.isNullOrZero() && !numberOfQuesToDisplay.isNullOrZero()
                    && !totalAssesmentTime.isNullOrZero() && !passingCriteria.isNullOrZero()
        }

    fun getQuizSettings(): QuizData {
        return QuizData(
            courseId,
            lectureId,
            quizId,
            sectionId,
            freezeContent,
            assessmentId = assessmentId
        ).also { quiz ->
            quiz.markOfCorrectAns = markOfCorrectAns
            quiz.numberOfQuesToDisplay = numberOfQuesToDisplay
            quiz.passingCriteria = passingCriteria
            quiz.totalAssesmentTime = totalAssesmentTime
            if (assessmentId.isNullOrZero()) {
                quiz.makeQuizMandatory = makeQuizMandatory
                quiz.quizName = quizName
            } else {
                quiz.makeAssessmentMandatory = makeQuizMandatory
                quiz.assessmentName = quizName
            }
        }
    }


    fun isAssessmentValid(): Int {
        return when {
            assessmentId.isNullOrZero() -> 0
            list.isNullOrEmpty() -> R.string.plz_add_ques_in_assessment
            !list!!.filter { !it.isAnsMarked() }
                .isNullOrEmpty() -> R.string.plz_mark_ans_ques_in_assessment
            assessmentName.isNullOrEmpty() -> R.string.plz_update_settings_in_assessment
            else -> 0
        }
    }

    fun isQuizValid(): Int {
        return when {
            list.isNullOrEmpty() -> R.string.plz_add_ques
            list!!.find { it.questionId.isNullOrZero() || it.isEnabled == true } != null -> R.string.plz_save_all_quiz_ques_ans
            !list!!.filter { !it.isAnsMarked() }.isNullOrEmpty() -> R.string.plz_mark_ans_ques
            else -> 0
        }
    }

    fun isSingleQuizValid(): Int {
        return when {

            list!!.find { it.questionId.isNullOrZero() || it.isEnabled == true } != null -> R.string.plz_save_question_first
            !list!!.filter { !it.isAnsMarked() }
                .isNullOrEmpty() -> R.string.plz_mark_ans_ques_single
            else -> 0
        }
    }

    fun getBasicData(): QuizData {
        return QuizData(courseId = courseId, sectionId = sectionId)
    }
}