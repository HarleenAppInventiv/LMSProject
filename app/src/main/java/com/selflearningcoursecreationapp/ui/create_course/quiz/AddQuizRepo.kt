package com.selflearningcoursecreationapp.ui.create_course.quiz

import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizSettings
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import java.io.File

interface AddQuizRepo {
    suspend fun addQuiz(data: QuizData): Flow<Resource>
    suspend fun addQuizQues(data: QuizQuestionData): Flow<Resource>
    suspend fun saveQuizAns(data: RequestBody?): Flow<Resource>
    suspend fun deleteQuizQues(data: QuizData, questionId: Int?): Flow<Resource>
    suspend fun saveQuiz(data: QuizSettings?): Flow<Resource>

    suspend fun uploadQuizImage(data: QuizData, file: File, type: Int): Flow<Resource>

}