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
    override suspend fun getQuiz(quizId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.getQuizData(quizId)
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

    override suspend fun getAssessment(assessmentId: String): Flow<Resource> {
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

    override suspend fun assessmentReport(attemptId: String): Flow<Resource> {
        return object : BaseRepo<BaseResponse<AssessmentReportData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<AssessmentReportData>> {
                return apiService.assessmentReport(attemptId)
            }
        }.safeApiCall(ApiEndPoints.API_ASSESSMENT_REPORT).flowOn(Dispatchers.IO)
    }


    override suspend fun assessmentReportStatus(map: HashMap<String, Any>): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.assessmentReportStatus(map)
            }
        }.safeApiCall(ApiEndPoints.API_ASSESSMENT_REPORT_STATUS).flowOn(Dispatchers.IO)
    }
}
