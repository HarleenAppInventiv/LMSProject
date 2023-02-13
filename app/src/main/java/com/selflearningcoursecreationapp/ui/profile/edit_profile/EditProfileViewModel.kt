package com.selflearningcoursecreationapp.ui.profile.edit_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel(private val repo: EditProfileRepo) : BaseViewModel() {

    var userData = MutableLiveData<UserProfile>().apply {
        getUserData()
        value = userProfile
        value?.professionId = value?.profession?.id?.toString() ?: ""
        value?.genderId = value?.genderId
        value?.countryCode = value?.countryCode.toString()
    }


    var email: String = userProfile?.email ?: ""
    var phone: String = userProfile?.number ?: ""
    var selectedCountryCodeWithPlus: String = ""

    fun saveEditProfile() {
        userData.value?.let {
            val errorId = it.isProfileValid()
            if (errorId == 0) {
                update()
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))
            }
        }
    }


    fun update() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any?>()
            userData.value?.let {
                var city =
                    if (it.stateId != null && it.cityId.isNullOrEmpty()) 0 else it.cityId ?: 0
                showLog("stateId", "" + it.stateId)
                showLog("cityId", "" + it.cityId)
                showLog("city", "" + city)
                map["name"] = it.name.trim()
                if (email != it.email.toString()) map["email"] = it.email
                if (phone != it.number) map["phoneNumber"] = it.number
                map["dob"] = it.dob ?: ""
                map["cityId"] =
                    if (it.stateId != null && it.cityId.isNullOrEmpty()) 0 else it.cityId ?: 0
                map["stateId"] = it.stateId ?: 0
                map["bio"] = it.bio ?: ""
                map["professionId"] = it.professionId
                map["genderId"] = it.genderId
                map["countryCode"] = selectedCountryCodeWithPlus

            }

            val response = repo.updateProfile(map)
            withContext(Dispatchers.IO) {
                response.collect {
                    updateResponseObserver(it)
                }
            }

        }

    fun getStateList() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.stateList()
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }


    var stateId = 0
    fun getCityList() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.cityList(stateId ?: 0)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_UPDATE_PROFILE -> {
                saveEditProfile()
            }


        }
    }

}