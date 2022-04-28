package com.selflearningcoursecreationapp.ui.profile.edit_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.user.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileViewModel(private val repo: EditProfileRepo) : BaseViewModel() {

    var userData = MutableLiveData<UserProfile>().apply {
        getUserData()
        value = userProfile
        value!!.professionId = value?.profession?.id?.toString() ?: ""
        value!!.genderId = value?.genderId.toString() ?: ""
        value!!.countryCode = value?.countryCode.toString() ?: ""
    }


    var email: String = userProfile?.email ?: ""
    var phone: String = userProfile?.number ?: ""


    fun saveEditProfile(selectedCountryCodeWithPlus: String) {
        userData.value?.let {
            val errorId = it.isProfileValid()
            if (errorId == 0) {
                update(selectedCountryCodeWithPlus)
            } else {
                updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))
            }
        }
    }


    fun update(selectedCountryCodeWithPlus: String) =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()
            userData.value?.let {
                map["name"] = it.name
//            if (email != it.email.toString()) map["email"] = it.email
                if (phone != it.number.toString()) map["phoneNumber"] =
                    it.number
                map["dob"] = it.dob ?: ""
                map["cityId"] = it.cityId ?: 0
                map["stateId"] = it.stateId ?: 0
                map["bio"] = it.bio ?: ""
                map["professionId"] = it.professionId ?: 0
                map["genderId"] = it.genderId ?: 0
                map["countryCode"] = selectedCountryCodeWithPlus ?: ""

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


    fun getCityList(stateId: Int) = viewModelScope.launch(coroutineExceptionHandle) {
        var response = repo.cityList(stateId)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }

    fun getProfessionList() = viewModelScope.launch(coroutineExceptionHandle) {
        var response = repo.professionList()
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }
}