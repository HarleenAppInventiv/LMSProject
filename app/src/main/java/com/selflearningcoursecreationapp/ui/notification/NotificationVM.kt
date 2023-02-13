package com.selflearningcoursecreationapp.ui.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.NotificationData
import com.selflearningcoursecreationapp.models.NotificationModel
import com.selflearningcoursecreationapp.ui.moderator.myCategories.ModeratorMyCategories
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationVM(private val repo: NotificationRepo) : BaseViewModel() {

    //    var notificationId: Int = 0
    var apiType: Int = 0
    var notificationLiveData = MutableLiveData<ArrayList<NotificationData>?>().apply {
        value = ArrayList()
    }
    var adapterPosition: Int = 0

    fun reset() {
        notificationLiveData.value?.clear()
    }

    fun getNotification() = viewModelScope.launch(coroutineExceptionHandle) {
        apiType = 1
        val response = repo.getNotification()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val resource = (it.value as BaseResponse<NotificationModel>).resource
                    notificationLiveData.value?.clear()
                    val list = notificationLiveData.value
                    list?.addAll(resource?.list ?: ArrayList())
                    notificationLiveData.postValue(list)
//                    notificationLiveData.postValue(resource?.list)
                }
                updateResponseObserver(it)
            }
        }
    }

    fun patchNotification() = viewModelScope.launch(coroutineExceptionHandle) {
        apiType = 3
        val response = repo.patchNotification(
            notificationLiveData.value?.get(adapterPosition)?.notificationId ?: 0
        )
        withContext(Dispatchers.IO) {
            response.collect {
//                if (it is Resource.Success<*>) {
//                    notificationLiveData.value?.get(adapterPosition)?.isRead= true
//                }
                updateResponseObserver(it)
            }
        }
    }

    fun deleteNotification() = viewModelScope.launch(coroutineExceptionHandle) {
        apiType = 2
        val response = repo.deleteNotification(
            notificationLiveData.value?.get(adapterPosition)?.notificationId ?: 0
        )
        withContext(Dispatchers.IO) {
            response.collect {
//                if (it is Resource.Success<*>) {
//                    notificationLiveData.value?.removeAt(adapterPosition)
//
//                }
                updateResponseObserver(it)

            }
        }
    }


    fun readAllNotification() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.readAllNotification()
        withContext(Dispatchers.IO) {
            response.collect {
//                if (it is Resource.Success<*>) {
//                    isFirst = false
//                    val resource = (it.value as BaseResponse<ArrayList<CourseTypeModel>>).resource
//                    courseLiveData.postValue(resource)
//                }
                updateResponseObserver(it)
            }
        }
    }

    fun deleteAllNotification() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.deleteAllNotification()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    reset()
                }
                updateResponseObserver(it)
            }
        }
    }

    var myCategoriesLiveData = MutableLiveData<ModeratorMyCategories>()

    fun myCategories() = viewModelScope.launch(Dispatchers.IO) {
        val response = repo.myCategories()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    updateResponseObserver(it)
                    val data = it.value as BaseResponse<ModeratorMyCategories>
                    myCategoriesLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
                /*else {
                    updateResponseObserver(it)
                }*/
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_GET_NOTIFICATION -> {
                getNotification()
            }
            ApiEndPoints.API_NOTIFICATION_READ_ALL -> {
                readAllNotification()
            }
            ApiEndPoints.API_NOTIFICATION_DELETE_ALL -> {
                deleteNotification()
            }
            ApiEndPoints.API_PATCH_NOTIFICATION -> {
                patchNotification()
            }
            ApiEndPoints.API_DELETE_NOTIFICATION -> {
                deleteNotification()
            }

        }

    }
}