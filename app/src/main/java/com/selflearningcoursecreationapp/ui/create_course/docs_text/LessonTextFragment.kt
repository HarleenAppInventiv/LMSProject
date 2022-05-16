package com.selflearningcoursecreationapp.ui.create_course.docs_text

import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentLessonTextBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
import org.koin.androidx.viewmodel.ext.android.viewModel


class LessonTextFragment : BaseFragment<FragmentLessonTextBinding>() {
    var sectionId = 0
    var lectureId = 0
    var courseId = 0
    var descHTML = ""

    private val viewModel: TextViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        menu.close()
        inflater.inflate(R.menu.course_menu, menu)
    }

    private fun initUI() {
        setHasOptionsMenu(true)
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.textLesson = viewModel
        arguments?.let {
            sectionId = it.getInt("sectionId")
            lectureId = it.getInt("lectureId")
            courseId = it.getInt("courseId")
        }
        activityResultListener()


        binding.btAdd.setOnClickListener {
            viewModel.textValidations(sectionId, MEDIA_TYPE.TEXT, "", lectureId, courseId)
        }

        binding.edtText.setOnClickListener {
            var action =
                LessonTextFragmentDirections.actionLessonTextFragmentToTextEditorFragment(
                    Constant.DESC, viewModel.courseData.value?.courseDescription ?: "", 0
                )
            findNavController().navigate(action)
        }
    }


    override fun getLayoutRes() = R.layout.fragment_lesson_text
    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)

        when (apiCode) {
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                (value as BaseResponse<UserProfile>)
                value.resource?.lectureTitle = binding.edtDocTitle.content()
                requireActivity().supportFragmentManager.setFragmentResult(
                    "response",
                    bundleOf("value" to value.resource)
                )
                findNavController().navigateUp()
            }
        }

    }

    private fun activityResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            "valueHTML",
            viewLifecycleOwner
        ) { _, bundle ->
            val value = bundle.getString("value")
            val type = bundle.getInt("type")
            if (type == Constant.DESC) {
                descHTML = value.toString()
                viewModel.courseData.value?.courseDescription = value ?: ""
                binding.edtText.setText(Html.fromHtml(value))
                viewModel.courseData.value?.notifyChange()
//                binding.tvWordCount.apply {
//                    text = "${count}"
//                    if (count < 500) {
//                        setTextColor(ContextCompat.getColor(context, R.color.black))
//                    } else {
//                        setTextColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
//                    }
//                }
            }
        }
    }


}
