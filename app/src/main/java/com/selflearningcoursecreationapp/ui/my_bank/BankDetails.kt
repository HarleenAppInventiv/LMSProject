@file:Suppress("SuspiciousVarProperty")

package com.selflearningcoursecreationapp.ui.my_bank

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.blank
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.isValidEmail
import com.selflearningcoursecreationapp.utils.ValidationConst
import kotlinx.android.parcel.Parcelize


@Parcelize
data class BankDetails(


    @SerializedName("isPrimaryAccount")
    var isPrimaryChecked: Boolean = false

) : BaseObservable(), Parcelable {

    @SerializedName("email")
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.bankDetailsDataEntered)

        }

    @SerializedName("accountNumber")
    var accountNumber: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.bankDetailsDataEntered)

        }

    @SerializedName("confirmAccountNumber")
    var confirmAccountNumber: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.bankDetailsDataEntered)

        }

    @SerializedName("bankName")
    var bankName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.bankDetailsDataEntered)

        }


    @SerializedName("status")
    var status: Int = 0

    @SerializedName("id")
    var id: Int = 0


    @SerializedName("phone", alternate = ["number", "phoneNumber"])
    var number: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.bankDetailsDataEntered)

        }

    @SerializedName("ifscCode")
    var ifscCode: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.bankDetailsDataEntered)

        }

    @Transient
    @get:Bindable
    var bankDetailsDataEntered: Boolean = false
        get() = /*!email.isBlank() && !password.isEmpty() &&*/
            email.isNotEmpty() && !number.isNullOrEmpty() && !bankName.isNullOrEmpty() && !accountNumber.isNullOrEmpty() && !confirmAccountNumber.isNullOrEmpty() && !ifscCode.isNullOrEmpty()

    @Transient
    @get:Bindable
    var dataEntered: Boolean = false
        get() =/* !email.isBlank() &&*/
            email.isNotEmpty() && !number.isNullOrEmpty() && !bankName.isNullOrEmpty() && !accountNumber.isNullOrEmpty() && !confirmAccountNumber.isNullOrEmpty() && !ifscCode.isNullOrEmpty()


    fun isValid(): Int {
        return when {
            email.isEmpty() -> {
                R.string.plz_enter_phone_email
            }
            !email.isValidEmail() -> {
                R.string.enter_valid_email
            }
            number.blank() -> {
                R.string.enter_phone_number
            }
            number.toLongOrNull().isNullOrZero() -> {
                R.string.enter_valid_phone_number

            }
            number.length < ValidationConst.MIN_NO_LENGTH -> {
                R.string.enter_valid_phone_number
            }
            bankName.isEmpty() -> {
                R.string.plz_enter_bank_account_name
            }
            bankName.length < ValidationConst.MIN_BANK_ACCOUNT_NAME -> {
                R.string.plz_enter_valid_bank_account_name
            }
            accountNumber.blank() -> {
                R.string.plz_enter_account_number
            }
            accountNumber.length < ValidationConst.MIN_BANK_ACCOUNT_NUM_LEN -> {
                R.string.enter_valid_account_number
            }

            confirmAccountNumber.blank() -> {
                R.string.plz_enter_account_number
            }
            confirmAccountNumber.length < ValidationConst.MIN_BANK_ACCOUNT_NUM_LEN -> {
                R.string.enter_valid_confirm_account_number
            }

            !accountNumber.equals(confirmAccountNumber) -> {
                R.string.confirm_account_number_not_match_desc
            }

            ifscCode.blank() -> {
                R.string.plz_enter_ifsc_code
            }
            ifscCode.length < ValidationConst.MIN_IFSC_LENGTH -> {
                R.string.enter_valid_ifsc_code
            }
            else -> {
                0
            }
        }
    }


}
