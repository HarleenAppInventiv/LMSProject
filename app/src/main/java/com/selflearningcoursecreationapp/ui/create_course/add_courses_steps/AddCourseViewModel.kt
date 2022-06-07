package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.models.SingleClickResponse
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.AddCourseRepo
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.COAUTHOR_STATUS
import com.selflearningcoursecreationapp.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddCourseViewModel(private val repo: AddCourseRepo) : BaseViewModel() {
    var completedStep: Int = 0
    var job: Job? = null

//    var addSectionLiveData = MutableLiveData<EventObserver<Int>>().apply {
//        value = EventObserver(0)
//    }

    var sectionUpdationData = MutableLiveData<EventObserver<Int>>().apply {
        value = EventObserver(0)
    }
    var reviewUpdationData = MutableLiveData<EventObserver<Int>>().apply {
        value = EventObserver(0)
    }
    var keywordsData = MutableLiveData<ArrayList<SingleChoiceData>>().apply {
        value = ArrayList()
    }

//    var deleteSectionLiveData = MutableLiveData<EventObserver<Boolean>>().apply {
//        value = EventObserver(false)
//    }


    var courseData = MutableLiveData<CourseData>().apply {
        value = CourseData(createdById = userProfile?.id, isCreator = true)

    }

    var assessmentData = MutableLiveData<QuizData>().apply {
        value = QuizData()
    }

    var addLectureLiveData = MutableLiveData<EventObserver<Int>>().apply {
        value = EventObserver(0)

    }

    var isCreator = MutableLiveData<Boolean>().apply {
        value = courseData.value?.createdById == userProfile?.id
    }

    //    var deleteLectureLiveData = MutableLiveData<EventObserver<Boolean>>().apply {
//        value = EventObserver(false)
//
//    }1119
//
//    var addPatchSectionLiveData = MutableLiveData<EventObserver<Boolean>>().apply {
//        value = EventObserver(false)
//
//    }
    var masterData = MasterDataItem()
    fun getSectionList(): ArrayList<SectionModel>? = courseData.value?.sectionData


    fun step1Validation() {
        if (isCreator.value == true) {
            courseData.value?.let {
                val errorId = it.isStep1Verified()
                if (errorId == 0) {
                    step1Api()
                } else {
                    updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))
                }
            }
        } else {
            updateResponseObserver(Resource.Success(true, ApiEndPoints.VALID_DATA))

        }

    }


    fun step2Validation() {
        if (isCreator.value == true) {
            courseData.value?.let {
                val errorId = it.isStep2Verified()
                if (errorId == 0) {
                    step2Api(it.courseLogoId)
                } else {
                    updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))
                }
            }
        } else {
            if (getCoAuthor() != null && !getCoAuthor()?.courseLogoId.isNullOrEmpty()) {
                step2Api(getCoAuthor()?.courseLogoId)
            } else {
                updateResponseObserver(Resource.Success(true, ApiEndPoints.VALID_DATA))

            }
        }
    }

    fun step3Validation() {
        courseData.value?.let {
            val errorId =
                /* if (isCreator.value == true) it.isStep3Verified() else*/
                it.isStep3CoAuthorVerified(userProfile?.id)
            if (errorId == 0) {
                if (completedStep == 2) {
                    completedStep += 1
                }
                if (isCreator.value == true)
                    updateResponseObserver(Resource.Success(true, ApiEndPoints.VALID_DATA))
                else {
                    hitCoAuthorExitApi()
                }
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }


    }

    fun step3SingleValidation() {
        courseData.value?.let {
            val errorId =
                it.isStep3CoAuthorVerified(userProfile?.id)
            if (errorId == 0) {
                sectionUpdationData.postValue(EventObserver(Constant.CLICK_UPLOAD))
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }
    }

    private fun hitCoAuthorExitApi() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
            map["status"] = COAUTHOR_STATUS.SUBMITTED.toInt()
            val response = repo.updateCoAuthorStatus(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }

    fun step4Validation() {
        assessmentData.value?.let {
            val errorId = it.isAssessmentValid()
            if (errorId == 0) {
                if (completedStep == 3) {
                    completedStep += 1
                }
                getCourseSections()
                updateResponseObserver(Resource.Success(true, ApiEndPoints.VALID_DATA))
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }


    }

    fun step1Api() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["courseTitle"] = courseData.value!!.courseTitle ?: ""
        map["courseDescription"] = courseData.value!!.courseDescription ?: ""
        map["categoryId"] = courseData.value!!.categoryId ?: ""
        map["keyTakeaways"] = courseData.value!!.keyTakeaways ?: ""
        map["languageId"] = courseData.value!!.languageId ?: ""
        val response = if (courseData.value!!.courseId.isNullOrZero()) {
            repo.step1AddCourse(map)
        } else {
            map["courseId"] = courseData.value!!.courseId ?: ""

            repo.step1UpdateCourse(map)
        }

        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    if (courseData.value!!.courseId.isNullOrZero()) {
                        val data = it.value as BaseResponse<CourseData>

                        courseData.value?.courseId = data.resource?.courseId ?: 0
                    }
                    if (completedStep == 0) {
                        completedStep += 1
                    }
                }
                updateResponseObserver(it)
            }
        }


    }


    fun step2Api(courseLogoId: String?) = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["courseId"] = courseData.value!!.courseId!!
        map["courseTypeId"] = courseData.value!!.courseTypeId!!
        map["courseBannerId"] = courseData.value!!.courseBannerId ?: ""
        map["courseLogoId"] = courseLogoId ?: ""
