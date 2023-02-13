package com.selflearningcoursecreationapp.ui.home

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.ui.moderator.myCategories.ModeratorMyCategories
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MODTYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivityViewModel(private val repo: HomeActivityRepo) : BaseViewModel() {
    private var courseId: String = ""
    private var status: Int = 0
    var purchaseCourseLiveData = MutableLiveData<EventObserver<OrderData?>>()
    var isPolicyAccepted = MutableLiveData<EventObserver<Boolean>>()
    var notificationId = 0
    var bundle: Bundle? = null

    init {
        getUserData()
    }

    fun manageCoAuthorInvitation(courseId: String, status: Int) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["courseId"] = courseId.toInt()
            map["status"] = status
            val response = repo.manageCouthorInvitation(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {

                        updateResponseObserver(
                            Resource.Success(
                                Pair(courseId, status),
                                ApiEndPoints.API_COAUTHOR_INVITATION + "/home"
                            )
                        )
                    } else
                        updateResponseObserver(it)
                }
            }

        }
    }

    fun coursePolicyStatus(status: Boolean) = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["Status"] = status
        val response = repo.courseStatusPolicy(map)
        withContext(Dispatchers.IO) {
            response.collect {
//                if (it is Resource.Success<*>) {
//                    isPolicyAccepted.value = EventObserver(true )
//                }
                updateResponseObserver(it)
            }
        }

    }

    fun switchMod() = viewModelScope.launch(Dispatchers.IO) {
        val response =
            repo.switchMod(if (userProfile?.currentMode == MODTYPE.LEARNER) MODTYPE.MODERATOR else MODTYPE.LEARNER)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {

                    val data = it.value as BaseResponse<UserProfile>
                    saveUser(UserResponse(user = data.resource))
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

    fun patchNotification() = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repo.patchNotification(notificationId)
        withContext(Dispatchers.IO) {
            response.collect {
//                if (it is Resource.Success<*>) {
//                    notificationLiveData.value?.get(adapterPosition)?.isRead= true
//                }
                updateResponseObserver(it)
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_COAUTHOR_INVITATION + "/home" -> {
                manageCoAuthorInvitation(courseId, status)
            }
            ApiEndPoints.API_PATCH_NOTIFICATION -> {
                patchNotification()
            }
            ApiEndPoints.API_VIEW_PROFILE -> {
                viewProfile()
            }
        }
    }

    fun purchaseCourse(courseId: Int?, courseType: Int) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Int>()


            map["courseId"] = courseId ?: 0
            map["courseType"] = courseType


            val response = repo.purchaseCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val resource = (it.value as BaseResponse<OrderData>).resource
                        purchaseCourseLiveData.postValue(EventObserver(resource))
                    }
                    updateResponseObserver(it)
                }
            }

        }
    }

    fun viewProfile() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.profileApi()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<UserProfile>
                    saveUser(UserResponse(user = data.resource))
                }
                updateResponseObserver(it)
            }
        }
    }
}