package com.selflearningcoursecreationapp.ui.authentication.forgotPass

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isValidEmail
import com.selflearningcoursecreationapp.ui.authentication.otp_verify.OTPVerifyRepo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.OTP_TYPE
import com.selflearningcoursecreationapp.utils.VALIDATION_CONST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPassViewModel(private val repo: OTPVerifyRepo) : BaseViewModel() {
    var emailPhone = MutableLiveData<String>().apply {
        value = ""
    }


    fun validate(selectedCountryCodeWithPlus: String) {
        when {
            emailPhone.value.isNullOrEmpty() -> {

                updateResponseObserver(
                    Resource.Error(
                        ToastData(R.string.plz_enter_phone_email)
                    )
                )
            }
            emailPhone.value!!.isDigitsOnly() -> {

                if (emailPhone.value!!.length < VALIDATION_CONST.MIN_NO_LENGTH) {
                    updateResponseObserver(
                        Resource.Error(
                            ToastData(R.string.enter_valid_phone_number)
                        )
                    )
                } else {
                    forgotApi(true, selectedCountryCodeWithPlus)

                    //                    viewModel.reqOTP(OTPVerifyFragment.TYPE_FORGOT,
                    //                        binding.edtForgotphone.content())

                }

            }
            !emailPhone.value!!.isValidEmail() -> {
                updateResponseObserver(
                    Resource.Error(
                        ToastData(
                            R.string.enter_valid_email
                        )
                    )
                )

            }
            else -> {
                forgotApi(false, selectedCountryCodeWithPlus)

            }
        }
    }

    fun forgotApi(isPhone: Boolean, selectedCountryCodeWithPlus: String) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            if (isPhone) {
                map["phone"] = emailPhone.value!!
                map["countryCode"] = selectedCountryCodeWithPlus
            } else {
                map["email"] = emailPhone.value!!
            }
            map["OtpType"] = OTP_TYPE.TYPE_FORGOT.toString()


            updateResponseObserver(Resource.Loading())
            var response = repo.reqOtp(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        updateResponseObserver(Resource.Success(isPhone, ApiEndPoints.API_OTP_REQ))
                    } else {
                        updateResponseObserver(it)
                    }
                }
            }
        }
    }

}