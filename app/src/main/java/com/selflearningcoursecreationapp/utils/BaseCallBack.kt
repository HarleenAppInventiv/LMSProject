package com.selflearningcoursecreationapp.utils

interface BaseCallBack {

    fun showLoading(message: String)
    fun hideLoading()
    fun showToastShort(message: String)
    fun showToastLong(message: String)
}