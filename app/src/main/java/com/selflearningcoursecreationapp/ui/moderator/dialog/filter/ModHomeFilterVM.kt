package com.selflearningcoursecreationapp.ui.moderator.dialog.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.extensions.convertToUtc
import com.selflearningcoursecreationapp.extensions.createDate
import com.selflearningcoursecreationapp.extensions.getStringDate
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.models.moderator.ModFilterData
import com.selflearningcoursecreationapp.models.moderator.ModFilterOptionData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.ui.create_course.AddCourseRepo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CommonPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ModHomeFilterVM(private val repo: AddCourseRepo) : BaseViewModel() {

    var selectedLanguageId = 0
    var catArrayList = arrayListOf<ModFilterOptionData>()
    var filterRequestModel = GetReviewsRequestModel()
    var filterData = MutableLiveData<ModFilterData>().apply {
        value = ModFilterData()
    }
    val languageData = MutableLiveData<ArrayList<CategoryData>>().apply {
        value = ArrayList()
    }


    fun getMasterData() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getMasterData()
            withContext(Dispatchers.IO) {
                response.collect {
                    if (it is Resource.Success<*>) {
                        val masterData =
                            (it.value as BaseResponse<MasterDataItem>).resource ?: MasterDataItem()
                        masterData.allLanguages?.list?.forEach {
                            if (it.id == selectedLanguageId) {
                                it.isSelected = true
                            }
                        }
                        languageData.postValue(masterData.allLanguages?.list ?: ArrayList())
                    }
                    updateResponseObserver(it)
                }
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {

            ApiEndPoints.API_MASTER_DATA -> {
                getMasterData()
            }

        }
    }

    fun validate() {
        filterData?.value?.let {
            val errorId = it.isValid()
            if (errorId == 0) {
                createFilterPayload()
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorId)))
            }
        }
    }


    fun resetFields() {
        filterData.value = ModFilterData()
        languageData.value?.forEach {
            it.isSelected = false
        }
        createFilterPayload()
    }

    private fun createFilterPayload() {
//Filters:-
//Langauge If multiple
//{
//  "searchFields": [
//    {
//      "fieldName": "languageId",
//      "fieldValue": [
//        "1",  "2"
//      ],
//      "operatorType": 2
//    }
//  ]
//}
//
//if Single
//
//{
//  "searchFields": [
//    {
//      "fieldName": "languageId",
//      "fieldValue": [
//        "1"
//      ],
//      "operatorType": 1
//    }
//  ]
//}
//
//CourseFees:-
//
//{
//  "searchFields": [
//    {
//      "fieldName": "courseFee",
//      "fieldValue": [
//        "10"
//      ],
//      "operatorType": 6
//    },
//   {
//      "fieldName": "courseFee",
//      "fieldValue": [
//        "100"
//      ],
//      "operatorType": 7
//    }
//  ]
//}
//
//Created by:-
//
//{
//  "searchFields": [
//    {
//      "fieldName": "createdByName",
//      "fieldValue": [
//        "qa"
//      ],
//      "operatorType": 3
//    }
//  ]
//}
//
//
//CreatedDate:-
//{
//  "searchFields": [
//    {
//      "fieldName": "createdDate",
//      "fieldValue": [
//        "'2022-08-17'"
//      ],
//      "operatorType": 6
//    },
//   {
//      "fieldName": "createdDate",
//      "fieldValue": [
//        "'2022-08-18'"
//      ],
//      "operatorType": 7
//    }
//  ]
//}

        filterRequestModel = GetReviewsRequestModel()
        filterRequestModel.searchFields = ArrayList()
        filterRequestModel.apply {
            val list = languageData.value?.filter { it.isSelected }
                ?.map { it.id.toString() } as ArrayList<String?>
            if (!list.isNullOrEmpty()) {
                searchFields?.add(
                    SearchFieldsItem(
                        "languageId",
                        if (list.size > 1) CommonPayload.OPERATOR_IN else CommonPayload.OPERATOR_EQUAL,
                        fieldValue = list
                    )
                )
            }


            if (!filterData.value?.feeStart.isNullOrEmpty()) {
                searchFields?.add(
                    SearchFieldsItem(
                        "courseFee",
                        CommonPayload.OPERATOR_GREATER_EQUAL,
                        fieldValue = ArrayList<String?>().apply {
                            add(filterData.value?.feeStart)
                        })
                )

            }
            if (!filterData.value?.feeEnd.isNullOrEmpty()) {
                searchFields?.add(
                    SearchFieldsItem(
                        "courseFee",
                        CommonPayload.OPERATOR_LESS_EQUAL,
                        fieldValue = ArrayList<String?>().apply {
                            add(filterData.value?.feeEnd)
                        })
                )

            }
            if (!filterData.value?.creatorName.isNullOrEmpty()) {
                searchFields?.add(
                    SearchFieldsItem(
                        "createdByName",
                        CommonPayload.OPERATOR_LIKE,
                        fieldValue = ArrayList<String?>().apply {
                            add(filterData.value?.creatorName)
                        })
                )

            }
            if (!filterData.value?.startDate.isNullOrEmpty()) {
                searchFields?.add(
                    SearchFieldsItem(
                        "modifiedDate",
                        CommonPayload.OPERATOR_GREATER_EQUAL,
                        fieldValue = ArrayList<String?>().apply {
                            add("'${filterData.value?.startDate?.convertToUtc()}'")
                        })
                )

            }
            if (!filterData.value?.endDate.isNullOrEmpty()) {
                val endCal = Calendar.getInstance()
                filterData.value?.endDate?.createDate("yyyy-MM-dd")?.let {
                    endCal.time = it
                }
                endCal.add(Calendar.DATE, 1)

                searchFields?.add(
                    SearchFieldsItem(
                        "modifiedDate",
                        CommonPayload.OPERATOR_LESS_EQUAL,
                        fieldValue = ArrayList<String?>().apply {
                            add("'${endCal.time?.getStringDate("yyyy-MM-dd")?.convertToUtc()}'")
                        })
                )

            }

        }

        updateResponseObserver(Resource.Success(true, apiCode = ApiEndPoints.HOME_SUCCESS))

    }

    fun setFilterData() {
        filterRequestModel.searchFields?.forEach { searchFieldsItem ->
            val value = if (!searchFieldsItem.fieldValue.isNullOrEmpty()) {
                searchFieldsItem.fieldValue?.get(0)?.toString() ?: ""
            } else {
                ""
            }
            when (searchFieldsItem.fieldName) {
                "modifiedDate", "createdDate" -> {
                    val date = value.replace("'", "")
                    if (searchFieldsItem.operatorType == CommonPayload.OPERATOR_GREATER_EQUAL) {
                        var date1 = date.changeDateFormat("yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd")
                        filterData.value?.startDate = date1
                    }
                    if (searchFieldsItem.operatorType == CommonPayload.OPERATOR_LESS_EQUAL) {
                        val endCal = Calendar.getInstance()

                        date?.createDate("yyyy-MM-dd HH:mm:ss.SSS")?.let {
                            endCal.time = it
                        }
                        endCal.add(Calendar.DATE, -1)
                        filterData.value?.endDate = endCal.time?.getStringDate("yyyy-MM-dd")
                    }
                }

                "createdByName" -> {
                    filterData.value?.creatorName = value

                }
                "courseFee" -> {
                    if (searchFieldsItem.operatorType == CommonPayload.OPERATOR_LESS_EQUAL) {
                        filterData.value?.feeEnd = value
                    } else {
                        filterData.value?.feeStart = value
                    }
                }
                "languageId" -> {
                    selectedLanguageId = value?.toIntOrNull() ?: 0
                    languageData.value?.forEach {
                        if (it.id == selectedLanguageId) {
                            it.isSelected = true
                        }
                    }
                }
            }

        }
    }


}