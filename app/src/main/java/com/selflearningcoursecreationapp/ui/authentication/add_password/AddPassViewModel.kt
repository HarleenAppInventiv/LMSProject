package com.selflearningcoursecreationapp.ui.authentication.add_password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.ChangePasswordData
import com.selflearningcoursecreationapp.models.user.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPassViewModel(var repo: AddPassRepo) : BaseViewModel() {

    var addData = MutableLiveData<ChangePasswordData>().apply {
        value = ChangePasswordData()
    }

    fun onAdd(userId: String) {
        addData.value?.let {
            val errorId = it.isResetValid()
            if (errorId == 0) {
                addPass(userId)
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }

    }

    fun addPass(userId: String) = viewModelScope.launch(coroutineExceptionHandle) {
        var map = HashMap<String, Any>()
        map["userId"] = userId
        map["newPassword"] = addData.value?.newPassword ?: ""
        updateResponseObserver(Resource.Loading())
        var response = repo.addPass(map)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
//                    if (args?.type != OTP_TYPE.TYPE_FORGOT) {
                    val data = it.value as BaseResponse<UserResponse>
                    saveUserDataInDB(data)
                    delay(2000)
//                    }
                }
                updateResponseObserver(it)
            }
        }

    }
}