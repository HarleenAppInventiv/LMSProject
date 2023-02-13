package com.selflearningcoursecreationapp.ui.bottom_more.payments.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_more.payments.PaymentsFragmentRepo
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.MyPurchaseList
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.PurchaseDetailDataModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentDetailVM(private val repo: PaymentsFragmentRepo) : BaseViewModel() {

    var orderData: OrderData? = null
    var purchaseData: MyPurchaseList? = null
    var courseid: Int = 0

    var purchasedCourseDetailLiveData = MutableLiveData<PurchaseDetailDataModel?>().apply {

    }
    var invoiceLiveData = MutableLiveData<EventObserver<String?>>()

    fun getPurchasedCourseDetails(courseId: Int, setData: Boolean = true) =
        viewModelScope.launch(coroutineExceptionHandle) {
            courseid = courseId
            val response = repo.getPurchasedCourseDetail(courseid)
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val data = it.value as BaseResponse<PurchaseDetailDataModel>
                        if (setData) {
                            purchasedCourseDetailLiveData.postValue(data.resource)
                        } else {
                            invoiceLiveData.postValue(EventObserver(data.resource?.invoiceURL))

                        }
                    }
                    updateResponseObserver(it)
                }
            }
        }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_PAYMENT_COURSE_DETAILS -> {
                getPurchasedCourseDetails(courseid)
            }


        }
    }
}