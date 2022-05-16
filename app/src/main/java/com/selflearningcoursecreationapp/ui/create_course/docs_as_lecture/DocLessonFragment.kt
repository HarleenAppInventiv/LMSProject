package com.selflearningcoursecreationapp.ui.create_course.docs_as_lecture

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentDocLessonBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.DecimalFormat


class DocLessonFragment : BaseFragment<FragmentDocLessonBinding>(),
        (String??) -> Unit {
    private val imagePickUtils: ImagePickUtils by inject()
    private val viewModel: DocViewModel by viewModel()
    var uri = ""
    var sectionId = 0
    var lectureId = 0
    var courseId = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setHasOptionsMenu(true)
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.docs = viewModel
        openDoc()

        arguments?.let {
//            uri = it.getString("docPath").toString()
            sectionId = it.getInt("sectionId")
            lectureId = it.getInt("lectureId")
            courseId = it.getInt("courseId")
        }

        binding.ivEditPdf.setOnClickListener {
            openDoc()
        }

        binding.btnAddLesson.setOnClickListener {
//            viewModel.addLecture(sectionID, 3)
            viewModel.docValidations(
                sectionId,
                MEDIA_TYPE.DOC,
                binding.edtDocTitle.content(),
                lectureId, courseId
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
        val file = File(Uri.parse(uri).path)
        binding.tvDocName.text = getFileName(file)
        binding.tvDocSize.text = getStringSizeLengthFile(file.length())
    }

    fun getFileName(file: File): String? {
        return file.name
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
}



