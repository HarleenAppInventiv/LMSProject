package com.selflearningcoursecreationapp.ui.profile.profileDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.ui.moderator.ModeCertificate
import com.selflearningcoursecreationapp.ui.moderator.model.AddModeratorResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class ProfileDetailViewModel(private val repo: ProfileDetailRepo) : BaseViewModel() {
    var txtEmail = MutableLiveData<String>()
    var certificateFiles = arrayListOf<ModeCertificate>()
    var categoryData = arrayListOf<CategoryData>()
    var laguageData = arrayListOf<CategoryData>()
    var position = -1
    private var uploadFile: File? = null
    fun viewProfile() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.viewProfile()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<UserProfile>
                    saveUser(UserResponse(user = data.resource))
                    userProfile = data.resource
                    delay(1000)
                }
                updateResponseObserver(it)
            }
        }
    }

    fun uploadImage(file: File) {
        uploadFile = file
        viewModelScope.launch {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData(
                "File",
                file.name,
                requestFile
            )
            val body =
                file.name.toRequestBody(("text/plain").toMediaTypeOrNull()) // String requestBody
            val response = repo.uploadImage(filePart, body)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun uploadModeDoc(file: File, position: Int) {
        uploadFile = file
        viewModelScope.launch {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData(
                "File",
                file.name,
                requestFile
            )
            val response = repo.uploadModeDoc(filePart)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<ImageResponse>

                        certificateFiles[position] =
                            ModeCertificate(id = data.resource?.id ?: "", file)
                        certificateFiles.add(ModeCertificate("", File("")))
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun addModerator() = viewModelScope.launch {

        val languageList = laguageData.joinToString(separator = ",") { "${it.id}" }

        val categoryId = categoryData[0].id
        val docu = arrayListOf<String>()

        certificateFiles.forEach {
            if (it.id.isNotEmpty()) {
                docu.add(it.id)
            }

        }


        val response = repo.addModerator(
            AddModeratorResponse(
                language = languageList,
                documentsContentId = docu,
                categoryId = categoryId
            )
        )
        withContext(Dispatchers.IO) {
            response.collect {

                updateResponseObserver(it)
            }

        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_VIEW_PROFILE -> {
                viewProfile()
            }
            ApiEndPoints.API_UPLOAD_IMAGE -> {
                uploadFile?.let { uploadImage(it) }
            }

            ApiEndPoints.API_UPLOAD_MODERATOR_DOC -> {
                uploadFile?.let { uploadModeDoc(it, position) }
            }
        }
    }
}