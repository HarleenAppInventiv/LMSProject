package com.selflearningcoursecreationapp.utils


const val ACTION_NOTIFICATION_BROADCAST = "com.skillfy.notification_broadcast"


enum class Constants {
    IS_LOGGED_IN,
    APP_THEME,
    FONT_THEME,
    LANGUAGE_THEME,
    WALKTHROUGH_DONE,
    THEME_FILE,
    USER_RESPONSE,
    VI_MODE,
    USER_TOKEN,
    EMAIL,
    PASSWORD,
    COUNTYRY_CODE,
    COURSE_ID,
    FCM_TOKEN

}

object RequestCode {
    const val ACCESSIBILITY = 11
}

object ThemeConstant {
    const val BLUE = 1
    const val WINE = 2
    const val BLACK = 3
    const val SEA = 4
}

object FontConstant {
    const val ROBOTO = 1
    const val IBM = 2
    const val WORK_SANS = 3
}

object LanguageConstant {
    const val ENGLISH = "en"
    const val KANNADA = "kn"
    const val TAMIL = "ta"
    const val TELUGU = "te"
    const val BENGALI = "bn"
    const val HINDI = "hi"
}


object Lecture {
    const val CLICK_LESSON_DOCS = 1001
    const val CLICK_LESSON_VIDEO = 1002
    const val CLICK_LESSON_AUDIO = 1003
    const val CLICK_LESSON_TEXT = 1004
    const val CLICK_LESSON_SCREEN_RECORD = 1005
    const val CLICK_LESSON_QUIZ = 1006

}

object Permission {
    const val TAKE_PHOTO = 100
    const val GALLERY = 200
    const val DOC = 300
    const val RECORD_AUDIO = 400
    const val CAPTURE_VIDEO = 500
    const val FILE_MANAGER = 600
}


object Constant {
    const val CLICK_VIEW = 100
    const val CLICK_MORE = 101
    const val CLICK_DELETE = 102
    const val CLICK_UPLOAD = 103
    const val CLICK_EDIT = 104
    const val CLICK_IMAGE = 105
    const val CLICK_ADD = 108
    const val CLICK_ADD_SECTION = 109
    const val CLICK_SWAP = 110
    const val CLICK_COAUTHOR_VISIBLE = 111
    const val CLICK_PLAY = 112
    const val CLICK_TEXT_CHANGES = 113
    const val CLICK_SAVE = 200
    const val CLICK_OPTION_DELETE = 300
    const val CLICK_DETAILS = 500
    const val CLICK_SEE_ALL = 1000
    const val CLICK_BOOKMARK = 1500
    const val CLICK_BUYBUTTON = 2000
    const val CLICK_CAT = 201
    const val CLICK_CAT_OPTIONS = 402
    const val CLICK_ACCEPT = 114
    const val CLICK_REJECT = 115
    const val CLICK_ASSESSMENT = 116
    const val CLICK_LESSON = 117


    const val TYPE_ROUND = 1
    const val TYPE_LINE = 2
    const val TYPE_MONTH = 3
    const val TYPE_YEAR = 4

    const val TYPE_CATEGORY = 0
    const val TYPE_REQUEST_DATE = 1
    const val TYPE_FEE_RANGE = 2
    const val TYPE_CREATOR_NAME = 3


    const val COURSE_COMPLETED = 1
    const val COURSE_COMPLETED_REWARD = 4
    const val COURSE_IN_PROGRESS = 2
    const val MYCOURSES = 5
    const val COURSE_BOOKMARKED = 3

    const val COURSE = 1
    const val LANG = 2


    const val DESC = 1
    const val KEY_TAKEAWAY = 2
    const val TEXT_AS_LESSON = 2


}

object ValidationConst {
    const val MIN_NO_LENGTH = 5
    const val MAX_NO_LENGTH = 15
    const val MAX_NAME_LENGTH = 30
    const val MIN_NAME_LENGTH = 2
    const val MAX_PASSWORD_LENGTH = 16
    const val MIN_PASSWORD_LENGTH = 8
    const val OTP_TIME = 60000L
    const val OTP_TIME_REQUEST = 5
    const val MAX_COURSE_TITLE_LENGTH = 500
    const val MAX_COURSE_TITLE_LENGTH_SHOW = 256
    const val MAX_COURSE_DESC_LENGTH = 4000
    const val MAX_COURSE_DESC_LENGTH_SHOW = 500
    const val MAX_QUIZ_TITLE = 1000
    const val MAX_QUIZ_OPTION_LENGTH = 1000
}

object LoginConstant {
    const val IS_LOGIN = 0
}


