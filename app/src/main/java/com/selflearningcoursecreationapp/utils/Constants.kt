package com.selflearningcoursecreationapp.utils


val ACTION_NOTIFICATION_BROADCAST = "com.skillfy.notification_broadcast"
val ACTION_RECORDER_BROADCAST = "com.skillfy.recorder_broadcast"
val ACTION_RECORDER_BROADCAST_NOTIFY = "com.skillfy.recorder_broadcast_notify"


enum class Constants {
    IS_LOGGED_IN,
    APP_THEME,
    FONT_THEME,
    LANGUAGE_THEME,
    LANGUAGE_ID,
    WALKTHROUGH_DONE,
    THEME_FILE,
    USER_RESPONSE,
    VI_MODE,
    USER_TOKEN,
    EMAIL,
    INTERCOM_LOGIN,
    PASSWORD,
    COUNTYRY_CODE,
    COURSE_ID,
    FCM_TOKEN,


}


object PaymentWithdrawlStatys {
    val BLOCKED = 0
    val ACCEPTED = 1
    val PENDING = 2
    val REJECTED = 3
    val CANCELLED = 4
}

object WalletHistoryStatus {
    val FAILED = 16
    val PROCESSING = 32
    val PROCESSED = 64
    val REVERSED = 128
    val QUEUED = 256
    val PENDING = 512
    val REJECTED = 1024
}

object RequestCode {
    val CAPTURE_IMAGE = 10
    val ACCESSIBILITY = 11
    val PICK_IMAGE = 12
    val CAPTURE_BANNER_IMAGE = 13
    val PICK_BANNER_IMAGE = 14
    val CAPTURE_VIDEO = 15
    val PICK_VIDEO = 16
    val PICK_DOCUMENT = 17
    val PICK_AUDIO = 18
    val PICK_PDF_DOCUMENT = 19
    val OVERLAY_PERMISSION = 20
    val RECORD_PERMISSION = 21
    val PRACTICE_ACCENT = 22
}

object ThemeConstant {
    val BLUE = 1
    val WINE = 2
    val BLACK = 3
    val SEA = 4
}

object FontConstant {
    val ROBOTO = 1
    val IBM = 2
    val WORK_SANS = 3
}

object LanguageConstant {
    val ENGLISH = "en"
    val KANNADA = "kn"
    val TAMIL = "ta"
    val TELUGU = "te"
    val BENGALI = "bn"
    val HINDI = "hi"
}

object NotificationType {
    val COURSE_PUBLISHED = "1"
    val COURSE_REJECTED = "2"
    val MODERATOR_REQUEST_APPROVED = "3"
    val MODERATOR_REQUEST_REJECTED = "4"
    val COAUTHOR_REQUEST = "5"
    val COURSE_REVIEW_REQUEST = "6"
    val REWARDS_EARNED = "8"
    val REVIEW_ADDED = "9"
    val COAUTHOR_ACCEPT_REQUEST = "10"
    val COAUTHOR_REJECT_REQUEST = "11"
    val COAUTHOR_COURSE_SUBMIT = "12"
    val LEARNER_ENROLLED_COURSE = "13"
    val ENROLLED_COURSE = "14"
    val AS_MODERATOR_BLOCKED = "15"
    val COURSE_SUBMITTED = "16"
    val UPLOAD_SIGN = "17"
    val DELETE_SIGN = "18"
}


object Lecture {
    val CLICK_LESSON_DOCS = 1001
    val CLICK_LESSON_VIDEO = 1002
    val CLICK_LESSON_AUDIO = 1003
    val CLICK_LESSON_TEXT = 1004
    val CLICK_LESSON_SCREEN_RECORD = 1005
    val CLICK_LESSON_QUIZ = 1006

}

object Permission {
    val TAKE_PHOTO = 100
    val GALLERY = 200
    val DOC = 300
    val RECORD_AUDIO = 400
    val CAPTURE_VIDEO = 500
    val FILE_MANAGER = 600
    val FIXCROP = 1
}



