package com.selflearningcoursecreationapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivityViewModel(private val repo: HomeActivityRepo) : BaseViewModel() {
    private var courseId: String = ""
    private var status: Int = 0
    var purchaseCourseLiveData = MutableLiveData<EventObserver<OrderData?>>()


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

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_COAUTHOR_INVITATION + "/home" -> {
                manageCoAuthorInvitation(courseId, status)
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
}