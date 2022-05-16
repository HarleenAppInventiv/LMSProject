package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Build
import android.os.Bundle
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
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.ui.dialog.CourseCategoriesOptionDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.HandleClick


class Step1Fragment : BaseFragment<FragmentStep1Binding>(), HandleClick,
    BaseBottomSheetDialog.IDialogClick {
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment() })
    var descHTML = ""
    var keyAwayHTML = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        binding.step1Click = this
        binding.step1 = viewModel


//        activityResultListener()

        binding.evEnterTitle.doAfterTextChanged { text ->
            binding.tvTitleTotalChar.apply {
                setText("${text?.length}")
                if (text?.length!! < 256) {
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                } else {
                    setTextColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
                }
            }
        }
    }

//    private fun activityResultListener() {
//        requireActivity().supportFragmentManager.setFragmentResultListener(
//            "valueHTML",
//            viewLifecycleOwner
//        ) { _, bundle ->
//            val value = bundle.getString("value")
//            val type = bundle.getInt("type")
//            if (type == Constant.DESC) {
//                descHTML = value.toString()
//                var count = (Html.fromHtml(value).toString()).wordCount()
//                viewModel.courseData.value?.courseDescription = value ?: ""
//                binding.evEnterDescription.setText(Html.fromHtml(value))
//                viewModel.notifyData()
//                binding.tvWordCount.apply {
//                    text = "${count}"
//                    if (count < 500) {
//                        setTextColor(ContextCompat.getColor(context, R.color.black))
//                    } else {
//                        setTextColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
//                    }
//                }
//            }
//        }
//    }


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
                    var action =
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.DESC, viewModel.courseData.value?.courseDescription ?: "", 1
                        )
                    findNavController().navigate(action)
                }
//                R.id.ev_enter_key_takeaway -> {
//                    var action =
//                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
//                            Constant.KEY_TAKEAWAY, keyAwayHTML
//                        )
//                    findNavController().navigate(action)
//                }
            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            var type = items[0] as Int
            var value = items[1] as CategoryData
            when (type) {
                DialogType.CATEGORY -> {
                    viewModel.courseData.value?.categoryId = value.id!!
                    binding.evChooseCourseCategory.setText(value.name)
                    viewModel.notifyData()
                }
                DialogType.LANGUAGE -> {
                    viewModel.courseData.value?.languageId = value.id!!
                    binding.evChooseCourseLanguage.setText(value.name)

                }
            }
        }
    }
}