package com.selflearningcoursecreationapp.extensions

import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.models.offline.OfflineLessonData
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel

fun CourseData?.getOfflineCourseData(
    position: Int,
    innerPosition: Int,
    userId: Int
): OfflineCourseData {
    val offlineCourseData = OfflineCourseData()
    if (this == null) {
        return offlineCourseData
    }

    offlineCourseData.userId = userId ?: 0
    offlineCourseData.courseId = this.courseId
    offlineCourseData.description = this.courseDescription
    offlineCourseData.createdByName = this.createdByName
    offlineCourseData.categoryTypeName = this.categoryName
    offlineCourseData.title = this.courseTitle

    val offlineSection = OfflineSectionData()
    offlineSection.courseId = this.courseId
    offlineSection.userId = userId ?: 0
    offlineSection.description =
        this.sectionData?.get(position)?.sectionDescription
    offlineSection.sectionId = this.sectionData?.get(position)?.sectionId
    offlineSection.title = this.sectionData?.get(position)?.sectionTitle

    val offlineLesson = OfflineLessonData()
    offlineLesson.duration =
        this.sectionData?.get(position)?.lessonList?.get(innerPosition)?.lectureContentDuration
    offlineLesson.lessonId =
        this.sectionData?.get(position)?.lessonList?.get(innerPosition)?.lectureId
    offlineLesson.title =
        this.sectionData?.get(position)?.lessonList?.get(innerPosition)?.lectureTitle
    offlineLesson.type =
        this.sectionData?.get(position)?.lessonList?.get(innerPosition)?.mediaType

    val offlineLessonList: ArrayList<OfflineLessonData> = ArrayList()
    offlineLessonList.add(offlineLesson)
    offlineSection.lessonList = offlineLessonList


    val offlineSectionList: ArrayList<OfflineSectionData> = ArrayList()
    offlineSectionList.add(offlineSection)


    offlineCourseData.sectionList = offlineSectionList
    return offlineCourseData
}

fun CourseData.getOfflineCourseData(
    position: Int,
    lessonList: ArrayList<ChildModel?>,
    userId: Int
): OfflineCourseData {
    val offlineCourseData = OfflineCourseData()
    if (this == null) {
        return offlineCourseData
    }

    offlineCourseData.userId = userId ?: 0
    offlineCourseData.courseId = this.courseId
    offlineCourseData.description = this.courseDescription
    offlineCourseData.createdByName = this.createdByName
    offlineCourseData.categoryTypeName = this.categoryName
    offlineCourseData.title = this.courseTitle

    val offlineSection = OfflineSectionData()
    offlineSection.courseId = this.courseId
    offlineSection.userId = userId ?: 0
    offlineSection.description =
        "section description"
    offlineSection.sectionId = lessonList?.get(position)?.sectionId
    offlineSection.title = "section title"

    val offlineLesson = OfflineLessonData()
    offlineLesson.duration =
        lessonList?.get(position)?.lectureContentDuration
    offlineLesson.lessonId =
        lessonList?.get(position)?.lectureId
    offlineLesson.title =
        lessonList?.get(position)?.lectureTitle
    offlineLesson.type =
        lessonList?.get(position)?.mediaType

    val offlineLessonList: ArrayList<OfflineLessonData> = ArrayList()
    offlineLessonList.add(offlineLesson)
    offlineSection.lessonList = offlineLessonList


    val offlineSectionList: ArrayList<OfflineSectionData> = ArrayList()
    offlineSectionList.add(offlineSection)


    offlineCourseData.sectionList = offlineSectionList
    return offlineCourseData
}

fun ChildModel.getOfflineCourseData(
    userName: String,
    userId: Int,
    courseId: Int,
): OfflineCourseData {
    val offlineCourseData = OfflineCourseData()
    if (this == null) {
        return offlineCourseData
    }

    offlineCourseData.userId = userId ?: 0
    offlineCourseData.courseId = courseId
    offlineCourseData.description = this.courseDesc
    offlineCourseData.createdByName = userName
    offlineCourseData.categoryTypeName = this.courseCategory
    offlineCourseData.title = this.courseTitle

    val offlineSection = OfflineSectionData()
    offlineSection.courseId = courseId
    offlineSection.userId = userId ?: 0
    offlineSection.description =
        this?.sectionDesc
    offlineSection.sectionId = this?.sectionId
    offlineSection.title = this?.sectionTitle

    val offlineLesson = OfflineLessonData()
    offlineLesson.duration =
        this?.lectureContentDuration
    offlineLesson.lessonId =
        this?.lectureId
    offlineLesson.title =
        this?.lectureTitle
    offlineLesson.type =
        this?.mediaType

    val offlineLessonList: ArrayList<OfflineLessonData> = ArrayList()
    offlineLessonList.add(offlineLesson)
    offlineSection.lessonList = offlineLessonList


    val offlineSectionList: ArrayList<OfflineSectionData> = ArrayList()
    offlineSectionList.add(offlineSection)


    offlineCourseData.sectionList = offlineSectionList
    return offlineCourseData
}
