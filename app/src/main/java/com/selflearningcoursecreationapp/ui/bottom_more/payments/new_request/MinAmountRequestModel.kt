package com.selflearningcoursecreationapp.ui.bottom_more.payments.new_request

import com.google.gson.annotations.SerializedName

data class MinAmountRequestModel(

    @SerializedName("amount", alternate = ["count"]) val amount: Int? = 0,

    )