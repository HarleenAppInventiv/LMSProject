package com.selflearningcoursecreationapp.ui.bottom_more.payments


import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentsFragmentRepo {

    suspend fun remainingWalletBalance(): Flow<Resource>

    suspend fun userPurchases(map: HashMap<String, Any>): Flow<Resource>

    suspend fun userEarnings(map: Int, i: Int): Flow<Resource>

    suspend fun paymentWithdrawRequest(map: HashMap<String, Any>): Flow<Resource>

    suspend fun minWithdrawAmount(): Flow<Resource>

    suspend fun getPurchasedCourseDetail(courseId: Int): Flow<Resource>

    suspend fun getWalletHistory(map: HashMap<String, Any>): Flow<Resource>


}