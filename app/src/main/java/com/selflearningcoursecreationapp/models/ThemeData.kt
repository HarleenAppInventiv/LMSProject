package com.selflearningcoursecreationapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ThemeData(
    val themeName: Int=0,

    val themeColor: Int=0,
    var isSelected: Boolean = false,
    val id: Int = 0,
    val languageId: String? = null,
):Parcelable