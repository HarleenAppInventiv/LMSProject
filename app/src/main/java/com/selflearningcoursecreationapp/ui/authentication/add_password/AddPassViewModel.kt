package com.selflearningcoursecreationapp.ui.authentication.add_password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.ChangePasswordData
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPassViewModel(var repo: AddPassRepo) : BaseViewModel() {

    var addData = MutableLiveData<ChangePasswordData>().apply {
        value = ChangePasswordData()
    }
    private var userId: String? = null
    private var token: String? = null
    fun setUserData(userId: String, token: String) {
        this.userId = userId
        this.token = token
    }


    fun validate() {
        addData.value?.let {
            val errorId = it.isResetValid()
            if (errorId == 0) {
                addPass()
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }

    }

    private fun addPass() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["userId"] = userId ?: ""
        map["newPassword"] = addData.value?.newPassword ?: ""
        map["fcmDeviceToken"] = token ?: ""
        updateResponseObserver(Resource.Loading())
        val response = repo.addPass(map)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<UserResponse>
                    saveUserDataInDB(data)
                    delay(2000)
                }
                updateResponseObserver(it)
            }
        }

    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ADD_PASSWORD -> {
                validate()
            }
        }
    }
}