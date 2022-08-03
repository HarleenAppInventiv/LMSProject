package com.selflearningcoursecreationapp.ui.authentication.resetPassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.ChangePasswordData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResetViewModel(private val repo: ResetPassRepo) : BaseViewModel() {

    var resetData = MutableLiveData<ChangePasswordData>().apply {
        value = ChangePasswordData()
    }
    var userId = ""

    fun onReset() {
        resetData.value?.let {
            val errorId = it.isResetValid()
            if (errorId == 0) {
                resetPass()
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }

    }

    fun resetPass() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()
        map["userId"] = userId
        map["newPassword"] = resetData.value?.newPassword ?: ""
        updateResponseObserver(Resource.Loading())
        val response = repo.resetPass(map)
        withContext(Dispatchers.IO) {
            response.collect {

                updateResponseObserver(it)
            }
        }

    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_RESET_PASS -> {
                onReset()
            }
        }
    }
}