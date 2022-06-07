package com.selflearningcoursecreationapp.ui.create_course.docs_text

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
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class LessonTextFragment : BaseFragment<FragmentLessonTextBinding>() {

    var descHTML = ""
    var model: SectionModel? = null
    var childPosition: Int? = 0
    var lectureId: Int? = null
    var courseId: Int? = null
    var type: Int? = null
    var lectureContentId: String? = ""
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
            val lessonData = LessonTextFragmentArgs.fromBundle(it)
            childPosition = lessonData.childPosition
            type = lessonData.type
            model = lessonData.sendSectionModel
            lectureId = lessonData.lectureId
            courseId = lessonData.courseId


        }
        activityResultListener()

        if (type == Constant.CLICK_EDIT) {
            viewModel.getLectureDetail(lectureId!!.toInt())
        }

        binding.btAdd.setOnClickListener {


            viewModel.textValidations(
                model?.sectionId!!,
                MEDIA_TYPE.TEXT,
                binding.edtDocTitle.content(),
                lectureId ?: -1,
                courseId!!,
                lectureContentId.toString(),
                TimeUnit.MINUTES.toMillis(
                    binding.edtTime.content().toLong()
                )
            )

        }

        if (childPosition != null && childPosition != -1) {
            binding.btAdd.setText(baseActivity.getString(R.string.update_lesson))
//            binding.edtDocTitle.setText(model?.lessonList?.get(childPosition!!)?.lectureContentName)
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
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureContentName = binding.edtDocTitle.content()
                    if (childPosition != null && childPosition != -1) {
                        model?.lessonList?.set(childPosition!!, it)

                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))
                    } else {
                        model?.lessonList?.add(it)
                        showToastLong(baseActivity.getString(R.string.lesson_saved_successfully))
                    }
                }
                model?.notifyPropertyChanged(BR.uploadLesson)

                findNavController().navigateUp()
            }
            ApiEndPoints.API_CONTENT_UPLOAD -> {
                (value as BaseResponse<ImageResponse>).resource?.let {
                    lectureContentId = it.id.toString()
                }
            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    binding.edtText.setText(Html.fromHtml(it.textFileText))
                    binding.edtDocTitle.setText(it.lectureTitle)
                    var min = (it.lectureContentDuration?.toLong()?.div(10000))?.div(60000)
                    binding.edtTime.setText(min.toString())
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
                uploadServer(value)
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

    fun uploadServer(value: String?) {
        viewModel.uploadContent(
            courseId,
            model!!.sectionId,
            lectureId!!,
            MEDIA_TYPE.TEXT,
            value.toString(),
            0
        )
    }

}
