package com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.ChangePasswordData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordVM(private val repo: ChangePassRepo) : BaseViewModel() {


    val passwordLiveData = MutableLiveData<ChangePasswordData>().apply {
        value = ChangePasswordData()
    }
    var isLogout = MutableLiveData<Boolean>().apply {
        value = true
    }

    fun isValid() {
        passwordLiveData.value?.let {
            val errorId = it.isChangeValid()
            if (errorId == 0) {
                callChangePass(it.oldPassword, it.newPassword)
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))
            }

        }
    }

    fun callChangePass(oldPassword: String, newPassword: String) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            map["oldPassword"] = oldPassword
            map["newPassword"] = newPassword
            map["isLogout"] = isLogout.value ?: false
            updateResponseObserver(Resource.Loading())
            var response = repo.changePass(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }

        }
}