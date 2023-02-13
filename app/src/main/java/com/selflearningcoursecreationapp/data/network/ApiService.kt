package com.selflearningcoursecreationapp.data.network

//import com.selflearningcoursecreationapp.utils.ApiEndPoints.API_BASE_COURSE_URL
import LearnerDashboardData
import ModDashboardData
import RevenueDataModel
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.models.*
import com.selflearningcoursecreationapp.models.course.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizReportData
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.models.rewardPoints.RewardPointResponse
import com.selflearningcoursecreationapp.models.user.ModeratorStatusResponses
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.FilterResponse
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.MyPaymentEarningDataModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.MyPurchaseDataModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.PurchaseDetailDataModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.RemainingWalletBalanceData
import com.selflearningcoursecreationapp.ui.bottom_more.payments.new_request.MinAmountRequestModel
import com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet.WalletDataModel
import com.selflearningcoursecreationapp.ui.course_details.model.AddReviewRequestModel
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorDetailsData
import com.selflearningcoursecreationapp.ui.course_details.model.ShareLinkUrl
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.ReviewResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.co_author.ExistsCoAuthorResponse
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorAudienceStateCount
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorCourseUserCount
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorCourseUserCountWithFilter
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorTotalEarnings
import com.selflearningcoursecreationapp.ui.dashboard.model.ActivityHoursModel
import com.selflearningcoursecreationapp.ui.dashboard.model.LearnerDashboardFilteredStatCountModel
import com.selflearningcoursecreationapp.ui.dashboard.model.LearnerDashboardStatCountModel
import com.selflearningcoursecreationapp.ui.moderator.model.AddModeratorResponse
import com.selflearningcoursecreationapp.ui.moderator.model.ModDashboardStatCountModel
import com.selflearningcoursecreationapp.ui.moderator.model.RequestCountModel
import com.selflearningcoursecreationapp.ui.moderator.myCategories.ModeratorMyCategories
import com.selflearningcoursecreationapp.ui.my_bank.BankDetails
import com.selflearningcoursecreationapp.ui.payment.AmountWithGSTModel
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ChangeViModeModel
import com.selflearningcoursecreationapp.ui.profile.requestTracker.paymentWithdrawls.PaymentWithdrawData
import com.selflearningcoursecreationapp.ui.search.ElasticSearchData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @POST(ApiEndPoints.API_SIGNUP)
    suspend fun signUp(@Body body: UserProfile): Response<BaseResponse<UserProfile>>

    @POST(ApiEndPoints.API_OTP_REQ)
    suspend fun signOTPReq(@Body body: Any): Response<BaseResponse<Any>>


    @POST(ApiEndPoints.API_ADD_EMAIL)
    suspend fun requestEmailOTP(@Body body: Any): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.API_VERIFY_EMAIL)
    suspend fun validateEmailOTP(@Body body: Any): Response<BaseResponse<UserResponse>>


    @POST(ApiEndPoints.API_OTP_VAL)
    suspend fun signOTPVal(@Body body: Any): Response<BaseResponse<UserResponse>>


    @POST(ApiEndPoints.API_LOGIN)
    suspend fun loginUser(@Body body: Any): Response<BaseResponse<UserResponse>>

    @POST(ApiEndPoints.API_SAVE_PREFERENCES)
    suspend fun savePreferences(@Body body: Any): Response<BaseResponse<Any>>


    @GET(ApiEndPoints.API_GET_CATEGORIES)
    suspend fun getCategories(): Response<BaseResponse<CategoryResponse>>


    @GET(ApiEndPoints.API_MY_CATEGORIES)
    suspend fun myCategories(): Response<BaseResponse<CategoryResponse>>


    @GET(ApiEndPoints.API_GET_THEME_LIST)
    suspend fun getThemeList(): Response<BaseResponse<CategoryResponse>>


    @GET(ApiEndPoints.API_VIEW_PROFILE)
    suspend fun viewProfile(): Response<BaseResponse<UserProfile>>


    @POST(ApiEndPoints.API_CHANGE_PASS)
    suspend fun changePass(@Body body: Any): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.API_RESET_PASS)
    suspend fun resetPass(@Body body: Any): Response<BaseResponse<Any>>

    @PATCH(ApiEndPoints.API_UPDATE_PROFILE)
    suspend fun updateProfile(@Body body: Any): Response<BaseResponse<UserResponse>>

    @DELETE(ApiEndPoints.API_DELETE_ACCOUNT)
    suspend fun deleteAccount(
        @Query("DeleteWithEnrolledUsers") deleteUsers: Boolean,
        @Query("DeleteWithWalletBalance") deleteWallet: Boolean
    ): Response<BaseResponse<UserResponse>>

    @POST(ApiEndPoints.API_LOGOUT)
    suspend fun logoutUser(
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.API_PROFESSION)
    suspend fun profession(
    ): Response<BaseResponse<AllProfessions>>

    @GET(ApiEndPoints.API_PROFESSION)
    suspend fun getProfession(
    ): Response<BaseResponse<SingleClickResponse>>

    @GET(ApiEndPoints.API_GET_ALL_STATES)
    suspend fun stateList(
    ): Response<BaseResponse<ArrayList<StateModel>>>

    @GET(ApiEndPoints.API_GET_ALL_CITY)
    suspend fun cityList(@Query("StateId") stateId: Int): Response<BaseResponse<ArrayList<CityModel>>>

    @POST(ApiEndPoints.API_ADD_PASSWORD)
    suspend fun addPassword(@Body body: Any): Response<BaseResponse<UserResponse>>

    @POST(ApiEndPoints.API_ADD_EMAIL)
    suspend fun addEmail(@Body body: Any): Response<BaseResponse<Any>>

    @POST
    suspend fun addCourseStep1(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_1,
        @Body body: Any
    ): Response<BaseResponse<CourseData>>

    @PATCH
    suspend fun updateCourseStep1(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_1,
        @Body body: Any
    ): Response<BaseResponse<CourseData>>

    @POST
    suspend fun addCourseStep2(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_2,
        @Body body: Any
    ): Response<BaseResponse<CourseData>>

    @POST(ApiEndPoints.API_ADD_MODERATOR)
    suspend fun addModerator(@Body body: AddModeratorResponse): Response<BaseResponse<CategoryResponse>>

    @GET
    suspend fun getCourseDetail(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_1,
        @Query("CourseId") courseId: Int
    ): Response<BaseResponse<CourseData>>

    @GET
    suspend fun getCourseSections(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_GET_SECTIONS,
        @Query("CourseId") courseId: Int
    ): Response<BaseResponse<CourseData>>

    @GET(ApiEndPoints.API_MASTER_DATA)
    suspend fun getMasterData(): Response<BaseResponse<MasterDataItem>>

    @Multipart
    @POST(ApiEndPoints.API_UPLOAD_IMAGE)
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST(ApiEndPoints.API_UPLOAD_MODERATOR_DOC)
    suspend fun uploadModeDoc(
        @Part file: MultipartBody.Part,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST
    suspend fun uploadCourseBanner(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_UPLOAD_BANNER,
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody,
        @Part("CourseId") courseId: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST
    suspend fun uploadCourseLogo(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_UPLOAD_LOGO,
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody,
        @Part("CourseId") courseId: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST
    suspend fun uploadQuizImage(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ_IMAGE,
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody?,
        @Part("CourseId") courseId: RequestBody?,
        @Part("SectionId") sectionId: RequestBody?,
        @Part("QuizId") quizId: RequestBody?,
        @Part("LectureId") lectureId: RequestBody?,
        @Part("ImageTypeId") imageTypeId: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST
    suspend fun uploadAssessmentImage(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT_IMAGE,
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody?,
        @Part("CourseId") courseId: RequestBody?,
        @Part("AssessmentId") assessmentId: RequestBody?,
        @Part("ImageTypeId") imageTypeId: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @POST
    suspend fun addQuiz(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ,
        @Body body: QuizData
    ): Response<BaseResponse<QuizData>>

    @POST
    suspend fun addAssessment(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT,
        @Body courseId: RequestBody?
    ): Response<BaseResponse<QuizData>>

    @POST
    suspend fun saveQuiz(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ_SAVE,
        @Body body: QuizData?
    ): Response<BaseResponse<ChildModel>>

    @POST
    suspend fun saveAssessment(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT_SAVE,
        @Body body: QuizData?
    ): Response<BaseResponse<ChildModel>>

    @POST
    suspend fun addQuizQues(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ_QUESTION,
        @Body body: QuizQuestionData?
    ): Response<BaseResponse<QuizQuestionData>>

    @POST()
    suspend fun addAssessmentQues(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT_QUESTION,
        @Body body: QuizQuestionData?
    ): Response<BaseResponse<QuizQuestionData>>

    @PATCH
    suspend fun updateQuizQues(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ_QUESTION,
        @Body body: QuizQuestionData?
    ): Response<BaseResponse<QuizQuestionData>>

    @PATCH
    suspend fun updateAssessmentQues(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT_QUESTION,
        @Body body: QuizQuestionData?
    ): Response<BaseResponse<QuizQuestionData>>

    @DELETE
    suspend fun deleteQuizQues(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ_QUESTION,
        @Query("courseId") courseId: Int?,
        @Query("sectionId") sectionId: Int?,
        @Query("lectureId") lectureId: Int?,
        @Query("quizId") quizId: Int?,
        @Query("questionId") questionId: Int?,
    ): Response<BaseResponse<Any>>

    @DELETE
    suspend fun deleteAssessmentQues(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT_QUESTION,
        @Query("courseId") courseId: Int?,
        @Query("AssessmentId") sectionId: Int?,
        @Query("questionId") questionId: Int?,
    ): Response<BaseResponse<Any>>

    @GET
    suspend fun getQuizQues(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ,
        @Query("quizId") quizId: Int?,
    ): Response<BaseResponse<QuizData>>

    @GET
    suspend fun getAssessmentQues(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT,
        @Query("AssessmentId") quizId: Int?,
    ): Response<BaseResponse<QuizData>>

    @DELETE
    suspend fun deleteAssessment(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT,
        @Query("AssessmentId") assessmentId: Int?,
        @Query("CourseId") courseId: Int?,
    ): Response<BaseResponse<QuizData>>


    @POST
    suspend fun saveQuesAns(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_QUIZ_ANS,
        @Body body: RequestBody?
    ): Response<BaseResponse<Any>>


    @POST
    suspend fun saveAssessmentAns(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_ASSESSMENT_ANS,
        @Body body: RequestBody?
    ): Response<BaseResponse<Any>>


    @POST
    suspend fun addSection(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_SECTION,
        @Body body: Any
    ): Response<BaseResponse<SectionModel>>

    @PATCH
    suspend fun addPatchSection(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_SECTION,
        @Body body: Any
    ): Response<BaseResponse<SectionModel>>

    @DELETE
    suspend fun deleteSection(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_SECTION,
        @Query("CourseId") courseId: String,
        @Query("SectionId") sectionId: String,
    ): Response<BaseResponse<SectionModel>>

    @POST
    suspend fun dragAndDropSection(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_SECTION_DRAG_DROP,
        @Body body: Any
    ): Response<BaseResponse<UserProfile>>

    @POST
    suspend fun publishCourse(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_PUBLISH_COURSE,
        @Body body: Any?
    ): Response<BaseResponse<CourseData>>

    @POST
    suspend fun getKeywords(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_GET_KEYWORDS,
        @Body body: Any?
    ): Response<BaseResponse<SingleClickResponse>>

    @POST
    suspend fun addLecture(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_LECTURE_POST,
        @Body body: Any
    ): Response<BaseResponse<ChildModel>>

    @PATCH
    suspend fun addPatchLecture(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_ADD_LECTURE_PATCH,
        @Body body: Any
    ): Response<BaseResponse<ChildModel>>

    @DELETE
    suspend fun deleteLecture(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_LECTURE_DELETE,
        @Query("CourseId") courseId: String,
        @Query("SectionId") sectionId: String,
        @Query("LectureId") lectureId: String,
    ): Response<BaseResponse<UserProfile>>

    @POST
    suspend fun dragAndDropLecture(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_LECTURE_DRAG_DROP,
        @Body body: Any
    ): Response<BaseResponse<UserProfile>>

    @Multipart
    @POST
/*(ApiEndPoints.API_CONTENT_UPLOAD)*/
    suspend fun contentUpload(
        @Part("CourseId") courseId: RequestBody?,
        @Part("SectionId") sectionId: RequestBody?,
        @Part("LectureId") lectureId: RequestBody?,
        @Part("FileName") fileName: RequestBody?,
        @Part file: MultipartBody.Part,
        @Part("UploadType") uploadType: RequestBody?,
        @Part("Duration") duration: RequestBody?,
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CONTENT_UPLOAD

    ): Response<BaseResponse<ImageResponse>>

    //    @Multipart
//    @POST/*(ApiEndPoints.API_CONTENT_UPLOAD)*/
//    suspend fun contentUploadText(
////        @Body body: Any,
//        @Part("CourseId") courseId: RequestBody?,
//        @Part("SectionId") sectionId: RequestBody?,
//        @Part("LectureId") lectureId: RequestBody?,
////        @Part("FileName") fileName: RequestBody?,
////        @Part file: MultipartBody.Part,
//        @Part("UploadType") uploadType: RequestBody?,
//        @Part("Text") text: RequestBody?,
//        @Part("Duration") duration: RequestBody?,
//        @Url url:String= BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_TEXT_CONTENT_UPLOAD
//    ): Response<BaseResponse<ImageResponse>>
    @POST
/*(ApiEndPoints.API_CONTENT_UPLOAD)*/
    suspend fun contentUploadText(
        @Body body: HashMap<String, Any>,
//        @Part("CourseId") courseId: RequestBody?,
//        @Part("SectionId") sectionId: RequestBody?,
//        @Part("LectureId") lectureId: RequestBody?,
////        @Part("FileName") fileName: RequestBody?,
////        @Part file: MultipartBody.Part,
//        @Part("UploadType") uploadType: RequestBody?,
//        @Part("Text") text: RequestBody?,
//        @Part("Duration") duration: RequestBody?,
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_TEXT_CONTENT_UPLOAD
    ): Response<BaseResponse<ImageResponse>>


    @GET
    suspend fun getLectureDetail(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_GET_LECTURE_DETAIL,
        @Query("LectureId") lectureId: Int?,
        @Query("CourseId") courseId: Int?
    ): Response<BaseResponse<ChildModel>>

    @POST
    suspend fun uploadMetadata(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_UPLOAD_METADATA,
        @Body body: Any
    ): Response<BaseResponse<UploadMetaData>>

    @Multipart
    @POST
    suspend fun thumbnailUpload(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_THUMBNAIL_UPLOAD,
        @Part("CourseId") courseId: RequestBody?,
        @Part("SectionId") sectionId: RequestBody?,
        @Part("LectureId") lectureId: RequestBody?,
        @Part("FileName") fileName: RequestBody?,
        @Part file: MultipartBody.Part,
    ): Response<BaseResponse<ImageResponse>>
//sd

    @POST(ApiEndPoints.API_INVITE_COAUTHOR)
    suspend fun inviteCoAuthor(@Body body: Any): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.API_EXISTS_COAUTHOR)
    suspend fun existsCoAuthorDetails(@Query("courseId") courseId: Int?): Response<BaseResponse<ExistsCoAuthorResponse>>

    @POST(ApiEndPoints.API_COAUTHOR_INVITATION)
    suspend fun manageCoAuthorInvitation(@Body body: Any): Response<BaseResponse<UserProfile>>

    @GET(ApiEndPoints.API_HOME_ALL_CATEGORIES)
    suspend fun getHomeAllCategories(): Response<BaseResponse<HomeAllCategoryResponse>>

    @GET(ApiEndPoints.API_HOME_FILTERS)
    suspend fun getHomeFilters(): Response<BaseResponse<FilterResponse>>

    @POST(ApiEndPoints.API_ALL_COURSES)
    suspend fun getAllCourses(
        @Body search: RequestBody?,
    ): Response<BaseResponse<AllCoursesResponse>>

    @POST(ApiEndPoints.API_GUEST_ALL_COURSES)
    suspend fun getAllGuestCourses(
        @Body search: RequestBody?,
    ): Response<BaseResponse<AllCoursesResponse>>

    @POST(ApiEndPoints.API_HOME_COURSES)
    suspend fun homeCourses(@Body body: RequestBody?): Response<BaseResponse<ArrayList<CourseTypeModel>>>

    @POST(ApiEndPoints.API_HOME_GUESTCOURSES)
    suspend fun guestHomeCourses(@Body body: RequestBody?): Response<BaseResponse<ArrayList<CourseTypeModel>>>

    @GET(ApiEndPoints.API_HOME_TAB_CATE)
    suspend fun homeTabCate(): Response<BaseResponse<ArrayList<CategoryData>>>

    @POST(ApiEndPoints.API_WISHLIST)
    suspend fun addWishlist(@Body body: Any): Response<BaseResponse<UserProfile>>

    @GET(ApiEndPoints.API_COURSE_DETAILS)
    suspend fun getCourseDetails(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<CourseData>>

    @GET(ApiEndPoints.API_COURSE)
    suspend fun getCourse(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<CourseData>>


    @GET(ApiEndPoints.API_HOME_WISHLIST)
    suspend fun getWishListData(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<WishListResponse>>

    @GET(ApiEndPoints.API_COURSE_QUIZ)
    suspend fun getQuizData(
        @Query("QuizId") quizId: Int?,
        @Query("CourseId") courseId: Int?
    ): Response<BaseResponse<QuizData>>


    @GET(ApiEndPoints.API_COURSE_SECTIONS)
    suspend fun getViewCourseSections(@Query("CourseId") courseId: Int?): Response<BaseResponse<ArrayList<SectionModel>>>


    @POST(ApiEndPoints.API_PURCHASE_COURSE)
    suspend fun purchaseCourse(@Body body: Any): Response<BaseResponse<OrderData>>


    @POST(ApiEndPoints.API_RAZORPAY_COURSE)
    suspend fun buyRazorPayCourse(@Body body: Any): Response<BaseResponse<OrderData>>

    @GET(ApiEndPoints.API_COURSE_SHARE_URL)
    suspend fun getShareUrl(@Query("courseId") courseId: Int?): Response<BaseResponse<ShareLinkUrl>>


    @POST(ApiEndPoints.API_ALL_COURSES)
    suspend fun getOnGoingCourses(
        @Body body: Any?,
    ): Response<BaseResponse<AllCoursesResponse>>

    @POST(ApiEndPoints.API_ADD_REVIEW)
    suspend fun getAddReview(@Body data: AddReviewRequestModel): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.API_GET_REVIEW)
    suspend fun getReviewList(@Body data: GetReviewsRequestModel): Response<BaseResponse<ReviewResponse>>

    @POST(ApiEndPoints.API_REWARDS_POINTS)
    suspend fun getRewardList(@Body data: GetReviewsRequestModel): Response<BaseResponse<RewardPointResponse>>

    @POST(ApiEndPoints.API_SUBMIT_COURSE_QUIZ)
    suspend fun submitTestQuiz(
        @Body data: RequestBody?,
    ): Response<BaseResponse<QuizReportData>>


    @GET(ApiEndPoints.API_REVIEW_FILTERS)
    suspend fun getReviewFilters(@Query("courseId") courseId: String): Response<BaseResponse<FilterResponse>>

    @GET(ApiEndPoints.API_CERTIFICATE)
    suspend fun getCertificate(@Query("courseId") courseId: String): Response<BaseResponse<UserProfile>>

    @GET(ApiEndPoints.API_APP_CERTIFICATE)
    suspend fun getAppCertificate(@Query("courseId") courseId: String): Response<BaseResponse<UserProfile>>

    @GET(ApiEndPoints.API_ASSESSMENT_DETAIL)
    suspend fun assessmentdetail(@Query("courseId") courseId: String): Response<BaseResponse<ContentAssessmentData>>

    @GET(ApiEndPoints.API_ASSESSMENT)
    suspend fun assessmentList(@Query("courseId") courseId: Int): Response<BaseResponse<QuizData>>

    @POST(ApiEndPoints.API_ASSESSMENT_SUBMIT)
    suspend fun assessmentListSubmit(@Body data: RequestBody?): Response<BaseResponse<QuizReportData>>

    @GET(ApiEndPoints.API_ASSESSMENT_REPORT)
    suspend fun assessmentReport(
        @Query("attemptedId") attemptedId: String,
        @Query("CourseId") courseId: Int
    ): Response<BaseResponse<AssessmentReportData>>

    @GET(ApiEndPoints.API_ASSESSMENT_REPORT_STATUS)
    suspend fun assessmentReportStatus(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<QuizData>>

    @GET(ApiEndPoints.API_QUIZ_REPORT)
    suspend fun quizReport(
        @Query("attemptedId") attemptedId: String,
        @Query("CourseId") courseId: Int
    ): Response<BaseResponse<AssessmentReportData>>

    @GET(ApiEndPoints.API_QUIZ_REPORT_STATUS)
    suspend fun quizReportStatus(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<QuizData>>


    @GET(ApiEndPoints.API_SWITCH_TO_MOD)
    suspend fun switchToMod(@Query("userMode") userMode: Int): Response<BaseResponse<UserProfile>>

    @GET(ApiEndPoints.API_MOD_STATUS)
    suspend fun modStatus(): Response<BaseResponse<ModeratorStatusResponses>>

    @GET(ApiEndPoints.API_REQUEST_COUNT)
    suspend fun requestCount(): Response<BaseResponse<RequestCountModel>>

    @GET(ApiEndPoints.API_REQUEST_LIST)
    suspend fun requestList(@QueryMap data: HashMap<String, Any>): Response<BaseResponse<WishListResponse>>

    @POST(ApiEndPoints.API_COURSE_REQUEST)
    suspend fun courseRequest(@Body data: GetReviewsRequestModel): Response<BaseResponse<WishListResponse>>

    @POST(ApiEndPoints.API_MOD_COURSES)
    suspend fun modCourse(@Body data: GetReviewsRequestModel): Response<BaseResponse<WishListResponse>>

    @POST(ApiEndPoints.API_UPDATE_MOD_REQUEST)
    suspend fun updateCourseRequest(@Body body: Any): Response<BaseResponse<CourseData>>

    @POST(ApiEndPoints.API_CANCEL_REQ)
    suspend fun cancelReq(@Body body: Any): Response<BaseResponse<UserProfile>>

    @POST(ApiEndPoints.API_COMMENT_SECTION)
    suspend fun addCommentSection(@Body body: Any): Response<BaseResponse<SectionModel>>

    @PATCH(ApiEndPoints.API_COMMENT_SECTION)
    suspend fun editCommentSection(@Body body: Any): Response<BaseResponse<SectionModel>>

    @DELETE(ApiEndPoints.API_COMMENT_SECTION)
    suspend fun deleteCommentSection(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<SectionModel>>

    @POST(ApiEndPoints.API_COMMENT_MISC)
    suspend fun addCommentMisc(@Body body: Any): Response<BaseResponse<CourseComments>>

    @PATCH(ApiEndPoints.API_COMMENT_MISC)
    suspend fun editCommentMisc(@Body body: Any): Response<BaseResponse<CourseComments>>

    @DELETE(ApiEndPoints.API_COMMENT_MISC)
    suspend fun deleteCommentMisc(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<CourseComments>>


    @POST(ApiEndPoints.API_COMMENT_LECTURE)
    suspend fun addCommentLecture(@Body body: Any): Response<BaseResponse<ChildModel>>

    @PATCH(ApiEndPoints.API_COMMENT_LECTURE)
    suspend fun editCommentLecture(@Body body: Any): Response<BaseResponse<ChildModel>>

    @DELETE(ApiEndPoints.API_COMMENT_LECTURE)
    suspend fun deleteCommentLecture(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<ChildModel>>


    @POST(ApiEndPoints.API_UPDATE_MOD_COURSE_STATUS)
    suspend fun updateCourseStatus(@Body body: Any): Response<BaseResponse<CourseData>>

    @GET(ApiEndPoints.API_MOD_DASHBOARD_STAT_COUNT)
    suspend fun modDashboardStatCount(): Response<BaseResponse<ModDashboardStatCountModel>>

    @POST(ApiEndPoints.API_MOD_DASHBOARD_COURSES_LIST)
    suspend fun getModDashboardCoursesList(
        @Body body: GetReviewsRequestModel?,
    ): Response<BaseResponse<ModDashboardData>>


    @POST(ApiEndPoints.API_MOD_DASHBOARD_FILTERED_COUNT)
    suspend fun getFiltedModDashboardCount(
        @Body body: GetReviewsRequestModel?,
    ): Response<BaseResponse<ModDashboardStatCountModel>>

    @POST(ApiEndPoints.API_LEARNER_DASHBOARD_FILTERED_COUNT)
    suspend fun getFiltedLearnerDashboardCount(
        @Body body: GetReviewsRequestModel?,
    ): Response<BaseResponse<LearnerDashboardFilteredStatCountModel>>

    @GET(ApiEndPoints.API_LEARNER_DASHBOARD_STAT_COUNT)
    suspend fun learnerDashboardStatCount(): Response<BaseResponse<LearnerDashboardStatCountModel>>

    @POST(ApiEndPoints.API_LEARNER_DASHBOARD_COURSES_LIST)
    suspend fun getLearnerDashboardCoursesList(
        @Body body: GetReviewsRequestModel?,
    ): Response<BaseResponse<LearnerDashboardData>>


    @GET(ApiEndPoints.API_DOC_DOWNLOAD)
    suspend fun myDocs(): Response<BaseResponse<ArrayList<DocModelItem>>>

    @GET(ApiEndPoints.API_MOD_MY_CATEGORIES)
    suspend fun modMyCategories(): Response<BaseResponse<ModeratorMyCategories>>

    @PATCH(ApiEndPoints.API_COURSE_UPDATE)
    suspend fun updateCourseMod(@Body map: HashMap<String, Any>): Response<BaseResponse<UserProfile>>


    @GET(ApiEndPoints.API_DETAIL_CREATOR)
    suspend fun getDetailCreator(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<CourseData>>

    @GET(ApiEndPoints.API_DETAIL_REQUEST_TRACKER)
    suspend fun getDetailRequestTracker(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<CourseData>>


    @GET(ApiEndPoints.API_SECTION_AND_LECTURE)
    suspend fun getSectionLecture(@Query("CourseId") courseId: Int): Response<BaseResponse<ArrayList<SectionModel>>>

    @GET(ApiEndPoints.API_SECTION_AND_LECTURE_REQUEST_TRACKER)
    suspend fun getSectionLectureForRequestTracker(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<ArrayList<SectionModel>>>


    @GET
    suspend fun editToDraft(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_EDIT_TO_DRAFT,
        @Query("courseId") courseId: Int
    ): Response<BaseResponse<UserProfile>>


    @GET(ApiEndPoints.API_MOD_COURSE_DETAIL)
    suspend fun getModCourse(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<CourseData>>


    @POST(ApiEndPoints.API_CHANGE_VI_MODE)
    suspend fun changeViModeStatus(@Body data: ChangeViModeModel): Response<BaseResponse<UserProfile>>

    @GET(ApiEndPoints.API_NOTIFICATION_COUNT)
    suspend fun getNotificationCount(): Response<BaseResponse<NotificationData>>

    @GET(ApiEndPoints.API_GET_NOTIFICATION)
    suspend fun getNotification(): Response<BaseResponse<NotificationModel>>

    @PATCH(ApiEndPoints.API_PATCH_NOTIFICATION)
    suspend fun patchNotification(@Query("NotificationId") notificationId: Int): Response<BaseResponse<NotificationData>>

    @DELETE(ApiEndPoints.API_DELETE_NOTIFICATION)
    suspend fun deleteNotification(@Query("NotificationId") notificationId: Int): Response<BaseResponse<NotificationData>>

    @PATCH(ApiEndPoints.API_NOTIFICATION_READ_ALL)
    suspend fun readAllNotification(): Response<BaseResponse<NotificationData>>

    @DELETE(ApiEndPoints.API_NOTIFICATION_DELETE_ALL)
    suspend fun deleteAllNotification(): Response<BaseResponse<NotificationData>>


    @GET(ApiEndPoints.API_GET_REVENUE_LIST)
    suspend fun getRevenueList(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<RevenueDataModel>>

    @GET(ApiEndPoints.API_COURSE_AUTHOR_DETAIL)
    suspend fun getAuthorDEtails(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<AuthorDetailsData>>

    @GET(ApiEndPoints.API_COURSE_COAUTHOR_DETAIL)
    suspend fun getCoAuthorDEtails(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<AuthorDetailsData>>

    @GET(ApiEndPoints.API_PAYMENT_REMAINING_WALLET_BALANCE)
    suspend fun remainingWalletBalance(): Response<BaseResponse<RemainingWalletBalanceData>>

    @GET(ApiEndPoints.API_PAYMENT_USER_EARNINGS)
    suspend fun getUserEarnings(
        @Query("pageNumber") number: Int,
        @Query("pageSize") size: Int
    ): Response<BaseResponse<MyPaymentEarningDataModel>>

    @POST(ApiEndPoints.API_PAYMENT_USER_PURCHASES)
    suspend fun getUserPurchases(@Body map: HashMap<String, Any>): Response<BaseResponse<MyPurchaseDataModel>>

    @GET(ApiEndPoints.API_GET_DETAIL_OF_CONTENT)
    suspend fun getContentDetails(@QueryMap hashMap: HashMap<String, Int>): Response<BaseResponse<ChildModel>>

    @GET(ApiEndPoints.API_PAYMENT_COURSE_DETAILS)
    suspend fun getPurchasedCourseDetail(@Query("courseId") courseId: Int): Response<BaseResponse<PurchaseDetailDataModel>>


    @GET(ApiEndPoints.API_GET_DETAIL_OF_CONTENT_MODE_CREATOR)
    suspend fun getContentDetailsCreator(@QueryMap hashMap: HashMap<String, Int>): Response<BaseResponse<ChildModel>>

    @GET(ApiEndPoints.API_CREATOR_DASHBOARD_AUDIENCE_STAT_COUNT)
    suspend fun audiencedStatCount(): Response<BaseResponse<CreatorAudienceStateCount>>

    @POST(ApiEndPoints.API_SUPPORT)
    suspend fun postSupport(@Body body: Any): Response<BaseResponse<UserProfile>>


    @DELETE
    suspend fun deleteCourse(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_CRE_STEP_1,
        @Query("CourseId") courseId: Int
    ): Response<BaseResponse<UserProfile>>


    @POST(ApiEndPoints.API_CREATOR_DASHBOARD_COURSE_USER_COUNT)
    suspend fun userCourseCountCreatorDash(
        @Body body: GetReviewsRequestModel?,
    ): Response<BaseResponse<CreatorCourseUserCount>>

    @POST(ApiEndPoints.API_CREATOR_DASHBOARD_COURSE_USER_COUNT_FILTERED)
    suspend fun userCourseCountWithFilterCreatorDash(
        @Body body: GetReviewsRequestModel?,
    ): Response<BaseResponse<CreatorCourseUserCountWithFilter>>


    @POST(ApiEndPoints.API_CREATOR_DASHBOARD_TOTAL_EARNING)
    suspend fun creatorDashTotalEarning(
        @Body body: GetReviewsRequestModel?,
    ): Response<BaseResponse<CreatorTotalEarnings>>

//    @GET(ApiEndPoints.API_STATIC)
//    suspend fun static(@Query("staticContentType") staticContentType: Int): Response<BaseResponse<StaticUrlModel>>
//
//    @GET(ApiEndPoints.API_STATIC_FAQ)
//    suspend fun static_faq(): Response<BaseResponse<StaticUrlModel>>

    @DELETE(ApiEndPoints.API_UPLOAD_IMAGE)
    suspend fun deleteProfileImage(): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.API_HOME_SEARCH)
    suspend fun getElasticSearchData(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<ElasticSearchData>>

    @GET(ApiEndPoints.API_AMOUNT_WITH_GST)
    suspend fun getAmountWithGst(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<AmountWithGSTModel>>

    @POST
    suspend fun getCompleteStatus(
        @Url url: String = BuildConfig.API_BASE_COURSE_URL + ApiEndPoints.API_COMPLETE_STATUS,
        @Body body: Any
    ): Response<BaseResponse<UserProfile>>


    @POST(ApiEndPoints.API_ADD_BANK_ACCOUNT)
    suspend fun addBankAccount(
        @Body body: HashMap<String, Any>?,
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.API_MIN_WITHDRAW_AMOUNT)
    suspend fun getMinWithdrawAmount(): Response<BaseResponse<MinAmountRequestModel>>

    @POST(ApiEndPoints.API_WITHDRAW_REQUEST)
    suspend fun getPaymentWithdrawRequest(
        @Body body: HashMap<String, Any>?,
    ): Response<BaseResponse<Any>>


    @POST(ApiEndPoints.API_WALLET_HISTORY)
    suspend fun walletHistory(
        @Body map: HashMap<String, Any>,
    ): Response<BaseResponse<WalletDataModel>>


    @GET(ApiEndPoints.API_GET_BANK_ACCOUNT_LISTING)
    suspend fun getListingOfBankAccount(): Response<BaseResponse<ArrayList<BankDetails>>>


    @PATCH(ApiEndPoints.API_MAKE_ACCOUNT_PRIMARY)
    suspend fun sendBankAccountPrimaryRequest(@QueryMap hashMap: HashMap<String, Any>): Response<BaseResponse<Any>>


    @DELETE(ApiEndPoints.API_ADD_BANK_ACCOUNT)
    suspend fun deleteBankAccount(
        @QueryMap body: HashMap<String, Any>?,
    ): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.API_PAYMENT_WITHDRAW_LIST)
    suspend fun paymentWithdrawList(
        @Body map: HashMap<String, Any>,
    ): Response<BaseResponse<PaymentWithdrawData>>

    @GET(ApiEndPoints.API_MA_BANK_COUNT)
    suspend fun getMaxBankCount(): Response<BaseResponse<MinAmountRequestModel>>


    @GET(ApiEndPoints.API_CREATOR_DASHBOARD_TOTAL_ACTIVITY_TIME)
    suspend fun getActivityTimeTracer(
        @Query("StartDate") StartDate: String,
        @Query("EndDate") EndDate: String
    ): Response<BaseResponse<ActivityHoursModel>>


    @POST(ApiEndPoints.API_REPORT_COMMENT)
    suspend fun reportComment(
        @Body map: HashMap<String, Any>,
    ): Response<BaseResponse<CourseData>>

    @PATCH(ApiEndPoints.API_COURSE_POLICY_STATUS)
    suspend fun coursePolicyStatus(
        @Body map: HashMap<String, Any>,
    ): Response<BaseResponse<UserProfile>>


    @POST(ApiEndPoints.API_USER_COURSE_CONTENT_HISTORY)
    suspend fun sendAudioVideoFinalTime(
        @Body map: HashMap<String, Any>,
    ): Response<BaseResponse<CourseData>>


}


