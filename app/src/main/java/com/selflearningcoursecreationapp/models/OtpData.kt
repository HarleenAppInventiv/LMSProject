package com.selflearningcoursecreationapp.models


import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.R
import kotlinx.android.parcel.Parcelize

@Parcelize
class OtpData(

) : BaseObservable(), Parcelable {

    @SerializedName("otp1")
    var otp1: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataValid)

        }

    @SerializedName("otp2")
    var otp2: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataValid)

        }

    @SerializedName("otp3")
    var otp3: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataValid)

        }


    @SerializedName("otp4")
    var otp4: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataValid)

        }

    @SerializedName("otp")
    @get:Bindable
    var otp: String? = ""
        get() = "$otp1$otp2$otp3$otp4"

    @Transient
    @get:Bindable
    var dataValid: Boolean = false
        get() {
            return isValid() == 0
        }

    fun isValid(): Int {

        return when {
            otp.isNullOrEmpty() -> {
                R.string.please_enter_otp
            }
            otp!!.length < 4 -> {
                R.string.please_enter_valid_otp
            }
            else -> {
                0
            }
        }
    }
}