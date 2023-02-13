package com.selflearningcoursecreationapp.ui.create_course.quiz

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.data.network.getRequestBody
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class AddQuizAnsVM(private var repo: AddQuizRepo) : BaseViewModel() {
    var hashmap = HashMap<String, ArrayList<QuizOptionData>>()
    var quizData = QuizData()
    var adapterPosition = 0
    var childPosition = 0
    var quizType: Int = QUIZ.MULTIPLE_CHOICE

    fun getListData(): QuizQuestionData? {
        return quizData.list?.get(adapterPosition)
    }


    private fun saveAnswer(ansList: JSONArray) {
        try {

            val map = JSONObject()
            map.put("courseId", quizData.courseId ?: 0)
            if (quizData.assessmentId.isNullOrZero()) {
                map.put("sectionId", quizData.sectionId ?: 0)
                map.put("lectureId", quizData.lectureId ?: 0)
                map.put("quizId", quizData.quizId ?: 0)
            } else {
                map.put("assessmentId", quizData.assessmentId ?: 0)

            }
            map.put("questionId", quizData.list?.get(adapterPosition)?.questionId ?: 0)
            map.put("questionTypeId", quizData.list?.get(adapterPosition)?.questionType ?: 0)
            map.put("answeres", ansList)
            viewModelScope.launch(coroutineExceptionHandle) {
                val response = if (quizData.assessmentId.isNullOrZero()) {
                    repo.saveQuizAns(map.getRequestBody())
                } else repo.saveAssessmentAns(map.getRequestBody())
                withContext(Dispatchers.IO) {
                    response.collect {
                        updateResponseObserver(it)
                    }
                }
            }
        } catch (e: Exception) {
            showException(e)
        }
    }


    fun isAnswerValid() {
        var isValid = true
        val ansList = JSONArray()
        hashmap.map { it.key }.forEachIndexed { index, s ->

            val list = hashmap[s]?.filter { it.isSelected == true }
            if (list.isNullOrEmpty()) {
                isValid = false
                updateResponseObserver(Resource.Error(ToastData(R.string.plz_select_ans)))
                return
            } else {
                list.forEach {
                    try {


                        val ansObj = JSONObject()

                        if (quizType == QUIZ.MATCH_COLUMN) {
                            ansObj.put(
                                "answere1",
                                getListData()?.optionList?.get(index)?.id
                            )
                            ansObj.put("answere2", it.id)
                        } else {
                            ansObj.put("answere1", it.id)
                        }
                        ansList.put(ansObj)
                    } catch (e: Exception) {
                        showException(e)
                    }
                }
            }
        }
        if (isValid) {
            saveAnswer(ansList)
        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {

            ApiEndPoints.API_ADD_QUIZ_ANS, ApiEndPoints.API_ADD_ASSESSMENT_ANS -> {
                isAnswerValid()
            }

        }
    }

}