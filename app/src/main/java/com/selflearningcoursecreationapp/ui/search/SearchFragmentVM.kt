package com.selflearningcoursecreationapp.ui.search


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchFragmentVM(private val repo: SearchFragmentRepo) : BaseViewModel() {

    var currentPage = 0
    var searchKeyword = ""


    init {
        getUserData()
    }

    var searchListLiveData = MutableLiveData<ArrayList<SearchList>>().apply {
        value = ArrayList()
    }

    var isLastPage = false
    var isEmpty = false
    fun reset() {
        currentPage = 0
        isLastPage = false
        searchListLiveData.value?.clear()
    }

    fun getElasticSearchData() = viewModelScope.launch(coroutineExceptionHandle)
    {
        if (!isLastPage && !isEmpty) {
            var map = HashMap<String, Any>()
            map["keyword"] = searchKeyword
            map["pageNumber"] = currentPage
            map["pageSize"] = 30

            val response = repo.elasticSearch(map)


            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        (it.value as BaseResponse<ElasticSearchData>).resource?.let { resource ->

                            if (resource.pageNumber == 1) {
                                currentPage = 1
                                searchListLiveData.postValue(ArrayList())
                            }
                            currentPage += 1
                            val list = searchListLiveData.value
                            list?.addAll(resource.list ?: ArrayList())
//                            if(resource.list.isNullOrEmpty())
                            isLastPage = resource.list.isNullOrEmpty()
                            searchListLiveData.postValue(list)
                        }
                    }
                    updateResponseObserver(it)
                }
            }
        } else if (isEmpty) {
            searchListLiveData.value?.clear()
            searchListLiveData.postValue(ArrayList())
        }

    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_HOME_SEARCH -> {
                getElasticSearchData()
            }


        }

    }

}








