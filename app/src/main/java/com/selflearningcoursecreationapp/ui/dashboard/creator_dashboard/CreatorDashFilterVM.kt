package com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.utils.CommonPayload
import com.selflearningcoursecreationapp.utils.PROFESSION_FILTER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreatorDashFilterVM(private val repo: CreatorDashboardRepo) : BaseViewModel() {

    var filterRequestModel = GetReviewsRequestModel()
    var combinedFilterRequestModel = GetReviewsRequestModel()
    var ageFilterFields = arrayListOf<SearchFieldsItem>()
    var professionFilterFields = arrayListOf<SearchFieldsItem>()

    fun createAgePayload(categoryData: CategoryData) {
        ageFilterFields = ArrayList()
        ageFilterFields.add(
            SearchFieldsItem(
                CommonPayload.AGE,
                CommonPayload.OPERATOR_GREATER_EQUAL,
                arrayListOf("'${categoryData.minAgeFilter}'")
            )
        )

        if (categoryData.id != PROFESSION_FILTER.PURSUING_GRADUATION)
            ageFilterFields.add(
                SearchFieldsItem(
                    CommonPayload.AGE,
                    CommonPayload.OPERATOR_LESS_EQUAL,
                    arrayListOf("'${categoryData.maxAgeFilter}'")
                )
            )

        createCombinedPayload()


    }

    private fun createCombinedPayload() {
        filterRequestModel.searchFields = ArrayList()

        filterRequestModel.searchFields?.addAll(ageFilterFields)
        filterRequestModel.searchFields?.addAll(professionFilterFields)
    }

    fun createProfessionPayload(categoryData: CategoryData) {
        professionFilterFields = ArrayList()
        professionFilterFields.add(
            SearchFieldsItem(
                CommonPayload.PROFESSIONID,
                CommonPayload.OPERATOR_GREATER_EQUAL,
                arrayListOf("'${categoryData.id}'")
            )
        )

        if (categoryData.id == PROFESSION_FILTER.ALL) {
            professionFilterFields.add(
                SearchFieldsItem(
                    CommonPayload.PROFESSIONID,
                    CommonPayload.OPERATOR_LESS_EQUAL,
                    arrayListOf("'${PROFESSION_FILTER.PURSUING_GRADUATION}'")
                )
            )
        } else {
            professionFilterFields.add(
                SearchFieldsItem(
                    CommonPayload.PROFESSIONID,
                    CommonPayload.OPERATOR_LESS_EQUAL,
                    arrayListOf("'${categoryData.id}'")
                )
            )
        }

        createCombinedPayload()

    }

    fun resetFilterPayloads() {
        professionFilterFields.clear()
        ageFilterFields.clear()
        filterRequestModel.searchFields?.clear()
        combinedFilterRequestModel.searchFields?.clear()
    }

    var masterData = MutableLiveData<MasterDataItem>().apply {
        value = MasterDataItem()
    }

    fun getMasterData() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getMasterData()
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        masterData.postValue(
                            (it.value as BaseResponse<MasterDataItem>).resource ?: MasterDataItem()
                        )
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }


}




