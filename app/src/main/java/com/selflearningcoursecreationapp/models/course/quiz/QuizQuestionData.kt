package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizQuestionData(

    @Transient
    var isExpanded: Boolean? = true,
    @SerializedName("questionTypeId")
    var questionType: Int? = 1,
    @SerializedName("questionImageId")
    var questionImageId: String? = null,
    @Transient
    var questionTypeTitle: String? = "",
    @SerializedName("questionContentUrl")
    var questionImage: String? = null,
    @Transient
    var isEnabled: Boolean? = true,
    @SerializedName("questionTitle")
    var title: String? = null,
    @Transient
    var optionSelected: Int = 0,
    @SerializedName("questionId")
    var questionId: Int? = null,
    @SerializedName("courseId")
    var courseId: Int? = null,
    @SerializedName("sectionId")
    var sectionId: Int? = null,
    @SerializedName("lectureId")
    var lectureId: Int? = null,
    @SerializedName("quizId")
    var quizId: Int? = null,

    @SerializedName("options", alternate = ["mcQs", "scQs", "dnDs", "ibQs", "mtCs"])
    var optionList: ArrayList<QuizOptionData> = ArrayList()
) : BaseObservable(), Parcelable {

    fun isDataValid(): Int {
        return when {
            title.isNullOrEmpty() -> R.string.plz_enter_question
            questionType == QUIZ.IMAGE_BASED && questionImageId.isNullOrEmpty() -> R.string.plz_upload_ques_image
            optionList.isNullOrEmpty() -> R.string.plz_add_option
            optionList.size == 1 -> R.string.plz_add_min_2_option
            else -> isOptionDataValid()
        }
    }

    private fun isOptionDataValid(): Int {
        optionList.forEach {
            if (it.option1.isNullOrEmpty() && it.imageId.isNullOrEmpty()) {
                return R.string.plz_enter_options
            }

            if (questionType == QUIZ.MATCH_COLUMN && it.option2.isNullOrEmpty()) {
                return R.string.plz_enter_option_column_B
            }
        }

        return 0
    }
}