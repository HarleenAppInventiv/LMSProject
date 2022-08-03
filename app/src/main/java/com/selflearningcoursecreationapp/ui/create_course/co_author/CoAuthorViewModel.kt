package com.selflearningcoursecreationapp.ui.create_course.co_author

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isValidEmail
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.ValidationConst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoAuthorViewModel(private val repo: CoAuthorRepo) : BaseViewModel() {
    var emailPhone = MutableLiveData<String>().apply {
        value = ""
    }
    var phone = MutableLiveData<String>().apply {
        value = ""
    }
    var selectedCountryCodeWithPlus: String = ""
    var courseId: Int = 0
    private var isEmail = false
    fun validate(isEmail: Boolean) {
        this.isEmail = isEmail
        when {
            emailPhone.value.isNullOrEmpty() && isEmail -> {

                updateResponseObserver(
                    Resource.Error(
                        ToastData(R.string.plz_enter_phone_email), "email"
                    )
                )
            }
            !emailPhone.value!!.isValidEmail() && isEmail -> {
                updateResponseObserver(
                    Resource.Error(
                        ToastData(
                            R.string.enter_valid_email
                        ), "email"
                    )
                )

            }
            phone.value.isNullOrEmpty() && !isEmail -> {

                updateResponseObserver(
                    Resource.Error(
                        ToastData(R.string.enter_phone_number), "phone"
                    )
                )
            }

            phone.value!!.length < ValidationConst.MIN_NO_LENGTH && !isEmail -> {

                updateResponseObserver(
                    Resource.Error(
                        ToastData(R.string.enter_valid_phone_number), "phone"
                    )
                )


            }

            else -> {
                inviteCoAuthor(!isEmail)

            }
        }
    }

    private fun inviteCoAuthor(isPhone: Boolean) {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            if (isPhone) {
                map["phone"] = phone.value!!
                map["countryCode"] = selectedCountryCodeWithPlus
            } else {
                map["email"] = emailPhone.value!!
            }
            map["courseId"] = courseId.toString()


            updateResponseObserver(Resource.Loading())
            val response = repo.inviteCoAuthor(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)

                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_INVITE_COAUTHOR -> {
                validate(isEmail)
            }
        }
    }

}