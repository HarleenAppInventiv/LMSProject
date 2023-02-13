package com.selflearningcoursecreationapp.ui.content_creator.course_detail.assessment

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentContentCreatorAssessmentBinding
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.create_course.add_assessment.AssessmentDetailAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class ContentCreatorAssessmentFragment : BaseFragment<FragmentContentCreatorAssessmentBinding>() {
    private var adapter: AssessmentDetailAdapter? = null
    private val viewModel: CourseDetailVM by viewModel()

    override fun getLayoutRes() = R.layout.fragment_content_creator_assessment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        initUI()
    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observe()
        arguments?.let {
            viewModel.getAssessment(it.getInt("assessmentId"))
//            baseActivity.setToolbar(title = it.getString("assessmentName"))
//            showToastShort( it.getInt("assessmentId").toString() +" "+it.getString("assessmentName"))
        }


    }

    private fun observe() {
        viewModel.assessmentData.observe(viewLifecycleOwner) {
            baseActivity.setToolbar(title = it?.assessmentName)
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