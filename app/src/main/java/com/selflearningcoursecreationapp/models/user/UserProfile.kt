@file:Suppress("SuspiciousVarProperty")

package com.selflearningcoursecreationapp.models.user

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.ValidationConst
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserProfile(

    @SerializedName("categories")
    var categoryData: ArrayList<CategoryData>? = null,

    @SerializedName("emailVerified")
    var emailVerified: Boolean? = null,
    @SerializedName("themeUpdated")
    var themeUpdated: Boolean? = null,
    @SerializedName("categoryUpdated")
    var categoryUpdated: Boolean? = null,
    @SerializedName("languageUpdated")
    var languageUpdated: Boolean? = null,
    @SerializedName("fontUpdated")
    var fontUpdated: Boolean? = null,
    @SerializedName("phoneNumberVerified")
    var phoneNumberVerified: Boolean? = null,
    @SerializedName("isActive")
    var isActive: Boolean? = null,
    @SerializedName("noPreferenceUpdated")
    var noPreferenceUpdated: Boolean? = null,
    @SerializedName("font")
    var font: CategoryData? = null,
    @SerializedName("id", alternate = ["userId"])
    var id: Int? = null,
    @SerializedName("preferencesUpdatedLevel")
    var preferencesUpdatedLevel: Int? = null,
    @SerializedName("language")
    var language: CategoryData? = null,
    @SerializedName("profileUrl")
    var profileUrl: String? = null,
    @SerializedName("courseLogoURL")
    var courseLogoURL: String? = null,
    @SerializedName("profileBlurHash")
    var profileBlurHash: String? = null,
    @SerializedName("courseLogoId")
    var courseLogoId: String? = null,

    @SerializedName("profession_name")
    var professionName: String? = null,

    @SerializedName("contentName")
    var contentName: String? = null,

    @SerializedName("theme")
    var theme: CategoryData? = null,

    @SerializedName("profession")
    var profession: CategoryData? = null,

    @SerializedName("isLogout")
    var isLogout: String? = "",
    @SerializedName("validated")
    var validated: String? = "",
    @SerializedName("passwordUpdated")
    var passwordUpdated: Boolean? = false,

    @SerializedName("success")
    var success: String? = "",

    @SerializedName("city")
    var city: String? = "",

    @SerializedName("state")
    var state: String? = "",

    @SerializedName("contentURL")
    var contentURL: String? = "",

    @SerializedName("updated")
    var updated: String? = "",

    @SerializedName("deleted")
    var deleted: Boolean? = false,
) : BaseObservable(), Parcelable {

    @SerializedName("email")
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.signUpDataEntered)
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("password")
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.signUpDataEntered)
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("name")
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.signUpDataEntered)
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("phone", alternate = ["number", "phoneNumber"])
    var number: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.signUpDataEntered)
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("professionId")
    var professionId: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.signUpDataEntered)
            notifyPropertyChanged(BR.dataEntered)

        }


    @SerializedName("cityId")
    var cityId: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("dob")
    var dob: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("stateId")
    var stateId: Int? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)
        }

    @SerializedName("bio")
    var bio: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("genderId")
    var genderId: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("countryCode")
    var countryCode: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)

        }

    @SerializedName("genderName")
    var genderName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataEntered)

        }

    @Transient
    @get:Bindable
    var signUpDataEntered: Boolean = false
        get() = /*!email.isBlank() && !password.isEmpty() &&*/
            number.isNotEmpty() && name.isNotEmpty() && professionId.isNotEmpty()

    @Transient
    @get:Bindable
    var dataEntered: Boolean = false
        get() =/* !email.isBlank() &&*/
            number.isNotEmpty() && name.isNotEmpty() && !dob.isNullOrEmpty() && !stateId.isNullOrZero() && !cityId.isNullOrEmpty() && professionId.isNotEmpty() && !bio.isNullOrEmpty()
                    && !genderId.isNullOrEmpty() && !countryCode.isNullOrEmpty()


    fun isSignUpValid(): Int {
        return when {
            name.isBlank() -> {
                R.string.enter_name
            }
            name.length < ValidationConst.MIN_NAME_LENGTH -> {
                R.string.enter_valid_name
            }
//            email.isBlank() -> {
//                R.string.enter_email
//            }
//            !email.isValidEmail() -> {
//                R.string.enter_valid_email
//            }
            number.isBlank() -> {
                R.string.enter_phone_number
            }
            number.length < ValidationConst.MIN_NO_LENGTH -> {
                R.string.enter_valid_phone_number
            }
//            password.isBlank() -> {
//                R.string.enter_password
//            }
//            !password.isPasswordValid() -> {
//                R.string.valid_password_instructions
//            }
            professionId.isBlank() -> {
                R.string.plz_select_profession
            }
            else -> {
                0
            }
        }
    }

    fun getFormattedDob(): String {
        return dob?.changeDateFormat() ?: ""
    }

    fun isProfileValid(): Int {
        return when {
            name.isBlank() -> {
                R.string.enter_name
            }
            name.length < ValidationConst.MIN_NAME_LENGTH -> {
                R.string.enter_valid_name
            }
//            email.isEmpty() -> {
//                R.string.enter_email
//
//            }
//            email.isValidEmail() -> {
//                R.string.enter_valid_email
//
//            }
//            number.isEmpty() -> {
//                R.string.enter_phone_number
//
//            }
//            number.length < 10 -> {
//                R.string.enter_valid_phone_number
//
//            }
            dob.isNullOrEmpty() -> {
                R.string.please_enter_dob

            }
            cityId.isNullOrEmpty() -> {
                R.string.please_enter_city

            }
            stateId.isNullOrZero() -> {
                R.string.please_enter_state
            }
            professionId.isEmpty() -> {
                R.string.plz_select_profession
            }
            bio.isNullOrEmpty() -> {
                R.string.please_enter_bio
            }
            genderId.isNullOrEmpty() -> {
                R.string.please_select_gender
            }

            else -> {
                0
            }
        }
    }

    fun getPreferenceValue(): Int {
        return when {
            languageUpdated ?: false -> 4
            fontUpdated ?: false -> 3
            themeUpdated ?: false -> 2
            categoryUpdated ?: false -> 1
            else -> 0
        }
    }

}
