package com.selflearningcoursecreationapp.ui.my_bank


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.ui.bottom_more.payments.new_request.MinAmountRequestModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BankAccountVM(private val repository: BankAccountRepo?) : BaseViewModel() {

    var isPrimaryChecked = MutableLiveData<Boolean>().apply {
        value = false
    }

    var makeAccountPrimaryPayload = hashMapOf<String, Any>()

    var bankDetailsLiveData = MutableLiveData<BankDetails>().apply {
        value = BankDetails()
    }

    var bankAccountListingLiveData = MutableLiveData<ArrayList<BankDetails>>().apply {
        value = ArrayList()
    }
    var maxBankCount = MutableLiveData<Int>()

    fun maxBankAddedCount() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repository?.getMaxBankCount()
        withContext(Dispatchers.IO) {
            response?.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<MinAmountRequestModel>
                    maxBankCount.postValue(data.resource?.amount ?: 0)
                }
                updateResponseObserver(it)
            }
        }
    }

    fun bankDetailsValidation() {
        bankDetailsLiveData.value?.let {
            val errorId = it.isValid()
            if (errorId == 0) {
                addBankAccount()


            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }

        }
    }


    private fun addBankAccount() = viewModelScope.launch(coroutineExceptionHandle) {
        val map = HashMap<String, Any>()

        map["name"] = bankDetailsLiveData.value?.bankName.toString()
        map["email"] = bankDetailsLiveData.value?.email.toString()
        map["contact"] = "91" + bankDetailsLiveData.value?.number.toString()
        map["ifsc"] = bankDetailsLiveData.value?.ifscCode.toString()
        map["accountNumber"] = bankDetailsLiveData.value?.accountNumber.toString()
        map["isPrimaryAccount"] = isPrimaryChecked.value ?: false

//        Log.e("bankdetails", "" + map)

        val response = repository?.addBankAccount(
            map
        )
        withContext(Dispatchers.IO) {
            response?.collect {
                updateResponseObserver(it)
            }
        }

    }


    internal fun deleteBankAccount() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repository?.deleteBankAccount(
            makeAccountPrimaryPayload
        )
        withContext(Dispatchers.IO) {
            response?.collect {

                updateResponseObserver(it)
            }
        }

    }

    internal fun getBankAccountListing() = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repository?.getBankAccountListing(

        )
        withContext(Dispatchers.IO) {
            response?.collect {
                if (it is Resource.Success<*>) {
                    val resource =
                        (it.value as BaseResponse<ArrayList<BankDetails>>).resource
                    bankAccountListingLiveData.postValue(resource)
                }
                updateResponseObserver(it)
            }
        }

    }


    internal fun sendMakeAccountPrimaryRequest() = viewModelScope.launch(coroutineExceptionHandle) {

        val response = repository?.sendBankAccountPrimary(
            makeAccountPrimaryPayload
        )
        withContext(Dispatchers.IO) {
            response?.collect {
                updateResponseObserver(it)
            }
        }

    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {

            ApiEndPoints.API_GET_BANK_ACCOUNT_LISTING -> {

                getBankAccountListing()
            }


        }
    }


}