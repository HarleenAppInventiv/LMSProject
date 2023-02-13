package com.selflearningcoursecreationapp.utils

import com.selflearningcoursecreationapp.BuildConfig

object ApiEndPoints {
    //    const val UPLOAD_FILE_URL =
//        "https://api-dev.skillfy.in/api/Course/Section/Lecture/upload-content"
//  const val UPLOAD_FILE_URL =
//    "https://api-uat.skillfy.in/creator/data/api/Course/Section/Lecture/upload-content"
//
    const val APP_CERTIFICATE =
        "https://skillfystorageprod.blob.core.windows.net/skillfy-prod/AppContent/Certificates/certificate_to_achieve.pdf"

    const val COM_CERTIFICATE =
        "https://skillfystorageprod.blob.core.windows.net/skillfy-prod/AppContent/Certificates/certificate_to_complete.pdf"
    const val UPLOAD_FILE_URL =
        "https://creator-api.skillfy.in/api/Course/Section/Lecture/upload-content"
//    const val UPLOAD_FILE_URL =
//      "https://api-qa.skillfy.in/creator/data/api/Course/Section/Lecture/upload-content"


//    const val UPLOAD_FILE_URL =
//        "https://api-qa.skillfy.in/creator/api/Course/Section/Lecture/upload-content"

    //    const val UPLOAD_FILE_URL =
//        "https://a256-101-0-38-98.in.ngrok.io/api/Course/Section/Lecture/upload-content"
    const val HOME_SUCCESS = "home_success"
    const val DIALOG_SUCCESS = "dialog_success"
    const val LINK_TERM_COND = "terms-&-conditions"
    const val LINK_PRIVACY_POL = "privacy-policy"
    const val LINK_ABOUT_US = "about-us"
    const val LINK_FAQ = "faq"
    const val LINK_CONTACT_US = "contact-us"
    const val PRIVACY_P = "https://privacy-policy.skillfy.in"

    // ws
    const val VALID_DATA: String = "valid_Data"
    const val GUEST_LOGIN: String = "guest_login"


    //    const val BASE_URL = "https://appinventivlearningclientapi20220314151937.azurewebsites.net/Api/" //dev server
//    const val BASE_URL = "https://appinventivlearningclientapiqa.azurewebsites.net/Api/" //qa server
//    const val BASE_URL = "http://20.197.6.132/api/" //qa server
//    const val BASE_URL = "https://api-qa.skillfy.in/Api/" //qa server
//  const val BASE_URL = "https://api-uat.skillfy.in/learner/Api/" //qa server
//  const val BASE_URL = "https://api-qa.skillfy.in/learner/Api/" //qa server
//  const val BASE_URL = "http://20.193.146.59:5000/Api/" //qa server
    const val BASE_URL = "https://learner-api.skillfy.in/Api/" //production server

    //    const val BASE_URL = "https://api-qa.skillfy.in/learner/Api/" //qa server
//    const val BASE_URL_COURSE = "https://api-dev.skillfy.in/api/" //course server
//    const val BASE_URL_COURSE = "https://api-uat.skillfy.in/creator/api/" //course server
//    const val BASE_URL_COURSE = "https://skillfy.in/creator/api/" //course server
//  const val BASE_URL_COURSE = "https://api-uat.skillfy.in/creator/api/" //course server
//  const val BASE_URL_COURSE = "https://api-qa.skillfy.in/creator/api/" //course  qa server
//  const val BASE_URL_COURSE = "http://20.219.145.188:6050/api/" //course  qa server
    const val BASE_URL_COURSE = "https://creator-api.skillfy.in/api/" // production server

