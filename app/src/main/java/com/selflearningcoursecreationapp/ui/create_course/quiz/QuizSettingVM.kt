package com.selflearningcoursecreationapp.ui.create_course.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizSettingVM(private var repo: AddQuizRepo) : BaseViewModel() {
    var quizSettings = MutableLiveData<QuizData>().apply {
        value = QuizData()
    }
    var isQuiz: Boolean = true

    fun saveSettings() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                if (isQuiz) repo.saveQuiz(quizSettings.value?.getQuizSettings()) else repo.saveAssessment(
                    quizSettings.value?.getQuizSettings()
                )
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }
}