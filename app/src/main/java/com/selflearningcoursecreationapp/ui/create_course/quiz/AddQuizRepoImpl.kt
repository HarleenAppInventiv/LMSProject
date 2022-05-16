package com.selflearningcoursecreationapp.ui.create_course.quiz

import com.selflearningcoursecreationapp.base.BaseRepo
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.getMultiPartBody
import com.selflearningcoursecreationapp.data.network.getRequestBody
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizSettings
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class AddQuizRepoImpl(private var apiService: ApiService) : AddQuizRepo {
    override suspend fun addQuiz(data: QuizData): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.addQuiz(data)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ).flowOn(Dispatchers.IO)
    }

    override suspend fun addQuizQues(data: QuizQuestionData): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizQuestionData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizQuestionData>> {
                return if (data.questionId.isNullOrZero()) apiService.addQuizQues(data) else apiService.updateQuizQues(
                    data
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_QUESTION).flowOn(Dispatchers.IO)
    }

    override suspend fun saveQuizAns(data: RequestBody?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.saveQuesAns(data)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_ANS).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteQuizQues(data: QuizData, questionId: Int?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.deleteQuizQues(
                    data.courseId,
                    data.sectionId,
                    data.lectureId,
                    data.quizId,
                    questionId
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_QUESTION).flowOn(Dispatchers.IO)
    }

    override suspend fun saveQuiz(data: QuizSettings?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.saveQuiz(data)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_SAVE).flowOn(Dispatchers.IO)
    }

    override suspend fun uploadQuizImage(data: QuizData, file: File, type: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ImageResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ImageResponse>> {
                return apiService.uploadQuizImage(
                    file.getMultiPartBody("File"),
                    file.name.getRequestBody(),
                    data.courseId.getRequestBody(),
                    data.sectionId.getRequestBody(),
                    data.quizId.getRequestBody(),
                    data.lectureId.getRequestBody(),
                    type.getRequestBody()
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_IMAGE).flowOn(Dispatchers.IO)
    }


}