    //    const val BASE_URL = "https://e4be-122-179-201-74.in.ngrok.io/Api/" //qa server
    const val API_SIGNUP = "SignUp/SignUpRequest"
    const val API_OTP_REQ = "SignUp/OtpRequest"
    const val API_OTP_VAL = "SignUp/ValidateOtpRequest"
    const val API_LOGIN = "Login"
    const val API_GET_CATEGORIES = "Category/GetCategoryList"
    const val API_MY_CATEGORIES = "Category/MyCategories"
    const val API_SAVE_PREFERENCES = "Preferences/SavePreferences"
    const val API_GET_THEME_LIST = "Themes/GetThemesList"
    const val API_VIEW_PROFILE = "User/ViewProfile"
    const val API_CHANGE_PASS = "User/ChangePasswordRequest"
    const val API_RESET_PASS = "User/ResetPassword"
    const val API_UPDATE_PROFILE = "User/UpdateProfile"
    const val API_DELETE_ACCOUNT = "User/Delete"
    const val API_LOGOUT = "User/Logout"
    const val API_PROFESSION = "Profession/GetProfessionsList"
    const val API_GET_ALL_STATES = "Master/GetAllStates"
    const val API_GET_ALL_CITY = "Master/GetCitiesByStateId"
    const val API_ADD_PASSWORD = "User/AddPassword"
    const val API_ADD_EMAIL = "SignUp/OtpRequestForEmailChange"
    const val API_VERIFY_EMAIL = "SignUp/ValidateOtpRequestForEmailChange"
    const val API_UPLOAD_IMAGE = "User/ProfilePicture"
    const val API_ADD_MODERATOR = "Moderator"
    const val API_UPLOAD_MODERATOR_DOC = "$API_ADD_MODERATOR/UploadDocuments"

    //course
    const val API_CRE_STEP_1 = "Course"
    const val API_CRE_STEP_2 = "Course/Details"
    const val API_MASTER_DATA = "Master/GetMasterData"
    const val API_UPLOAD_BANNER = "Course/Banner"
    const val API_UPLOAD_LOGO = "Course/Logo"
    const val API_PUBLISH_COURSE = "Course/Submit"
    const val API_GET_KEYWORDS = "Course/SearchKeyword"
    const val API_GET_SECTIONS = "Course/AllSections"
    const val API_GET_REVENUE_LIST = "Revenue"
    const val API_COMPLETE_STATUS = "Course/UpdateCourseCompletedStatus"

    //section
    const val API_ADD_SECTION = "Course/Section"
    const val API_SECTION_DRAG_DROP = "Course/Section/DragAndDrop"
    const val API_REWARDS_POINTS = "Rewards/Courses"

    //Lecture
    const val API_ADD_LECTURE_POST = "Course/Section/Lecture"
    const val API_ADD_LECTURE_PATCH = "Course/Section/Lecture"
    const val API_LECTURE_DELETE = "Course/Section/Lecture"
    const val API_LECTURE_DRAG_DROP = "Course/Section/Lecture/DragAndDrop"
    const val API_GET_LECTURE_DETAIL = "Course/Section/LectureDetail"

    //quiz
    const val API_ADD_QUIZ = "Course/Section/Lecture/quiz"
    const val API_ADD_QUIZ_QUESTION = "Course/Section/Lecture/quiz/Questions"
    const val API_ADD_QUIZ_ANS = "Course/Section/Lecture/quiz/Questions/Answeres"
    const val API_ADD_QUIZ_IMAGE = "Course/Section/Lecture/quiz/Image"
    const val API_ADD_QUIZ_SAVE = "Course/Section/Lecture/SaveQuiz"

    //assessment
    const val API_ADD_ASSESSMENT = "Course/Assessment"
    const val API_ADD_ASSESSMENT_QUESTION = "Course/Assessment/Questions"
    const val API_ADD_ASSESSMENT_ANS = "Course/Assessment/Questions/Answeres"
    const val API_ADD_ASSESSMENT_IMAGE = "Course/Assessment/Image"
    const val API_ADD_ASSESSMENT_SAVE = "Course/Assessment/SaveAssessment"

