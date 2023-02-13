package com.selflearningcoursecreationapp.ui.create_course.add_assessment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.LessonArgs
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseBaseNewFragmentDirections
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick

@SuppressLint("NotifyDataSetChanged")

class AssessmentFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentAssessmentBinding>(),
    HandleClick {
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment().requireParentFragment() })
    private lateinit var mainFragment: Fragment

    private var adapter: AssessmentDetailAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainFragment = requireParentFragment().requireParentFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }


    private fun initUI() {
        enableFields()
        binding.parentNSV.gone()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.handleClick = this

        if (!viewModel.courseData.value?.assessmentId.isNullOrZero()) {
            viewModel.getAssessmentQues()
            observeAssessmentData()

        } else {
            viewModel.assessmentData.value = QuizData()
            binding.tvNoData.text =
                baseActivity.getString(R.string.there_are_no_assessments_in_n_this_course)
            binding.btnAddAssessment.text = baseActivity.getString(R.string.add_assessment)
            adapter?.notifyDataSetChanged()
            adapter = null

            binding.llNoAssessment.visible()
            binding.parentNSV.visibleView(true)
        }


    }

    private fun enableFields() {

        binding.parentNSV.isEnabled = viewModel.courseData.value?.enableFields ?: true
        binding.parentNSV.isClickable = viewModel.courseData.value?.enableFields ?: true

        binding.disableView.visibleView(!(viewModel.courseData.value?.enableFields ?: true))


        binding.parentNSV.alpha = if (viewModel.courseData.value?.enableFields ?: true) 1f else 0.3f
    }

    private fun observeAssessmentData() {
        viewModel.assessmentData.observe(viewLifecycleOwner) {
            if (!it.assessmentId.isNullOrZero()) {
                if (it.list.isNullOrEmpty() && it.assessmentName.isNullOrEmpty()) {
                    viewModel.deleteAssessment(false)
                } else if (it.list.isNullOrEmpty() && !it.assessmentName.isNullOrEmpty()) {
                    binding.tvNoData.text =
                        baseActivity.getString(R.string.there_are_no_ques_in_n_this_assessment)
                    binding.btnAddAssessment.text = baseActivity.getString(R.string.add_questions)
                }
            } else {
                binding.tvNoData.text =
                    baseActivity.getString(R.string.there_are_no_assessments_in_n_this_course)
                binding.btnAddAssessment.text = baseActivity.getString(R.string.add_assessment)
            }
            binding.llNoAssessment.visibleView(it.list.isNullOrEmpty())
            binding.rvAssessment.visibleView(!it.list.isNullOrEmpty())
            adapter?.notifyDataSetChanged()
            adapter = null
            setAdapter()
            binding.parentNSV.visibleView(true)

        }
    }

    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = AssessmentDetailAdapter(
                viewModel.assessmentData.value?.list ?: ArrayList(),
                viewModel.assessmentData.value?.markOfCorrectAns ?: 0
            )
            binding.rvAssessment.adapter = adapter
        }
    }

    override fun getLayoutRes() = R.layout.fragment_assessment
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.btn_add_assessment -> {
                    val lessonArgs = LessonArgs(
                        courseId = viewModel.courseData.value?.courseId ?: 0,
                        type = Constant.CLICK_ADD,
                        isQuiz = false,
                        courseData = viewModel.courseData.value
                    )
                    mainFragment.findNavController().navigateTo(
                        AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToAddQuizFragment(
                            lessonArgs
                        )
                    )
                }
//                R.id.btn_submit -> {
//                    CommonAlertDialog.builder(requireContext())
//                        .title(getString(R.string.submit_succesfully))
//                        .description(getString(R.string.submit_succesfully_done))
//                        .positiveBtnText(getString(R.string.okay))
//                        .hideNegativeBtn(true)
//                        .icon(R.drawable.ic_assessment_submitted)
//                        .build()
//
//                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        //handled in AddCourseBaseFragment

    }

}