package com.selflearningcoursecreationapp.ui.payment

import LearnerDashboardDataList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CheckoutVM(private val repo: CheckoutRepo) : BaseViewModel() {


    var stateId: Int? = 0
    var amount = ""

    var courseLiveData = MutableLiveData<ArrayList<LearnerDashboardDataList>>().apply {
        value = ArrayList()
    }

    var amountWithGSTLiveData = MutableLiveData<AmountWithGSTModel>().apply {
        value = AmountWithGSTModel()
    }


    fun amountWithGST() = viewModelScope.launch(coroutineExceptionHandle) {
        var map = HashMap<String, Any>()
        map["stateId"] = stateId.toString()
        map["amount"] = amount

        val response = repo.getAmountWithGst(map)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<AmountWithGSTModel>
                    amountWithGSTLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_AMOUNT_WITH_GST -> {
                amountWithGST()
            }

        }

    }


}