//        map["courseLogoUrl"] = courseData.value!!.courseLogoUrl ?: ""
//        map["courseLogoHash"] = courseData.value!!.courseLogoHash ?: ""
        map["targetAudienceId"] =
            courseData.value!!.targetAudiences?.map { it.id }?.joinToString(",") ?: ""
        map["courseComplexityId"] = courseData.value!!.courseComplexityId!!
        map["keyTakeaways"] = courseData.value!!.keyTakeaways ?: ""
        map["courseFee"] = courseData.value!!.courseFee ?: ""
//        map["rewardPoints"] = checkedReward.value!!

        val response = repo.step2AddCourse(map)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    if (completedStep == 1) {
                        completedStep += 1
                    }
                    getCourseSections()
                }
                updateResponseObserver(it)
            }
        }


    }

    fun notifyData() {
//        courseData.value = courseData.value
        courseData.value?.notifyChange()
    }

    fun getMasterData() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getMasterData()
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        masterData =
                            (it.value as BaseResponse<MasterDataItem>).resource ?: MasterDataItem()
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun getCourseDetail() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCourseDetail(courseData.value?.courseId ?: 0)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource =
                            (it.value as BaseResponse<CourseData>).resource ?: CourseData()
                        resource.isPaid = resource.courseTypeId == 2
                        resource.isCreator = resource.createdById == userProfile?.id
                        resource.isCoAuthor = resource.getCoAuthor(userProfile?.id ?: 0) != null
                        resource.coAuthorId = resource.getCoAuthor(userProfile?.id ?: 0)?.id ?: 0
                        resource?.sectionData = resource.sectionData?.map {
                            it.apply {
                                isSaved = it.isDataValid(false) == 0
                            }
                        } as ArrayList<SectionModel>
                        courseData.postValue(resource)
                        courseData.value?.notifyChange()
                        isCreator.value = (courseData.value?.createdById == userProfile?.id)

                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun getCourseSections() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCourseSections(courseData.value?.courseId ?: 0)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource =
                            (it.value as BaseResponse<CourseData>).resource ?: CourseData()

                        val data = courseData.value ?: CourseData()

                        data.isPaid = data.courseTypeId == 2
                        data.isCreator = data.createdById == userProfile?.id
                        data.isCoAuthor = data.getCoAuthor(userProfile?.id ?: 0) != null
                        data.coAuthorId = data.getCoAuthor(userProfile?.id ?: 0)?.id ?: 0

                        data?.sectionData = resource.sectionData?.map {
                            it.apply {
                                isVisible = false
                                isSaved = it.isDataValid(false) == 0
                            }
                        } as ArrayList<SectionModel>
                        courseData.postValue(data)
                        courseData.value?.notifyChange()
                        sectionUpdationData.postValue(EventObserver(Constant.CLICK_DETAILS))
                        reviewUpdationData.postValue(EventObserver(Constant.CLICK_DETAILS))