    //Content upload
    const val API_CONTENT_UPLOAD = "Course/Section/Lecture/UploadCourseContent"
    const val API_TEXT_CONTENT_UPLOAD = "Course/Section/Lecture/UploadTextContent"
    const val API_THUMBNAIL_UPLOAD = "Course/Section/Lecture/UploadThumbnail"
    const val API_UPLOAD_METADATA = "Course/Section/Lecture/upload-metadata"

    //coAuthor
    const val API_INVITE_COAUTHOR = "CoAuthor/AddCoAuthor"
    const val API_EXISTS_COAUTHOR = "CoAuthor/ExistCoAuthorDetails"

    //CoAuthor
    const val API_COAUTHOR_INVITATION = "CoAuthor/UpdateInvitationStatus"
    const val API_REQUEST_COUNT = "CoAuthor/RequestCount"
    const val API_REQUEST_LIST = "CoAuthor/RequestList"
    const val API_CANCEL_REQ = "CoAuthor/CancelCoAuthorRequest"


    // Guest home
    const val API_HOME_GUESTCOURSES = "HomeScreen/GuestCourses"
    const val API_HOME_COURSES = "HomeScreen/Courses"
    const val API_HOME_TAB_CATE = "HomeScreen/TabCategories"
    const val API_HOME_SEARCH = "HomeScreen/search"


    const val API_HOME_ALL_CATEGORIES = "HomeScreen/AllCategories"

    const val API_HOME_WISHLIST = "Wishlist/WishlistedCourses"
    const val API_HOME_FILTERS = "HomeScreen/Filters"
    const val API_ALL_COURSES = "HomeScreen/AllCourses"
    const val API_GUEST_ALL_COURSES = "HomeScreen/AllGuestCourses"

    //course details
    const val API_COURSE_DETAILS = "CourseContent/Details"
    const val API_COURSE = "Course"
    const val API_COURSE_QUIZ = "CourseContent/Quiz"
    const val API_SUBMIT_COURSE_QUIZ = "CourseContent/SubmitQuiz"
    const val API_COURSE_SECTIONS = "CourseContent/SectionAndLectures"
    const val API_COURSE_SHARE_URL = "CourseContent/ShareLink"
    const val API_COURSE_AUTHOR_DETAIL = "CourseContent/AuthorProfile"
    const val API_COURSE_COAUTHOR_DETAIL = "CourseContent/CoAuthorProfile"

    //Wishlist
    const val API_WISHLIST = "Wishlist/WishlistCourses"

    //Purchase course
    const val API_PURCHASE_COURSE = "PurchaseCourse"
    const val API_RAZORPAY_COURSE = "Payment"
    const val API_AMOUNT_WITH_GST = "Payment/AmountWithGST"

    //Reviews
    const val API_ADD_REVIEW = "Review/Add"
    const val API_GET_REVIEW = "Review"
    const val API_REVIEW_FILTERS = "Review/Filters"

    //certificates
    const val API_CERTIFICATE = "CourseContent/CourseCertificate"
    const val API_APP_CERTIFICATE = "CourseContent/AppreciationCertificate"

    // certificate api
    const val API_ASSESSMENT_DETAIL = "CourseContent/AssessmentTotalQuestionAndDuration"
    const val API_ASSESSMENT = "CourseContent/Assessment"
    const val API_ASSESSMENT_SUBMIT = "CourseContent/SubmitAssessment"


    //assessment report
    const val API_QUIZ_REPORT = "CourseContent/QuizReport"
    const val API_QUIZ_REPORT_STATUS = "CourseContent/AttemptedQuizReport"

    //assessment report
    const val API_ASSESSMENT_REPORT = "CourseContent/AssessmentReport"
    const val API_ASSESSMENT_REPORT_STATUS = "CourseContent/AttemptedAssessmentReport"

