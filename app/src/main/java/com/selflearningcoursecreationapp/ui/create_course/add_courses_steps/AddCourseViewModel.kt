package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.AddCourseRepo
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddCourseViewModel(private val repo: AddCourseRepo) : BaseViewModel() {

    val users = ArrayList<SectionModel>()


    var addSectionLiveData = MutableLiveData<EventObserver<Int>>().apply {
        value = EventObserver(0)
    }

    var deleteSectionLiveData = MutableLiveData<EventObserver<Boolean>>().apply {
        value = EventObserver(false)
    }

    var dragAndDropLiveData = MutableLiveData<String>().apply {
        value = "false"

    }
    var courseData = MutableLiveData<CourseData>().apply {
        value = CourseData()

    }

    var addLectureLiveData = MutableLiveData<EventObserver<Int>>().apply {
        value = EventObserver(0)

    }

    var deleteLectureLiveData = MutableLiveData<EventObserver<Boolean>>().apply {
        value = EventObserver(false)

    }

    var addPatchSectionLiveData = MutableLiveData<EventObserver<Boolean>>().apply {
        value = EventObserver(false)

    }
    var masterData = MasterDataItem()


    fun step1Validation() {
        courseData.value?.let {
            val errorId = it.isStep1Verified()
            if (errorId == 0) {
                step1Api()
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))
            }
        }

    }


    fun step2Validation() {
        courseData.value?.let {
            val errorId = it.isStep2Verified()
            if (errorId == 0) {
                step2Api()
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
        map["languageId"] = courseData.value!!.languageId ?: ""
        val response = if (courseData.value!!.courseId.isNullOrZero()) {
            repo.step1AddCourse(map)
        } else {
            map["courseId"] = courseData.value!!.courseId ?: ""

            repo.step1UpdateCourse(map)
        }

        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*> && courseData.value!!.courseId.isNullOrZero()) {
                    val data = it.value as BaseResponse<CourseData>
                    courseData.value?.courseId = data.resource?.courseId ?: 0
                }
                updateResponseObserver(it)
            }
        }


    }


    fun step2Api() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["courseId"] = courseData.value!!.courseId!!
        map["courseTypeId"] = courseData.value!!.courseTypeId!!
        map["courseBannerUrl"] = courseData.value!!.courseBannerUrl ?: ""
        map["courseBannerHash"] = courseData.value!!.courseBannerHash ?: ""
        map["courseLogoUrl"] = courseData.value!!.courseLogoUrl ?: ""
        map["courseLogoHash"] = courseData.value!!.courseLogoHash ?: ""
        map["targetAudienceId"] = courseData.value!!.targetAudienceId!!
        map["courseComplexityId"] = courseData.value!!.courseComplexityId!!
        map["keyTakeaways"] = courseData.value!!.keyTakeaways ?: ""
        map["courseFee"] = courseData.value!!.courseFee ?: ""
//        map["rewardPoints"] = checkedReward.value!!

        val response = repo.step2AddCourse(map)
        withContext(Dispatchers.IO) {
            response.collect {
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

    fun uploadImage(file: File, isBanner: Boolean) = viewModelScope.launch {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData(
            "File",
            file.name,
            requestFile
        )
        val body =
            file.name.toRequestBody(("text/plain").toMediaTypeOrNull()); // String requestBody
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
                    val data = (it.value as BaseResponse<ImageResponse>)?.resource

                    if (isBanner) {
//                        courseData.value?.courseBannerUrl = data?.fileUrl
                        courseData.value?.courseBannerHash = data?.blurHash
                    } else {
//                        courseData.value?.courseLogoUrl = data?.fileUrl
                        courseData.value?.courseLogoHash = data?.blurHash

                    }
                }

                updateResponseObserver(it)
            }
        }
    }

    fun addSection() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["CourseId"] = courseData?.value?.courseId ?: 0
        var response = repo.addSection(map)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {

                    addSectionLiveData.postValue(EventObserver((it.value as BaseResponse<UserProfile>).resource?.sectionId) as EventObserver<Int>?)

                }

                updateResponseObserver(it)
            }
        }

    }

    fun deleteSection(sectionId: Int?, adapterPosition: Int) =
        viewModelScope.launch(coroutineExceptionHandle) {
            var response = repo.deleteSection("8", sectionId.toString())
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)

                    if (it is Resource.Success<*>) {
                        deleteSectionLiveData.postValue(EventObserver((it.value as BaseResponse<UserProfile>).resource?.deleted) as EventObserver<Boolean>?)
                        users.removeAt(adapterPosition)
                    }

                    updateResponseObserver(it)
                }
            }

        }

    fun dragAndDropSection(final: Int?, first: Int?, second: Int?) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData.value?.courseId ?: 0
            map["firstSectionId"] = first.toString()
            map["secondSectionId"] = second.toString()
            map["finalSectionId"] = final.toString()
            var response = repo.dragAndDropSection(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        dragAndDropLiveData.postValue((it.value as BaseResponse<UserProfile>).resource?.success)
                    }
                    updateResponseObserver(it)
                }
            }
        }


    fun addLecture(sectionId: Int?, mediaTypeId: Int?) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData?.value?.courseId ?: 0
            map["sectionId"] = sectionId.toString()
            map["mediaTypeId"] = mediaTypeId.toString()
            var response = repo.addLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        addLectureLiveData.postValue(EventObserver((it.value as BaseResponse<UserProfile>).resource?.lectureId) as EventObserver<Int>?)
                    }
                    updateResponseObserver(it)
                }
            }
        }

    fun deleteLecture(sectionId: Int?, lectureId: Int?) =
        viewModelScope.launch(coroutineExceptionHandle) {
            var response = repo.deleteLecture("8", sectionId.toString(), lectureId.toString())
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        deleteLectureLiveData.postValue(EventObserver((it.value as BaseResponse<UserProfile>).resource?.deleted) as EventObserver<Boolean>?)
                    }

                    updateResponseObserver(it)
                }
            }
        }


    fun dragAndDropLecture(final: Int?, first: Int?, second: Int?) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseData?.value?.courseId ?: 0
            map["firstLectureId"] = first.toString()
            map["secondLectureId"] = second.toString()
            map["finalLectureId"] = final.toString()
            var response = repo.dragAndDropLecture(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        dragAndDropLiveData.postValue((it.value as BaseResponse<UserProfile>).resource?.success)
                    }
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
            map["courseId"] = courseData?.value?.courseId ?: 0
            map["sectionId"] = sectionId.toString()
            map["sectionTitle"] = sectionTitle.toString()
            map["sectionDescription"] = sectionDesc.toString()
            var response = repo.addPatchSection(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        addPatchSectionLiveData.postValue(EventObserver(true))
                    }
                    updateResponseObserver(it)
                }
            }
        }
}