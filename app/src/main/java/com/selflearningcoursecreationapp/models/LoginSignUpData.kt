@file:Suppress("SuspiciousVarProperty")
package com.selflearningcoursecreationapp.models

import android.os.Parcelable
import androidx.core.text.isDigitsOnly
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.isValidEmail
import com.selflearningcoursecreationapp.utils.ValidationConst
import kotlinx.android.parcel.Parcelize

@Parcelize
class LoginData : BaseObservable(), Parcelable {
    @SerializedName("email")
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }


    @SerializedName("countryCode")
    var countryCode: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

//    @SerializedName("phone")
//    var phone: String = ""
//        set(value) {
//            field = value
//            notifyPropertyChanged(BR.dataEntered)
//        }


    @SerializedName("password")
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)

        }

    @Transient
    @get:Bindable
    var dataEntered: Boolean = false
        get() = email.isNotBlank() && password.isNotEmpty()

    fun isValid(): Int {

        return when {

            email.isEmpty() -> {
                R.string.plz_enter_phone_email
            }
            email.isDigitsOnly() && email.length < ValidationConst.MIN_NO_LENGTH -> {
                R.string.enter_valid_phone_number
            }
            !email.isDigitsOnly() && !email.isValidEmail() -> {
                R.string.enter_valid_email
            }
            password.isBlank() -> {
                R.string.enter_password
            }
//            !password.isPasswordValid() -> {
//                R.string.enter_valid_password
//            }
            else -> {
                0
            }
        }

    }


}




