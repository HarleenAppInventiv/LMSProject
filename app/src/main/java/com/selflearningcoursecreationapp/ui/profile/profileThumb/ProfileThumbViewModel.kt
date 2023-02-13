package com.selflearningcoursecreationapp.ui.profile.profileThumb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MODTYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileThumbViewModel(private val repo: ProfileThumbRepo) : BaseViewModel() {


    var checkedLiveData = MutableLiveData(false)
    var isDRFEmployee = MutableLiveData(false)
    var changeViMode: Boolean = false


    init {
        viewUserProfile()
    }

    fun callLogout() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.logout()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    saveUserToken("")
                    saveUser(null)
                    updateResponseObserver(it)
                    //Logout Intercom client
//                    setIntercomLoginStatus(false)
                } else {
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun deleteAccount(deleteUsers: Boolean = false, deleteWallet: Boolean = false) =
        viewModelScope.launch(Dispatchers.IO) {
            val response = repo.deleteAccount(deleteUsers, deleteWallet)
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

    fun changeViModeStatus() = viewModelScope.launch(Dispatchers.IO) {
        val response =
            repo.changeViMode(ChangeViModeModel(toogleViMode = changeViMode))
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {

                    val data = it.value as BaseResponse<UserProfile>
                    saveViMode(data.resource?.viMode ?: true)
                }
                updateResponseObserver(it)

            }
        }
    }

    fun viewUserProfile() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.profileApi()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<UserProfile>
                    isDRFEmployee.postValue(data.resource?.IsDRFEmployee ?: true)
                    saveUser(UserResponse(user = data.resource))
                }
                updateResponseObserver(it)
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_LOGOUT -> {
                callLogout()
            }
            ApiEndPoints.API_DELETE_ACCOUNT -> {
                deleteAccount()
            }
        }
    }


}