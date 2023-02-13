package com.selflearningcoursecreationapp.models.moderator

import android.os.Parcelable
import com.selflearningcoursecreationapp.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModFilterData(
    var startDate: String? = null,
    var endDate: String? = null,
    var feeStart: String? = null,
    var feeEnd: String? = null,
    var creatorName: String? = null
) : Parcelable {


    fun isValid(): Int {
        return when {
            !startDate.isNullOrEmpty() && endDate.isNullOrEmpty() -> R.string.plz_select_end_date
            !feeStart.isNullOrEmpty() && feeEnd.isNullOrEmpty() -> R.string.plz_enter_end_fee
            !feeEnd.isNullOrEmpty() && feeStart.isNullOrEmpty() -> R.string.plz_enter_start_fee
            isFeeRangeValid() -> R.string.end_fee_should_be_greater_than_start_fee
            else -> 0
        }
    }

    private fun isFeeRangeValid(): Boolean {
        val start = if (feeStart.isNullOrEmpty()) {
            0.0
        } else feeStart?.toDoubleOrNull() ?: 0.0

        val end = if (feeEnd.isNullOrEmpty()) {
            0.0
        } else feeEnd?.toDoubleOrNull() ?: 0.0

        return end < start
    }
}