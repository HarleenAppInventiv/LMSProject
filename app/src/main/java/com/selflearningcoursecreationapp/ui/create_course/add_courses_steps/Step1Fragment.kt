package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep1Binding
import com.selflearningcoursecreationapp.ui.dialog.CourseCategoriesOptionDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick


class Step1Fragment : BaseFragment<FragmentStep1Binding>(), HandleClick,
    BaseBottomSheetDialog.IDialogClick {
    private val viewModel: AddCourseViewModel by viewModels({ if (parentFragment != null) requireParentFragment() else this })
    var descHTML = ""
    var keyAwayHTML = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        binding.step1Click = this


        requireActivity().supportFragmentManager.setFragmentResultListener(
            "valueHTML",
            viewLifecycleOwner
        ) { requestKey, bundle ->
            val value = bundle.getString("value")
            val type = bundle.getInt("type")
            if (type == Constant.DESC) {
                descHTML = value.toString()
                binding.evEnterDescription.setText(Html.fromHtml(value))
            } else {
                keyAwayHTML = value.toString()
                binding.evEnterKeyTakeaway.setText(Html.fromHtml(value))

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
//                        arguments = bundleOf("type" to Constant.COURSE)
                        setOnDialogClickListener(this@Step1Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.ev_choose_course_language -> {
                    CourseCategoriesOptionDialog().apply {
//                        arguments = bundleOf("type" to Constant.COURSE)
                        setOnDialogClickListener(this@Step1Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.ev_enter_title -> {

                }
                R.id.ev_enter_description -> {
                    var action =
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.DESC, descHTML
                        )
                    findNavController().navigate(action)

                }
                R.id.ev_enter_key_takeaway -> {
                    var action =
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.KEY_TAKEAWAY, keyAwayHTML
                        )
                    findNavController().navigate(action)
                }
            }
        }


    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            var type = items[0] as Int
            var value = items[1] as String
            when (type) {
                Constant.COURSE -> {
                    binding.evChooseCourseCategory.setText(value)
                }
                Constant.LANG -> {
                    binding.evChooseCourseLanguage.setText(value)

                }
            }
        }
    }


}