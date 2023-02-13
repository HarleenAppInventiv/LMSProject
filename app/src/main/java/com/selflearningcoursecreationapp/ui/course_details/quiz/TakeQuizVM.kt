package com.selflearningcoursecreationapp.ui.course_details.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.models.AssessmentReportData
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class TakeQuizVM(private var repo: TakeQuizRepo) : BaseViewModel() {
    var quizId: Int = 0
    var courseId: Int = 0
    var isQuizReport: Boolean = false
    var type: Int = 0
    var currentPos = 0
    var endTime: Long? = null
    var timerValue: Long? = null
    var attemptId: String = ""
    var quizData = MutableLiveData<QuizData?>()
    var innerPosition = 0
    var position = 0

    var assessmentReportLiveData = MutableLiveData<AssessmentReportData>().apply {
        value = AssessmentReportData()


    }

    var lectureId: Int = 0
    var modType: Int = 0
    var sectionId: Int = 0
    var duration: Int = 0
    var mediaType: Int = 0


    var lessonList = ArrayList<ChildModel?>()
    var sectionList = ArrayList<SectionModel?>()

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ASSESSMENT_REPORT_STATUS -> {
                getAssessmentReportStatus()
            }
            ApiEndPoints.API_ASSESSMENT_REPORT -> {
                getAssessmentReport()
            }
        }
    }

    fun getQuizQues() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                if (type == Constant.CLICK_ASSESSMENT || type == Constant.CLICK_ASSESSMENT_REVIEW) repo.getAssessment(
                    courseId
                )
                else repo.getQuiz(quizId, courseId.toInt())

            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<QuizData>).resource
                        quizData.postValue(resource)
                    }
                    updateResponseObserver(it)
                }
            }
        }

    }


    fun createSubmitPayload() {
        try {
            val jsonObject = JSONObject()
            if (type == Constant.CLICK_ASSESSMENT) jsonObject.put(
                "courseId",
                quizData.value?.courseId
            ) else jsonObject.put("quizId", quizId)

            val quesArray = JSONArray()
            quizData.value?.list?.filter {
                !it.optionList?.filter { it.isSelected == true }?.isNullOrEmpty()
            }?.forEach { quesData ->
                val quesObject = JSONObject()
                quesObject.put("questionId", quesData.questionId)
                quesObject.put("questionTypeId", quesData.questionType)
                quesData.optionList.filter { it.isSelected == true }?.let { optionList ->
                    if (!optionList.isNullOrEmpty()) {
                        val ansArray = JSONArray()
                        optionList?.forEach { quizOptionData ->
                            val ansObject = JSONObject()
                            ansObject.put("answere1", quizOptionData.id)

                            when (quesData.questionType) {
                                QUIZ.MATCH_COLUMN -> {
                                    ansObject.put("answere2", quizOptionData.ansId)
                                }

                            }
                            ansArray.put(ansObject)

                        }

                        quesObject.put("answeres", ansArray)
                        quesArray.put(quesObject)
                    }

                }
            }
            jsonObject.put("questions", quesArray)
            jsonObject.put(
                "duration",
                quizData.value?.totalAssessmentTime?.toDouble()
                    ?.minus(timerValue?.toDouble() ?: 0.0)
            )
            hitSubmitQuizApi(jsonObject)
        } catch (e: Exception) {
            showException(e)
        }

//        val ansList = JSONArray()
//        hashmap.map { it.key }.forEachIndexed { index, s ->
//
//            val list = hashmap[s]?.filter { it.isSelected == true }
//            if (list.isNullOrEmpty()) {
//                isValid = false
//                updateResponseObserver(Resource.Error(ToastData(R.string.plz_select_ans)))
//                return
//            } else {
//                list.forEach {
//                    val ansObj = JSONObject()
//
//                    if (quizType == QUIZ.MATCH_COLUMN) {
//                        ansObj.put(
//                            "answere1",
//                            getListData()?.optionList?.get(index)?.id
//                        )
//                        ansObj.put("answere2", it.id)
//                    } else {
//                        ansObj.put("answere1", it.id)
//                    }
//                    ansList.put(ansObj)
//                }
//            }
//        }
//        if (isValid) {
//            saveAnswer(ansList)
//        }
    }

    private fun hitSubmitQuizApi(jsonObject: JSONObject) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                if (type == Constant.CLICK_ASSESSMENT) repo.submitAssessment(jsonObject) else repo.submitQuiz(
                    jsonObject
                )
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }

    fun isAnsSelected() {
        quizData.value?.list?.get(currentPos)?.let { quizQuestionData ->
            quizQuestionData?.optionList?.filter { it.isSelected == true }?.let { optionList ->
                if (optionList.isNullOrEmpty()) {
                    updateResponseObserver(Resource.Error(ToastData(R.string.plz_select_ans)))
                } else if (quizQuestionData.questionType == QUIZ.MATCH_COLUMN && optionList.size != quizQuestionData.optionList.size) {
                    updateResponseObserver(Resource.Error(ToastData(R.string.plz_select_ans)))
                } else {
                    updateResponseObserver(
                        Resource.Success(
                            currentPos + 1 == (quizData.value?.list?.size ?: 0),
                            ApiEndPoints.VALID_DATA
                        )
                    )
                }

            }

        }
    }

    fun getAssessmentReport() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.assessmentReport(attemptId, courseId, isQuizReport)

            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<AssessmentReportData>).resource
                        assessmentReportLiveData.postValue(resource)

                    }
                    updateResponseObserver(it)

                }
            }
        }

    }

    var status = false
    var assessmentId = ""
    fun getAssessmentReportStatus() {
        viewModelScope.launch(coroutineExceptionHandle) {

            val map = HashMap<String, Any>()
            map["CourseId"] = courseId
            map["AttemptedId"] = attemptId

            map["MarkedAnswerCorrect"] = status

            if (!isQuizReport)
                map["AssessmentId"] = assessmentId
            val response = repo.assessmentReportStatus(map, isQuizReport)

            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
//                        val resource = (it.value as BaseResponse<AssessmentReportData>).resource
//                        assessmentReportLiveData.postValue(resource)
                        updateResponseObserver(it)

                    }


                }
            }
        }

    }
}