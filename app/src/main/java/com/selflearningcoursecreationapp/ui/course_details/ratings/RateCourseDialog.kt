package com.selflearningcoursecreationapp.ui.course_details.ratings

import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomDialogRateCourseBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.setLimitedName
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.utils.DialogType
import org.koin.androidx.viewmodel.ext.android.viewModel

class RateCourseDialog : BaseBottomSheetDialog<BottomDialogRateCourseBinding>(),
    View.OnTouchListener {
    private val viewModel: CourseDetailVM by viewModel()

    override fun getLayoutRes() = R.layout.bottom_dialog_rate_course

    override fun initUi() {
        expandLayout()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        arguments?.let {
            viewModel.courseId = it.getInt("courseId")
            it.getParcelable<CourseData>("courseData")?.let { courseData ->
                binding.tvName.text = courseData.courseTitle
                binding.tvAuthor.setLimitedName(courseData.getCreatedName())
                binding.ivCertification.text = courseData.categoryName
                binding.ivPreview.loadImage(
                    courseData.courseBannerUrl,
                    R.drawable.ic_home_default_banner, 3
                )
            }
        }


        binding.evEnterDescription.setOnTouchListener(this)
        binding.btnSubmit.setOnClickListener {
            if (binding.rating.rating == 0f) {
                showToastShort("Please provide rating")
            } else if (binding.evEnterDescription.content().isEmpty()) {
                showToastShort("Please provide review.")


            } else {
                viewModel.addReviewRequest.rating = binding.rating.rating.toInt()
                viewModel.addReviewRequest.courseId = viewModel.courseId
                viewModel.addReviewRequest.description = binding.evEnterDescription.text.toString()

                viewModel.addReviewsRequest()
                baseActivity.showProgressBar()

            }


        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        (value as BaseResponse<Any>)?.let {
            showToastShort(it.message)
        }
        onDialogClick(DialogType.RATE_COURSE)
        dismiss()
    }


}