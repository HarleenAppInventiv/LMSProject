package com.selflearningcoursecreationapp.ui.profile.profileThumb

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileThumbViewModel(private val repo: ProfileThumbRepo) : BaseViewModel() {


    init {

        getUserData()
    }

    fun callLogout() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.logout()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    saveUserToken("")
                    saveUser(null)
                    updateResponseObserver(it)
                } else {
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun deleteAccount() = viewModelScope.launch(Dispatchers.IO) {
        var response = repo.deleteAccount()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    saveUserToken("")
                    saveUser(null)
                    updateResponseObserver(it)
                } else {
                    updateResponseObserver(it)
                }
            }
        }
    }
}