package com.selflearningcoursecreationapp.base

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.showException
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.AppThemeFile
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.utils.Constants
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {

    var LogTag = "BaseViewModel"
    var userProfile: UserProfile? = null
    var isViOn: Boolean? = null
    abstract fun onApiRetry(apiCode: String)

    init {
        getUserData()
    }


    fun getUserData() {
        viewModelScope.launch {
            withContext(viewModelScope.coroutineContext) {
                val userResponse = PreferenceDataStore.getString(Constants.USER_RESPONSE)
                isViOn = PreferenceDataStore.getBoolean(Constants.VI_MODE)
                if (!userResponse.isNullOrEmpty()) {
                    val resp = Gson().fromJson(userResponse, UserResponse::class.java)
                    if (resp.user != null) {
                        userProfile = resp.user
                    }
                }
            }
        }
    }

    private val _response = MutableLiveData<EventObserver<Resource>>()


    val coroutineExceptionHandle = CoroutineExceptionHandler { _, e ->
        Log.d(LogTag, "CoroutineExceptionHandler >> ${e.message}")

        updateResponseObserver(Resource.Failure(false, "", ApiError().apply {
            exception = e
        }))
    }

    fun getApiResponse(): LiveData<EventObserver<Resource>> = _response

    fun updateResponseObserver(response: Resource) {
        try {
            _response.postValue(EventObserver(response))
        } catch (e: Exception) {
            _response.value = EventObserver(response)
        }
    }

    suspend fun saveUser(userResponse: UserResponse?) {
        if (userResponse == null) {
            PreferenceDataStore.saveString(Constants.USER_RESPONSE, "")
        } else {
            PreferenceDataStore.saveString(Constants.USER_RESPONSE, Gson().toJson(userResponse))
        }
    }

//    suspend fun setIntercomLoginStatus(isLogIn: Boolean) {
//        PreferenceDataStore.saveBoolean(Constants.INTERCOM_LOGIN, isLogIn)
//    }

//    suspend fun getIntercomLoginStatus(): Boolean {
//        return PreferenceDataStore.getBoolean(Constants.INTERCOM_LOGIN) == true
//    }

    suspend fun saveUserToken(token: String?) {
        PreferenceDataStore.saveString(Constants.USER_TOKEN, token ?: "")
        (getAppContext() as SelfLearningApplication).updateToken()
    }


    fun getThemeFile(colorString: String): AppThemeFile {
        val myTheme = AppThemeFile()
        myTheme.themeColor = colorString
        myTheme.btnTextColor = colorString
        val themeColor = try {


            Color.parseColor(colorString)
        } catch (e: Exception) {
            Color.parseColor("#8900F2")
        }
        var red = (Color.red(themeColor))
        if (red >= 100) {
            red -= 10
        } else {
            red += 10
        }


        var green = (Color.green(themeColor))
        if (green >= 100) {
            green -= 10
        } else {
            green += 10
        }


        var blue = (Color.blue(themeColor))
        if (blue >= 100) {
            blue -= 10
        } else {
            blue += 10
        }




        showLog(
            "COLOR_HEX_MAIN",
            colorString
        )
        showLog(
            "COLOR_HEX",
            String.format(
                "#%02x%02x%02x%02x",
                100,
                red,
                green,

                blue
            )
        )
        myTheme.themeTint = String.format(
            "#%02x%02x%02x%02x",
            80,
            red,
            green,
            blue
        )
        return myTheme
    }

    suspend fun saveThemeFile(themeFile: AppThemeFile) {
        PreferenceDataStore.saveString(Constants.THEME_FILE, Gson().toJson(themeFile))
    }


    suspend fun saveFont(fontId: Int) {
        PreferenceDataStore.saveInt(Constants.FONT_THEME, fontId)
    }


    suspend fun saveLanguage(languageCode: String) {
        PreferenceDataStore.saveString(
            Constants.LANGUAGE_THEME,
            languageCode
        )


    }

    suspend fun saveLanguageId(languageCodeId: Int) {
        PreferenceDataStore.saveInt(
            Constants.LANGUAGE_ID,
            languageCodeId
        )


    }

    suspend fun saveUserDataInDB(
        data: BaseResponse<UserResponse>,
    ) {
        try {


            saveUser(data.resource)

            saveUserToken(data.resource?.token)
            data.resource?.user?.let { user ->
                user.theme?.code?.let { themeCode ->
                    if (themeCode.isNotEmpty()) {
                        saveThemeFile(getThemeFile(themeCode))
                        (getAppContext() as SelfLearningApplication).updatedThemeFile()
                    }

                }
                saveViMode(user.viMode == true)
                user.font?.id?.let { fontId ->
                    if (fontId >= 0)
                        saveFont(fontId)
                }
                user.language?.code?.let { languageCode ->
                    if (languageCode.isNotEmpty())
                        saveLanguage(languageCode)
                }
                user.language?.id?.let { languageCode ->
                    if (!languageCode.isNullOrZero())
                        saveLanguageId(languageCode)
                }

            }
        } catch (e: Exception) {
            showException(e)
            updateResponseObserver(Resource.Failure(false, "", ApiError().apply {
                exception = e
            }))
        }
    }

    fun saveViMode(isViOn: Boolean) {
        viewModelScope.launch {
//            withContext(viewModelScope.coroutineContext) {
            PreferenceDataStore.saveBoolean(Constants.VI_MODE, isViOn)
            delay(500)
            (getAppContext() as SelfLearningApplication).updatedThemeFile()
//            }
        }
    }

//    fun localToGMT(date: String): String {
////        val date = Date()
//        Log.d("TimeTest", "date: $date ")
//        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
//        sdf.timeZone = TimeZone.getTimeZone("UTC")
//        return sdf.format(date).toString()
//    }

}