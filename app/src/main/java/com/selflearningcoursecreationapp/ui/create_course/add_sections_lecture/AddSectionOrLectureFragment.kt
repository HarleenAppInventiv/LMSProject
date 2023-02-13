package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.LessonArgs
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseBaseNewFragmentDirections
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.ui.dialog.SectionMoreDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.Lecture
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*


@SuppressLint("NotifyDataSetChanged")
class AddSectionOrLectureFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentAddSectionOrLectureBinding>(),
    BaseAdapter.IViewClick, BaseBottomSheetDialog.IDialogClick, (String?) -> Unit {
    private var adapter: AddSectionAdapter? = null
    private val imagePickUtils: ImagePickUtils by inject()
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment().requireParentFragment() })
    private var mediaFrom = 0
    private var filePath = ""
    private var mOrderChanged = false
    private var _lectureId = -1
    private var isTextChanging = false
    val timeoutHandler: Handler = Handler()
    private lateinit var mainFragment: Fragment

    val typingTimeout: Runnable? = Runnable {
        isTextChanging = false
//        serviceCall()
    }
    private val touchHelper = object : TouchHelper() {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            showLog(
                "SECTION_DATA",
                "onMove >> fromPos ${viewHolder.adapterPosition} ..... toPos >> ${target.adapterPosition}"
            )

            viewModel.fromPosition = viewHolder.adapterPosition
            viewModel.toPosition = target.adapterPosition
            mOrderChanged = true

            return false
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            showLog("SECTION_DATA", "onMoved >> actionState $actionState")
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
                mOrderChanged = false
                val recyclerviewAdapter = binding.rvSections.adapter as AddSectionAdapter
                if (!viewModel.toPosition.isNullOrNegative() && !viewModel.fromPosition.isNullOrNegative()) {


                    Collections.swap(
                        viewModel.getSectionList(),
                        viewModel.toPosition,
                        viewModel.fromPosition
                    )
                    val first =
                        if ((viewModel.toPosition - 1).isNullOrNegative()) 0 else viewModel.getSectionList()
                            ?.get(viewModel.toPosition - 1)?.sectionId
//                        if (viewModel.toPosition == 0 || viewModel.toPosition == viewModel.getSectionList()?.size!! - 1) 0 else viewModel.getSectionList()
//                            ?.get(viewModel.toPosition - 1)?.sectionId

                    val second =
                        if ((viewModel.toPosition + 1) >= viewModel.getSectionList()?.size ?: 0) 0 else viewModel.getSectionList()
                            ?.get(viewModel.toPosition + 1)?.sectionId
//                        if (viewModel.toPosition == viewModel.getSectionList()?.size!! - 1 || viewModel.toPosition == 0) 0 else viewModel.getSectionList()
//                            ?.get(viewModel.toPosition + 1)?.sectionId
                    viewModel.dragAndDropSection(
                        first,
                        second
                    )



                    recyclerviewAdapter.notifyItemMoved(
                        viewModel.fromPosition,
                        viewModel.toPosition
                    )
                    recyclerviewAdapter.notifyDataSetChanged()
                    viewModel.fromPosition = -1
                    viewModel.toPosition = -1
                }
            }
