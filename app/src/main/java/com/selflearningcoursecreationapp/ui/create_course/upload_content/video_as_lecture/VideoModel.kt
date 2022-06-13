package com.selflearningcoursecreationapp.ui.create_course.upload_content.video_as_lecture

import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel

data class VideoModel(
    var mFilePath: String? = null,
    var mLectureId: Int? = null,
    var mCourseId: Int? = null,
    var mModel: SectionModel? = null,
    var mSectionId: Int? = null,
    var mChildPosition: Int? = null,
    var mType: Int? = null,

    )