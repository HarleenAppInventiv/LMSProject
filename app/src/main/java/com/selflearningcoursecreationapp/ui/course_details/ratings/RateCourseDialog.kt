package com.selflearningcoursecreationapp.ui.course_details.ratings

import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomDialogRateCourseBinding
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
//                binding.tvProgress.text = (courseData.percentageCompleted).toString() + "% " + context?.getString(R.string.completed)

                binding.tvProgress.text =
                    if ((courseData.percentageCompleted
                            ?: 0.0) > 0 && (courseData.percentageCompleted
                            ?: 0.0) < 1
                    ) {
                        (courseData.percentageCompleted).toString() + "% " + context?.getString(R.string.completed)

                    } else {
                        (courseData.percentageCompleted)?.toInt()
                            .toString() + "% " + context?.getString(R.string.completed)

                    }
                binding.pbProgress.apply {

                    max = 100
                    progress = if ((courseData.percentageCompleted
                            ?: 0.0) > 0 && (courseData.percentageCompleted ?: 0.0) < 1
                    ) 1
                    else
                        courseData.percentageCompleted?.toInt() ?: 0

                }
            }

            binding.rating.onRatingBarChangeListener = OnRatingBarChangeListener { _, rating, _ ->
                binding.rating.contentDescription = "$rating star out of 5 star"
            }
        }


        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

        binding.evEnterDescription.setOnTouchListener(this)
        binding.btnSubmit.setOnClickListener {
            if (binding.rating.rating == 0f) {
                showToastShort(getString(R.string.please_provide_rating))
            }
//
//            else if (binding.evEnterDescription.content().isEmpty()) {
//                showToastShort(getString(R.string.please_provide_review))


//            }

            else {
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
        showToastShort((value as BaseResponse<Any>).message)
        onDialogClick(DialogType.RATE_COURSE)
        dismiss()
    }


}