object Constant {
    val CLICK_VIEW = 100
    val CLICK_MORE = 101
    val CLICK_DELETE = 102
    val CLICK_UPLOAD = 103
    val CLICK_EDIT = 104
    val CLICK_IMAGE = 105
    val CLICK_ADD = 108
    val CLICK_ADD_SECTION = 109
    val CLICK_SWAP = 110
    val CLICK_COAUTHOR_VISIBLE = 111
    val CLICK_PLAY = 112
    val CLICK_TEXT_CHANGES = 113
    val CLICK_ACCEPT = 114
    val CLICK_REJECT = 115
    val CLICK_ASSESSMENT = 116
    val CLICK_LESSON = 117
    val CLICK_BEFORE_TEXT_CHANGES = 118
    val CLICK_AFTER_TEXT_CHANGES = 119

    val CLICK_EDIT_COMMENT = 120
    val CLICK_DELETE_COMMENT = 121
    val CLICK_REQUEST = 122
    val CLICK_PENDING = 123
    val CLICK_ASSESSMENT_REVIEW = 124
    val CLICK_ADD_COMMENT = 125
    val CLICK_SAVE = 200
    val CLICK_CAT = 201
    val CLICK_INVOICE = 202
    val CLICK_QUIZ_REPORT = 203
    val CLICK_ADD_COMMENT_LEC = 204
    val CLICK_EDIT_COMMENT_LEC = 205
    val CLICK_DELETE_COMMENT_LEC = 206
    val CLICK_REPORT = 208

    val DOWNLOAD_COMPLETIION = 209
    val DOWNLOAD_APPRECIATION = 210
    val CLICK_OPTION_DELETE = 300
    val CLICK_INFO = 400
    val CLICK_CAT_OPTIONS = 402
    val CLICK_DETAILS = 500
    val CLICK_SEE_ALL = 1000
    val CLICK_BOOKMARK = 1500
    val CLICK_BUYBUTTON = 2000


    val COMMENT_TITLE = 1
    val COMMENT_DESC = 2
    val COMMENT_ASSESSMENT = 3

    val SEARCH_CROSS = 1
    val CLICK_SUGGESTION = 2


    val TYPE_ROUND = 1
    val TYPE_LINE = 2
    val TYPE_MONTH = 3
    val TYPE_YEAR = 4

    val TYPE_CATEGORY = 0
    val TYPE_REQUEST_DATE_START_DATE = 1
    val TYPE_FEE_RANGE = 2
    val TYPE_CREATOR_NAME = 3


    val COURSE_COMPLETED = 1
    val COURSE_COMPLETED_REWARD = 4
    val COURSE_IN_PROGRESS = 2
    val MYCOURSES = 5
    val COURSE_BOOKMARKED = 3

    val COURSE = 1
    val LANG = 2


    val DESC = 1
    val KEY_TAKEAWAY = 2
    val TEXT_AS_LESSON = 2

    val CLICK_AUTHOR_PROFILE = 1

    const val ACCEPTED_COURSES = 1
    const val PENDING_COURSES = 2
    const val REJECTED_COURSES = 3

    const val DESC_CHAR_COUNT_MAX = 150

}

object ValidationConst {
    const val MIN_NO_LENGTH = 10
    const val MAX_NO_LENGTH = 10

    const val MAX_EMAIL_LENGTH = 50

    const val MAX_NAME_LENGTH = 30
    const val MIN_NAME_LENGTH = 2

    const val MAX_PASSWORD_LENGTH = 16
    const val MIN_PASSWORD_LENGTH = 4

    const val OTP_TIME = 60000L
    const val OTP_TIME_REQUEST = 5

    //course const validation
    const val MAX_COURSE_TITLE_LENGTH = 256
    const val MAX_COURSE_DESC_LENGTH = 500

    const val MAX_COURSE_SECTION_LENGTH = 256
    const val MAX_COURSE_SECTION_DESC_LENGTH = 500

