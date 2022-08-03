package com.selflearningcoursecreationapp.ui.bottom_more.payments.details

import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.models.course.OrderData

class PaymentDetailVM : BaseViewModel() {

    var orderData: OrderData? = null

    override fun onApiRetry(apiCode: String) {

    }
}