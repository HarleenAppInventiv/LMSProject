@file:Suppress("SuspiciousVarProperty")

package com.selflearningcoursecreationapp.models

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.isPasswordValid
import kotlinx.android.parcel.Parcelize


@Parcelize
class ChangePasswordData : BaseObservable(), Parcelable {
    @SerializedName("oldPassword")
    var oldPassword: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.changeValid)
        }

    @SerializedName("newPassword")
    var newPassword: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.resetValid)
            notifyPropertyChanged(BR.changeValid)
        }

    @SerializedName("confirmPassword")
    var confirmPassword: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.resetValid)
            notifyPropertyChanged(BR.changeValid)
        }

    @Transient
    @get:Bindable
    var resetValid: Boolean = false
        get() {
            return newPassword.isNotEmpty() && confirmPassword.isNotEmpty()
        }

    @Transient
    @get:Bindable
    var changeValid: Boolean = false
        get() {
            return newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && oldPassword.isNotEmpty()

        }

    fun isChangeValid(): Int {
        return when {
            oldPassword.isEmpty() -> {
                R.string.plz_enter_old_password
            }
            !oldPassword.isPasswordValid() -> {
                R.string.old_password_should_be_valid

            }
            newPassword.isEmpty() -> {

                R.string.plz_enter_new_password

            }
            !newPassword.isPasswordValid() -> {
                R.string.password_should_be_valid

            }
            confirmPassword.isEmpty() -> {

                R.string.plz_enter_confirm_password

            }
            !confirmPassword.equals(newPassword) -> {
                R.string.new_confirm_paswrd_not_match

            }
            else -> {
                0
            }
        }
    }

    fun isResetValid(): Int {
        return when {
            newPassword.isBlank() -> {
                R.string.please_enter_new_password
            }
            !newPassword.isPasswordValid() -> {
                R.string.password_should_be_valid

            }
            confirmPassword.isBlank() -> {
                R.string.please_enter_confirm_password

            }
            newPassword != confirmPassword -> {
                R.string.new_confirm_paswrd_not_match

            }
            else -> {
                0
            }
        }
    }

}