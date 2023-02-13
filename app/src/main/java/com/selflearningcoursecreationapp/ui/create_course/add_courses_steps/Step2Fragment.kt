package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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

    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment().requireParentFragment() })
    private var dialogFragment: BottomSheetDialogFragment? = null

    var isLogoOptionalId = 1 // 0 for yes, 1 for no

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
        enableFields()
        binding.tvFee.addDecimalLimiter()
        if (!viewModel.courseData.value?.keyTakeaways.isNullOrEmpty()) {
            binding.tvKeywordTitle.text = Html.fromHtml(viewModel.courseData.value?.keyTakeaways)
        }

        viewModel.courseData.value?.let {
            if (it.logoRequiredOnCertificate == true) {
                isLogoOptionalId = 0
                binding.tvIsShowLogo.text = getString(R.string.yes)
            } else {
                binding.tvIsShowLogo.text = getString(R.string.no)
                isLogoOptionalId = 1
            }


            if (!it.keyTakeaways.isNullOrEmpty()) {
                binding.tvKeywordTitle.text =
                    Html.fromHtml(viewModel.courseData.value?.keyTakeaways)
            }
            if (!it.courseLogoUrl.isNullOrEmpty() || viewModel.isCreator.value == false) {
                binding.ivLogo.loadImage(it.courseLogoUrl, R.drawable.ic_logo_default)
                binding.tvLogo.gone()
                binding.ivEditLogo.visible()

            }

            if (!it.courseBannerUrl.isNullOrEmpty()) {
                binding.ivHeader.loadImage(it.courseBannerUrl, R.drawable.ic_default_banner)
                binding.tvHeader.gone()
                binding.ivEditBanner.visibleView(viewModel.isCreator.value == true)

            }

        }

        coAuthorHandling()

        viewModel.isCreator.observe(viewLifecycleOwner) {
            coAuthorHandling()
        }


    }

    private fun enableFields() {

        binding.svParent.isEnabled = viewModel.courseData.value?.enableFields ?: true
        binding.svParent.isClickable = viewModel.courseData.value?.enableFields ?: true

        binding.disableView.visibleView(!(viewModel.courseData.value?.enableFields ?: true))


        binding.svParent.alpha = if (viewModel.courseData.value?.enableFields ?: true) 1f else 0.3f
    }

    private fun coAuthorHandling() {
        viewModel.getCoAuthor()?.let {

            binding.noEditCL.visibleView(it.courseLogoURL.isNullOrEmpty())
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
        }

    }


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.iv_header, R.id.tv_header -> {
                    if (viewModel.courseData.value?.courseBannerUrl.isNullOrEmpty()) {
                        if (dialogFragment?.isVisible == true) {
                            dialogFragment?.dismiss()
                        }
                        dialogFragment = UploadImageOptionsDialog().apply {
                            arguments = bundleOf("type" to DialogType.CLICK_BANNER)

                            setOnDialogClickListener(this@Step2Fragment)
                        }
                        dialogFragment?.show(childFragmentManager, "")

                    }
                }
                R.id.iv_edit_banner -> {
                    if (dialogFragment?.isVisible == true) {
                        dialogFragment?.dismiss()
                    }
                    dialogFragment = UploadImageOptionsDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.CLICK_BANNER,

                            )
                        setOnDialogClickListener(this@Step2Fragment)
                    }
                    dialogFragment?.show(childFragmentManager, "")
                }
                R.id.iv_logo, R.id.tv_logo -> {
                    if (viewModel.courseData.value?.courseLogoUrl.isNullOrEmpty()) {
                        if (dialogFragment?.isVisible == true) {
                            dialogFragment?.dismiss()
                        }
                        dialogFragment = UploadImageOptionsDialog().apply {
                            arguments = bundleOf("type" to DialogType.CLICK_LOGO)
                            setOnDialogClickListener(this@Step2Fragment)
                        }
                        dialogFragment?.show(childFragmentManager, "")
                    }
                }
                R.id.iv_coauthor_edit_logo -> {
                    if (dialogFragment?.isVisible == true) {
                        dialogFragment?.dismiss()
                    }
                    dialogFragment = UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_CO_AUTHOR)
                        setOnDialogClickListener(this@Step2Fragment)
                    }
                    dialogFragment?.show(childFragmentManager, "")

                }
                R.id.iv_edit_logo -> {
                    val imageType = if (viewModel.courseData.value?.isCoAuthor == true) {
                        DialogType.CLICK_CO_AUTHOR
                    } else {
                        DialogType.CLICK_LOGO

                    }
                    if (dialogFragment?.isVisible == true) {
                        dialogFragment?.dismiss()
                    }
                    dialogFragment = UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to imageType)
                        setOnDialogClickListener(this@Step2Fragment)
                    }
                    dialogFragment?.show(childFragmentManager, "")
                }
                R.id.tv_is_show_logo -> {
                    if (dialogFragment?.isVisible == true) {
                        dialogFragment?.dismiss()
                    }
                    dialogFragment = SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.LOGO_OPTION,
                            "title" to this@Step2Fragment.baseActivity.getString(R.string.would_you_like_to_show_same_logo_to_the_certification),
                            "list" to arrayListOf<SingleChoiceData>(
                                SingleChoiceData(
                                    0,
                                    viewModel.courseData.value?.logoRequiredOnCertificate,
                                    false,
                                    false,
                                    "Yes"
                                ),
                                SingleChoiceData(
                                    1,
                                    !viewModel.courseData.value?.logoRequiredOnCertificate!!,
                                    false,
                                    false,
                                    "No"
                                )
                            ),
                            "id" to isLogoOptionalId/// need to pass type for yes or no
                        )
                        setOnDialogClickListener(this@Step2Fragment)
                    }
                    dialogFragment?.show(childFragmentManager, "")
                }

                R.id.tv_type -> {
                    if (dialogFragment?.isVisible == true) {
                        dialogFragment?.dismiss()
                    }

                    viewModel.masterData.courseTypes?.list?.apply {
                        this.forEach {
                            if (it.isPaid == true) {
                                it.isEnabled = false
                            }
                        }
                    }
                    dialogFragment = SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.COURSE_TYPE,
                            "title" to this@Step2Fragment.baseActivity.getString(R.string.course_type),
                            "list" to viewModel.masterData.courseTypes?.list,
                            "id" to viewModel.courseData.value?.courseTypeId
                        )
                        setOnDialogClickListener(this@Step2Fragment)
                    }
                    dialogFragment?.show(childFragmentManager, "")
                }
                R.id.tv_audience -> {

                    if (dialogFragment?.isVisible == true) {
                        dialogFragment?.dismiss()
                    }
                    dialogFragment = MultipleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.PROFESSION,
                            "title" to this@Step2Fragment.baseActivity.getString(R.string.target_audience),
                            "list" to viewModel.masterData.professions?.list,
                            "selectedIds" to viewModel.courseData.value?.targetAudiences
                        )
                        setOnDialogClickListener(this@Step2Fragment)
                    }
                    dialogFragment?.show(childFragmentManager, "")
                }
                R.id.tv_complexity -> {

                    if (dialogFragment?.isVisible == true) {
                        dialogFragment?.dismiss()
                    }
                    dialogFragment = SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.COURSE_COMPLEXITY,
                            "title" to this@Step2Fragment.baseActivity.getString(R.string.course_complexity),
                            "list" to viewModel.masterData.courseComplexities?.list,
                            "id" to viewModel.courseData.value?.courseComplexityId
                        )
                        setOnDialogClickListener(this@Step2Fragment)
                    }
                    dialogFragment?.show(childFragmentManager, "")
                }
                R.id.tv_keyword -> {
                    val action =
                        AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.KEY_TAKEAWAY, viewModel.courseData.value?.keyTakeaways ?: ""
                        )
                    findNavController().navigateTo(action)
                }


            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            when (items[0] as Int) {
                DialogType.COURSE_COMPLEXITY -> {
                    val value = items[1] as SingleChoiceData
                    binding.tvComplexity.text = value.title
                    viewModel.courseData.value?.courseComplexityId = value.id

                }
                DialogType.LOGO_OPTION -> {
                    val value = items[1] as SingleChoiceData
                    binding.tvIsShowLogo.text = value.title
                    isLogoOptionalId = value.id ?: 1
                    viewModel.courseData.value?.logoRequiredOnCertificate =
                        if (value.id == 1) false else true
                    viewModel.courseData.value?.notifyChange()

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
                    viewModel.courseData.value?.courseBannerUrl = items[1] as String
                    viewModel.uploadImage(File(items[1] as String), true)
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
                    )
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
                    )

                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        //handled in AddCourseBaseFragment
    }

}
