package com.selflearningcoursecreationapp.ui.moderator.qualification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.DocModelItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModCertificateVM(private val repo: ModCertificateRepo) : BaseViewModel() {
    var myDocLiveData = MutableLiveData<ArrayList<DocModelItem>>()

    fun myDoc() = viewModelScope.launch(Dispatchers.IO) {
        val response = repo.myDocs()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    updateResponseObserver(it)
                    val data = it.value as BaseResponse<ArrayList<DocModelItem>>
                    myDocLiveData.postValue(data.resource)
                }
                updateResponseObserver(it)
                /*else {
                    updateResponseObserver(it)
                }*/
            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }
}