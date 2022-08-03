package com.selflearningcoursecreationapp.ui.bottom_home.categories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.CategoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeCategoriesVM(private var repo: HomeCategoryRepo) : BaseViewModel() {
//    var categoryLiveData = MutableLiveData<HomeAllCategoryResponse>().apply {
//        value = HomeAllCategoryResponse()
//    }

    var isFirst = true
    var categoryLiveData = MutableLiveData<ArrayList<CategoryData>>().apply {
        value = ArrayList()
    }
    val selectedList = ArrayList<CategoryData>()

    override fun onApiRetry(apiCode: String) {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCategories()
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        isFirst = false
                        val resource = (it.value as BaseResponse<CategoryResponse>).resource
//                        selectedList.forEach { selectedData ->
//                            resource?.otherCategories?.forEach { data ->
//                                if (data.id == selectedData.id) data.isSelected = true
//                            }
//                            resource?.preferredCategories?.forEach { data ->
//                                if (data.id == selectedData.id) data.isSelected = true
//                            }
//
//                        }
//                        categoryLiveData.postValue(resource)
                        resource?.list?.sortedBy { categoryData -> categoryData.name }
                        resource?.list?.add(
                            0,
                            CategoryData(
                                codeId = R.string.all_courses,
                                imageId = R.drawable.ic_all_courses,
                                id = 0
                            )
                        )
                        categoryLiveData.postValue(resource?.list ?: ArrayList())
                    }

                    updateResponseObserver(it)
                }
            }
        }

    }
}