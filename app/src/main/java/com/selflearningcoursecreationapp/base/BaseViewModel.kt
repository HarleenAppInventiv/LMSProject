package com.selflearningcoursecreationapp.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.EventObserver

abstract class BaseViewModel : ViewModel() {


    private val _response = MutableLiveData<EventObserver<Resource>>()


    fun getApiResponse(): LiveData<EventObserver<Resource>> = _response

    fun updateResponseObserver(response: Resource) {
        _response.postValue(EventObserver(response))
    }

}