    //Moderator
    const val API_SWITCH_TO_MOD = "User/SwitchToAnotherMode"
    const val API_MOD_STATUS = "Moderator/Status"
    const val API_COURSE_REQUEST = "Moderator/ModeratorCourseRequest"
    const val API_MOD_COURSES = "Moderator/ModeratorCourses"
    const val API_MOD_COURSE_DETAIL = "Moderator/CourseDetail"
    const val API_UPDATE_MOD_REQUEST = "Moderator/UpdateModeratorCourseRequestStatus"
    const val API_COMMENT_SECTION = "Moderator/CommentSection"
    const val API_COMMENT_LECTURE = "Moderator/CommentLecture"
    const val API_COMMENT_MISC = "Moderator/Comment"
    const val API_UPDATE_MOD_COURSE_STATUS = "Moderator/UpdateModeratorCoursesStatus"
    const val API_MOD_DASHBOARD_STAT_COUNT = "Dashboard/Moderator/ModeratorCourseStatCount"
    const val API_MOD_DASHBOARD_COURSES_LIST = "Dashboard/Moderator/userModeratorCourses"
    const val API_MOD_DASHBOARD_FILTERED_COUNT =
        "Dashboard/Moderator/ModeratorCourseStatCountWithFilter"
    const val API_LEARNER_DASHBOARD_FILTERED_COUNT = "Dashboard/Learner/UserEnrolledCourseStatCount"
    const val API_LEARNER_DASHBOARD_STAT_COUNT = "Dashboard/Learner/UserCourseStatCount"
    const val API_LEARNER_DASHBOARD_COURSES_LIST = "Dashboard/Learner/EnrolledCourses"

    const val API_COURSE_UPDATE = "Moderator/UpdateCourse"
    const val API_MOD_MY_CATEGORIES = "Moderator/ModeratorCategories"

    //doc
    const val API_DOC_DOWNLOAD = "User/Moderator/QualificationDocs"

    //course content
    const val API_DETAIL_CREATOR = "CourseContent/DetailsForCreator"
    const val API_DETAIL_REQUEST_TRACKER = "CourseContent/DetailsForRequestTracker"
    const val API_SECTION_AND_LECTURE = "CourseContent/SectionAndLecturesForCreator"
    const val API_SECTION_AND_LECTURE_REQUEST_TRACKER =
        "CourseContent/SectionAndLecturesForRequestTracker"
    const val API_EDIT_TO_DRAFT = "Course/EditCourseToDraft"


    const val API_CHANGE_VI_MODE = "User/ToggleViMode"

    //Web sockets
    val WEB_SOCKET_ADD_LIKE_DISLIKE = "${BuildConfig.WEB_SOCKET_URL}LikeDislike"
    val WEB_SOCKET_USER_NOTIFICATION_COUNT =
        "${BuildConfig.WEB_SOCKET_URL}UserNotificationCount"
    val WEB_SOCKET_USER_CONTENT_HISTORY =
        "${BuildConfig.WEB_SOCKET_URL}UserCourseContentHistory"
    val WEB_SOCKET_USER_TRACKING =
        "${BuildConfig.WEB_SOCKET_URL}UserAppActivityTracker"

    //Web sockets production
//    val WEB_SOCKET_ADD_LIKE_DISLIKE = "wss://learner-api.skillfy.in/api/WebSocket/LikeDislike"
//    val WEB_SOCKET_USER_NOTIFICATION_COUNT =
//        "wss://learner-api.skillfy.in/api/WebSocket/UserNotificationCount"
//    val WEB_SOCKET_USER_CONTENT_HISTORY =
//        "wss://learner-api.skillfy.in/api/WebSocket/UserCourseContentHistory"
//    val WEB_SOCKET_USER_TRACKING =
//        "wss://learner-api.skillfy.in/api/WebSocket/UserAppActivityTracker"
//
    //Web sockets new urls
//    val WEB_SOCKET_ADD_LIKE_DISLIKE = "wss://api-qa.skillfy.in/learner/api/WebSocket/LikeDislike"
//    val WEB_SOCKET_USER_NOTIFICATION_COUNT = "wss://api-qa.skillfy.in/learner/api/WebSocket/UserNotificationCount"
//    val WEB_SOCKET_USER_CONTENT_HISTORY = "wss://api-qa.skillfy.in/learner/api/WebSocket/UserCourseContentHistory"


