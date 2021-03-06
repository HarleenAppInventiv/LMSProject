package com.selflearningcoursecreationapp.utils

object ApiEndPoints {
    const val BASE_URL_INTERNAL = "https://selflearndevweb.appskeeper.in/info/"
    const val LINK_TERM_COND = "terms-&-conditions"
    const val LINK_PRIVECY_POL = "privacy-policy"
    const val LINK_ABOUT_US = "about-us"
    const val LINK_FAQ = "faq"


    const val VALID_DATA: String = "valid_Data"


    const val BASE_URL = "https://appinventivlearningclientapi20220314151937.azurewebsites.net/Api/"
    const val API_SIGNUP = "SignUp/SignUpRequest"
    const val API_OTP_REQ = "SignUp/OtpRequest"
    const val API_OTP_VAL = "SignUp/ValidateOtpRequest"
    const val API_LOGIN = "Login"
    const val API_GET_CATEGORIES = "Category/GetCategoryList"
    const val API_MYCATEGORIES = "Category/MyCategories"
    const val API_SAVE_PREFRENCE = "Preferences/SavePreferences"
    const val API_GETTHEME_LIST = "Themes/GetThemesList"
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
    const val API_ADD_SECTION_POST = "Course/Section"
    const val API_ADD_SECTION_PATCH = "Course/Section"
    const val API_SECTION_DELETE = "Course/Section"
    const val API_SECTION_DRAG_DROP = "Course/Section/DragAndDrop"

    //Lecture
    const val API_ADD_LECTURE_POST = "Course/Section/Lecture"
    const val API_ADD_LECTURE_PATCH = "Course/Section/Lecture"
    const val API_LECTURE_DELETE = "Course/Section/Lecture"
    const val API_LECTURE_DRAG_DROP = "Course/Section/Lecture/DragAndDrop"
    const val API_GET_LECTURE_DETAIL = "Course/Section/LectureDetail"

    //quiz
    const val API_ADD_QUIZ = "Course/Section/Lecture/Quiz"
    const val API_ADD_QUIZ_QUESTION = "Course/Section/Lecture/Quiz/Questions"
    const val API_ADD_QUIZ_ANS = "Course/Section/Lecture/Quiz/Questions/Answeres"
    const val API_ADD_QUIZ_IMAGE = "Course/Section/Lecture/Quiz/Image"
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
}