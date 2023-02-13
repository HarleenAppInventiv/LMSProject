package com.selflearningcoursecreationapp.ui.course_details.quiz

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

interface TakeQuizRepo {
    suspend fun getQuiz(quizId: Int, courseId: Int): Flow<Resource>
    suspend fun submitQuiz(jsonObject: JSONObject): Flow<Resource>
    suspend fun getAssessment(assessmentId: Int): Flow<Resource>
    suspend fun submitAssessment(jsonObject: JSONObject): Flow<Resource>
    suspend fun assessmentReport(
        attemptId: String,
        courseId: Int,
        isQuizReport: Boolean
    ): Flow<Resource>

    suspend fun assessmentReportStatus(
        map: HashMap<String, Any>,
        isQuizReport: Boolean
    ): Flow<Resource>

}