//                        isCreator.value = (courseData.value?.createdById == userProfile?.id)

                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun uploadImage(file: File, isBanner: Boolean) = viewModelScope.launch {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData(
            "File",
            file.name,
            requestFile
        )
        val body =
            file.name.toRequestBody(("text/plain").toMediaTypeOrNull()) // String requestBody
        var response = repo.uploadCourseImage(
            filePart,
            body,
            courseData.value?.courseId?.toString()
                ?.toRequestBody(("text/plain").toMediaTypeOrNull()),
            isBanner
        )
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = (it.value as BaseResponse<ImageResponse>).resource

                    if (isBanner) {
                        courseData.value?.courseBannerUrl = data?.fileUrl
                        courseData.value?.courseBannerId = data?.id
                        courseData.value?.courseBannerHash = data?.blurHash
                    } else {
                        courseData.value?.courseLogoUrl = data?.fileUrl
                        courseData.value?.courseLogoId = data?.id
                        courseData.value?.courseLogoHash = data?.blurHash

                        if (courseData.value?.isCoAuthor == true) {
                            getCoAuthor()?.courseLogoURL = data?.fileUrl
                            getCoAuthor()?.courseLogoId = data?.id
                            isCreator.postValue(courseData.value?.createdById == userProfile?.id)
                        }

                    }
                }

                updateResponseObserver(it)
            }
        }
    }

    fun addSection() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["CourseId"] = courseData.value?.courseId ?: 0
//        map["CourseId"] = "8"
        var response = repo.addSection(map)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    getCourseSections()

//                    if (getSectionList() == null) {
//                        courseData.value?.sectionData = ArrayList()
//                    }
//                    courseData.value?.sectionData!!.add(
//                        SectionModel(
//                            sectionId = (it.value as BaseResponse<SectionModel>).resource?.sectionId,
//                            sectionCreatedById = userProfile?.id,
//                            sectionCreatedByProfileURL = userProfile?.profileUrl,
//                            sectionCreatedByName = userProfile?.name,
//                            sectionLogoURL = if (isCreator.value == true) courseData.value?.courseLogoUrl else getCoAuthor()?.courseLogoURL,
//                            isVisible = true,
//                        )
//                    )

                    sectionUpdationData.postValue(EventObserver(Constant.CLICK_ADD))
                }

                updateResponseObserver(it)
            }
        }

    }

    fun deleteSection(sectionId: Int?, adapterPosition: Int) =
        viewModelScope.launch(coroutineExceptionHandle) {
            var response = repo.deleteSection(
                courseData.value?.courseId.toString(),
                sectionId.toString()
            )
            withContext(Dispatchers.IO) {
                response.collect {

                    if (it is Resource.Success<*>) {
                        getSectionList()?.removeAt(adapterPosition)
                        sectionUpdationData.postValue(EventObserver(Constant.CLICK_DELETE))
                        getCourseSections()
                    }
                    updateResponseObserver(it)

//                    updateResponseObserver(Resource.Error(ToastData(errorString = "Section deleted ssuccessfully")))
                }
            }

        }


    fun dragAndDropSection(final: Int?, first: Int?, second: Int?) {
        job?.cancel()
        job = viewModelScope.launch(coroutineExceptionHandle)
        {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
//            map["courseId"] = "8"
            map["firstSectionId"] = first.toString()
            map["secondSectionId"] = second.toString()
            map["finalSectionId"] = final.toString()
            var response = repo.dragAndDropSection(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }


    fun addLecture(sectionId: Int?, mediaTypeId: Int?) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
//            map["courseId"] = "8"
            map["sectionId"] = sectionId.toString()
            map["mediaTypeId"] = mediaTypeId.toString()
            var response = repo.addLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        addLectureLiveData.postValue(EventObserver((it.value as BaseResponse<ChildModel>).resource?.lectureId) as EventObserver<Int>?)
//                        getCourseSections()
                    }
                    updateResponseObserver(it)
                }
            }
        }

    fun deleteLecture(adapterPosition: Int, childPosition: Int) =
        viewModelScope.launch(coroutineExceptionHandle) {
            var response = repo.deleteLecture(
                courseData.value?.courseId.toString(),
                getSectionList()?.get(adapterPosition)?.sectionId.toString(),
                getSectionList()?.get(adapterPosition)?.lessonList?.get(childPosition)?.lectureId.toString()

            )
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        getSectionList()?.get(adapterPosition)?.lessonList?.removeAt(childPosition)
                        sectionUpdationData.postValue(EventObserver(Constant.CLICK_DELETE))
//                        getCourseSections()
                    }
                    updateResponseObserver(it)

