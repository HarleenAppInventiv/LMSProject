package com.hd.videoEditor.model


import android.os.Parcelable
import com.hd.videoEditor.customViews.filter.egl.newFilter.NewFilterType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterData(
    var name: String = "",
    var icon: Int = 0,
    var filter: NewFilterType? = null,
    var isSelected: Boolean = false,
) : Parcelable