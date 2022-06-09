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
    USER_TOKEN,
    EMAIL,
    PASSWORD,
    COUNTYRY_CODE,
    COURSE_ID,
    FCMTOKEN

}

object REQUEST_CODE {
    const val ACCESSIBILITY = 11
}

object THEME_CONSTANT {
    const val BLUE = 1
    const val WINE = 2
    const val BLACK = 3
    const val SEA = 4
}

object FONT_CONSTANT {
    const val ROBOTO = 1
    const val IBM = 2
    const val WORK_SANS = 3
}

object LANGUAGE_CONSTANT {
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

    const val TYPE_ROUND = 1
    const val TYPE_LINE = 2
    const val TYPE_MONTH = 3
    const val TYPE_YEAR = 4

    const val COURSE_COMPLETED = 1
    const val COURSE_IN_PROGRESS = 2
    const val COURSE_BOOKMARKED = 3

    const val COURSE = 1
    const val LANG = 2


    const val DESC = 1
    const val KEY_TAKEAWAY = 2


}

object VALIDATION_CONST {
    const val MIN_NO_LENGTH = 5
    const val MAX_NO_LENGTH = 15
    const val MAX_NAME_LENGTH = 30
    const val MIN_NAME_LENGTH = 2
    const val MAX_PASSWORD_LENGTH = 16
    const val MIN_PASSWORD_LENGTH = 8
}

object LOGIN_CONSTANT {
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
    const val SCREEN_COURSE = 2

}

object OTP_TYPE {
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


}

object QUIZ {
    const val SINGLE_CHOICE = 2
    const val MULTIPLE_CHOICE = 1
    const val MATCH_COLUMN = 4
    const val DRAG_DROP = 3
    const val IMAGE_BASED = 5


}


object MEDIA_TYPE {
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

object MEDIA_FROM {
    const val RECORDING = 100001

}

object COAUTHOR_STATUS {
    const val ACCEPT = 1
    const val PENDING = 2
    const val REJECT = 3
    const val CANCELLED = 4
    const val SUBMITTED = 5
}
