package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizOptionData(
    @SerializedName("optionId")
    var id: Int? = null,
    @SerializedName("ansId")
    var ansId: Int? = null,
    @SerializedName("correctAns")
    var isSelected: Boolean? = null,
    @SerializedName("option2")
    var option2: String? = null,


    @Transient
    var quizType: Int? = null,
    @SerializedName("optionContentUrl")
    var image: String? = null
) : BaseObservable(), Parcelable {
    @SerializedName("option1", alternate = ["option"])
    var option1: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.noDataEntered)
        }

    @SerializedName("imageId", alternate = ["optionImageId"])
    var imageId: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.noDataEntered)
        }


    @Transient
    @get:Bindable
    var noDataEntered: Boolean = true
        get() {
            return option1.isNullOrEmpty() && imageId.isNullOrEmpty() && (quizType == QUIZ.DRAG_DROP || quizType == QUIZ.IMAGE_BASED)
        }


}