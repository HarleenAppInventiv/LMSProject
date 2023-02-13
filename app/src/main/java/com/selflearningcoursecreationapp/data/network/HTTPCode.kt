package com.selflearningcoursecreationapp.data.network

object HTTPCode {
    const val SUCCESS = 200  // Success
    const val CREATED = 201  // Success
    const val UNJOIN = 233  // Success
    const val AUTOMATIC_COMPLETE = 234  // Success
    const val UNAUTHORIZED_REQUEST = 206  // Unauthorized request
    const val MISSING_HEADER = 207 // Header is missing
    const val PHONE_EXISTS = 208  // Phone number alredy exists
    const val PARAM_MISSING = 418  // Required Parameter Missing or Invalid
    const val UPLOAD_FAILED = 421  // File Upload Failed
    const val SERVER_ERROR = 500  // Please try again
    const val TOKEN_EXPIRED = 401  // Token expired refresh token needed to be generated
    const val UPDATE_SUCCESS = 202  // Success
    const val NO_CONTENT = 204  // Success
    const val NOT_FOUND = 404
    const val UN_SUCESS = 400
    const val FORBIDDEN = 403
    const val CO_AUTHOR_ACCESS_DENIED = 406
    const val USER_NOT_FOUND = 410
    const val COURSE_HAS_ENROLLED_USERS = 412
    const val CREATOR_HAS_PENDING_BALANCE = 417
    const val CONTENT_DELETED = 423
    const val DATA_MISSING_VALIDATION = 424
    const val NO_MODERATOR_AVAILABLE = 428
    const val CONFLICT = 409
}