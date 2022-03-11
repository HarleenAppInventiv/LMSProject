package com.selflearningcoursecreationapp.ui.authentication.resetPassword

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isPasswordValid

class ResetViewModel(private val repo: ResetPassRepo) : BaseViewModel() {
    var newPass: String = ""
    var confirmNewPass: String = ""


    fun onReset() {
        if (newPass.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_new_password)))
        } else if (!newPass.isPasswordValid()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_valid_new_password)))

        } else if (confirmNewPass.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_confirm_password)))

        } else if (!confirmNewPass.isPasswordValid()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_valid_confirm_password)))

        } else if (!newPass.equals(confirmNewPass)) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.both_pass_does_not_match)))

        } else {
            updateResponseObserver(Resource.Success(null, ""))

        }
    }
}