package com.selflearningcoursecreationapp.ui.create_course.quiz

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.data.network.getRequestBody
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.utils.MEDIA
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class AddQuizVM(private var repo: AddQuizRepo) : BaseViewModel() {

    var quizData = QuizData()
    var adapterPosition = 0
    fun getListData(index: Int): QuizQuestionData? {
        adapterPosition = index
        return quizData.list?.get(index)
    }

    fun addQuiz() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.addQuiz(quizData)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = (it.value as? BaseResponse<QuizData>)?.resource
                        quizData.quizId = data?.quizId
                        quizData.lectureId = data?.lectureId
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun saveQuestionValidation(adapterPosition: Int) {
        val codeId = getListData(adapterPosition)?.isDataValid()
        if (codeId == 0) {
            saveQuestion(getListData(adapterPosition))
        } else {
            updateResponseObserver(Resource.Error(ToastData(codeId)))
        }
    }

    private fun saveQuestion(data: QuizQuestionData?) {

        try {
            val map = JSONObject()
            map.put("courseId", quizData.courseId ?: 0)
            map.put("sectionId", quizData.sectionId ?: 0)
            map.put("lectureId", quizData.lectureId ?: 0)
            map.put("quizId", quizData.quizId ?: 0)
            map.put("questionTypeId", data?.questionType ?: 0)
            map.put("questionTitle", data?.title ?: "")
            if (!data?.questionImageId.isNullOrEmpty()) {
                map.put("questionImageId", data?.questionImageId ?: "")

            }
            if (!data?.questionId.isNullOrZero()) {
                map.put("questionId", data?.questionId ?: 0)

            }
            val optionArray = JSONArray()
            data?.optionList?.forEach {
                val optionObject = JSONObject()
                if (!it?.id.isNullOrZero()) {
                    optionObject.put("optionId", it?.id ?: 0)

                }
                when (data?.questionType) {
                    QUIZ.DRAG_DROP, QUIZ.IMAGE_BASED -> {
                        if (!it.imageId.isNullOrEmpty()) {
                            optionObject.put("imageId", it.imageId)
                        } else {
                            optionObject.put("option1", it.option1)


                        }
                    }
                    QUIZ.MATCH_COLUMN -> {
                        optionObject.put("option2", it.option2)
                        optionObject.put("option1", it.option1)
                    }
                    else -> {
                        optionObject.put("option1", it.option1)

                    }

                }
                optionArray.put(optionObject)
            }

            map.put("options", optionArray)
            val quesData = Gson().fromJson(map.toString(), QuizQuestionData::class.java)
            viewModelScope.launch(coroutineExceptionHandle) {
                val response = repo.addQuizQues(quesData)
                withContext(Dispatchers.IO) {
                    response.collect {
                        if (it is Resource.Success<*>) {
                            val resource = (it.value as? BaseResponse<QuizQuestionData>)?.resource
                            quizData.list?.get(adapterPosition)?.questionId = resource?.questionId
                            quizData.list?.get(adapterPosition)?.questionImageId =
                                resource?.questionImageId
                            quizData.list?.get(adapterPosition)?.isEnabled = false
                            quizData.list?.get(adapterPosition)?.optionList =
                                resource?.optionList ?: ArrayList()
                        }
                        updateResponseObserver(it)
                    }
                }
            }

        } catch (e: JSONException) {
            showException(e)
        }
    }

    fun deleteQuestion(data: QuizQuestionData?) {

//        try {
//            val map = JSONObject()
//            map.put("courseId", quizData.courseId ?: 0)
//            map.put("sectionId", quizData.sectionId ?: 0)
//            map.put("lectureId", quizData.lectureId ?: 0)
//            map.put("quizId", quizData.quizId ?: 0)
//
//           if (!data?.questionId.isNullOrZero()) {
//                map.put("questionId", data?.questionId ?:0)
//
//            }

        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.deleteQuizQues(quizData, data?.questionId)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {

                        quizData.list?.removeAt(adapterPosition)
                    }
                    updateResponseObserver(it)
                }
            }
        }

//        } catch (e: JSONException) {
//            showException(e)
//        }
    }

    fun uploadImage(file: File, type: Int, position: Int, childPosition: Int) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.uploadQuizImage(quizData, file, type)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = (it.value as? BaseResponse<ImageResponse>)?.resource

                        when (type) {
                            MEDIA.QUIZ_QUES -> {
                                quizData.list?.get(position)?.questionImageId = data?.id
//                                quizData.list?.get(position)?.questionImage= data?.fileUrl
                            }
                            MEDIA.QUIZ_OPTION -> {
//                                quizData.list?.get(position)?.optionList?.get(childPosition)?.image=data?.fileUrl
                                quizData.list?.get(position)?.optionList?.get(childPosition)?.imageId =
                                    data?.id
                            }
                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }

    }

    fun saveAnswer(ansList: JSONArray) {
        val map = JSONObject()
        map.put("courseId", quizData.courseId ?: 0)
        map.put("sectionId", quizData.sectionId ?: 0)
        map.put("lectureId", quizData.lectureId ?: 0)
        map.put("quizId", quizData.quizId ?: 0)
        map.put("questionId", quizData.list?.get(adapterPosition)?.questionId ?: 0)
        map.put("questionTypeId", quizData.list?.get(adapterPosition)?.questionType ?: 0)
        map.put("answeres", ansList)
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.saveQuizAns(map.getRequestBody())
            withContext(Dispatchers.IO) {
                response.collect {
//                    if (it is Resource.Success<*>) {
//                        val resource = (it.value as? BaseResponse<QuizQuestionData>)?.resource
//                        quizData.list?.get(adapterPosition)?.questionId = resource?.questionId
//                        quizData.list?.get(adapterPosition)?.questionImageId = resource?.questionImageId
//                        quizData.list?.get(adapterPosition)?.isEnabled = false
//                        quizData.list?.get(adapterPosition)?.optionList = resource?.optionList?:ArrayList()
//                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

}