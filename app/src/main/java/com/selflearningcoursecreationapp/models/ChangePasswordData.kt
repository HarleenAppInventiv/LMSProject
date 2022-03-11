package com.selflearningcoursecreationapp.models


data class ChangePasswordData(
    var oldPassword: String="",
    var newPassword: String="",
    var confirmPassword: String=""
)