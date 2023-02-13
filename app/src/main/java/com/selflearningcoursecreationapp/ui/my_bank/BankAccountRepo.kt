package com.selflearningcoursecreationapp.ui.my_bank


import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface BankAccountRepo {

    suspend fun addBankAccount(map: HashMap<String, Any>): Flow<Resource>
    suspend fun deleteBankAccount(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getBankAccountListing(): Flow<Resource>
    suspend fun getMaxBankCount(): Flow<Resource>
    suspend fun sendBankAccountPrimary(hashMap: HashMap<String, Any>): Flow<Resource>

}