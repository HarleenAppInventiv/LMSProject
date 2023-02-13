package com.selflearningcoursecreationapp.models.offline


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//@Entity
@Parcelize
data class OfflineSectionData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "course_id")
    @SerializedName("course_id")
    var courseId: Int? = null,
    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    var userId: Int? = null,
    @ColumnInfo(name = "lesson_data")
    @SerializedName("lesson_data")
    var lessonList: ArrayList<OfflineLessonData>? = ArrayList(),
    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String? = null,
    @ColumnInfo(name = "section_id")
    @SerializedName("section_id")
    var sectionId: Int? = null,
    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("isVisible")
    var isVisible: Boolean = false
) : Parcelable