@file:Suppress("SuspiciousVarProperty")

package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizQuestionData(

    @Transient
    var isExpanded: Boolean? = true,

    @Transient
    var questionTypeTitle: String? = "",
    @SerializedName("questionContentUrl")
    var questionImage: String? = null,
    @Transient
    var isEnabled: Boolean? = true,
    @Transient
    var ansMarked: Boolean? = false,
    @Transient
    var optionSelected: Int = 0,
    @SerializedName("questionId")
    var questionId: Int? = null,
    @SerializedName("assessmentId")
    var assessmentId: Int? = null,
    @SerializedName("courseId")
    var courseId: Int? = null,
    @SerializedName("sectionId")
    var sectionId: Int? = null,
    @SerializedName("lectureId")
    var lectureId: Int? = null,
    @SerializedName("quizId")
    var quizId: Int? = null,

    @SerializedName("markAnsweres", alternate = ["quizMarkAnsweres"])
    var markAnsList: ArrayList<MarkAnswer>? = null,

    @Transient
    var optionIds: String? = null,


    ) : BaseObservable(), Parcelable {

    @SerializedName("questionTitle")
    var title: String? = null
        set(value) {
            field = value

            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("questionTypeId", alternate = ["questionType"])
    var questionType: Int? = 1
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @SerializedName("options", alternate = ["mcQs", "scQs", "dnDs", "ibQs", "mtCs"])
    var optionList: ArrayList<QuizOptionData> = ArrayList()
        set(value) {
            field = value


            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("questionImageId")
    var questionImageId: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }


    @Transient
    @get:Bindable
    var dataEntered: Boolean = false
        get() {
            return !title.isNullOrEmpty() && !(questionType == QUIZ.IMAGE_BASED && questionImageId.isNullOrEmpty()) && !optionList.isNullOrEmpty()
        }

    fun isDataValid(): Int {
        return when {
            title.isNullOrEmpty() -> R.string.plz_enter_question
            title.isNullOrBlank() -> R.string.plz_enter_question
            questionType == QUIZ.IMAGE_BASED && questionImageId.isNullOrEmpty() -> R.string.plz_upload_ques_image
            optionList.isNullOrEmpty() -> R.string.plz_add_option
            optionList.size == 1 -> R.string.plz_add_min_2_option
            else -> isOptionDataValid()
        }
    }

    private fun isOptionDataValid(): Int {
        optionList.forEach {
            if (questionType == QUIZ.MATCH_COLUMN && (it.option2.isNullOrEmpty() || it.option1.isNullOrEmpty())) {
                return R.string.plz_enter_option_column
            } else if (questionType == QUIZ.MATCH_COLUMN && (it.option2.isNullOrBlank() || it.option1.isNullOrBlank())) {
                return R.string.plz_enter_option_column
            } else if ((it.option1.isNullOrEmpty() || it.option1.isNullOrBlank()) && it.imageId.isNullOrEmpty()) {
                return R.string.plz_enter_options
            }


        }

        return 0
    }

    fun getAddAssessmentQuestionData(quizData: QuizData): QuizQuestionData {
        return QuizQuestionData().also { assessmentQues ->
            if (!questionImageId.isNullOrEmpty()) {
                assessmentQues.questionImageId = questionImageId
            }
            assessmentQues.questionType = questionType
            assessmentQues.questionId = questionId
            assessmentQues.courseId = quizData.courseId
            assessmentQues.assessmentId = quizData.assessmentId
            assessmentQues.sectionId = quizData.sectionId
            assessmentQues.lectureId = quizData.lectureId
            assessmentQues.quizId = quizData.quizId
            assessmentQues.title = title?.trim()


            assessmentQues.optionList = optionList.map { option ->
                QuizOptionData().also { optionData ->
                    optionData.id = option.id
                    optionData.option2 =
                        if (questionType == QUIZ.MATCH_COLUMN) option.option2?.trim() else null
                    if (!option.imageId.isNullOrEmpty()) {
                        optionData.imageId = option.imageId
                    } else {
                        optionData.option1 = option.option1?.trim()
                    }
                }
            } as ArrayList<QuizOptionData>
        }
    }

    fun isAnsMarked(): Boolean {
        return if (QUIZ.MATCH_COLUMN == questionType) optionList.find { it.ansId.isNullOrZero() } == null else !optionList.filter { it.isSelected == true }
            .isNullOrEmpty()
    }
}