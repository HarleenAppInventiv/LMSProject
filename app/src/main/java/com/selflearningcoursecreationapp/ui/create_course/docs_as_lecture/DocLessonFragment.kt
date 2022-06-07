package com.selflearningcoursecreationapp.ui.create_course.docs_as_lecture

import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


class DocLessonFragment : BaseFragment<FragmentDocLessonBinding>(),
        (String??) -> Unit {
    private val imagePickUtils: ImagePickUtils by inject()
    private val viewModel: DocViewModel by viewModel()
    var uri = ""
    var model: SectionModel? = null
    var childPosition: Int? = 0
    var lectureId: Int? = null
    var courseId: Int? = null
    var type: Int? = null
    var id: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setHasOptionsMenu(true)
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.docs = viewModel

        arguments?.let {
            val lessonData = DocLessonFragmentArgs.fromBundle(it)
            childPosition = lessonData.childPosition
            type = lessonData.type
            model = lessonData.sendSectionModel
            lectureId = lessonData.lectureId
            courseId = lessonData.courseId
            uri = lessonData.filePath
        }

        if (type == Constant.CLICK_ADD) {
            setDataFromFile()
        } else {
            viewModel.getLectureDetail(lectureId!!.toInt())
        }


        if (!childPosition.isNullOrNegative()) {
            binding.btnAddLesson.setText(baseActivity.getString(R.string.update_lesson))
//            binding.edtDocTitle.setText(model?.lessonList?.get(childPosition!!)?.lectureContentName)
        }

        binding.ivEditPdf.setOnClickListener {
            openDoc()
        }

        binding.btnAddLesson.setOnClickListener {
            viewModel.docValidations(
                model?.sectionId!!,
                MEDIA_TYPE.DOC,
                binding.edtDocTitle.content(),
                lectureId ?: -1,
                courseId!!,
                id,
                TimeUnit.MINUTES.toMillis(
                    binding.edtReadTime.content().toLong()
                )
            )
        }

    }

    override fun getLayoutRes() = R.layout.fragment_doc_lesson

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        menu.close()
        inflater.inflate(R.menu.course_menu, menu)
    }

    fun setDataFromFile() {
        viewModel.notifyData()
        val file = File(Uri.parse(uri).path)
        viewModel.docLiveData.value?.lectureContentName = file.name
        binding.tvDocSize.text = getStringSizeLengthFile(file.length())
        uploadServer(file.name, file)


    }

    fun getStringSizeLengthFile(size: Long): String? {
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        if (size < sizeMb) return df.format(size / sizeKb)
            .toString() + " Kb" else if (size < sizeGb) return df.format(size / sizeMb)
            .toString() + " Mb" else if (size < sizeTerra) return df.format(size / sizeGb)
            .toString() + " Gb"
        return ""
    }

    override fun invoke(p1: String?) {
        uri = p1.toString()
        Log.d("varun", "invoke: ${p1}")
        setDataFromFile()
    }

    fun openDoc() {

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
                    id = it.id.toString()
                }
            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    id = it.lectureContentId.toString()

                    binding.tvDocName.text = it.lectureContentName
                    binding.edtDocTitle.setText(it.lectureTitle)
                    var min = (it.lectureContentDuration?.toLong()?.div(10000))?.div(60000)
                    binding.edtReadTime.setText(min.toString())
                    binding.tvDocSize.setText(
                        android.text.format.Formatter.formatFileSize(
                            requireActivity(),
                            it.lectureContentSize!!
                        )
                    )
                }
            }
        }
    }

    fun uploadServer(fileName: String, file: File) {
        viewModel.uploadContent(
            courseId,
            model!!.sectionId,
            lectureId!!,
            fileName,
            file,
            MEDIA_TYPE.DOC,
            "",
            0
        )
    }
}



