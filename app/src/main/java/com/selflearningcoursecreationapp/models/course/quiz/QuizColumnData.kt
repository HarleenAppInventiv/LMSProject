package com.selflearningcoursecreationapp.models.course.quiz


import android.os.Parcelable
import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizColumnData(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("isSelected")
    var isSelected: Boolean? = false,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("list")
    var optionList: ArrayList<QuizOptionData> = ArrayList()
) : BaseObservable(), Parcelable