    const val MAX_COURSE_LESSON_LENGTH = 256
    const val MAX_COURSE_COMMENT_LENGTH = 4000
    const val MAX_QUIZ_TITLE = 1000
    const val MAX_QUIZ_OPTION_LENGTH = 1000

    //bank const validation
    const val MAX_BANK_ACCOUNT_NUM_LEN = 17
    const val MIN_BANK_ACCOUNT_NUM_LEN = 10
    const val MAX_BANK_ACCOUNT_NAME = 60
    const val MIN_BANK_ACCOUNT_NAME = 2
    const val MIN_IFSC_LENGTH = 11
}

object LoginConstant {
    val IS_LOGIN = 0
}


object TAG {
    val ACCESSIBILITY = "Accessibility"
}

object PREFERENCES {
    val TYPE_CATEGORY = 0
    val TYPE_THEME = 1
    val TYPE_FONT = 2
    val TYPE_LANGUAGE = 3
    val TYPE_ALL = -1
    val SCREEN_APP = 1
    val SCREEN_SELECT = 2

}

object OtpType {
    val TYPE_SIGNUP = 1
    val TYPE_LOGIN = 2
    val TYPE_FORGOT = 3
    val TYPE_EMAIL = 4
}

object DialogType {
    val PROFESSION = 5
    val COURSE_COMPLEXITY = 2
    val COURSE_TYPE = 3
    val STATE = 7
    val CITY = 1
    val GENDER = 6
    val CATEGORY = 8
    val LANGUAGE = 9
    val CLICK_LOGO = 10
    val CLICK_BANNER = 11
    val CLICK_QUIZ_TYPE = 12
    val CLICK_QUIZ_OPTION = 13
    val QUIZ_ANSWER = 14
    val CLICK_CO_AUTHOR = 15
    val HOME_FILTER = 16
    val RATE_COURSE = 17
    val CLICK_DRAG_OPTION = 18
    val CREATOR_DASH_FILTER = 19
    val PAYMENT = 20
    val CLICK_PORTRAIT_QUES = 21
    val LOGO_OPTION = 22

}

object QUIZ {
    const val SINGLE_CHOICE = 2
    const val MULTIPLE_CHOICE = 1
    const val MATCH_COLUMN = 4
    const val DRAG_DROP = 3
    const val IMAGE_BASED = 5


}


object MediaType {
    val VIDEO = 1
    val AUDIO = 2
    val DOC = 3
    val TEXT = 4
    val QUIZ = 5
    val QUIZ_QUES = 6
    val QUIZ_OPTION = 7
    val COURSE_BANNER = 8
    val COURSE_LOGO = 9
    val PROFILE_PIC = 10
    val ASSESSMENT_QUES = 11
    val ASSESSMENT_OPTION = 12
    val EDITED_VIDEO = 13
}

object MediaFrom {
    val RECORDING = 100001
    val EDITING = 100002

}


object LectureStatus {
    val IN_PROCESS = 2 //lesson uploading failed at server end
    val ERRORED = 13
    val CANCELLED = 12
    val COMPLETED = 1
    val IN_COMPLETE = 16
    val CONTENT_PROCESSING = 8
    val JOB_CREATED = 9
    val JOB_PROCESSING_START = 10
    val JOB_FINISHED = 11
    val LINK_GENERATED = 14
    val PARTIAL_SUBMITTED = 17
}

object CoAuthorStatus {
    val ACCEPT = 1
    val PENDING = 2
    val REJECT = 3
    val CANCELLED = 4
    val SUBMITTED = 5
    val CANCELLEDWITHSUBMIT = 5
}

object CourseScreenType {
    val ALL_COURSES = 1
    val ALL_COURSES_GUEST = 0
    val ONGOINGCOURSES = 6
    val MYCOURSES = 8
    val COMPLETED_COURSES = 11
}


object CourseType {
    const val FREE = 1
    const val PAID = 2
    const val RESTRICTED = 3
    const val REWARD_POINTS = 4
    const val REWARD_POINTS_EARNED_COURSES = 9
    const val REWARD_POINTS_PURCHASED_COURSES = 10

}

