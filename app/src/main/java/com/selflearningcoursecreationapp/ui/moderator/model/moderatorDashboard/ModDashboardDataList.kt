import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2022 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class ModDashboardDataList(

    @SerializedName("courseId") var courseId: Int,
    @SerializedName("courseTitle") var courseTitle: String,
    @SerializedName("courseFee") var courseFee: Double,
    @SerializedName("courseDurations") var courseDurations: Int,
    @SerializedName("totalSections") var totalSections: Int,
    @SerializedName("totalLectures") var totalLectures: Int,
    @SerializedName("averageRating") var averageRating: Double,
    @SerializedName("totalReviews") var totalReviews: Int,
    @SerializedName("createdById") var createdById: Int,
    @SerializedName("createdByName") var createdByName: String,
    @SerializedName("categoryTypeId") var categoryTypeId: Int,
    @SerializedName("categoryTypeName") var categoryTypeName: String,
    @SerializedName("languageId") var languageId: Int,
    @SerializedName("languageName") var languageName: String,
    @SerializedName("courseComplexityTypeId") var courseComplexityTypeId: Int,
    @SerializedName("courseComplexityTypeName") var courseComplexityTypeName: String,
    @SerializedName("courseTypeId") var courseTypeId: Int,
    @SerializedName("courseTypeName") var courseTypeName: String,
    @SerializedName("courseLogoContentId") var courseLogoContentId: String,
    @SerializedName("courseLogoContentURL") var courseLogoContentURL: String,
    @SerializedName("courseBannerContentId") var courseBannerContentId: String,
    @SerializedName("courseBannerContentURL") var courseBannerContentURL: String,
    @SerializedName("courseWishlisted") var courseWishlisted: Int,
    @SerializedName("userCourseStatus") var userCourseStatus: Int,
    @SerializedName("status") var status: Int,
    @SerializedName("paymentStatus") var paymentStatus: Int,
    @SerializedName("rewardPoints") var rewardPoints: Int,
    @SerializedName("currencyCode") var currencyCode: String,
    @SerializedName("currencySymbol") var currencySymbol: String,
    @SerializedName("publishedDate") var publishedDate: String
)