//                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//                    viewHolder?.itemView?.alpha = 0.5f
//                }

        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags =
                if (isTextChanging) 0 else if (viewModel.courseData.value?.sectionData?.size ?: 0 > 1) UP or DOWN or START or END else 0
            return makeMovementFlags(dragFlags, 0)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainFragment = requireParentFragment().requireParentFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {

        enableFields()
        binding.addSectionLecture = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        handleInitList()
        binding.btnAdd.setOnClickListener {
            viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
            viewModel.addSection()
        }
        binding.btAddSection.setOnClickListener {

            viewModel.step3SingleValidation()

        }
        observeAddLecture()

        observeSectionData()
    }

    private fun enableFields() {

        binding.clParent.isEnabled = viewModel.courseData.value?.enableFields ?: true
        binding.clParent.isClickable = viewModel.courseData.value?.enableFields ?: true
        binding.disableView.visibleView(!(viewModel.courseData.value?.enableFields ?: true))
        binding.clParent.alpha = if (viewModel.courseData.value?.enableFields ?: true) 1f else 0.3f
    }

    private fun observeSectionData() {
        viewModel.sectionUpdateData.observe(
            viewLifecycleOwner
        ) { event ->
            event.getContentIfNotHandled()?.let {
                viewModel.courseData.value?.notifyPropertyChanged(BR.allDataEntered)
                when (it) {
                    Constant.CLICK_UPLOAD -> {
                        viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                        viewModel.addSection()
                        viewModel.getSectionList()
                            ?.forEach { section -> section.isVisible = false }
                        adapter?.notifyDataSetChanged()
                    }
                    Constant.CLICK_DETAILS -> {
                        binding.llNoSection.gone()
                        adapter?.notifyDataSetChanged()
                        adapter = null
                        setAdapter()
                        viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                    }
                    Constant.CLICK_ADD -> {
                        binding.llNoSection.gone()

                        setAdapter(true)
                        viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                    }

                    Constant.CLICK_SAVE -> {
                        viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.isVisible = false
                        viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.isSaved = true
                        viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.changesMade = false
                        adapter?.notifyDataSetChanged()
                        viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                        binding.llNoSection.gone()

                    }

                    else -> {
                        viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                        setAdapter()
                        if (viewModel.getSectionList().isNullOrEmpty()) {
                            adapter = null
                        }
                    }
                }
            }

        }
    }

    private fun observeAddLecture() {
        viewModel.addLectureLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { lectureId ->
                baseActivity.hideProgressBar()
                if (lectureId != 0) {
                    _lectureId = lectureId
                    when (viewModel.mediaType) {

                        MediaType.DOC -> {

                            imagePickUtils.openDocs(
                                baseActivity,
                                this,

                                )
                        }

                        MediaType.TEXT -> {
                            val lessonArgs = LessonArgs(
                                courseId = viewModel.courseData.value?.courseId ?: 0,
                                type = Constant.CLICK_ADD,
                                sectionId = viewModel.getSectionList()
                                    ?.get(viewModel.sectionAdapterPosition)?.sectionId
                            )

                            val action =
                                AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToLessonTextFragment(
                                    lessonArgs
                                )
                            mainFragment.findNavController().navigateTo(action)
                        }
                        MediaType.AUDIO -> {
                        }
                        MediaType.VIDEO -> {
                        }

                    }


                }
            }
        }
    }


    override fun getLayoutRes() = R.layout.fragment_add_section_or_lecture


    fun setAdapter(scrollTo: Boolean = false) {

        binding.rvSections.visibleView(!viewModel.getSectionList().isNullOrEmpty())
        binding.llNoSection.visibleView(viewModel.getSectionList().isNullOrEmpty())
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            if (!viewModel.getSectionList().isNullOrEmpty()) {
                val itemTouchHelper = ItemTouchHelper(touchHelper)
                itemTouchHelper.attachToRecyclerView(binding.rvSections)
                adapter = AddSectionAdapter(
                    viewModel.getSectionList() ?: ArrayList(),
                    viewModel.userProfile?.id ?: 0,
                    viewModel.isCreator.value == true,
                    viewModel.courseData.value?.createdById ?: 0
                )
                binding.rvSections.adapter = adapter
                adapter?.setOnAdapterItemClickListener(this)
            }
        }
        if (scrollTo && !viewModel.getSectionList().isNullOrEmpty()) {
            binding.rvSections.smoothScrollToPosition(viewModel.getSectionList()?.size ?: 0)

        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            when (items[0] as Int) {
                MediaType.AUDIO -> {
                    filePath = items[1] as String
                    if (filePath.isFileLimitExceed(1024)) {
//                    if (isFileLessThan5MB(File(filePath))) {
                        showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
                    } else {
                        viewModel.mediaType = MediaType.AUDIO
                        mediaFrom = 2
                        viewModel.sectionChildPosition = -1
                        viewModel.addLecture()
                    }
                }
                Lecture.CLICK_LESSON_DOCS -> {
                    viewModel.mediaType = MediaType.DOC
                    viewModel.sectionChildPosition = -1
                    viewModel.addLecture()
                }
                Lecture.CLICK_LESSON_AUDIO -> {
                    viewModel.mediaType = MediaType.AUDIO
                    mediaFrom = 1
                    viewModel.sectionChildPosition = -1
                    viewModel.addLecture()
                }
                MediaType.VIDEO -> {
                    filePath = items[1] as String
                    if (filePath.isFileLimitExceed(1024)) {
//                    if (isFileLessThan5MB(File(filePath))) {
                        showToastShort(baseActivity.getString(R.string.file_limit_alert_text))
                    } else {
                        viewModel.mediaType = MediaType.VIDEO
                        viewModel.sectionChildPosition = -1
                        viewModel.addLecture()

                    }
                }
            }
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            viewModel.sectionAdapterPosition = items[1] as Int

            when (type) {
                Constant.CLICK_INFO -> {
                    CommonAlertDialog.builder(baseActivity)
                        .title(baseActivity.getString(R.string.alert))
                        .description(baseActivity.getString(R.string.section_not_saved_info_alert_text))
                        .icon(R.drawable.ic_info_large)
                        .hideNegativeBtn(true)
                        .setThemeIconColor(true)
                        .positiveBtnText(baseActivity.getString(R.string.okay))
                        .build()
                }
                Constant.CLICK_TEXT_CHANGES -> {
//                    isTextChanging=true
                    viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                    typingTimeout?.let {
                        timeoutHandler.removeCallbacks(it)
                        timeoutHandler.postDelayed(typingTimeout, 2000)
                    }


                    if (!isTextChanging) {
                        isTextChanging = true
//                        serviceCall();
                    }
                }
                Constant.CLICK_AFTER_TEXT_CHANGES -> {
//isTextChanging=false
                }
                Constant.CLICK_BEFORE_TEXT_CHANGES -> {
                    viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                    typingTimeout?.let {
                        timeoutHandler.removeCallbacks(it)
                        timeoutHandler.postDelayed(typingTimeout, 2000)
                    }


                    if (!isTextChanging) {
                        isTextChanging = true
                    }
                }
                Constant.CLICK_MORE -> {
                    SectionMoreDialog().apply {
                        setOnDialogClickListener(this@AddSectionOrLectureFragment)
                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_UPLOAD -> {
                    mainFragment.findNavController().navigateTo(
                        R.id.action_addCourseBaseFragment_to_uploadDocOptionsDialog, bundleOf(
                            "courseId" to viewModel
                                .courseData.value?.courseId,
                            "sectionId" to viewModel.getSectionList()
                                ?.get(viewModel.sectionAdapterPosition)?.sectionId
                        )
                    )
//
//                    UploadDocOptionsDialog().apply {
//                        setOnDialogClickListener(this@AddSectionOrLectureFragment)
//                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_DELETE -> {
                    deleteSectionFunctionality()

                }
                Constant.CLICK_OPTION_DELETE -> {
                    viewModel.sectionChildPosition = items[2] as Int

                    deleteLessonFunctionality()

                }
                Constant.CLICK_SWAP -> {
                    val fromPosition = items[2] as Int
                    val toPosition = items[3] as Int


                    swapFunctionality(toPosition, fromPosition)
                }
                Constant.CLICK_SAVE -> {
                    viewModel.updateSection()
                }
                Constant.CLICK_EDIT -> {
                    viewModel.sectionChildPosition = items[2] as Int

                    editLessonFunctionality()

                }
            }
        }
    }

    private fun swapFunctionality(toPosition: Int, fromPosition: Int) {
        val previous = if ((toPosition - 1).isNullOrNegative()) 0 else viewModel.getSectionList()
            ?.get(viewModel.sectionAdapterPosition)?.lessonList
            ?.get(toPosition - 1)?.lectureId
        val last = if ((toPosition + 1) >= viewModel.getSectionList()
                ?.get(viewModel.sectionAdapterPosition)?.lessonList?.size ?: 0
        ) 0 else viewModel.getSectionList()?.get(viewModel.sectionAdapterPosition)?.lessonList
            ?.get(toPosition + 1)?.lectureId


        viewModel.dragAndDropLecture(
            viewModel.getSectionList()?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(
                toPosition
            )?.lectureId,
            previous,
            last
        )
    }

    private fun editLessonFunctionality() {
        when (viewModel.getSectionList()?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(
            viewModel.sectionChildPosition
        )?.mediaType) {
            MediaType.VIDEO -> {
                val lessonArgs = LessonArgs(
                    viewModel.courseData.value?.courseId,
                    viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.sectionId,
                    lectureId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                        ?: 0,
                    type = Constant.CLICK_EDIT
                )
                val action =
                    AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToVideoLectureFragment(
                        lessonArgs
//                        sendSectionModel = viewModel.getSectionList()
//                            ?.get(viewModel.sectionAdapterPosition),
//                        lectureId = viewModel.getSectionList()
//                            ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
//                            ?: 0,
//                        type = Constant.CLICK_EDIT,
//                        childPosition = viewModel.sectionChildPosition,
//                        courseId = viewModel.courseData.value?.courseId ?: 0
                    )
                mainFragment.findNavController().navigateTo(action)
            }
            MediaType.AUDIO -> {
                val lessonArgs = LessonArgs(
                    viewModel.courseData.value?.courseId,
                    viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.sectionId,
                    lectureId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                        ?: 0,
                    type = Constant.CLICK_EDIT
                )
                val action =
                    AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToAudioLectureFragment(
                        lessonArgs
                    )
                mainFragment.findNavController().navigateTo(action)
            }
            MediaType.DOC -> {
                val lessonArgs = LessonArgs(
                    courseId = viewModel.courseData.value?.courseId ?: 0,
                    type = Constant.CLICK_EDIT,
                    sectionId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.sectionId ?: 0,
                    lectureId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                        ?: 0
                )


                val action =
                    AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToDocLessonFragment(
                        lessonArgs
                    )
//                AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToDocLessonFragment(
//                        sendSectionModel = viewModel.getSectionList()
//                            ?.get(viewModel.sectionAdapterPosition),
//                        lectureId = viewModel.getSectionList()
//                            ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
//                            ?: 0,
//                        type = Constant.CLICK_EDIT,
//                        childPosition = viewModel.sectionChildPosition,
//                        courseId = viewModel.courseData.value?.courseId ?: 0
//                    )
                mainFragment.findNavController().navigateTo(action)
            }
            MediaType.TEXT -> {
                val lessonArray = LessonArgs(
                    courseId = viewModel.courseData.value?.courseId ?: 0,
                    sectionId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.sectionId ?: 0,
                    type = Constant.CLICK_EDIT,
                    lectureId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                        ?: 0

                )
                val action =
                    AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToLessonTextFragment(
                        lessonArray
                    )
                mainFragment.findNavController().navigateTo(action)
            }
            MediaType.QUIZ -> {
                val lessonArgs = LessonArgs(
                    courseId = viewModel.courseData.value?.courseId ?: 0,
                    type = Constant.CLICK_EDIT,
                    sectionId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.sectionId,
                    isQuiz = true,
                    quizId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.quizId,
                    lectureId = viewModel.getSectionList()
                        ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                )
                mainFragment.findNavController().navigateTo(
                    AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToAddQuizFragment(
                        lessonArgs
                    )
                )

            }

        }
    }

    private fun deleteLessonFunctionality() {
        CommonAlertDialog.builder(baseActivity)
            .title(getString(R.string.delete_lesson))
            .description(getString(R.string.sure_delete_lecture))
            .positiveBtnText(getString(R.string.yes))
            .icon(R.drawable.ic_delete_icon)
            .getCallback {
                if (it) {
                    viewModel.deleteLecture()
                }
            }.build()
    }

    private fun deleteSectionFunctionality() {
        CommonAlertDialog.builder(baseActivity)
            .title(getString(R.string.delete_section))
            .description(getString(R.string.sure_delete_section))
            .positiveBtnText(getString(R.string.yes))
            .icon(R.drawable.ic_delete_icon)
            .getCallback {
                if (it) {
                    viewModel.deleteSection()

                }
            }.build()
    }

    private fun handleInitList() {
        adapter?.notifyDataSetChanged()
        adapter = null
        setAdapter()

    }

    override fun invoke(p1: String?) {
        when (viewModel.mediaType) {
            MediaType.DOC -> {
            }
        }


    }


    private fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 1024 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize > maxFileSize


    }

    override fun onResume() {
        super.onResume()
        if (viewModel.courseData.value?.enableFields ?: true)
            viewModel.getCourseSections()
    }

    override fun onApiRetry(apiCode: String) {
        //handled in AddCourseBaseFragment
    }

    fun onClickBack() {
        showToastShort("yo")
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

}