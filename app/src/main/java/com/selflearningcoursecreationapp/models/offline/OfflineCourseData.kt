package com.selflearningcoursecreationapp.models.offline


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class OfflineCourseData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "course_id")
    @SerializedName("course_id")
    var courseId: Int? = null,
    @ColumnInfo(name = "description")
    @SerializedName("description")
    var description: String? = null,
    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String? = null,
    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    var userId: Int? = null,
    @ColumnInfo(name = "createdByName")
    @SerializedName("createdByName", alternate = ["createdName", "prefill_Name"])
    var createdByName: String? = "",
    @ColumnInfo(name = "categoryTypeName")
    @SerializedName("categoryTypeName")
    var categoryTypeName: String? = "",
    @SerializedName("sectionData")
    @ColumnInfo(name = "sectionData")
    var sectionList: ArrayList<OfflineSectionData>? = ArrayList()

) : Parcelable