package com.selflearningcoursecreationapp.ui.home

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivityViewModel(private val repo: HomeActivityRepo) : BaseViewModel() {

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
}