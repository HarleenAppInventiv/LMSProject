package com.selflearningcoursecreationapp.data.network

import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.models.*
import com.selflearningcoursecreationapp.models.course.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizReportData
import com.selflearningcoursecreationapp.models.masterData.MasterDataItem
import com.selflearningcoursecreationapp.models.rewardPoints.RewardPointResponse
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.FilterResponse
import com.selflearningcoursecreationapp.ui.course_details.model.AddReviewRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.moderator.model.AddModeratorResponse
import com.selflearningcoursecreationapp.ui.moderator.model.RequestCountModel
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
    suspend fun deleteAccount(): Response<BaseResponse<UserResponse>>

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

    @POST(ApiEndPoints.API_CRE_STEP_1)
    suspend fun addCourseStep1(@Body body: Any): Response<BaseResponse<CourseData>>

    @PATCH(ApiEndPoints.API_CRE_STEP_1)
    suspend fun updateCourseStep1(@Body body: Any): Response<BaseResponse<CourseData>>

    @POST(ApiEndPoints.API_CRE_STEP_2)
    suspend fun addCourseStep2(@Body body: Any): Response<BaseResponse<CourseData>>

    @POST(ApiEndPoints.API_ADD_MODERATOR)
    suspend fun addModerator(@Body body: AddModeratorResponse): Response<BaseResponse<ChildModel>>

    @GET(ApiEndPoints.API_CRE_STEP_1)
    suspend fun getCourseDetail(@Query("CourseId") courseId: Int): Response<BaseResponse<CourseData>>

    @GET(ApiEndPoints.API_GET_SECTIONS)
    suspend fun getCourseSections(@Query("CourseId") courseId: Int): Response<BaseResponse<CourseData>>

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
    @POST(ApiEndPoints.API_UPLOAD_BANNER)
    suspend fun uploadCourseBanner(
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody,
        @Part("CourseId") courseId: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST(ApiEndPoints.API_UPLOAD_LOGO)
    suspend fun uploadCourseLogo(
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody,
        @Part("CourseId") courseId: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST(ApiEndPoints.API_ADD_QUIZ_IMAGE)
    suspend fun uploadQuizImage(
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody?,
        @Part("CourseId") courseId: RequestBody?,
        @Part("SectionId") sectionId: RequestBody?,
        @Part("QuizId") quizId: RequestBody?,
        @Part("LectureId") lectureId: RequestBody?,
        @Part("ImageTypeId") imageTypeId: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST(ApiEndPoints.API_ADD_ASSESSMENT_IMAGE)
    suspend fun uploadAssessmentImage(
        @Part file: MultipartBody.Part,
        @Part("FileName") fileName: RequestBody?,
        @Part("CourseId") courseId: RequestBody?,
        @Part("AssessmentId") assessmentId: RequestBody?,
        @Part("ImageTypeId") imageTypeId: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @POST(ApiEndPoints.API_ADD_QUIZ)
    suspend fun addQuiz(@Body body: QuizData): Response<BaseResponse<QuizData>>

    @POST(ApiEndPoints.API_ADD_ASSESSMENT)
    suspend fun addAssessment(@Body courseId: RequestBody?): Response<BaseResponse<QuizData>>

    @POST(ApiEndPoints.API_ADD_QUIZ_SAVE)
    suspend fun saveQuiz(@Body body: QuizData?): Response<BaseResponse<ChildModel>>

    @POST(ApiEndPoints.API_ADD_ASSESSMENT_SAVE)
    suspend fun saveAssessment(@Body body: QuizData?): Response<BaseResponse<ChildModel>>

    @POST(ApiEndPoints.API_ADD_QUIZ_QUESTION)
    suspend fun addQuizQues(@Body body: QuizQuestionData?): Response<BaseResponse<QuizQuestionData>>

    @POST(ApiEndPoints.API_ADD_ASSESSMENT_QUESTION)
    suspend fun addAssessmentQues(@Body body: QuizQuestionData?): Response<BaseResponse<QuizQuestionData>>

    @PATCH(ApiEndPoints.API_ADD_QUIZ_QUESTION)
    suspend fun updateQuizQues(@Body body: QuizQuestionData?): Response<BaseResponse<QuizQuestionData>>

    @PATCH(ApiEndPoints.API_ADD_ASSESSMENT_QUESTION)
    suspend fun updateAssessmentQues(@Body body: QuizQuestionData?): Response<BaseResponse<QuizQuestionData>>

    @DELETE(ApiEndPoints.API_ADD_QUIZ_QUESTION)
    suspend fun deleteQuizQues(
        @Query("courseId") courseId: Int?,
        @Query("sectionId") sectionId: Int?,
        @Query("lectureId") lectureId: Int?,
        @Query("quizId") quizId: Int?,
        @Query("questionId") questionId: Int?,
    ): Response<BaseResponse<Any>>

    @DELETE(ApiEndPoints.API_ADD_ASSESSMENT_QUESTION)
    suspend fun deleteAssessmentQues(
        @Query("courseId") courseId: Int?,
        @Query("AssessmentId") sectionId: Int?,
        @Query("questionId") questionId: Int?,
    ): Response<BaseResponse<Any>>

    @GET(ApiEndPoints.API_ADD_QUIZ)
    suspend fun getQuizQues(

        @Query("quizId") quizId: Int?,
    ): Response<BaseResponse<QuizData>>

    @GET(ApiEndPoints.API_ADD_ASSESSMENT)
    suspend fun getAssessmentQues(

        @Query("AssessmentId") quizId: Int?,
    ): Response<BaseResponse<QuizData>>

    @DELETE(ApiEndPoints.API_ADD_ASSESSMENT)
    suspend fun deleteAssessment(

        @Query("AssessmentId") assessmentId: Int?,
        @Query("CourseId") courseId: Int?,
    ): Response<BaseResponse<QuizData>>


    @POST(ApiEndPoints.API_ADD_QUIZ_ANS)
    suspend fun saveQuesAns(@Body body: RequestBody?): Response<BaseResponse<Any>>


    @POST(ApiEndPoints.API_ADD_ASSESSMENT_ANS)
    suspend fun saveAssessmentAns(@Body body: RequestBody?): Response<BaseResponse<Any>>


    @POST(ApiEndPoints.API_ADD_SECTION)
    suspend fun addSection(@Body body: Any): Response<BaseResponse<SectionModel>>

    @PATCH(ApiEndPoints.API_ADD_SECTION)
    suspend fun addPatchSection(@Body body: Any): Response<BaseResponse<SectionModel>>

    @DELETE(ApiEndPoints.API_ADD_SECTION)
    suspend fun deleteSection(
        @Query("CourseId") courseId: String,
        @Query("SectionId") sectionId: String,
    ): Response<BaseResponse<SectionModel>>

    @POST(ApiEndPoints.API_SECTION_DRAG_DROP)
    suspend fun dragAndDropSection(@Body body: Any): Response<BaseResponse<UserProfile>>

    @POST(ApiEndPoints.API_PUBLISH_COURSE)
    suspend fun publishCourse(@Body body: Any?): Response<BaseResponse<CourseData>>

    @POST(ApiEndPoints.API_GET_KEYWORDS)
    suspend fun getKeywords(@Body body: Any?): Response<BaseResponse<SingleClickResponse>>

    @POST(ApiEndPoints.API_ADD_LECTURE_POST)
    suspend fun addLecture(@Body body: Any): Response<BaseResponse<ChildModel>>

    @PATCH(ApiEndPoints.API_ADD_LECTURE_PATCH)
    suspend fun addPatchLecture(@Body body: Any): Response<BaseResponse<ChildModel>>

    @DELETE(ApiEndPoints.API_LECTURE_DELETE)
    suspend fun deleteLecture(
        @Query("CourseId") courseId: String,
        @Query("SectionId") sectionId: String,
        @Query("LectureId") lectureId: String,
    ): Response<BaseResponse<UserProfile>>

    @POST(ApiEndPoints.API_LECTURE_DRAG_DROP)
    suspend fun dragAndDropLecture(@Body body: Any): Response<BaseResponse<UserProfile>>

    @Multipart
    @POST(ApiEndPoints.API_CONTENT_UPLOAD)
    suspend fun contentUpload(
        @Part("CourseId") courseId: RequestBody?,
        @Part("SectionId") sectionId: RequestBody?,
        @Part("LectureId") lectureId: RequestBody?,
        @Part("FileName") fileName: RequestBody?,
        @Part file: MultipartBody.Part,
        @Part("UploadType") uploadType: RequestBody?,
        @Part("Duration") duration: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>

    @Multipart
    @POST(ApiEndPoints.API_CONTENT_UPLOAD)
    suspend fun contentUploadText(
//        @Body body: Any,
        @Part("CourseId") courseId: RequestBody?,
        @Part("SectionId") sectionId: RequestBody?,
        @Part("LectureId") lectureId: RequestBody?,
//        @Part("FileName") fileName: RequestBody?,
//        @Part file: MultipartBody.Part,
        @Part("UploadType") uploadType: RequestBody?,
        @Part("Text") text: RequestBody?,
        @Part("Duration") duration: RequestBody?,
    ): Response<BaseResponse<ImageResponse>>


    @GET(ApiEndPoints.API_GET_LECTURE_DETAIL)
    suspend fun getLectureDetail(
        @Query("LectureId") lectureId: Int?,
    ): Response<BaseResponse<ChildModel>>


    @Multipart
    @POST(ApiEndPoints.API_THUMBNAIL_UPLOAD)
    suspend fun thumbnailUpload(
        @Part("CourseId") courseId: RequestBody?,
        @Part("SectionId") sectionId: RequestBody?,
        @Part("LectureId") lectureId: RequestBody?,
        @Part("FileName") fileName: RequestBody?,
        @Part file: MultipartBody.Part,
    ): Response<BaseResponse<ImageResponse>>
//sd

    @POST(ApiEndPoints.API_INVITE_COAUTHOR)
    suspend fun inviteCoAuthor(@Body body: Any): Response<BaseResponse<Any>>

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
    suspend fun getCourseDetails(@Query("CourseId") courseId: Int?): Response<BaseResponse<CourseData>>

    @GET(ApiEndPoints.API_HOME_WISHLIST)
    suspend fun getWishListData(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<WishListResponse>>

    @GET(ApiEndPoints.API_COURSE_QUIZ)
    suspend fun getQuizData(@Query("QuizId") quizId: Int?): Response<BaseResponse<QuizData>>


    @GET(ApiEndPoints.API_COURSE_SECTIONS)
    suspend fun getViewCourseSections(@Query("CourseId") courseId: Int?): Response<BaseResponse<ArrayList<SectionModel>>>


    @POST(ApiEndPoints.API_PURCHASE_COURSE)
    suspend fun purchaseCourse(@Body body: Any): Response<BaseResponse<OrderData>>


    @POST(ApiEndPoints.API_RAZORPAY_COURSE)
    suspend fun buyRazorPayCourse(@Body body: Any): Response<BaseResponse<OrderData>>


    @POST(ApiEndPoints.API_ALL_COURSES)
    suspend fun getOnGoingCourses(
        @Body body: Any?,
    ): Response<BaseResponse<AllCoursesResponse>>

    @POST(ApiEndPoints.API_ADD_REVIEW)
    suspend fun getAddReview(@Body data: AddReviewRequestModel): Response<BaseResponse<Any>>

    @POST(ApiEndPoints.API_GET_REVIEW)
    suspend fun getReviewList(@Body data: GetReviewsRequestModel): Response<BaseResponse<WishListResponse>>

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

    @GET(ApiEndPoints.API_ASSESSMENT_DETAIL)
    suspend fun assessmentdetail(@Query("courseId") courseId: String): Response<BaseResponse<ContentAssessmentData>>

    @GET(ApiEndPoints.API_ASSESSMENT)
    suspend fun assessmentList(@Query("courseId") courseId: String): Response<BaseResponse<QuizData>>

    @POST(ApiEndPoints.API_ASSESSMENT_SUBMIT)
    suspend fun assessmentListSubmit(@Body data: RequestBody?): Response<BaseResponse<QuizReportData>>

    @GET(ApiEndPoints.API_ASSESSMENT_REPORT)
    suspend fun assessmentReport(@Query("attemptedId") attemptedId: String): Response<BaseResponse<AssessmentReportData>>

    @GET(ApiEndPoints.API_ASSESSMENT_REPORT_STATUS)
    suspend fun assessmentReportStatus(@QueryMap map: HashMap<String, Any>): Response<BaseResponse<QuizData>>

    @GET(ApiEndPoints.API_SWITCH_TO_MOD)
    suspend fun switchToMod(@Query("userMode") userMode: Int): Response<BaseResponse<UserProfile>>

    @GET(ApiEndPoints.API_MOD_STATUS)
    suspend fun modStatus(): Response<BaseResponse<UserProfile>>

    @GET(ApiEndPoints.API_REQUEST_COUNT)
    suspend fun requestCount(): Response<BaseResponse<RequestCountModel>>

    @GET(ApiEndPoints.API_REQUEST_LIST)
    suspend fun requestList(@QueryMap map: HashMap<String, Int>): Response<BaseResponse<WishListResponse>>

    @POST(ApiEndPoints.API_COURSE_REQUEST)
    suspend fun courseRequest(@Body map: HashMap<String, Int>): Response<BaseResponse<WishListResponse>>

    @POST(ApiEndPoints.API_MOD_COURSES)
    suspend fun modCourse(@Body map: HashMap<String, Int>): Response<BaseResponse<WishListResponse>>
}

