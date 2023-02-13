package com.selflearningcoursecreationapp.ui.moderator.courseDetails.Assessment

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModAssessmentBinding
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.ui.create_course.add_assessment.AssessmentDetailAdapter


class ModAssessmentFragment : BaseFragment<FragmentModAssessmentBinding>() {
    //    private val viewModel: ModCourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private var adapter: AssessmentDetailAdapter? = null

    override fun getLayoutRes() = R.layout.fragment_mod_assessment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    private fun initUI() {
        callMenu()
        arguments?.let {
            setAdapter(it.getParcelable<QuizData>("list"))
            baseActivity.setToolbar(title = it.getString("title"))
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