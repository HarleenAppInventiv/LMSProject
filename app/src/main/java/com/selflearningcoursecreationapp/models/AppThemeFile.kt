package com.selflearningcoursecreationapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppThemeFile(
    @SerializedName("theme_color")
    var themeColor: String? = "",
   @SerializedName("button_text_color")
    var btnTextColor: String? = "",
    @SerializedName("theme_tint_color")
    var themeTint: String? = "",
  @SerializedName("primary_bg_color")
    var primaryBgColor: String? = "",
 @SerializedName("secondary_bg_color")
    var secondaryBgColor: String? = "",
 @SerializedName("primary_text_color")
    var primaryTextColor: String? = ""

) : Parcelable