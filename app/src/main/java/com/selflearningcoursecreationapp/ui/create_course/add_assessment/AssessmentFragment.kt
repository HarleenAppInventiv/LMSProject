package com.selflearningcoursecreationapp.ui.create_course.add_assessment

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.HandleClick


class AssessmentFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentAssessmentBinding>(),
    HandleClick {

    lateinit var adapterAssessment: AdpterCourseAssessment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    private fun initUI() {
        binding.handleClick = this
        binding.llNoAssessment.visible()
        binding.btnEdit.gone()


    }

    override fun getLayoutRes() = R.layout.fragment_assessment
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.btn_add_assessment -> {
                    binding.llNoAssessment.gone()
                    binding.btnEdit.visible()
                    binding.recyclerAssignmentList.apply {
                        visible()
                        adapterAssessment = AdpterCourseAssessment()
                        adapter = adapterAssessment
                    }


                }
                R.id.btn_submit -> {

                    CommonAlertDialog.builder(requireContext())
                        .title(getString(R.string.submit_succesfully))
                        .description(getString(R.string.submit_succesfully_done))
                        .positiveBtnText(getString(R.string.ok))
                        .hideNegativeBtn(true)
                        .icon(R.drawable.ic_assessment_submitted)
                        .getCallback {
                            if (it) {


                            }
                        }.build()

                }
            }
        }
    }

}