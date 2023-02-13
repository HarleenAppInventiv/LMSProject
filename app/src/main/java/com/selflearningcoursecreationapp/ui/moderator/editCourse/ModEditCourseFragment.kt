package com.selflearningcoursecreationapp.ui.moderator.editCourse

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentModEditCourseBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.moderator.ModeratorActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.ValidationConst
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class ModEditCourseFragment() : BaseFragment<FragmentModEditCourseBinding>() {
    private val viewModel: ModEditVM by viewModel()
    override fun getLayoutRes() = R.layout.fragment_mod_edit_course
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            viewModel.courseData.value = it.getParcelable<CourseData>("courseData")
        }
        updateValue(viewModel.courseData.value)

        binding.btApprove.setOnClickListener {
            if (binding.evEnterTitle.content().isBlank()) {
                showToastLong(getString(R.string.enter_course_title))
            } else {
                viewModel.editCourse(binding.evEnterTitle.content())

            }
        }


        binding.evEnterTitle.doAfterTextChanged { textAfter ->
            binding.tvTitleTotalChar.setText(textAfter?.length.toString())
            binding.tvTitleTotalChar.apply {
                if ((textAfter?.length ?: 0) < ValidationConst.MAX_COURSE_TITLE_LENGTH) {
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                } else {
                    setTextColor(
                        context.getAttrResource(R.attr.accentColor_Red)
                    )

                }
            }
        }

    }

    private fun updateValue(value: CourseData?) {
//        binding.evEnterDescription.setLimitedText(
//            Html.fromHtml(value?.courseDescription).toString(),10
//        )

        binding.courseData = value
        binding.notifyChange()
        binding.evEnterTitle.setText(value?.courseTitle)
        binding.evChooseCourseCategory.setText(value?.categoryName)
        binding.evChooseCourseLanguage.setText(value?.languageName)
        binding.tvTitleTotalChar.setText("${value?.courseTitle?.length}")
        binding.tvWordCount.setText(value?.getWordCount().toString())
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COURSE_UPDATE -> {
                showToastShort((value as BaseResponse<*>).message)
                findNavController().popBackStack()
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (exception.statusCode) {
            HTTPCode.USER_NOT_FOUND -> {
                hideLoading()
                CommonAlertDialog.builder(baseActivity)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .icon(R.drawable.ic_alert)
                    .title("")
                    .notCancellable(true)
                    .hideNegativeBtn(true)
                    .getCallback {
                        if (it) {
                            (baseActivity as ModeratorActivity).setSelected(R.id.action_home)
                        }
                    }.build()
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
        }
    }

}