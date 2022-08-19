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
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class AddQuizRepoImpl(private var apiService: ApiService) : AddQuizRepo {
    override suspend fun addQuiz(data: QuizData): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.addQuiz(data)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ + "/add").flowOn(Dispatchers.IO)
    }

    override suspend fun addAssessment(data: Int?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.addAssessment(JSONObject().apply { put("courseId", data) }
                    .getRequestBody())
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT + "/add").flowOn(Dispatchers.IO)

    }

    override suspend fun getQuizQues(quizId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.getQuizQues(quizId)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ + "/get").flowOn(Dispatchers.IO)
    }

    override suspend fun getAssessmentQues(assessmentId: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.getAssessmentQues(assessmentId)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT + "/get").flowOn(Dispatchers.IO)
    }

    override suspend fun addQuizQues(data: QuizQuestionData?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizQuestionData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizQuestionData>> {
                return if (data?.questionId.isNullOrZero()) apiService.addQuizQues(data) else apiService.updateQuizQues(
                    data
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_QUESTION + "/add").flowOn(Dispatchers.IO)
    }

    override suspend fun addAssessmentQues(data: QuizQuestionData?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizQuestionData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizQuestionData>> {
                return if (data?.questionId.isNullOrZero()) apiService.addAssessmentQues(data) else apiService.updateAssessmentQues(
                    data
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT_QUESTION + "/add").flowOn(Dispatchers.IO)
    }

    override suspend fun saveQuizAns(data: RequestBody?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.saveQuesAns(data)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_ANS).flowOn(Dispatchers.IO)
    }

    override suspend fun saveAssessmentAns(data: RequestBody?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.saveAssessmentAns(data)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT_ANS).flowOn(Dispatchers.IO)
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
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_QUESTION + "/delete").flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAssessmentQues(data: QuizData, questionId: Int?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<Any>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<Any>> {
                return apiService.deleteAssessmentQues(
                    data.courseId,
                    data.assessmentId,
                    questionId
                )
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT_QUESTION + "/delete").flowOn(Dispatchers.IO)
    }

    override suspend fun saveQuiz(data: QuizData?): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ChildModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ChildModel>> {
                return apiService.saveQuiz(data)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_QUIZ_SAVE).flowOn(Dispatchers.IO)
    }

    override suspend fun saveAssessment(data: QuizData?): Flow<Resource> {
        val call = object : BaseRepo<BaseResponse<ChildModel>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ChildModel>> {
                return apiService.saveAssessment(data)
            }
        }
        return call.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT_SAVE).flowOn(Dispatchers.IO)
    }

    override suspend fun uploadQuizImage(data: QuizData, file: File, type: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ImageResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ImageResponse>> {
                return if (data.assessmentId.isNullOrZero()) {
                    apiService.uploadQuizImage(
                        file.getMultiPartBody("File"),
                        file.name.getRequestBody(),
                        data.courseId.getRequestBody(),
                        data.sectionId.getRequestBody(),
                        data.quizId.getRequestBody(),
                        data.lectureId.getRequestBody(),
                        type.getRequestBody()
                    )
                } else {
                    apiService.uploadAssessmentImage(
                        file.getMultiPartBody("File"),
                        file.name.getRequestBody(),
                        data.courseId.getRequestBody(),
                        data.assessmentId.getRequestBody(),
                        type.getRequestBody()
                    )
                }
            }
        }.safeApiCall(
            if (data.assessmentId.isNullOrZero()) {
                ApiEndPoints.API_ADD_QUIZ_IMAGE
            } else ApiEndPoints.API_ADD_ASSESSMENT_IMAGE
        ).flowOn(Dispatchers.IO)
    }

    override suspend fun uploadAssessmentImage(
        data: QuizData,
        file: File,
        type: Int
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<ImageResponse>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<ImageResponse>> {
                return apiService.uploadAssessmentImage(
                    file.getMultiPartBody("File"),
                    file.name.getRequestBody(),
                    data.courseId.getRequestBody(),
                    data.assessmentId.getRequestBody(),
                    type.getRequestBody()
                )

            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT_IMAGE).flowOn(Dispatchers.IO)
    }

    override suspend fun deleteAssessment(assessmentId: Int, courseID: Int): Flow<Resource> {
        return object : BaseRepo<BaseResponse<QuizData>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<QuizData>> {
                return apiService.deleteAssessment(assessmentId, courseID)
            }
        }.safeApiCall(ApiEndPoints.API_ADD_ASSESSMENT + "/delete").flowOn(Dispatchers.IO)

    }


    override suspend fun deleteLecture(
        courseId: String,
        sectionId: String,
        lectureId: String,
    ): Flow<Resource> {
        return object : BaseRepo<BaseResponse<UserProfile>>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponse<UserProfile>> {
                return apiService.deleteLecture(courseId, sectionId, lectureId)
            }
        }.safeApiCall(ApiEndPoints.API_LECTURE_DELETE + "/delete").flowOn(Dispatchers.IO)
    }

}