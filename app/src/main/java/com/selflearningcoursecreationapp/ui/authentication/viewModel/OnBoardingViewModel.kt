package com.selflearningcoursecreationapp.ui.authentication.viewModel


import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.ui.authentication.login_signup.OnBoardingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnBoardingViewModel(private val repository: OnBoardingRepo?) : BaseViewModel() {

    //login
    var login_email: String = ""
    var login_pass: String = ""
    var isRememberChecked: Boolean = false

    //signup
    var reg_name: String = ""
    var reg_email: String = ""
    var reg_phone: String = ""
    var reg_pass: String = ""
    var isPrivacyPolicyChecked= MutableLiveData<Boolean>().apply {
        value=false
    }

    fun allStates() = viewModelScope.launch {
        updateResponseObserver(Resource.Loading())
      val response=  repository?.allStates()
            withContext(Dispatchers.IO){
                response?.collect {
                    updateResponseObserver(it)
                }
        }

    }

    //
//    fun allTestStates() = viewModelScope.launch {
//        updateResponseObserver(Resource.Loading())
//        repository?.allTestStates()?.let { updateResponseObserver(it) }
//    }
    fun rememberMeCheck(btn: CompoundButton, checked: Boolean) {
        updateResponseObserver(Resource.Error(ToastData(errorString = "" + checked.toString())))
    }

    fun loginValidation() {

//        if (login_email.isBlank()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_email)))
//        } else if (!login_email.isValidEmail()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_email)))
//        } else if (login_pass.isBlank()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_password)))
//        } else if (!login_pass.isPasswordValid()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_password)))
//        } else {
            updateResponseObserver(Resource.Success(null, ""))

//        }


    }

    fun checkPrivacyPolicy(btn: CompoundButton, isChecked: Boolean) {

        updateResponseObserver(Resource.Error(ToastData(errorString = "" + isChecked.toString())))

    }

    fun signupValidations() {
//        if (reg_name.isBlank()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_name)))
//        } else if (reg_name.length < 2) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_name)))
//        } else if (reg_email.isBlank()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_email)))
//        } else if (!reg_email.isValidEmail()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_email)))
//        } else if (reg_phone.isBlank()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_phone_number)))
//        } else if (reg_phone.length < 10) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_phone_number)))
//        } else if (reg_pass.isBlank()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_password)))
//        } else if (!reg_pass.isPasswordValid()) {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_password)))
//        } else if (isPrivacyPolicyChecked.value==false)
//        {
//            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.plz_accept_terms_conditions)))
//
//        }else {
            updateResponseObserver(Resource.Success(null, ""))

//        }

    }

}