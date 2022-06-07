package com.selflearningcoursecreationapp.ui.create_course.quiz

import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import java.io.File

interface AddQuizRepo {
    suspend fun addQuiz(data: QuizData): Flow<Resource>
    suspend fun addAssessment(data: Int?): Flow<Resource>
    suspend fun getQuizQues(quizId: Int): Flow<Resource>
    suspend fun getAssessmentQues(assessmentId: Int): Flow<Resource>
    suspend fun addQuizQues(data: QuizQuestionData?): Flow<Resource>
    suspend fun addAssessmentQues(data: QuizQuestionData?): Flow<Resource>
    suspend fun saveQuizAns(data: RequestBody?): Flow<Resource>
    suspend fun saveAssessmentAns(data: RequestBody?): Flow<Resource>
    suspend fun deleteQuizQues(data: QuizData, questionId: Int?): Flow<Resource>
    suspend fun deleteAssessmentQues(data: QuizData, questionId: Int?): Flow<Resource>
    suspend fun saveQuiz(data: QuizData?): Flow<Resource>
    suspend fun saveAssessment(data: QuizData?): Flow<Resource>
    suspend fun uploadQuizImage(data: QuizData, file: File, type: Int): Flow<Resource>
    suspend fun uploadAssessmentImage(data: QuizData, file: File, type: Int): Flow<Resource>
    suspend fun deleteAssessment(assessmentId: Int, courseID: Int): Flow<Resource>
    suspend fun deleteLecture(
        courseID: String,
        sectionID: String,
        lectureId: String,
    ): Flow<Resource>
}