package com.selflearningcoursecreationapp.ui.dialog.unlockCourse

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.models.OtpData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnlockVM(private val repo: UnlockRepo) : BaseViewModel() {
    var otpData = OtpData()
    var courseId: Int = 0
    var courseType: Int = 0

    fun otpVerify() {
        val errorId = otpData.isValid()
        if (errorId == 0) {
            purchaseCourse()
        } else {
            updateResponseObserver(Resource.Error(ToastData(errorId)))
        }
    }

    fun purchaseCourse() =
        viewModelScope.launch(coroutineExceptionHandle) {
            val map = HashMap<String, Any>()

            map["unlockCode"] = otpData.otp.toString()
            map["courseId"] = courseId
            map["courseType"] = courseType
            val response = repo.unlockCourse(map)
            withContext(Dispatchers.IO) {
                response.collect {

                    updateResponseObserver(it)
                }
            }

        }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_PURCHASE_COURSE -> otpVerify()
        }
    }

}