//                    updateResponseObserver(Resource.Error(ToastData(errorString = "Lecture deleted ssuccessfully")))
                }
            }
        }


    fun dragAndDropLecture(final: Int?, first: Int?, second: Int?) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
//            map["courseId"] = "8"
            map["firstLectureId"] = first.toString()
            map["secondLectureId"] = second.toString()
            map["finalLectureId"] = final.toString()
            var response = repo.dragAndDropLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }


    fun addPatchSection(
        sectionId: Int?,
        sectionTitle: String,
        sectionDesc: String,
    ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
//            map["courseId"] = "8"
            map["sectionId"] = sectionId.toString()
            map["sectionTitle"] = sectionTitle.toString()
            map["sectionDescription"] = sectionDesc.toString()
            var response = repo.addPatchSection(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        getCourseSections()
                        sectionUpdationData.postValue(EventObserver(Constant.CLICK_SAVE))
                    }
                    updateResponseObserver(it)
//                    updateResponseObserver(Resource.Error(ToastData(errorString = "Section saved ssuccessfully")))
                }
            }
        }

    fun getAssessmentQues() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                repo.getAssessmentQues(
                    courseData.value?.assessmentId ?: 0
                )
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as? BaseResponse<QuizData>)?.resource?.let { data ->

                            data.list?.forEach {
                                it.isEnabled = false
                                it.ansMarked = true
                                it.isExpanded = true
                            }
                            assessmentData.postValue(data)
                        }

                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun deleteAssessment(show: Boolean = true) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response =
                repo.deleteAssessment(
                    courseData.value?.assessmentId ?: 0, courseData.value?.courseId ?: 0, show
                )
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        courseData.value?.assessmentId = null
                        courseData.value?.assessmentFreezeContent = false
                        courseData.value?.assessmentMandatory = false
                        courseData.value?.assessmentName = ""
                        courseData.value?.notifyPropertyChanged(BR.dataEntered)
                        assessmentData.postValue(QuizData())


                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun step5Validation() {
        courseData.value?.let {
            var errorId = it.isStep1Verified()
            if (errorId == 0) {
                errorId = it.isStep2Verified()
            }
            if (errorId == 0) {
                errorId = it.isStep3CoAuthorVerified(userProfile?.id)
            }
            if (errorId == 0) {
                errorId = assessmentData.value?.isAssessmentValid() ?: 0
            }

            if (errorId == 0) {
                publishCourse(false)
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }
    }

    fun publishCourse(submit: Boolean) {


        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            map.put("courseId", courseData.value?.courseId ?: 0)
            map.put("courseSubmit", submit)
            if (!courseData.value?.keywords.isNullOrEmpty()) {
                map.put(
                    "keywords",
                    courseData.value?.keywords?.map { SingleChoiceData(title = it) } as ArrayList<SingleChoiceData>)
            }
            val response =
                repo.publishCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }

    fun updateSection(adapterPosition: Int) {
        courseData.value?.sectionData?.get(adapterPosition)?.let {
            val errorId = it.isDataValid(false, currentId = userProfile?.id ?: 0)

            if (errorId == 0) {
                addPatchSection(
                    it.sectionId,
                    it.sectionTitle!!,
                    it.sectionDescription!!
                )
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }
        }
    }


    fun getCoAuthor(): UserProfile? {
        return courseData.value?.getCoAuthor(userProfile?.id ?: 0)
    }

    fun getKeywords(keyword: String) {

        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            map.put("categoryId", courseData.value?.categoryId ?: 0)
            map.put("keyword", keyword)
            val response =
                repo.getKeywords(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        keywordsData.postValue(
                            (it.value as BaseResponse<SingleClickResponse>).resource?.list
                                ?: ArrayList()
                        )
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun checkAllSteps(step: Int) {

    }

}