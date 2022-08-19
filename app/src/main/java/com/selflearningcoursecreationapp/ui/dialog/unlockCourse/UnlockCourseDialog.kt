package com.selflearningcoursecreationapp.ui.dialog.unlockCourse

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.UnlockCourseLayoutBinding
import com.selflearningcoursecreationapp.extensions.otpHelper
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UnlockCourseDialog : BaseDialog<UnlockCourseLayoutBinding>() {

    override fun getLayoutRes() = R.layout.unlock_course_layout
    private val viewModel: UnlockVM by viewModel()
    private val sharedHomeModel: HomeVM by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTransparent_95)

    }

    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.unlockCourses = viewModel
        binding.etOtp1.otpHelper()
        binding.etOtp2.otpHelper()
        binding.etOtp3.otpHelper()
        binding.etOtp4.otpHelper()

        arguments?.let {
            viewModel.courseId = it.getInt("courseId")
            viewModel.courseType = it.getInt("courseType")
        }


        binding.btnSubmitOtp.setOnClickListener {
            viewModel.otpVerify()
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_PURCHASE_COURSE -> {
                (value as BaseResponse<OrderData>).let {
                    dismiss()
                    sharedHomeModel.updateCourse(viewModel.courseId)
                    showToastShort(it.message)
                    onDialogClick(Constant.CLICK_VIEW, viewModel.courseId)
                }

            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)

    }


}

