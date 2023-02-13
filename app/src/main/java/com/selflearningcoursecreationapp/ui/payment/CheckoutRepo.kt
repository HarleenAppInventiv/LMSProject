package com.selflearningcoursecreationapp.ui.payment


import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface CheckoutRepo {

    suspend fun getAmountWithGst(map: HashMap<String, Any>): Flow<Resource>
}