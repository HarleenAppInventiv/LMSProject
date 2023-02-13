package com.selflearningcoursecreationapp.ui.course_details.quizList

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentQuiZListForModCreatorBinding
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.create_course.add_assessment.AssessmentDetailAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class QuiZListForModCreatorFragment : BaseFragment<FragmentQuiZListForModCreatorBinding>() {
    private var adapter: AssessmentDetailAdapter? = null
    private val viewModel: CourseDetailVM by viewModel()
    override fun getLayoutRes() = R.layout.fragment_qui_z_list_for_mod_creator
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observer()
        arguments?.let {
//            setAdapter(it.getParcelable<QuizData>("list"))
            viewModel.courseId = it.getInt("courseId")
            baseActivity.setToolbar(title = it.getString("title"))
            viewModel.getQuizQuestions(it.getInt("quizId"))

        }

    }

    private fun observer() {
        viewModel.quizData.observe(viewLifecycleOwner) {
            setAdapter(it)
        }
    }

    override fun onApiRetry(apiCode: String) {
    }

    private fun setAdapter(data: QuizData?) {
        adapter = AssessmentDetailAdapter(
            data?.list ?: ArrayList(),
            data?.markOfCorrectAns ?: 0
        )
        binding.rvAssessment.adapter = adapter


    }

}