    const val API_GET_DETAIL_OF_CONTENT = "CourseContent/Lectures"
    const val API_GET_DETAIL_OF_CONTENT_MODE_CREATOR = "CourseContent/LecturesForCreator"

    //notification
    const val API_NOTIFICATION_COUNT = "Notification/NotificationCount"
    const val API_GET_NOTIFICATION = "Notification"
    const val API_PATCH_NOTIFICATION = "Notification"
    const val API_DELETE_NOTIFICATION = "Notification"
    const val API_NOTIFICATION_READ_ALL = "Notification/ReadAll"
    const val API_NOTIFICATION_DELETE_ALL = "Notification/DeleteAll"

    //payments
    const val API_PAYMENT_REMAINING_WALLET_BALANCE = "Payment/RemainingWalletBalance"
    const val API_PAYMENT_USER_PURCHASES = "Payment/UserPurchases"
    const val API_PAYMENT_USER_EARNINGS = "Payment/UserEarnings"
    const val API_GET_BANK_ACCOUNT_LISTING = "Payment/UserBankAccounts"
    const val API_MAKE_ACCOUNT_PRIMARY = "Payment/MakePrimaryBankAccount"


    const val API_PAYMENT_COURSE_DETAILS = "PurchaseCourse/Details"


    //offline courses
    val ADD_OFFLINE_COURSE: String = "add_offline_course"
    val UPDATE_OFFLINE_COURSE: String = "update_offline_course"
    val DELETE_OFFLINE_COURSE: String = "delete_offline_course"
    val ADD_OFFLINE_COURSE_SECTION: String = "add_offline_section"
    val ALL_OFFLINE_COURSES: String = "all_offline_courses"
    val OFFLINE_COURSE: String = "offline_course"
    val OFFLINE_COURSE_SECTIONS: String = "offline_sections"


    //support
    const val API_SUPPORT = "Support/GenerateSupportTicket"

    //staticLink
//    const val API_STATIC = "StaticContent"
//    const val API_STATIC_FAQ = "StaticContent/FAQ"

    const val API_CREATOR_DASHBOARD_AUDIENCE_STAT_COUNT = "Dashboard/Creator/AudienceStatCount"
    const val API_CREATOR_DASHBOARD_COURSE_USER_COUNT = "Dashboard/Creator/CourseUserCount"
    const val API_CREATOR_DASHBOARD_COURSE_USER_COUNT_FILTERED =
        "Dashboard/Creator/CourseUserCountWithFilter"
    const val API_CREATOR_DASHBOARD_TOTAL_EARNING = "Dashboard/Creator/CreatorTotalEarning"
    const val API_CREATOR_DASHBOARD_TOTAL_ACTIVITY_TIME = "Dashboard/Creator/TotalUserActivityTime"


    //Bank Account
    const val API_ADD_BANK_ACCOUNT = "Payment/BankAccount"
    const val API_MIN_WITHDRAW_AMOUNT = "Payment/MinimiumWithdrawAmount"
    const val API_WITHDRAW_REQUEST = "Payment/PaymentWithdrawRequest"
    const val API_WALLET_HISTORY = "Payment/UserWithdrawHistory"
    const val API_PAYMENT_WITHDRAW_LIST = "Payment/PaymentWithdrawList"
    const val API_MA_BANK_COUNT = "Payment/MaximumBankAccountAdd"

    // report comment
    const val API_REPORT_COMMENT = "Review/CourseReviewReport"

    //Course policy status
    const val API_COURSE_POLICY_STATUS = "User/CoursePolicyStatus"

    //complete video/audio process
    const val API_USER_COURSE_CONTENT_HISTORY = "CourseContent/UserCourseContentHistory"
}
