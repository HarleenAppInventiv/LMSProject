package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep1Binding
import com.selflearningcoursecreationapp.extensions.disableCopyPaste
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.ui.dialog.CourseCategoriesOptionDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.ValidationConst


class Step1Fragment : BaseFragment<FragmentStep1Binding>(), HandleClick,
    BaseBottomSheetDialog.IDialogClick, View.OnTouchListener {
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment() })
    private var filter: InputFilter? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    fun init() {
        binding.step1Click = this
        binding.step1 = viewModel


        binding.evEnterTitle.setOnTouchListener(this)

        binding.evEnterKeyTakeaway.setOnTouchListener(this)
        binding.evEnterDescription.setOnTouchListener(this)
        binding.evEnterTitle.doAfterTextChanged { text ->
            binding.tvTitleTotalChar.apply {
                setText("${text?.length}")
                if (text?.length!! < ValidationConst.MAX_COURSE_TITLE_LENGTH) {
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                } else {
                    setTextColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
                }
            }
        }
    }


    override fun getLayoutRes() = R.layout.fragment_step1

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.ev_choose_course_category -> {
                    CourseCategoriesOptionDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.CATEGORY,
                            "selectedId" to viewModel.courseData.value?.categoryId
                        )
                        setOnDialogClickListener(this@Step1Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.ev_choose_course_language -> {


                    CourseCategoriesOptionDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.LANGUAGE,
                            "list" to viewModel.masterData.languages?.list,
                            "selectedId" to viewModel.courseData.value?.languageId
                        )
                        setOnDialogClickListener(this@Step1Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.ev_enter_description -> {
                    val action =
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.DESC, viewModel.courseData.value?.courseDescription ?: "", 1
                        )
                    findNavController().navigate(action)
                }
                R.id.ev_enter_key_takeaway -> {
                    val action =
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.KEY_TAKEAWAY,
                            viewModel.courseData.value?.keyTakeaways ?: "",
                            from = 1
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val value = items[1] as CategoryData
            when (type) {
                DialogType.CATEGORY -> {
                    viewModel.courseData.value?.categoryId = value.id ?: 0
                    binding.evChooseCourseCategory.setText(value.name)
                    viewModel.notifyData()
                }
                DialogType.LANGUAGE -> {
                    viewModel.courseData.value?.languageId = value.id ?: 0
                    binding.evChooseCourseLanguage.setText(value.name)

                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        //handled in AddCourseBaseFragment

    }

    override fun onResume() {
        super.onResume()
        binding.evEnterDescription.disableCopyPaste()
        binding.evEnterKeyTakeaway.disableCopyPaste()
        binding.evChooseCourseCategory.disableCopyPaste()
        binding.evChooseCourseLanguage.disableCopyPaste()
    }


}