package com.selflearningcoursecreationapp.ui.profile.edit_profile

import android.util.Log
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.isValidEmail
import com.selflearningcoursecreationapp.ui.profile.repo.EditProfileRepo

class EditProfileViewModel(private val repo: EditProfileRepo) : BaseViewModel() {

    var name: String = "Akash"
    var email: String = ""
    var phone: String = ""
    var dob: String = ""
    var city: String = ""
    var state: String = ""


    fun saveEditProfile() {
        if (name.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_name)))
        } else if (email.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_email)))

        } else if (!email.isValidEmail()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_email)))

        } else if (phone.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_phone_number)))

        } else if (phone.length < 10) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.enter_valid_phone_number)))

        } else if (dob.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_dob)))

        } else if (city.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_city)))

        } else if (state.isBlank()) {
            updateResponseObserver(Resource.Error(ToastData(errorCode = R.string.please_enter_state)))
        } else {
            Log.d("main", "")
        }

    }
}