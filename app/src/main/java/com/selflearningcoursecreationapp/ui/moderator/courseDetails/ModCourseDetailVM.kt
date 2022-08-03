package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import androidx.lifecycle.MutableLiveData
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ComplexityLevel
import com.selflearningcoursecreationapp.utils.MediaType

class ModCourseDetailVM : BaseViewModel() {
    var courseData = MutableLiveData<CourseData>().apply {
        value = CourseData().apply {
            courseTitle = "UI/UX  Design Specialization"
            createdByName = "Allene Wen"
            categoryName = "Arts"
            languageName = "English"
            createdDate = "2022-02-16'T'00:00:00"
            courseComplexityName = "Intermediate"
            totalSections = 4
            courseComplexityId = ComplexityLevel.INTERMEDIATE
            courseDuration = 5400000L
            courseDescription =
                "The UI/UX Design Specialization brings a design-centric approach to user interface and user experience design, and offers practical, skill-based instruction centered around a visual communications perspective, rather than on one focused on marketing or programming alone The UI/UX Design Specialization brings a design-centric approach to user interface and user experience design, and offers practical, skill-based instruction centered around a visual communications perspective, rather than on one focused on marketing or programming alone"

            courseCoAuthors = ArrayList<UserProfile>().apply {
                add(UserProfile().apply {
                    name = "Allen Wen"
                })
                add(UserProfile().apply {
                    name = "Amanda Wen"
                })
            }
        }

    }
    var sectionData = MutableLiveData<ArrayList<SectionModel>>().apply {
        value = ArrayList<SectionModel>()
        value?.add(SectionModel().apply {
            sectionTitle = "Visual Elements of User Interface Design"
            sectionDescription =
                "The UI/UX Design Specialization brings a design-centric approach to user interface and user experience design, and offers practical, skill-based instruction centered around a visual communications perspective, rather than on one focused on marketing or programming alone"
            isVisible = false
            lessonList = ArrayList()
            sectionDuration = 1200000
            lessonList.add(ChildModel().also {
                it.lectureTitle = "Entropy (spontaneity)"
                it.mediaType = MediaType.VIDEO
                it.lectureContentDuration = 12000000000

            })
        })
        value?.add(SectionModel().apply {
            sectionTitle = "Visual Elements of User Interface Design"
            sectionDescription =
                "The UI/UX Design Specialization brings a design-centric approach to user interface and user experience design, and offers practical, skill-based instruction centered around a visual communications perspective, rather than on one focused on marketing or programming alone"
            isVisible = false
            sectionDuration = 1500000
            lessonList = ArrayList()
            lessonList.add(ChildModel().also {
                it.lectureTitle = "Entropy (spontaneity)"
                it.mediaType = MediaType.VIDEO
                it.lectureContentDuration = 15000000000

            })
        })
        value?.add(SectionModel().apply {
            sectionTitle = "Visual Elements of User Interface Design"
            sectionDescription =
                "The UI/UX Design Specialization brings a design-centric approach to user interface and user experience design, and offers practical, skill-based instruction centered around a visual communications perspective, rather than on one focused on marketing or programming alone"
            isVisible = false
            sectionDuration = 1200000
            lessonList = ArrayList()
            lessonList.add(ChildModel().also {
                it.lectureTitle = "Entropy (spontaneity)"
                it.mediaType = MediaType.VIDEO
                it.lectureContentDuration = 12000000000

            })
        })
        value?.add(SectionModel().apply {
            sectionTitle = "Visual Elements of User Interface Design"
            sectionDescription =
                "The UI/UX Design Specialization brings a design-centric approach to user interface and user experience design, and offers practical, skill-based instruction centered around a visual communications perspective, rather than on one focused on marketing or programming alone"
            isVisible = false
            lessonList = ArrayList()
            sectionDuration = 1200000
            lessonList.add(ChildModel().also {
                it.lectureTitle = "Entropy (spontaneity)"
                it.mediaType = MediaType.VIDEO
                it.lectureContentDuration = 12000000000

            })
        })


    }


    override fun onApiRetry(apiCode: String) {

    }
}