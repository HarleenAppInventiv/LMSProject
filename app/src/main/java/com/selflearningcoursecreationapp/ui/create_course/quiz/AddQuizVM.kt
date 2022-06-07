package com.selflearningcoursecreationapp.ui.create_course.quiz

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.data.network.getRequestBody
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class AddQuizVM(private var repo: AddQuizRepo) : BaseViewModel() {

    var quizData = QuizData()
    var adapterPosition = 0
    var childPosition = 0
    var isQuiz: Boolean = true

    var currentAction: Int = 0
    fun getListData(index: Int): QuizQuestionData? {
        adapterPosition = index
        return quizData.list?.get(index)
    }


    fun addQuizAssessment() {
        if (isQuiz) {
            addQuiz()
        } else {
            addAssessment()
        }
    }


    fun addQuiz() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.addQuiz(quizData.getBasicData())
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

    fun addAssessment() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.addAssessment(quizData.courseId)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = (it.value as? BaseResponse<QuizData>)?.resource
                        quizData.assessmentId = data?.assessmentId
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    fun isQuizAdded(): Boolean {
        return (isQuiz && !quizData.quizId.isNullOrZero()) || (!isQuiz && !quizData.assessmentId.isNullOrZero())
    }

    fun isQuizTitleAdded(): Boolean {
        return (isQuiz && !quizData.quizName.isNullOrEmpty()) || (!isQuiz && !quizData.assessmentName.isNullOrEmpty())
    }

    fun saveQuestionValidation(adapterPosition: Int) {
        val codeId = getListData(adapterPosition)?.isDataValid()
        if (codeId == 0) {

            if (isQuizAdded()) {
                saveQuestion(getListData(adapterPosition))
            } else {
                this.adapterPosition = adapterPosition
                this.currentAction = 1
                addQuizAssessment()
            }
        } else {
            updateResponseObserver(Resource.Error(ToastData(codeId)))
        }
    }


    /*  private fun saveQuestion(data: QuizQuestionData?) {

          try {
              val map = JSONObject()
              map.put("courseId", quizData.courseId ?: 0)

              map.put("assessmentId", quizData.assessmentId ?: 0)
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
      }*/

    private fun saveQuestion(data: QuizQuestionData?) {
        val quesData = data?.getAddAssessmentQuestionData(quizData)
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                if (isQuiz) repo.addQuizQues(quesData) else repo.addAssessmentQues(quesData)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as? BaseResponse<QuizQuestionData>)?.resource
                        quizData.list?.get(adapterPosition)?.questionId = resource?.questionId
                        val optionId = resource?.optionList?.map { it.id }?.joinToString { "," }
                        quizData.list?.get(adapterPosition)?.optionList =
                            if (quizData.list?.get(adapterPosition)?.optionIds?.equals(optionId) == true) {
                                resource?.optionList ?: ArrayList()

                            } else {
                                resource?.optionList?.apply {
                                    forEach {
                                        it.ansId = 0
                                        it.isSelected = false
                                    }
                                } ?: ArrayList()
                            }
                        quizData.list?.get(adapterPosition)?.optionIds = optionId
                        quizData.list?.get(adapterPosition)?.questionImageId =
                            resource?.questionImageId
                        quizData.list?.get(adapterPosition)?.isEnabled = false


                    }
                    updateResponseObserver(it)
                }
            }
        }

//        } catch (e: JSONException) {
//            showException(e)
//        }
    }

    fun deleteQuestion(data: QuizQuestionData?) {


        viewModelScope.launch(coroutineExceptionHandle) {
            val response = if (isQuiz) repo.deleteQuizQues(
                quizData,
                data?.questionId
            ) else repo.deleteAssessmentQues(quizData, data?.questionId)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        quizData.list?.removeAt(adapterPosition)
                        updateResponseObserver(it)

                        if (!isQuizTitleAdded() && quizData.list.isNullOrEmpty()) {
                            deleteQuizAssessment()
                        }

                    } else {
                        updateResponseObserver(it)
                    }
                }
            }
        }

    }

    fun deleteQuizAssessment() {
        if (isQuiz) {
            deleteQuiz()
        } else {
            deleteAssessment()
        }
    }

    private fun deleteQuiz() {
        viewModelScope.launch(coroutineExceptionHandle) {
            var response = repo.deleteLecture(
                quizData?.courseId.toString(),
                quizData?.sectionId.toString(),
                quizData.lectureId.toString()
            )
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        quizData.quizId = null
                    }
                    updateResponseObserver(it)

//                    updateResponseObserver(Resource.Error(ToastData(errorString = "Lecture deleted ssuccessfully")))
                }
            }
        }
    }


    private fun deleteAssessment() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                repo.deleteAssessment(
                    quizData?.assessmentId ?: 0, quizData?.courseId ?: 0
                )
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }

    fun uploadImage(file: File, type: Int, position: Int, childPosition: Int) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = if (isQuiz) repo.uploadQuizImage(
                quizData,
                file,
                type
            ) else repo.uploadAssessmentImage(quizData, file, type)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = (it.value as? BaseResponse<ImageResponse>)?.resource

                        when (type) {
                            MEDIA_TYPE.QUIZ_QUES, MEDIA_TYPE.ASSESSMENT_QUES -> {
                                quizData.list?.get(position)?.questionImageId = data?.id
                                quizData.list?.get(position)?.questionImage = data?.fileUrl
                            }
                            MEDIA_TYPE.QUIZ_OPTION, MEDIA_TYPE.ASSESSMENT_OPTION -> {
                                quizData.list?.get(position)?.optionList?.get(childPosition)?.image =
                                    data?.fileUrl
                                quizData.list?.get(position)?.optionList?.get(childPosition)?.imageId =
                                    data?.id
                            }
                        }
                        delay(250)
                        updateResponseObserver(it)


                    } else {
                        updateResponseObserver(it)
                    }
                }
            }
        }

    }

    fun saveAnswer(ansList: JSONArray) {
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
    }

    fun getQuizQuestions() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                if (isQuiz) repo.getQuizQues(quizData.quizId ?: 0) else repo.getAssessmentQues(
                    quizData.assessmentId ?: 0
                )
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as? BaseResponse<QuizData>)?.resource?.let { data ->
                            data.courseId = quizData.courseId
                            data.list?.forEach { ques ->
                                ques.isEnabled = false
                                val optionId = ques?.optionList?.map { it.id }?.joinToString { "," }
                                ques.optionIds = optionId
                                ques.ansMarked =
                                    if (QUIZ.MATCH_COLUMN == ques.questionType) ques.optionList.find { it.ansId.isNullOrZero() } == null else !ques.optionList.filter { it.isSelected == true }
                                        .isNullOrEmpty()
                                ques.isExpanded = false
                            }
                            quizData = data
                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun continueAction() {
        when (currentAction) {
            1 -> {
                saveQuestionValidation(adapterPosition)
            }
            2 -> {
                val image = getListData(adapterPosition)?.questionImage
                uploadImage(
                    File(image),
                    if (isQuiz) MEDIA_TYPE.QUIZ_QUES else MEDIA_TYPE.ASSESSMENT_QUES,
                    adapterPosition,
                    0
                )
            }
            3 -> {
                val image = getListData(adapterPosition)?.optionList?.get(
                    childPosition
                )?.image

                uploadImage(
                    File(image),
                    if (isQuiz) MEDIA_TYPE.QUIZ_OPTION else MEDIA_TYPE.ASSESSMENT_OPTION,
                    adapterPosition,
                    childPosition
                )
            }
        }
    }

}