object RevenueType {

    const val PURCHASED = 1
    const val REWARD_POINTS = 2

}

object ComplexityLevel {
    val BEGINNER = 1
    val INTERMEDIATE = 2
    val ADVANCED = 3
}

//object ModeratorListType {
//     val REQUESTED = 1
//     val APPROVED = 2
//     val REJECTED = 3
//
//}

object PaymentStatus {
    val NOT_DONE = 0
    val INITIATED = 1
    val IN_PROGRESS = 2
    val SUCCESS = 4
    val FAILED = 16
    val REFUNDED = 8
    val AUTHORIZED = 2

}

object CourseStatus {
    val ENROLLED = 1
    val NOT_ENROLLED = 0
    val IN_PROGRESS = 2
    val COMPELETD = 3
    val REWARD_POINTS_EARNED_COURSES = 9
}

object BundleConst {
    val IMAGE = "bundle_image"
    val BLUR_HASH = "bundle_key_hash"
}

object CommonPayload {
    val PUBLISHED_DATE = "publishedDate"
    val MODIFIED_DATE = "modifiedDate"
    val CREATED_DATE = "CreatedDate"
    val AGE = "age"
    val PROFESSIONID = "professionId"
    val OPERATOR_TYPE_6 = 6
    val OPERATOR_TYPE_7 = 7
    val OPERATOR_EQUAL = 1
    val OPERATOR_IN = 2
    val OPERATOR_LIKE = 3
    val OPERATOR_GREATER = 4
    val OPERATOR_LESS_THAN = 5
    val OPERATOR_GREATER_EQUAL = 6
    val OPERATOR_LESS_EQUAL = 7

}

object DASHBOARD_FILTER_TYPE {
    val DAY = 0
    val WEEK = 1
    val MONTH = 2
    val ALL = 3

}

object PROFESSION_FILTER {
    val ALL = 0
    val STUDENT = 1
    val WORKING_PROFESSIONAL = 2
    val PURSUING_GRADUATION = 3

}


object STATIC_PAGES_TYPE {
    val PRIVACY = 1
    val TERMS = 2
    val HELP = 3
    val ABOUT_US = 4
    val CONTACT_US = 5

}

object MODTYPE {
    val LEARNER = 1
    val MODERATOR = 2
}

object MODSTATUS {
    val BLOCKED = 0
    val ACCEPTED = 1
    val REJECTED = 3
    val PENDING = 2
    val CANCELLED = 4
}


object ModHomeConst {
    val REQUEST = 1
    val PENDING = 2
    val APPROVED = 3
    val REJECTED = 4
}

object ModeratorDashboard {
    val PENDING_COUNT = 1
    val ACCEPTED_COUNT = 2
    val REJECTED_COUNT = 3
    val MODERATOR_COMMENTS = 4
    val REJECTED_COURSES_COUNT = 5
    val REQUEST_COUNT = 6
    val ACCEPTED_FRAGMENT = 1
    val PENDING_FRAGMENT = 2
    val REJECTED_FRAGMENT = 3

    val INPROGRESS_FRAGMENT = 1
    val TODO_FRAGMENT = 2
    val DONE_FRAGMENT = 3
}

object CreatedCourseStatus {
    val BLOCKED = 0
    val DRAFT = 1
    val INPROCESS = 2
    val SUBMIT = 3
    val APPROVAL = 4
    val PUBLISHED = 5
    val REJECTED = 6
    val PARTIALREJECTED = 7
    val UNDER_PROCESSING = 17
    val PARTIAL_SUBMITTED = 17
}

object SentRequestsStatus {
    val ACCEPTED = 1
}

object WithdrawalType {
    val RAZORPAY_WITHDRAW_AMOUNT = 5
    val MANUAL_WITHDRAW_AMOUNT = 6
}

object DefaultMime {
    val AUDIO = "audio/mp3"
    val VIDEO = "video/mp4"
}

object DefaultExt {
    val AUDIO = ".mp3"
    val VIDEO = ".mp4"
}