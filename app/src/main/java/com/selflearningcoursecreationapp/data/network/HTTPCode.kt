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
    const val NOT_FOUND = 404
    const val UN_SUCESS = 400
    const val NETWORK_ISSUE = 403
    const val FORBIDDEN = 403
    const val CONFLICT = 409
}