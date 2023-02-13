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
import com.selflearningcoursecreationapp.utils.OtpType
import com.selflearningcoursecreationapp.utils.ValidationConst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPassViewModel(private val repo: OTPVerifyRepo) : BaseViewModel() {
    var emailPhone = MutableLiveData<String>().apply {
        value = ""
    }
    var selectedCountryCodeWithPlus: String? = null


    fun validate() {
        when {
            emailPhone.value.isNullOrEmpty() -> {

                updateResponseObserver(
                    Resource.Error(
                        ToastData(R.string.plz_enter_phone_email)
                    )
                )
            }
            emailPhone.value?.isDigitsOnly() == true -> {

                if ((emailPhone.value?.length ?: 0) < ValidationConst.MIN_NO_LENGTH) {
                    updateResponseObserver(
                        Resource.Error(
                            ToastData(R.string.enter_valid_phone_number)
                        )
                    )
                } else {
                    forgotApi(true)
                }

            }
            emailPhone.value?.isValidEmail() == false -> {
                updateResponseObserver(
                    Resource.Error(
                        ToastData(
                            R.string.enter_valid_email
                        )
                    )
                )

            }
            else -> {
                forgotApi(false)

            }
        }
    }

    fun forgotApi(isPhone: Boolean) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            if (isPhone) {
                map["phone"] = emailPhone.value ?: ""
                map["countryCode"] = selectedCountryCodeWithPlus ?: ""
            } else {
                map["email"] = emailPhone.value ?: ""
            }
            map["OtpType"] = OtpType.TYPE_FORGOT.toString()
            map["unit"] = 60


            updateResponseObserver(Resource.Loading())
            val response = repo.reqOtp(map)
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

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_OTP_REQ -> {
                validate()
            }
        }
    }

}