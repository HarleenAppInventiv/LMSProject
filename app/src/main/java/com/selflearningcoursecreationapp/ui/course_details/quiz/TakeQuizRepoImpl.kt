package com.selflearningcoursecreationapp.ui.course_details.quiz

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.getRequestBody
import com.selflearningcoursecreationapp.models.AssessmentReportData
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizReportData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.Response

class TakeQuizRepoImpl(private var apiService: ApiService) : TakeQuizRepo {
    override suspend fun getQuiz(quizId: Int, courseId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.getQuizData(quizId, courseId)
            }

        }.safeApiCall(ApiEndPoints.API_COURSE_QUIZ).flowOn(Dispatchers.IO)

    }

    override suspend fun submitQuiz(jsonObject: JSONObject): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizReportData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizReportData>> {
                return apiService.submitTestQuiz(jsonObject.getRequestBody())
            }

        }.safeApiCall(ApiEndPoints.API_SUBMIT_COURSE_QUIZ).flowOn(Dispatchers.IO)

    }

    override suspend fun getAssessment(assessmentId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.assessmentList(assessmentId)
            }
        }.safeApiCall(ApiEndPoints.API_ASSESSMENT).flowOn(Dispatchers.IO)
    }

    override suspend fun submitAssessment(jsonObject: JSONObject): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizReportData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizReportData>> {
                return apiService.assessmentListSubmit(jsonObject.getRequestBody())
            }
        }.safeApiCall(ApiEndPoints.API_ASSESSMENT_SUBMIT).flowOn(Dispatchers.IO)
    }

    override suspend fun assessmentReport(
        attemptId: String,
        courseId: Int,
        isQuizReport: Boolean
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AssessmentReportData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AssessmentReportData>> {
                if (isQuizReport) return apiService.quizReport(attemptId, courseId)
                else return apiService.assessmentReport(attemptId, courseId)
            }
        }.safeApiCall(ApiEndPoints.API_ASSESSMENT_REPORT).flowOn(Dispatchers.IO)
    }


    override suspend fun assessmentReportStatus(
        map: HashMap<String, Any>,
        isQuizReport: Boolean
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                if (!isQuizReport) return apiService.assessmentReportStatus(map)
                else return apiService.quizReportStatus(map)
            }
        }.safeApiCall(ApiEndPoints.API_ASSESSMENT_REPORT_STATUS).flowOn(Dispatchers.IO)
    }
}
