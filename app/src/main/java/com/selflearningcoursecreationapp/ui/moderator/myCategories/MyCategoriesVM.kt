package com.selflearningcoursecreationapp.ui.moderator.myCategories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCategoriesVM(private val repo: MyCategoriesRepo) : BaseViewModel() {
    var myCategoriesLiveData = MutableLiveData<ModeratorMyCategories>()

    fun myCategories() = viewModelScope.launch(Dispatchers.IO) {
        val response = repo.myCategories()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    updateResponseObserver(it)
                    val data = it.value as BaseResponse<ModeratorMyCategories>
                    myCategoriesLiveData.postValue(data.resource)
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