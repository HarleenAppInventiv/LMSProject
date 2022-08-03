package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_text

import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentLessonTextBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.disableCopyPaste
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel


class LessonTextFragment : BaseFragment<FragmentLessonTextBinding>() {

    private var childPosition: Int? = 0
    private var isFirstTime: Boolean = true

    private var type: Int? = null
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
//        binding.edtDocTitle.setOnTouchListener(this)

        arguments?.let {
            val lessonData = LessonTextFragmentArgs.fromBundle(it)
            childPosition = lessonData.childPosition
            type = lessonData.type
            viewModel.model = lessonData.sendSectionModel
            viewModel.lectureId = lessonData.lectureId
            viewModel.courseId = lessonData.courseId


        }
        activityResultListener()

        if (type == Constant.CLICK_EDIT && isFirstTime) {

            viewModel.getLectureDetail()
            isFirstTime = false
        }

        binding.btAdd.setOnClickListener {


            viewModel.textValidations()

        }

        if (childPosition != null && childPosition != -1) {
            binding.btAdd.text = baseActivity.getString(R.string.update_lesson)
//            binding.edtDocTitle.setText(model?.lessonList?.get(childPosition!!)?.lectureContentName)
        }

        binding.edtText.setOnClickListener {
            val actionNew =
                LessonTextFragmentDirections.actionLessonTextFragmentToTextEditorFragment(
                    Constant.DESC,
                    viewModel.textLiveData.value?.textFileText ?: "",
                    0,


                    )

            findNavController().navigate(actionNew)
        }
    }


    override fun getLayoutRes() = R.layout.fragment_lesson_text


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ADD_LECTURE_PATCH -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureContentName = binding.edtDocTitle.content()
                    if (childPosition != null && childPosition != -1) {
                        viewModel.model?.lessonList?.set(childPosition!!, it)

                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))
                    } else {
                        viewModel.model?.lessonList?.add(it)
                        showToastLong(baseActivity.getString(R.string.lesson_saved_successfully))
                    }
                }
                viewModel.model?.notifyPropertyChanged(BR.uploadLesson)

                findNavController().navigateUp()
            }
            ApiEndPoints.API_CONTENT_UPLOAD -> {

            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    binding.edtText.setText(Html.fromHtml(it.textFileText))

                    binding.textLesson?.textLiveData?.value?.textFileText = it.textFileText
                    binding.edtDocTitle.setText(it.lectureTitle)
                    val min = it.lectureContentDuration?.div(60000) ?: 0
                    binding.edtTime.setText(min.toString())
                    viewModel.lectureContentId = it.lectureContentId
                }
            }
        }

    }

    private fun activityResultListener() {
        setFragmentResultListener(
            "valueHTML"
        ) { _, bundle ->
            val value = bundle.getString("value")
            val type = bundle.getInt("type")
            if (type == Constant.DESC) {
                viewModel.textLiveData.value?.textFileText = value ?: ""
                viewModel.textLiveData.value?.notifyChange()
//                binding.edtDocTitle.setLimitedText(Html.fromHtml(value?:"").toString(),9)
                uploadServer()

            }
        }
    }

    private fun uploadServer() {
        viewModel.uploadContent()
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onResume() {
        super.onResume()
        binding.edtText.disableCopyPaste()

    }
}
