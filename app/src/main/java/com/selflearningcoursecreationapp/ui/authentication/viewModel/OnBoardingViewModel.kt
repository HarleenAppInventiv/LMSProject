package com.selflearningcoursecreationapp.ui.authentication.viewModel


import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.models.LoginData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.ui.authentication.login_signup.OnBoardingRepo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OnBoardingViewModel(private val repository: OnBoardingRepo?) : BaseViewModel() {

    var isRememberChecked = MutableLiveData<Boolean>().apply {
        value = false
    }

    var isMovedToPrivacy = false
    var token: String? = null
    var countryCode: String? = null

    var loginLiveData = MutableLiveData<LoginData>().apply {
        value = LoginData()
        viewModelScope.launch {
            val email = PreferenceDataStore.getString(Constants.EMAIL)
            val password = PreferenceDataStore.getString(Constants.PASSWORD)
            val countryCode = PreferenceDataStore.getString(Constants.COUNTYRY_CODE)
            if (!email.isNullOrEmpty()) {
                value?.email = email
                value?.password = password ?: ""
                isRememberChecked.value = true
                value?.countryCode = countryCode ?: ""
            }
        }

    }


    var signUpLiveData = MutableLiveData<UserProfile>().apply {
        value = UserProfile()
    }
    var isPrivacyPolicyChecked = MutableLiveData<Boolean>().apply {
        value = false
    }

    init {
        LogTag = "OnBoardingViewModel"
    }


    private fun signUpApi() = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repository?.signUpApi(
            signUpLiveData.value ?: UserProfile()
        )
        withContext(Dispatchers.IO) {
            response?.collect {

                updateResponseObserver(it)
            }
        }

    }

    private fun loginUser(selectedCountryCodeWithPlus: String, token: String) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            if (loginLiveData.value?.email?.isDigitsOnly() == true) {
                map["phone"] = loginLiveData.value?.email ?: ""
                map["countryCode"] = selectedCountryCodeWithPlus
            } else {
                map["email"] = loginLiveData.value?.email ?: ""
            }
            map["password"] = loginLiveData.value?.password ?: ""
            map["fcmDeviceToken"] = token
            map["viMode"] = loginLiveData.value?.viMode ?: false

            val response = repository?.loginInApi(map)
            withContext(Dispatchers.IO) {
                response?.collect {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<UserResponse>
                        if (data.resource?.user?.phoneNumberVerified == true) {
                            saveUserDataInDB(data)
                            saveViMode(data.resource?.user?.viMode ?: false)

                        }
                        rememberMeFunctionality(selectedCountryCodeWithPlus)

                        delay(2000)


                    }
                    updateResponseObserver(it)

                }
            }

        }

    private suspend fun rememberMeFunctionality(selectedCountryCodeWithPlus: String) {
        if (isRememberChecked.value == true) {
            PreferenceDataStore.saveString(
                Constants.EMAIL,
                loginLiveData.value?.email
            )
            PreferenceDataStore.saveString(
                Constants.PASSWORD,
                loginLiveData.value?.password
            )
            PreferenceDataStore.saveString(
                Constants.COUNTYRY_CODE,
                selectedCountryCodeWithPlus
            )
        } else {
            PreferenceDataStore.saveString(Constants.EMAIL, "")
            PreferenceDataStore.saveString(Constants.PASSWORD, "")
            PreferenceDataStore.saveString(Constants.COUNTYRY_CODE, "")

        }
    }


    fun loginValidation() {
        loginLiveData.value?.let {
            val errorId = it.isValid()
            if (errorId == 0) {
                loginUser(selectedCountryCodeWithPlus = countryCode ?: "", token ?: "")
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))
            }
        }

    }


    fun signupValidations() {
        signUpLiveData.value?.let {
            val errorId = it.isSignUpValid()
            if (errorId == 0) {
                if (isPrivacyPolicyChecked.value == false) {
                    updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.plz_accept_terms_conditions)))
                } else {
                    signUpApi()
                }

            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }

        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {

            ApiEndPoints.API_SIGNUP -> {
                signupValidations()
            }

            ApiEndPoints.API_LOGIN -> {
                loginValidation()
            }

        }
    }


}