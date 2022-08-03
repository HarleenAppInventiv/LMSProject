package com.selflearningcoursecreationapp.ui.create_course.quiz

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.QUIZ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddQuizVM(private var repo: AddQuizRepo) : BaseViewModel() {
    var hashmap = HashMap<String, ArrayList<QuizOptionData>>()

    var quizData = QuizData()
    var adapterPosition = 0
    var childPosition = 0
    var isQuiz: Boolean = true
    private var uploadFile: File? = null
    private var uploadFileType: Int? = null
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


    private fun addQuiz() {
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

    private fun addAssessment() {
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

    fun saveQuestionValidation() {
        val codeId = getListData(adapterPosition)?.isDataValid()
        if (codeId == 0) {

            if (isQuizAdded()) {
                saveQuestion(getListData(adapterPosition))
            } else {
                this.currentAction = 1
                addQuizAssessment()
            }
        } else {
            updateResponseObserver(Resource.Error(ToastData(codeId)))
        }
    }



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
                        val optionId =
                            resource?.optionList?.map { option -> option.id }?.joinToString { "," }
                        quizData.list?.get(adapterPosition)?.optionList =
                            if (quizData.list?.get(adapterPosition)?.optionIds?.equals(optionId) == true) {
                                resource?.optionList ?: ArrayList()

                            } else {
                                resource?.optionList?.onEach { option ->
                                    option.ansId = 0
                                    option.isSelected = false
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

    }

    fun deleteQuestion() {


        viewModelScope.launch(coroutineExceptionHandle) {
            val response = if (isQuiz) repo.deleteQuizQues(
                quizData,
                getListData(adapterPosition)?.questionId
            ) else repo.deleteAssessmentQues(quizData, getListData(adapterPosition)?.questionId)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        quizData.list?.removeAt(adapterPosition)
                        updateResponseObserver(it)
//
//                        if (!isQuizTitleAdded() && quizData.list.isNullOrEmpty()) {
//                            deleteQuizAssessment()
//                        }

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
            val response = repo.deleteLecture(
                quizData.courseId.toString(),
                quizData.sectionId.toString(),
                quizData.lectureId.toString()
            )
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        quizData.quizId = null
                    }
                    updateResponseObserver(it)

                }
            }
        }
    }


    private fun deleteAssessment() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                repo.deleteAssessment(
                    quizData.assessmentId ?: 0, quizData.courseId ?: 0
                )
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }

    fun uploadImage(file: File, type: Int, position: Int, childPosition: Int) {
        uploadFile = file
        uploadFileType = type
        this.childPosition = childPosition
        adapterPosition = position
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
                            MediaType.QUIZ_QUES, MediaType.ASSESSMENT_QUES -> {
                                quizData.list?.get(position)?.questionImageId = data?.id
                                quizData.list?.get(position)?.questionImage = data?.fileUrl
                            }
                            MediaType.QUIZ_OPTION, MediaType.ASSESSMENT_OPTION -> {
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
                                val optionId =
                                    ques.optionList.map { option -> option.id }.joinToString { "," }
                                ques.optionIds = optionId
                                ques.ansMarked =
                                    if (QUIZ.MATCH_COLUMN == ques.questionType) {
                                        ques.optionList.find { option -> option.ansId.isNullOrZero() } == null
                                    } else {
                                        !ques.optionList.filter { option -> option.isSelected == true }
                                            .isNullOrEmpty()
                                    }
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
                saveQuestionValidation()
            }
            2 -> {
                val image = getListData(adapterPosition)?.questionImage
                uploadImage(
                    File(image),
                    if (isQuiz) MediaType.QUIZ_QUES else MediaType.ASSESSMENT_QUES,
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
                    if (isQuiz) MediaType.QUIZ_OPTION else MediaType.ASSESSMENT_OPTION,
                    adapterPosition,
                    childPosition
                )
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ADD_QUIZ + "/add" -> {
                addQuiz()
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/add" -> {
                addAssessment()
            }
            ApiEndPoints.API_ADD_QUIZ_QUESTION + "/add", ApiEndPoints.API_ADD_ASSESSMENT_QUESTION + "/add" -> {
                saveQuestionValidation()
            }
            ApiEndPoints.API_ADD_QUIZ_QUESTION + "/delete", ApiEndPoints.API_ADD_ASSESSMENT_QUESTION + "/delete" -> {
                deleteQuestion()
            }
            ApiEndPoints.API_LECTURE_DELETE + "/delete" -> {
                deleteQuiz()
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete" -> {
                deleteAssessment()
            }
            ApiEndPoints.API_ADD_QUIZ_IMAGE, ApiEndPoints.API_ADD_ASSESSMENT_IMAGE -> {
                uploadFile?.let {
                    uploadImage(
                        it,
                        uploadFileType ?: MediaType.QUIZ_QUES,
                        adapterPosition,
                        childPosition
                    )
                }
            }

            ApiEndPoints.API_ADD_QUIZ + "/get", ApiEndPoints.API_ADD_ASSESSMENT + "/get" -> {
                getQuizQuestions()
            }
        }
    }

}