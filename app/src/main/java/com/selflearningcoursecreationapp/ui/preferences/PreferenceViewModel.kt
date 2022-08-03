package com.selflearningcoursecreationapp.ui.preferences

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.CategoryResponse
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.FontConstant
import com.selflearningcoursecreationapp.utils.LanguageConstant
import com.selflearningcoursecreationapp.utils.PREFERENCES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreferenceViewModel(private val repo: PreferenceRepo) : BaseViewModel() {


    var categoryListLiveData = MutableLiveData<ArrayList<CategoryData>>().apply {
        value = ArrayList()
    }

    var themeListLiveData = MutableLiveData<ArrayList<CategoryData>>().apply {
        value = ArrayList()
    }
    var languageListLiveData = MutableLiveData<ArrayList<CategoryData>>().apply {
        value = ArrayList()
    }
    var fontListData = MutableLiveData<ArrayList<CategoryData>>().apply {
        value = ArrayList()
    }
    private var currentSelection = 0
    private var showCategories = false

    init {
        getUserData()
        userProfile?.let {
            Log.d("main1", "font name" + it.font?.name)
            Log.d("main1", "language name" + it.language?.name)
            Log.d("main1", "theme name" + it.theme?.name)

        }


    }

    fun savePreference(currentSelection: Int) {
        this.currentSelection = currentSelection
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>().apply {
                when (currentSelection) {
                    0 -> {
                        put(
                            "categories",
                            ""
                        )
                        categoryListLiveData.value?.filter { it.isSelected }?.map { it.id }
                            ?.joinToString()
                            ?.let {
                                put(
                                    "categories",
                                    it
                                )
                            }


                    }
                    1 -> {
                        themeListLiveData.value?.singleOrNull { it.isSelected }?.id?.let {
                            put(
                                "themeId",
                                it
                            )
                        }
                    }
                    2 -> {
                        fontListData.value?.singleOrNull { it.isSelected }?.id?.let {
                            put(
                                "fontId",
                                it
                            )
                        }
                    }
                    3 -> {
                        languageListLiveData.value?.singleOrNull { it.isSelected }?.id?.let {
                            put(
                                "languageId",
                                it
                            )
                        }
                    }
                }

            }
            val response = repo.savePreference(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }
        }
    }


    fun getThemeData() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.getThemeList()
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    val list =
                        (it.value as BaseResponse<CategoryResponse>).resource?.list ?: ArrayList()

                    list.forEach { data ->
                        if (userProfile?.theme?.id == data.id) {
                            data.isSelected = true
                        }

                    }


                    themeListLiveData.postValue(
                        list
                    )
                }

                updateResponseObserver(it)
            }
        }
    }

    fun getCategories(showMyCategories: Boolean = true) {
        showCategories = showMyCategories
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.getCategory()
            withContext(Dispatchers.IO) {

                response.collect {
                    if (it is Resource.Success<*>) {

                        val list = (it.value as BaseResponse<CategoryResponse>).resource?.list
                            ?: ArrayList()
                        if (showMyCategories) {
                            getMyCategories()
                        }
                        categoryListLiveData.postValue(list)
                    }

                    updateResponseObserver(it)
                }
            }
        }

    }

    fun getMyCategories() {
        viewModelScope.launch(coroutineExceptionHandle) {
            val response = repo.myCategories()
            withContext(Dispatchers.IO) {
                response.catch { cause ->
                    updateResponseObserver(
                        Resource.Failure(
                            false,
                            ApiEndPoints.API_MY_CATEGORIES,
                            ApiError().apply {
                                exception = cause
                            })
                    )

                }
                response.collect {
                    if (it is Resource.Success<*>) {

                        val list = (it.value as BaseResponse<CategoryResponse>).resource?.list
                            ?: ArrayList()
                        categoryListLiveData.value?.forEach { data ->
                            list?.forEach { selectedData ->
                                if (data.id == selectedData.id) {
                                    data.isSelected = true
                                }
                            }
                        }
                        categoryListLiveData.postValue(categoryListLiveData.value)
                    }

                    updateResponseObserver(it)
                }
            }
        }

    }

    suspend fun savePrefDataInDB(type: Int) {


        if (type == PREFERENCES.TYPE_ALL || type == PREFERENCES.TYPE_THEME) {

            themeListLiveData.value?.singleOrNull { it.isSelected }?.let { themeData ->
                userProfile?.theme = themeData

                saveThemeFile(
                    getThemeFile(
                        themeData.code ?: ""
                    )
                )
            }


        }
        if (type == PREFERENCES.TYPE_ALL || type == PREFERENCES.TYPE_FONT) {
            fontListData.value?.singleOrNull { it.isSelected }?.let {
                userProfile?.font = it
                saveFont(it.id ?: FontConstant.IBM)
            }


        }
        if (type == PREFERENCES.TYPE_ALL || type == PREFERENCES.TYPE_LANGUAGE) {
            languageListLiveData.value?.singleOrNull { it.isSelected }?.let {
                userProfile?.language = it
                saveLanguage(it.code ?: LanguageConstant.ENGLISH)
            }

        }
        saveUser(UserResponse(user = userProfile))
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_SAVE_PREFERENCES -> {
                savePreference(currentSelection)
            }
            ApiEndPoints.API_GET_CATEGORIES -> {
                getCategories(showCategories)
            }
            ApiEndPoints.API_MY_CATEGORIES -> {
                getMyCategories()
            }
            ApiEndPoints.API_GET_THEME_LIST -> {
                getThemeData()
            }
        }
    }
}