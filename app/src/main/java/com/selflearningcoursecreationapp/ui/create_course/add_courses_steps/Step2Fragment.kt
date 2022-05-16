package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep2Binding
import com.selflearningcoursecreationapp.extensions.doEnable
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceBottomDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.HandleClick
import java.io.File


class Step2Fragment : BaseFragment<FragmentStep2Binding>(), HandleClick,
    BaseBottomSheetDialog.IDialogClick {
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment() })


    override fun getLayoutRes(): Int {
        return R.layout.fragment_step2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()


    }

    private fun initUi() {
        binding.handleClick = this
        binding.step2 = viewModel
//        activityResultListener()
//        SwRewardsFunctionality()
        if (!viewModel.courseData.value?.keyTakeaways.isNullOrEmpty()) {
            binding.tvKeywordTitle.text = Html.fromHtml(viewModel.courseData.value?.keyTakeaways)
        }

        viewModel.courseData.value?.let {
            if (!it.keyTakeaways.isNullOrEmpty()) {
                binding.tvKeywordTitle.text =
                    Html.fromHtml(viewModel.courseData.value?.keyTakeaways)
            }
            if (!it.courseLogoUrl.isNullOrEmpty()) {
                binding.ivLogo.setImageURI(Uri.parse(it.courseLogoUrl))
                binding.tvLogo.gone()
                binding.ivEditLogo.visible()
            }

            if (!it.courseBannerUrl.isNullOrEmpty()) {
                binding.ivHeader.setImageURI(Uri.parse(it.courseBannerUrl))
                binding.tvHeader.gone()
                binding.ivEditBanner.visible()
            }

        }
    }

//    private fun SwRewardsFunctionality() {
//        binding.swRewards.typeface = ResourcesCompat.getFont(
//            baseActivity,
//            ThemeUtils.getFont(SelfLearningApplication.fontId, ThemeConstants.FONT_MEDIUM)
//        )
//
//        binding.swRewards.setOnCheckedChangeListener { compoundButton, b ->
//            if (b) {
//                binding.swRewards.text = "Accepted"
//                binding.swRewards.setTextColor(
//                    ContextCompat.getColor(
//                        baseActivity,
//                        R.color.accent_color_2FBF71
//                    )
//                )
//            } else {
//                binding.swRewards.text = " Not Accepted"
//                binding.swRewards.setTextColor(
//                    ContextCompat.getColor(
//                        baseActivity,
//                        R.color.hint_color_929292
//                    )
//                )
//
//            }
//        }
//    }

//    private fun activityResultListener() {
//        requireActivity().supportFragmentManager.setFragmentResultListener(
//            "valueHTML",
//            viewLifecycleOwner
//        ) { _, bundle ->
//            val value = bundle.getString("value")
//            val type = bundle.getInt("type")
//            if (type == Constant.DESC) {
//                viewModel.courseData.value?.keyTakeaways = value ?: ""
//                binding.tvKeyword.text = Html.fromHtml(value)
//                viewModel.notifyData()
//
//            }
//        }
//    }


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.iv_header, R.id.tv_header, R.id.iv_edit_banner -> {
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_BANNER)

                        setOnDialogClickListener(this@Step2Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.iv_logo, R.id.tv_logo, R.id.iv_edit_logo -> {
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_LOGO)
                        setOnDialogClickListener(this@Step2Fragment)
                    }.show(childFragmentManager, "")

                }
                R.id.tv_type -> {
                    SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.COURSE_TYPE,
                            "title" to this@Step2Fragment.baseActivity.getString(R.string.course_type),
                            "list" to viewModel.masterData.courseTypes?.list,
                            "id" to viewModel.courseData.value?.courseTypeId
                        )
                        setOnDialogClickListener(this@Step2Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.tv_audience -> {

                    SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.PROFESSION,
                            "title" to this@Step2Fragment.baseActivity.getString(R.string.target_audience),
                            "id" to viewModel.courseData.value?.targetAudienceId
                        )
                        setOnDialogClickListener(this@Step2Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.tv_complexity -> {

                    SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.COURSE_COMPLEXITY,
                            "title" to this@Step2Fragment.baseActivity.getString(R.string.course_complexity),
                            "list" to viewModel.masterData.courseComplexities?.list,
                            "id" to viewModel.courseData.value?.courseComplexityId
                        )
                        setOnDialogClickListener(this@Step2Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.tv_keyword -> {
                    var action =
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.KEY_TAKEAWAY, viewModel.courseData.value?.keyTakeaways ?: ""
                        )
                    findNavController().navigate(action)
                }


            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                DialogType.COURSE_COMPLEXITY -> {
                    val value = items[1] as SingleChoiceData
                    binding.tvComplexity.text = value.title
                    viewModel.courseData.value?.courseComplexityId = value.id

                }

                DialogType.COURSE_TYPE -> {
                    val value = items[1] as SingleChoiceData
                    binding.tvType.text = value.title
                    viewModel.courseData.value?.courseTypeId = value.id
                    viewModel.courseData.value?.isPaid = value.isPaid ?: false

                    binding.tvFee.doEnable(value.isPaid ?: false)
                    binding.tvFee.setText("0")

                }
                DialogType.PROFESSION -> {
                    val value = items[1] as SingleChoiceData
                    binding.tvAudience.text = value.title
                    viewModel.courseData.value?.targetAudienceId = value.id


                }
                DialogType.CLICK_BANNER -> {
                    viewModel.uploadImage(File(items[1] as String), true)
//                    viewModel.upl
                    viewModel.courseData.value?.courseBannerUrl = items[1] as String
                    binding.ivHeader.setImageURI(Uri.parse(items[1] as String))
                    binding.tvHeader.gone()
                    binding.ivEditBanner.visible()
                }
                DialogType.CLICK_LOGO -> {
                    viewModel.courseData.value?.courseLogoUrl = items[1] as String

                    viewModel.uploadImage(File(items[1] as String), false)
                    binding.ivLogo.setImageURI(Uri.parse(items[1] as String))
                    binding.tvLogo.gone()
                    binding.ivEditLogo.visible()
                }
            }
        }
    }
}
