package com.selflearningcoursecreationapp.utils

object ApiEndPoints {
    const val HOME_SUCCESS = "home_success"
    const val BASE_URL_INTERNAL = "https://selflearndevweb.appskeeper.in/info/"
    const val LINK_TERM_COND = "terms-&-conditions"
    const val LINK_PRIVACY_POL = "privacy-policy"
    const val LINK_ABOUT_US = "about-us"
    const val LINK_FAQ = "faq"

    // dummy
    const val VALID_DATA: String = "valid_Data"
    const val GUEST_LOGIN: String = "guest_login"


    //    const val BASE_URL = "https://appinventivlearningclientapi20220314151937.azurewebsites.net/Api/" //dev server
//    const val BASE_URL = "https://appinventivlearningclientapiqa.azurewebsites.net/Api/" //qa server
//    const val BASE_URL = "http://20.197.6.132/api/" //qa server
    const val BASE_URL = "https://skillfy.in/Api/" //qa server
//    const val BASE_URL = "https://api-uat.skillfy.in/Api/" //client server

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
    const val API_THUMBNAIL_UPLOAD = "Course/Section/Lecture/UploadThumbnail"

    //coAuthor
    const val API_INVITE_COAUTHOR = "CoAuthor/AddCoAuthor"

    //CoAuthor
    const val API_COAUTHOR_INVITATION = "CoAuthor/UpdateInvitationStatus"
    const val API_REQUEST_COUNT = "CoAuthor/RequestCount"
    const val API_REQUEST_LIST = "CoAuthor/RequestList"


    // Guest home
    const val API_HOME_GUESTCOURSES = "HomeScreen/GuestCourses"
    const val API_HOME_COURSES = "HomeScreen/Courses"
    const val API_HOME_TAB_CATE = "HomeScreen/TabCategories"


    const val API_HOME_ALL_CATEGORIES = "HomeScreen/AllCategories"

    const val API_HOME_WISHLIST = "Wishlist/WishlistedCourses"
    const val API_HOME_FILTERS = "HomeScreen/Filters"
    const val API_ALL_COURSES = "HomeScreen/AllCourses"
    const val API_GUEST_ALL_COURSES = "HomeScreen/AllGuestCourses"

    //course details
    const val API_COURSE_DETAILS = "CourseContent/Details"
    const val API_COURSE_QUIZ = "CourseContent/Quiz"
    const val API_SUBMIT_COURSE_QUIZ = "CourseContent/SubmitQuiz"
    const val API_COURSE_SECTIONS = "CourseContent/SectionAndLectures"

    //Wishlist
    const val API_WISHLIST = "Wishlist/WishlistCourses"

    //Purchase course
    const val API_PURCHASE_COURSE = "PurchaseCourse"
    const val API_RAZORPAY_COURSE = "Payment"

    //Reviews
    const val API_ADD_REVIEW = "Review/Add"
    const val API_GET_REVIEW = "Review"
    const val API_REVIEW_FILTERS = "Review/Filters"

    //certificates
    const val API_CERTIFICATE = "CourseContent/CourseCertificate"

    // certificate api
    const val API_ASSESSMENT_DETAIL = "CourseContent/AssessmentTotalQuestionAndDuration"
    const val API_ASSESSMENT = "CourseContent/Assessment"
    const val API_ASSESSMENT_SUBMIT = "CourseContent/SubmitAssessment"

    //assessment report
    const val API_ASSESSMENT_REPORT = "CourseContent/AssessmentReport"
    const val API_ASSESSMENT_REPORT_STATUS = "CourseContent/AttemptedAssessmentReport"

    //Moderator
    const val API_SWITCH_TO_MOD = "User/SwitchToAnotherMode"
    const val API_MOD_STATUS = "Moderator/Status"
    const val API_COURSE_REQUEST = "Moderator/ModeratorCourseRequest"
    const val API_MOD_COURSES = "Moderator/ModeratorCourses"

}