object TAG {
    const val ACCESSIBILITY = "Accessibility"
}

object PREFERENCES {
    const val TYPE_CATEGORY = 0
    const val TYPE_THEME = 1
    const val TYPE_FONT = 2
    const val TYPE_LANGUAGE = 3
    const val TYPE_ALL = -1
    const val SCREEN_APP = 1
    const val SCREEN_SELECT = 2

}

object OtpType {
    const val TYPE_SIGNUP = 1
    const val TYPE_LOGIN = 2
    const val TYPE_FORGOT = 3
    const val TYPE_EMAIL = 4
}

object DialogType {
    const val PROFESSION = 5
    const val COURSE_COMPLEXITY = 2
    const val COURSE_TYPE = 3
    const val STATE = 7
    const val CITY = 1
    const val GENDER = 6
    const val CATEGORY = 8
    const val LANGUAGE = 9
    const val CLICK_LOGO = 10
    const val CLICK_BANNER = 11
    const val CLICK_QUIZ_TYPE = 12
    const val CLICK_QUIZ_OPTION = 13
    const val QUIZ_ANSWER = 14
    const val CLICK_CO_AUTHOR = 15
    const val HOME_FILTER = 16
    const val RATE_COURSE = 17
    const val CLICK_DRAG_OPTION = 18


}

object QUIZ {
    const val SINGLE_CHOICE = 2
    const val MULTIPLE_CHOICE = 1
    const val MATCH_COLUMN = 4
    const val DRAG_DROP = 3
    const val IMAGE_BASED = 5


}


object MediaType {
    const val VIDEO = 1
    const val AUDIO = 2
    const val DOC = 3
    const val TEXT = 4
    const val QUIZ = 5
    const val QUIZ_QUES = 6
    const val QUIZ_OPTION = 7
    const val COURSE_BANNER = 8
    const val COURSE_LOGO = 9
    const val PROFILE_PIC = 10
    const val ASSESSMENT_QUES = 11
    const val ASSESSMENT_OPTION = 12
}

object MediaFrom {
    const val RECORDING = 100001

}


object LectureStatus {
    const val IN_PROCESS = 2
    const val COMPLETED = 1
}

object CoAuthorStatus {
    const val ACCEPT = 1
    const val PENDING = 2
    const val REJECT = 3
    const val CANCELLED = 4
    const val SUBMITTED = 5
}

object CourseScreenType {
    const val ALL_COURSES = 1
    const val ONGOINGCOURSES = 6
    const val MYCOURSES = 8
}


object CourseType {
    const val FREE = 1
    const val PAID = 2
    const val RESTRICTED = 3
    const val REWARD_POINTS = 4
    const val REWARD_POINTS_EARNED_COURSES = 9
    const val REWARD_POINTS_PURCHASED_COURSES = 10

}

object ComplexityLevel {
    const val BEGINNER = 1
    const val INTERMEDIATE = 2
    const val ADVANCED = 3
}

//object ModeratorListType {
//    const val REQUESTED = 1
//    const val APPROVED = 2
//    const val REJECTED = 3
//
//}

object PaymentStatus {
    const val NOT_DONE = 0
    const val INITIATED = 1
    const val IN_PROGRESS = 2
    const val SUCCESS = 4
    const val FAILED = 16
    const val REFUNDED = 8
    const val AUTHORIZED = 2

}

object CourseStatus {
    const val ENROLLED = 1
    const val NOT_ENROLLED = 0
    const val REWARD_POINTS_EARNED_COURSES = 9
}

object BundleConst {
    const val IMAGE = "bundle_image"
    const val BLUR_HASH = "bundle_key_hash"
}

object CommonPayload {
    val PUBLISHED_DATE = "publishedDate"
    val OPERATOR_TYPE_6 = 6
    val OPERATOR_TYPE_7 = 7

}

object STATIC_PAGES_TYPE {
    const val PRIVACY = 1
    const val TERMS = 2
    const val HELP = 3
    const val ABOUT_US = 4

}

object MODTYPE {
    const val LEARNER = 1
    const val MODERATOR = 2
}

object MODSTATUS {
    const val BLOCKED = 0
    const val ACCEPTED = 1
    const val REJECTED = 3
    const val PENDING = 2
    const val CANCELLED = 4
}

object ModeratorDashboard {
    val PENDING_COUNT = 1
    val ACCEPTED_COUNT = 2
    val REJECTED_COUNT = 3
    val MODERATOR_COMMENTS = 4
    val REJECTED_COURSES_COUNT = 5
    val REQUEST_COUNT = 6
}