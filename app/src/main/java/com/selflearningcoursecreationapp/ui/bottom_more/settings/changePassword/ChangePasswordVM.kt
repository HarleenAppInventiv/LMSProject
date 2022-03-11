package com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword

import androidx.lifecycle.MutableLiveData
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isPasswordValid
import com.selflearningcoursecreationapp.models.ChangePasswordData

class ChangePasswordVM() : BaseViewModel() {

    val passwordLiveData = MutableLiveData<ChangePasswordData>().apply {
        value = ChangePasswordData()
    }

fun isValid(){
    passwordLiveData.value?.let {
        if (it.oldPassword.isEmpty())
        {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.plz_enter_old_password)))
        }else if (!it.oldPassword.isPasswordValid())
        {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.old_password_should_be_valid)))

        }else if (it.newPassword.isEmpty()){

            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.plz_enter_new_password)))

        }else if (!it.newPassword.isPasswordValid())
        {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.password_should_be_valid)))

        }else if (it.confirmPassword.isEmpty()){

            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.plz_enter_new_password)))

        }else if (!it.confirmPassword.equals(it.newPassword))
        {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.new_confirm_paswrd_not_matched)))

        }else{
            updateResponseObserver(Resource.Success(null,""))
        }
    }
}
}