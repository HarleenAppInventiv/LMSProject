package com.selflearningcoursecreationapp.ui.create_course.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.quiz.QuizSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizSettingVM(private var repo: AddQuizRepo) : BaseViewModel() {
    var quizSettings = MutableLiveData<QuizSettings>().apply {
        value = QuizSettings()
    }

    fun saveSettings() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.saveQuiz(quizSettings.value)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {

                    }
                    updateResponseObserver(it)
                }
            }
        }
    }
}