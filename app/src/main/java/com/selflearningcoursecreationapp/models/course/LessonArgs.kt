package com.selflearningcoursecreationapp.models.course

import android.os.Parcelable
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LessonArgs(

    var courseId: Int? = 0,
    var sectionId: Int? = 0,
    var lectureId: Int? = 0,
    var quizId: Int? = 0,
    var filePath: String? = "",
    var type: Int = 0,
    var finalUrl: String = "",
    var isQuiz: Boolean = false,
    var sectionData: SectionModel? = null,
    var courseData: CourseData? = null

) : Parcelable
