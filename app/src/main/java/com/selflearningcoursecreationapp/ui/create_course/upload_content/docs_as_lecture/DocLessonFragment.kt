package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_as_lecture

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentDocLessonBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.isNullOrNegative
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.DecimalFormat


class DocLessonFragment : BaseFragment<FragmentDocLessonBinding>(),
        (String?) -> Unit, View.OnTouchListener {
    private val imagePickUtils: ImagePickUtils by inject()
    private val viewModel: DocViewModel by viewModel()
    private var childPosition: Int? = 0

    private var type: Int? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setHasOptionsMenu(true)
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.docs = viewModel
        binding.edtDocTitle.setOnTouchListener(this)

        arguments?.let {
            val lessonData = DocLessonFragmentArgs.fromBundle(it)
            childPosition = lessonData.childPosition
            type = lessonData.type
            viewModel.model = lessonData.sendSectionModel
            viewModel.lectureId = lessonData.lectureId
            viewModel.courseId = lessonData.courseId
            viewModel.uri = lessonData.filePath
        }

        if (type == Constant.CLICK_ADD) {
            setDataFromFile()
        } else {
            viewModel.getLectureDetail()
        }


        if (!childPosition.isNullOrNegative()) {
            binding.btnAddLesson.text = baseActivity.getString(R.string.update_lesson)
        }

        binding.ivEditPdf.setOnClickListener {
            openDoc()
        }

        binding.btnAddLesson.setOnClickListener {
            viewModel.docValidations()
        }

    }

    override fun getLayoutRes() = R.layout.fragment_doc_lesson

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        menu.close()
        inflater.inflate(R.menu.course_menu, menu)
    }


    private fun setDataFromFile() {
        viewModel.notifyData()
        val file = File(Uri.parse(viewModel.uri).path ?: "")
        viewModel.docLiveData.value?.lectureContentName = file.name
        binding.tvDocSize.text = getStringSizeLengthFile(file.length())
        uploadServer()
    }

    private fun getStringSizeLengthFile(size: Long): String {
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        return when {
            size < sizeMb -> df.format(size / sizeKb)
                .toString() + " Kb"
            size < sizeGb -> df.format(size / sizeMb)
                .toString() + " Mb"
            size < sizeTerra -> df.format(size / sizeGb)
                .toString() + " Gb"
            else -> ""
        }
    }

    override fun invoke(p1: String?) {
        viewModel.uri = p1.toString()
        setDataFromFile()
    }

    private fun openDoc() {

        imagePickUtils.openDocs(
            baseActivity,
            this,
            registry = requireActivity().activityResultRegistry
        )


    }

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
                (value as BaseResponse<ImageResponse>).resource?.let {
                    viewModel.contentId = it.id.toString()
                }
            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    viewModel.contentId = it.lectureContentId.toString()

                    binding.tvDocName.text = it.lectureContentName
                    binding.edtDocTitle.setText(it.lectureTitle)
                    val min = (it.lectureContentDuration)?.div(60000)
                    binding.edtReadTime.setText(min.toString())
                    binding.tvDocSize.text = android.text.format.Formatter.formatFileSize(
                        requireActivity(),
                        it.lectureContentSize!!
                    )
                }
            }
        }
    }

    private fun uploadServer() {
        viewModel.uploadContent()
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }
}



