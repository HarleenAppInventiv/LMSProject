package com.selflearningcoursecreationapp.models.offline


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//@Entity
@Parcelize
data class OfflineLessonData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "duration")
    @SerializedName("duration")
    var duration: Long? = null,
    @ColumnInfo(name = "file_path")
    @SerializedName("file_path")
    var filePath: String? = null,
    @ColumnInfo(name = "file_extention")
    @SerializedName("file_extention")
    var fileExtention: String? = "",
    @ColumnInfo(name = "lesson_id")
    @SerializedName("lesson_id")
    var lessonId: Int? = null,
    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String? = null,
    @ColumnInfo(name = "type")
    @SerializedName("type")
    var type: Int? = null
) : Parcelable