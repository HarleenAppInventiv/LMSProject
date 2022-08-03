package com.selflearningcoursecreationapp.ui.profile.edit_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel(private val repo: EditProfileRepo) : BaseViewModel() {

    var userData = MutableLiveData<UserProfile>().apply {
        getUserData()
        value = userProfile
        value?.professionId = value?.profession?.id?.toString() ?: ""
        value?.genderId = value?.genderId.toString()
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
            val map = HashMap<String, Any>()
            userData.value?.let {
                map["name"] = it.name
//            if (email != it.email.toString()) map["email"] = it.email
                if (phone != it.number) map["phoneNumber"] =
                    it.number
                map["dob"] = it.dob ?: ""
                map["cityId"] = it.cityId ?: 0
                map["stateId"] = it.stateId ?: 0
                map["bio"] = it.bio ?: ""
                map["professionId"] = it.professionId
                map["genderId"] = it.genderId ?: 0
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


    fun getCityList() = viewModelScope.launch(coroutineExceptionHandle) {
        val response = repo.cityList(userData.value?.stateId ?: 0)
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
            ApiEndPoints.API_GET_ALL_STATES -> {
                getStateList()
            }
            ApiEndPoints.API_GET_ALL_CITY -> {
                getCityList()
            }

        }
    }

}