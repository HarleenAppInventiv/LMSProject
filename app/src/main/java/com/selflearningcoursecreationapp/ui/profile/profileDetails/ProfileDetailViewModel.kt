package com.selflearningcoursecreationapp.ui.profile.profileDetails

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileDetailViewModel(private val repo: ProfileDetailRepo) : BaseViewModel() {


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
}