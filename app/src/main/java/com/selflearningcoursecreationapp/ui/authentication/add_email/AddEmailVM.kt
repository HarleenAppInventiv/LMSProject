package com.selflearningcoursecreationapp.ui.authentication.add_email

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isValidEmail
import com.selflearningcoursecreationapp.utils.OTP_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEmailVM(private val repo: AddEmailRepo) : BaseViewModel() {
    var email = MutableLiveData<String>().apply {
        value = ""
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

    fun addEmail() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            map["email"] = email.value!!
            map["otpType"] = OTP_TYPE.TYPE_EMAIL.toString()


            updateResponseObserver(Resource.Loading())
            var response = repo.addEmail(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)

                }
            }
        }
    }

}