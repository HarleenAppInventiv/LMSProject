package com.selflearningcoursecreationapp.ui.authentication.add_email

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isValidEmail
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.OtpType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEmailVM(private val repo: AddEmailRepo) : BaseViewModel() {
    var email = MutableLiveData<String>().apply {
        value = ""
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ADD_EMAIL -> {
                validate()

            }
        }
    }

    fun validate() {


        when {
            email.value.isNullOrEmpty() -> {

                updateResponseObserver(
                    Resource.Error(
                        ToastData(R.string.enter_email)
                    )
                )
            }
            !email.value!!.isValidEmail() -> {
                updateResponseObserver(
                    Resource.Error(
                        ToastData(
                            R.string.enter_valid_email
                        )
                    )
                )

            }
            else -> {
                addEmail()

            }
        }
    }

    private fun addEmail() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            map["email"] = email.value!!
            map["otpType"] = OtpType.TYPE_EMAIL.toString()


            updateResponseObserver(Resource.Loading())
            val response = repo.addEmail(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)

                }
            }
        }
    }

}