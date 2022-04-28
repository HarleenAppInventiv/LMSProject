package com.selflearningcoursecreationapp.utils

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
    COUNTYRY_CODE

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

object Constant {
    const val CLICK_IMAGE = 105
    const val CLICK_VIEW = 100
    const val CLICK_SEE_ALL = 1000
    const val CLICK_MORE = 101
    const val CLICK_DELETE = 102
    const val CLICK_UPLOAD = 103
    const val CLICK_LOGO = 106
    const val CLICK_BANNER = 107
    const val CLICK_EDIT = 104
    const val CLICK_ADD = 108

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
}
