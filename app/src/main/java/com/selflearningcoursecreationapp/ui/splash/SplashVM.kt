package com.selflearningcoursecreationapp.ui.splash

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashVM(private val repo: SplashRepo) : BaseViewModel() {

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

    override fun onApiRetry(apiCode: String) {
        viewProfile()
    }

}