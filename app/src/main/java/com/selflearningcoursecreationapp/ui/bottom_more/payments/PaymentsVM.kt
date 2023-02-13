package com.selflearningcoursecreationapp.ui.bottom_more.payments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.*
import com.selflearningcoursecreationapp.ui.bottom_more.payments.new_request.MinAmountRequestModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet.AmountList
import com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet.WalletDataModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PaymentsVM(private val repo: PaymentsFragmentRepo) : BaseViewModel() {
    var currentPage = 1
    var totalPages = 10
    var isLastPage = false

    var currentDate: String = ""

    var currentEarningPage = 1
    var totalEarningPages = 2

    var amountEntered = MutableLiveData<String>().apply {
        value = ""
    }
    var minAmount = 0

    var remainingWalletBalanceLiveData = MutableLiveData<RemainingWalletBalanceData>().apply {
        value = RemainingWalletBalanceData()
    }

    var userEarningsLiveData = MutableLiveData<ArrayList<PaymentEarningsData>>().apply {
        value = ArrayList()
    }

    var userPurchasesLiveData = MutableLiveData<ArrayList<MyPurchaseList>>().apply {
        value = ArrayList()
    }

    var withdrawAmount = 0
    fun paymentWithdrawRequest() = viewModelScope.launch(coroutineExceptionHandle) {
        var map = HashMap<String, Any>()
        map["amount"] = withdrawAmount
        val response = repo.paymentWithdrawRequest(map)

        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<Any>
                }
                updateResponseObserver(it)
            }
        }
    }

    fun minWithdrawAmount() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.minWithdrawAmount()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<MinAmountRequestModel>
                    minAmount = data.resource?.amount ?: 0
                }
                updateResponseObserver(it)
            }
        }
    }

    fun userEarningsData() = viewModelScope.launch(coroutineExceptionHandle) {
        if (currentEarningPage < totalEarningPages) {

            val response = repo.userEarnings(currentEarningPage, 10)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<MyPaymentEarningDataModel>
                        val list = userEarningsLiveData.value ?: ArrayList()
                        if (data.resource?.pageNumber == 1) {
                            currentEarningPage = 1
                            list.clear()
                        }
                        currentEarningPage += 1
                        val totalPage =
                            (data.resource?.totalCount ?: 0) / (data.resource?.pageSize ?: 8)
                        val remCount =
                            (data.resource?.totalCount ?: 0) % (data.resource?.pageSize ?: 8)
                        totalEarningPages = totalPage + (if (remCount > 0) 1 else 0)
//                        totalEarningPages =
//                            (data.resource?.resultCount ?: 0).div(data.resource?.pageSize ?: 1)
                        list.addAll(data.resource?.list ?: ArrayList())
                        userEarningsLiveData.postValue(list)
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    fun remainingWalletBalance() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.remainingWalletBalance()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val data = it.value as BaseResponse<RemainingWalletBalanceData>
                    remainingWalletBalanceLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
            }
        }
    }


    fun getMyPurchases() = viewModelScope.launch(coroutineExceptionHandle)
    {

        if (!isLastPage) {

            var map = HashMap<String, Any>()
            map["courseType"] = 1
            map["pageNumber"] = currentPage
            map["pageSize"] = 10
            map["status"] = 0
            val response = repo.userPurchases(map)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<MyPurchaseDataModel>).resource?.let { resource ->

                            if (resource.pageNumber == 1) {
                                currentPage = 1
                                userPurchasesLiveData.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = userPurchasesLiveData.value
                            list?.addAll(resource.list ?: ArrayList())
                            isLastPage = resource.list.isNullOrEmpty()
                            userPurchasesLiveData.postValue(list)
                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    var walletHistoryLiveData = MutableLiveData<ArrayList<AmountList>>().apply {
        value = ArrayList()
    }


    fun getWalletHistory() = viewModelScope.launch(coroutineExceptionHandle)
    {

        if (!isLastPage) {

            var map = HashMap<String, Any>()
            map["pageNumber"] = currentPage
            map["pageSize"] = 10
            val response = repo.getWalletHistory(map)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<WalletDataModel>).resource?.let { resource ->

                            if (resource.pageNumber == 1) {
                                currentPage = 1
                                walletHistoryLiveData.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = walletHistoryLiveData.value
                            list?.addAll(resource.list)
//                            if (resource.list.isNullOrEmpty())
                            isLastPage = resource.list.isNullOrEmpty()
                            walletHistoryLiveData.postValue(list)
                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_PAYMENT_REMAINING_WALLET_BALANCE -> {
                remainingWalletBalance()
            }

            ApiEndPoints.API_PAYMENT_USER_PURCHASES -> {
                getMyPurchases()
            }

            ApiEndPoints.API_PAYMENT_USER_EARNINGS -> {
                userEarningsData()
            }

        }

    }


}




