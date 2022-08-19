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
import com.selflearningcoursecreationapp.utils.CoAuthorStatus
import com.selflearningcoursecreationapp.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddCourseViewModel(private val repo: AddCourseRepo) : BaseViewModel() {

    private var dragId: Int? = 0
    var completedStep: Int = 0
    private var job: Job? = null
    var sectionAdapterPosition: Int = -1
    var sectionChildPosition: Int = -1
    var fromPosition: Int = -1
    var toPosition: Int = -1
    var mediaType = 0
    private var submitCourse: Boolean = false
    private var keywords: String = ""
    var sectionUpdateData = MutableLiveData<EventObserver<Int>>().apply {
        value = EventObserver(0)
    }
    var reviewUpdateData = MutableLiveData<EventObserver<Int>>().apply {
        value = EventObserver(0)
    }
    var keywordsData = MutableLiveData<ArrayList<SingleChoiceData>>().apply {
        value = ArrayList()
    }
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

    var masterData = MasterDataItem()
    fun getSectionList(): ArrayList<SectionModel>? = courseData.value?.sectionData
    private var uploadFile: File? = null
    private var isBannerUpload: Boolean = false
    private var previous: Int? = null
    private var last: Int? = null

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
                it.isStep3Verified(userProfile?.id)
            if (errorId == 0) {

                completeSteps(2)
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
                it.isStep3Verified(userProfile?.id)
            if (errorId == 0) {
                sectionUpdateData.postValue(EventObserver(Constant.CLICK_UPLOAD))
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }
    }

    private fun hitCoAuthorExitApi() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
            map["status"] = CoAuthorStatus.SUBMITTED
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

                completeSteps(3)
                getCourseSections()
                updateResponseObserver(Resource.Success(true, ApiEndPoints.VALID_DATA))
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }


    }

    private fun step1Api() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["courseTitle"] = courseData.value?.courseTitle?.trim() ?: ""
        map["courseDescription"] = courseData.value?.courseDescription ?: ""
        map["categoryId"] = courseData.value?.categoryId ?: ""
        map["keyTakeaways"] = courseData.value?.keyTakeaways ?: ""
        map["languageId"] = courseData.value?.languageId ?: ""
        val response = if (courseData.value?.courseId.isNullOrZero()) {
            repo.step1AddCourse(map)
        } else {
            map["courseId"] = courseData.value?.courseId ?: ""

            repo.step1UpdateCourse(map)
        }

        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    if (courseData.value?.courseId.isNullOrZero()) {
                        val data = it.value as BaseResponse<CourseData>

                        courseData.value?.courseId = data.resource?.courseId ?: 0
                    }
                    completeSteps(0)
                }
                updateResponseObserver(it)
            }
        }


    }

    private fun completeSteps(i: Int) {
        if (completedStep == i) {
            completedStep += 1
        }
    }


    private fun step2Api(courseLogoId: String?) = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["courseId"] = courseData.value?.courseId ?: 0
        map["courseTypeId"] = courseData.value?.courseTypeId ?: 0
        map["courseBannerId"] = courseData.value?.courseBannerId ?: ""
        map["courseLogoId"] = courseLogoId ?: ""
        map["targetAudienceId"] =
            courseData.value?.targetAudiences?.map { it.id }?.joinToString(",") ?: ""
        map["courseComplexityId"] = courseData.value?.courseComplexityId ?: 0
        map["keyTakeaways"] = courseData.value?.keyTakeaways ?: ""
        map["courseFee"] = courseData.value?.courseFee ?: ""
        map["rewardPoints"] = courseData.value?.rewardPoints?.toIntOrNull() ?: 0

        val response = repo.step2AddCourse(map)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {


                    completeSteps(1)
                    getCourseSections()
                }
                updateResponseObserver(it)
            }
        }


    }

    fun notifyData() {
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
                        completedStep = when (resource.completeStep) {
                            1 -> 1
                            3 -> 2
                            7 -> 3
                            15 -> 4
                            else -> {
                                1
                            }
                        }
                        resource.sectionData = resource.sectionData?.map { section ->
                            section.apply {
                                isSaved = section.isDataValid(
                                    false,
                                    currentId = userProfile?.id ?: 0
                                ) == 0
                            }
                        } as ArrayList<SectionModel>
                        courseData.postValue(resource)
                        courseData.value?.notifyChange()


                        isCreator.postValue((courseData.value?.createdById == userProfile?.id))

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

                        data.sectionData = resource.sectionData?.map { section ->
                            section.apply {
                                isVisible = false
                                isSaved = section.isDataValid(
                                    false,
                                    currentId = userProfile?.id ?: 0
                                ) == 0
                            }
                        } as ArrayList<SectionModel>
                        courseData.postValue(data)
                        courseData.value?.notifyChange()
                        sectionUpdateData.postValue(EventObserver(Constant.CLICK_DETAILS))
                        reviewUpdateData.postValue(EventObserver(Constant.CLICK_DETAILS))

                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun uploadImage(file: File, isBanner: Boolean) {
        uploadFile = file
        isBannerUpload = isBanner
        viewModelScope.launch {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData(
                "File",
                file.name,
                requestFile
            )
            val body =
                file.name.toRequestBody(("text/plain").toMediaTypeOrNull()) // String requestBody
            val response = repo.uploadCourseImage(
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

                        setBannerImageData(isBanner, data)
                    }

                    updateResponseObserver(it)
                }
            }
        }
    }

    private fun setBannerImageData(
        isBanner: Boolean,
        data: ImageResponse?,
    ) {
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

    fun addSection() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["CourseId"] = courseData.value?.courseId ?: 0

        val response = repo.addSection(map)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    getCourseSections()

                    sectionUpdateData.postValue(EventObserver(Constant.CLICK_ADD))
                }

                updateResponseObserver(it)
            }
        }

    }

    fun deleteSection() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.deleteSection(
                courseData.value?.courseId.toString(),
                getSectionList()?.get(sectionAdapterPosition)?.sectionId.toString()
            )
            withContext(Dispatchers.IO) {
                response.collect {

                    if (it is Resource.Success<*>) {
                        getSectionList()?.removeAt(sectionAdapterPosition)
                        sectionUpdateData.postValue(EventObserver(Constant.CLICK_DELETE))
                        getCourseSections()
                    }
                    updateResponseObserver(it)

                }
            }

        }


    fun dragAndDropSection(first: Int?, second: Int?) {
        previous = first
        last = second
        job?.cancel()
        job = viewModelScope.launch(coroutineExceptionHandle)
        {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
            map["firstSectionId"] = first.toString()
            map["secondSectionId"] = second.toString()
            map["finalSectionId"] = getSectionList()?.get(fromPosition)?.sectionId?.toString() ?: ""
            val response = repo.dragAndDropSection(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }
    }


    fun addLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
            map["sectionId"] = getSectionList()?.get(sectionAdapterPosition)?.sectionId.toString()
            map["mediaTypeId"] = mediaType.toString()
            val response = repo.addLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        addLectureLiveData.postValue(EventObserver((it.value as BaseResponse<ChildModel>).resource?.lectureId) as EventObserver<Int>?)
                    }
                    updateResponseObserver(it)
                }
            }
        }

    fun deleteLecture() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.deleteLecture(
                courseData.value?.courseId.toString(),
                getSectionList()?.get(sectionAdapterPosition)?.sectionId.toString(),
                getSectionList()?.get(sectionAdapterPosition)?.lessonList?.get(sectionChildPosition)?.lectureId.toString()

            )
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        getSectionList()?.get(sectionAdapterPosition)?.lessonList?.removeAt(
                            sectionChildPosition
                        )
                        sectionUpdateData.postValue(EventObserver(Constant.CLICK_DELETE))
                    }
                    updateResponseObserver(it)
                }
            }
        }


    fun dragAndDropLecture(final: Int?, first: Int?, second: Int?) {
        previous = first
        last = second
        dragId = final
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
            map["firstLectureId"] = first.toString()
            map["secondLectureId"] = second.toString()
            map["finalLectureId"] = final.toString()
            val response = repo.dragAndDropLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }
        }

    }

    private fun addPatchSection(
        sectionId: Int?,
        sectionTitle: String,
        sectionDesc: String,
    ) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
            map["sectionId"] = sectionId.toString()
            map["sectionTitle"] = sectionTitle?.trim()
            map["sectionDescription"] = sectionDesc?.trim()
            val response = repo.addPatchSection(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        getCourseSections()
                        sectionUpdateData.postValue(EventObserver(Constant.CLICK_SAVE))
                    }
                    updateResponseObserver(it)
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

                            data.list?.forEach { quesData ->
                                quesData.isEnabled = false
                                quesData.ansMarked = true
                                quesData.isExpanded = true
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
                errorId = it.isStep3Verified(userProfile?.id)
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

        submitCourse = submit
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            map["courseId"] = courseData.value?.courseId ?: 0
            map["courseSubmit"] = submit
            if (!courseData.value?.keywords.isNullOrEmpty()) {
                map["keywords"] =
                    courseData.value?.keywords?.map { SingleChoiceData(title = it) } as ArrayList<SingleChoiceData>
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

    fun updateSection() {
        courseData.value?.sectionData?.get(sectionAdapterPosition)?.let {
            val errorId = it.isDataValid(false, currentId = userProfile?.id ?: 0)

            if (errorId == 0) {
                addPatchSection(
                    it.sectionId,
                    it.sectionTitle?.trim() ?: "",
                    it.sectionDescription?.trim() ?: ""
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
        keywords = keyword
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            map["categoryId"] = courseData.value?.categoryId ?: 0
            map["keyword"] = keyword
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

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_COAUTHOR_INVITATION -> {
                step3Validation()
            }
            ApiEndPoints.API_CRE_STEP_1 -> {
                step1Validation()
            }
            ApiEndPoints.API_CRE_STEP_2 -> {
                step2Validation()
            }
            ApiEndPoints.API_MASTER_DATA -> {
                getMasterData()
            }
            ApiEndPoints.API_CRE_STEP_1 + "/detail" -> {
                getCourseDetail()
            }
            ApiEndPoints.API_GET_SECTIONS -> {
                getCourseSections()
            }
            ApiEndPoints.API_UPLOAD_IMAGE -> {
                uploadFile?.let { uploadImage(it, isBannerUpload) }
            }
            ApiEndPoints.API_ADD_SECTION -> {
                addSection()
            }
            ApiEndPoints.API_ADD_SECTION + "/delete" -> {
                deleteSection()
            }
            ApiEndPoints.API_SECTION_DRAG_DROP -> {
                dragAndDropSection(previous, last)
            }
            ApiEndPoints.API_ADD_LECTURE_POST -> {
                addLecture()
            }
            ApiEndPoints.API_LECTURE_DELETE + "/delete" -> {
                deleteLecture()
            }
            ApiEndPoints.API_LECTURE_DRAG_DROP -> {
                dragAndDropLecture(dragId, previous, last)
            }
            ApiEndPoints.API_ADD_SECTION + "/patch" -> {
                updateSection()
            }
            ApiEndPoints.API_ADD_ASSESSMENT -> {
                getAssessmentQues()
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete/true" -> {
                deleteAssessment(true)
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete/false" -> {
                deleteAssessment(false)
            }
            ApiEndPoints.API_PUBLISH_COURSE -> {
                publishCourse(submitCourse)
            }
            ApiEndPoints.API_GET_KEYWORDS -> {
                getKeywords(keywords)
            }
        }
    }


}