package com.selflearningcoursecreationapp.models.user

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.VALIDATION_CONST
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserProfile(
    @SerializedName("userId")
    var userId: String? = null,
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
    @SerializedName("noPreferenceUpdated")
    var noPreferenceUpdated: Boolean? = null,
    @SerializedName("font")
    var font: CategoryData? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("preferencesUpdatedLevel")
    var preferencesUpdatedLevel: Int? = null,
    @SerializedName("language")
    var language: CategoryData? = null,
    @SerializedName("profileUrl")
    var profileUrl: String? = null,
    @SerializedName("profession_name")
    var professionName: String? = null,
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

    @SerializedName("courseId")
    var courseId: Int? = null,

    @SerializedName("avatar")
    var avatar: String = "",

    @SerializedName("sectionId")
    var sectionId: Int? = null,

    @SerializedName("deleted")
    var deleted: Boolean? = false,

    @SerializedName("lectureId")
    var lectureId: Int? = null,

    @SerializedName("mediaType")
    var mediaType: Int? = null,

    @SerializedName("lectureTitle", alternate = ["title"])
    var lectureTitle: String? = "",

    @SerializedName("lectureContentDuration")
    var lectureContentDuration: Int? = null,

    @SerializedName("lectureContentId")
    var lectureContentId: Int? = null,

    @SerializedName("quizId")
    var quizId: Int? = null,

//    @SerializedName("fileUrl")
//    var fileUrl: String? = "",

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
    var stateId: String? = null
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
            !number.isEmpty() && !name.isEmpty() && !professionId.isEmpty()

    @Transient
    @get:Bindable
    var dataEntered: Boolean = false
        get() =/* !email.isBlank() &&*/
            !number.isEmpty() && !name.isEmpty() && !dob.isNullOrEmpty() && !stateId.isNullOrEmpty() && !cityId.isNullOrEmpty() && !professionId.isEmpty() && !bio.isNullOrEmpty()
                    && !genderId.isNullOrEmpty() && !countryCode.isNullOrEmpty()


    fun isSignUpValid(): Int {
        return when {
            name.isBlank() -> {
                R.string.enter_name
            }
            name.length < VALIDATION_CONST.MIN_NAME_LENGTH -> {
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
            number.length < VALIDATION_CONST.MIN_NO_LENGTH -> {
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
            name.length < VALIDATION_CONST.MIN_NAME_LENGTH -> {
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
            stateId.isNullOrEmpty() -> {
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
