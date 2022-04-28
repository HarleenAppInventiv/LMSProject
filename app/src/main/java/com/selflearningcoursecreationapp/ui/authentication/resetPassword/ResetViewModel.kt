package com.selflearningcoursecreationapp.ui.authentication.resetPassword

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

class ResetViewModel(private val repo: ResetPassRepo) : BaseViewModel() {

    var resetData = MutableLiveData<ChangePasswordData>().apply {
        value = ChangePasswordData()
    }

    fun onReset(userId: String) {
        resetData.value?.let {
            val errorId = it.isResetValid()
            if (errorId == 0) {
                resetPass(userId)
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }

    }

    fun resetPass(userId: String) = viewModelScope.launch(coroutineExceptionHandle) {
        var map = HashMap<String, Any>()
        map["userId"] = userId
        map["newPassword"] = resetData.value?.newPassword ?: ""
        updateResponseObserver(Resource.Loading())
        var response = repo.resetPass(map)
        withContext(Dispatchers.IO) {
            response.collect {

                updateResponseObserver(it)
            }
        }

    }
}