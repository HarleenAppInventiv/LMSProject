package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.util.Log
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.ui.create_course.CreateCourseRepo

class AddCourseViewModel(private val repo: CreateCourseRepo) : BaseViewModel() {

    var course_title = ""
    var course_objective = ""

    fun validationStep1() {
        if (course_title.isEmpty()) {
            Log.d("main", "no title")
        } else if (course_objective.isEmpty()) {
            Log.d("main", "no obj")
        } else {
            Log.d("main", "succ")
        }
    }

}