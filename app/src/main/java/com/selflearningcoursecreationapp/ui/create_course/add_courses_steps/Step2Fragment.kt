package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep2Binding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog
import com.selflearningcoursecreationapp.ui.dialog.multipleChoice.MultipleChoiceBottomDialog
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
        binding.tvFee.addDecimalLimiter()
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
                binding.ivLogo.loadImage(it.courseLogoUrl, R.drawable.ic_logo_default)
                binding.tvLogo.gone()
                binding.ivEditLogo.visible()

            }

            if (!it.courseBannerUrl.isNullOrEmpty()) {
                binding.ivHeader.loadImage(it.courseBannerUrl, R.drawable.ic_default_banner)
                binding.tvHeader.gone()
                binding.ivEditBanner.visible()
            }

        }

        binding.noEditCL.visibleView(viewModel.courseData.value?.isCoAuthor == true && viewModel.getCoAuthor()?.courseLogoURL.isNullOrEmpty())

        if (!viewModel.getCoAuthor()?.courseLogoURL.isNullOrEmpty()) {
            viewModel.courseData.value?.courseLogoId = viewModel.getCoAuthor()?.courseLogoId
            binding.ivCoauthorLogo.loadImage(
                viewModel.getCoAuthor()?.courseLogoURL,
                R.drawable.ic_logo_default
            )
            binding.ivLogo.loadImage(
                viewModel.getCoAuthor()?.courseLogoURL,
                R.drawable.ic_logo_default
            )

        }

        viewModel.isCreator.observe(viewLifecycleOwner, Observer {
            binding.noEditCL.visibleView(viewModel.courseData.value?.isCoAuthor == true && viewModel.getCoAuthor()?.courseLogoURL.isNullOrEmpty())
            if (!viewModel.getCoAuthor()?.courseLogoURL.isNullOrEmpty()) {
                viewModel.courseData.value?.courseLogoId = viewModel.getCoAuthor()?.courseLogoId
                binding.ivCoauthorLogo.loadImage(
                    viewModel.getCoAuthor()?.courseLogoURL,
                    R.drawable.ic_logo_default
                )
                binding.ivLogo.loadImage(
                    viewModel.getCoAuthor()?.courseLogoURL,
                    R.drawable.ic_logo_default
                )

            }
        })


    }

//
//    private fun activityResultListener() {
//        requireActivity().supportFragmentManager.setFragmentResultListener(
//            "valueHTML",
//            viewLifecycleOwner
//        ) { _, bundle ->
//            val value = bundle.getString("value")
//            val type = bundle.getInt("type")
//            if (type == Constant.KEY_TAKEAWAY) {
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
                R.id.iv_header, R.id.tv_header -> {
                    if (viewModel.courseData.value?.courseBannerUrl.isNullOrEmpty()) {
                        UploadImageOptionsDialog().apply {
                            arguments = bundleOf("type" to DialogType.CLICK_BANNER)

                            setOnDialogClickListener(this@Step2Fragment)
                        }.show(childFragmentManager, "")
                    }
                }
                R.id.iv_edit_banner -> {
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_BANNER)

                        setOnDialogClickListener(this@Step2Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.iv_logo, R.id.tv_logo -> {
                    if (viewModel.courseData.value?.courseLogoUrl.isNullOrEmpty()) {
                        UploadImageOptionsDialog().apply {
                            arguments = bundleOf("type" to DialogType.CLICK_LOGO)
                            setOnDialogClickListener(this@Step2Fragment)
                        }.show(childFragmentManager, "")
                    }
                }
                R.id.iv_coauthor_edit_logo -> {
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_CO_AUTHOR)
                        setOnDialogClickListener(this@Step2Fragment)
                    }.show(childFragmentManager, "")

                }
                R.id.iv_edit_logo -> {
                    val imageType = if (viewModel.courseData.value?.isCoAuthor == true) {
                        DialogType.CLICK_CO_AUTHOR
                    } else {
                        DialogType.CLICK_LOGO

                    }
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to imageType)
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

                    MultipleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.PROFESSION,
                            "title" to this@Step2Fragment.baseActivity.getString(R.string.target_audience),
                            "list" to viewModel.masterData.professions?.list,
                            "selectedIds" to viewModel.courseData.value?.targetAudiences
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
                    viewModel.courseData.value?.notifyChange()
                    binding.tvFee.doEnable(value.isPaid ?: false)
                    if (value.isPaid == true) {
                        binding.tvFee.setText("")
                    } else {
                        binding.tvFee.setText("0")
                    }
                }
                DialogType.PROFESSION -> {
                    val value = items[1] as ArrayList<SingleChoiceData>
                    viewModel.courseData.value?.targetAudiences = value
                    binding.tvAudience.text = value.map { it.title }.joinToString()


                }
                DialogType.CLICK_BANNER -> {
                    viewModel.uploadImage(File(items[1] as String), true)
//                    viewModel.upl
                    viewModel.courseData.value?.courseBannerUrl = items[1] as String
//                    binding.ivHeader.setImageURI(Uri.parse(items[1] as String))
                    binding.ivHeader.loadImage(items[1] as String, R.drawable.ic_default_banner)
                    binding.tvHeader.gone()
                    binding.ivEditBanner.visible()
                }
                DialogType.CLICK_LOGO -> {
                    viewModel.courseData.value?.courseLogoUrl = items[1] as String

                    viewModel.uploadImage(File(items[1] as String), false)
                    binding.ivLogo.loadImage(
                        items[1] as String,
                        R.drawable.ic_logo_default
                    )/*setImageURI(Uri.parse(items[1] as String))*/
                    binding.tvLogo.gone()
                    binding.ivEditLogo.visible()
                }
                DialogType.CLICK_CO_AUTHOR -> {
                    viewModel.courseData.value?.courseLogoUrl = items[1] as String
                    binding.ivLogo.loadImage(
                        items[1] as String,
                        R.drawable.ic_logo_default
                    )
                    viewModel.uploadImage(File(items[1] as String), false)
                    binding.ivCoauthorLogo.loadImage(
                        items[1] as String,
                        R.drawable.ic_logo_default
                    )/*setImageURI(Uri.parse(items[1] as String))*/

                }
            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
//        super.onResponseSuccess(value, apiCode)
        showLog("CHILD", "apiSuccess")
    }
}
