package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseBaseFragmentDirections
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.ui.dialog.SectionMoreDialog
import com.selflearningcoursecreationapp.ui.dialog.UploadDocOptionsDialog
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
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment() })
    private var mediaFrom = 0
    private var filePath = ""
    private var mOrderChanged = false
    private var _lectureId = -1

    private val touchHelper = object : TouchHelper() {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {

            viewModel.fromPosition = viewHolder.adapterPosition
            viewModel.toPosition = target.adapterPosition
            mOrderChanged = true

            return false
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
                mOrderChanged = false
                val recyclerviewAdapter = binding.rvSections.adapter as AddSectionAdapter
                Collections.swap(
                    viewModel.getSectionList(),
                    viewModel.toPosition,
                    viewModel.fromPosition
                )


                val previous =
                    if (viewModel.toPosition == 0 || viewModel.toPosition == viewModel.getSectionList()?.size!! - 1) 0 else viewModel.getSectionList()
                        ?.get(viewModel.toPosition - 1)?.sectionId

                val last =
                    if (viewModel.toPosition == viewModel.getSectionList()?.size!! - 1 || viewModel.toPosition == 0) 0 else viewModel.getSectionList()
                        ?.get(viewModel.toPosition + 1)?.sectionId
                viewModel.dragAndDropSection(
                    previous,
                    last
                )
                recyclerviewAdapter.notifyItemMoved(viewModel.fromPosition, viewModel.toPosition)
                recyclerviewAdapter.notifyDataSetChanged()
                viewModel.fromPosition = -1
                viewModel.toPosition = -1
            }
//                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//                    viewHolder?.itemView?.alpha = 0.5f
//                }

        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {


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
                                registry = baseActivity.activityResultRegistry
                            )
                        }

                        MediaType.TEXT -> {
                            val action =
                                AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToLessonTextFragment(
                                    sendSectionModel = viewModel.getSectionList()
                                        ?.get(viewModel.sectionAdapterPosition),
                                    lectureId = lectureId,
                                    type = Constant.CLICK_ADD,
                                    childPosition = -1,
                                    courseId = viewModel.courseData.value?.courseId ?: 0

                                )
                            findNavController().navigate(action)
                        }
                        MediaType.AUDIO -> {
                            if (mediaFrom == 1) {
                                val action =
                                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToRecordAudioFragment(
                                        sendSectionModel = viewModel.getSectionList()
                                            ?.get(viewModel.sectionAdapterPosition),
                                        courseId = viewModel.courseData.value?.courseId
                                            ?: 0,
                                        lectureId = lectureId,
                                        childPosition = viewModel.sectionChildPosition,
                                        type = Constant.CLICK_ADD,
                                        from = mediaFrom

                                    )
                                findNavController().navigate(action)
                            } else {
                                val action =
                                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAudioLectureFragment(
                                        sendSectionModel = viewModel.getSectionList()
                                            ?.get(viewModel.sectionAdapterPosition),
                                        lectureId = lectureId,
                                        type = Constant.CLICK_ADD,
                                        childPosition = -1,
                                        courseId = viewModel.courseData.value?.courseId ?: 0,
                                        filePath = filePath

                                    )
                                findNavController().navigate(action)
                            }
                        }
                        MediaType.VIDEO -> {
                            val action =
                                AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToVideoLectureFragment(
                                    sendSectionModel = viewModel.getSectionList()
                                        ?.get(viewModel.sectionAdapterPosition),
                                    lectureId = lectureId,
                                    type = Constant.CLICK_ADD,
                                    childPosition = -1,
                                    courseId = viewModel.courseData.value?.courseId ?: 0,
                                    filePath = filePath

                                )
                            findNavController().navigate(action)
                        }

                    }


                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
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
                Constant.CLICK_DELETE -> {
                    deleteSectionFunctionality()
                }
                Constant.CLICK_EDIT -> {
                    showToastShort("edit")
                }
                Lecture.CLICK_LESSON_TEXT -> {
                    viewModel.mediaType = MediaType.TEXT
                    viewModel.sectionChildPosition = -1
                    viewModel.addLecture()
                }
                Lecture.CLICK_LESSON_QUIZ -> {
                    viewModel.mediaType = MediaType.QUIZ
                    viewModel.sectionChildPosition = -1

                    findNavController().navigate(
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAddQuizFragment(
                            viewModel.courseData.value,
                            true,
                            viewModel.sectionAdapterPosition,
                            viewModel.getSectionList()?.toTypedArray()
                        )
                    )
                }
                MediaType.AUDIO -> {
                    filePath = items[1] as String
                    if (isFileLessThan5MB(File(filePath))) {
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

                    if (isFileLessThan5MB(File(filePath))) {
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
                Constant.CLICK_TEXT_CHANGES -> {
                    viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                }
                Constant.CLICK_MORE -> {
                    SectionMoreDialog().apply {
                        setOnDialogClickListener(this@AddSectionOrLectureFragment)
                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_UPLOAD -> {
                    UploadDocOptionsDialog().apply {
                        setOnDialogClickListener(this@AddSectionOrLectureFragment)
                    }.show(childFragmentManager, "")
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
        val previous =
            if (toPosition == 0 || toPosition == viewModel.getSectionList()
                    ?.get(viewModel.sectionAdapterPosition)?.lessonList?.size!! - 1
            ) 0 else viewModel.getSectionList()
                ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(toPosition - 1)?.lectureId
        val last =
            if (toPosition == viewModel.getSectionList()
                    ?.get(viewModel.sectionAdapterPosition)?.lessonList?.size!! - 1 || toPosition == 0
            ) 0 else viewModel.getSectionList()
                ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(
                    toPosition + 1
                )?.lectureId

        viewModel.dragAndDropLecture(
            viewModel.getSectionList()?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(
                fromPosition
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
                val action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToVideoLectureFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition),
                        lectureId = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                            ?: 0,
                        type = Constant.CLICK_EDIT,
                        childPosition = viewModel.sectionChildPosition,
                        courseId = viewModel.courseData.value?.courseId ?: 0
                    )
                findNavController().navigate(action)
            }
            MediaType.AUDIO -> {
                val action =

                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAudioLectureFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition),
                        lectureId = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                            ?: 0,
                        type = Constant.CLICK_EDIT,
                        childPosition = viewModel.sectionChildPosition,
                        courseId = viewModel.courseData.value?.courseId ?: 0
                    )
                findNavController().navigate(
                    R.id.audioLectureFragment, bundleOf(
                        "sendSectionModel" to viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition),
                        "lectureId" to (viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                            ?: 0),
                        "type" to Constant.CLICK_EDIT,
                        "childPosition" to viewModel.sectionChildPosition,
                        "courseId" to (viewModel.courseData.value?.courseId ?: 0)
                    )
                )
            }
            MediaType.DOC -> {

                val action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToDocLessonFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition),
                        lectureId = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                            ?: 0,
                        type = Constant.CLICK_EDIT,
                        childPosition = viewModel.sectionChildPosition,
                        courseId = viewModel.courseData.value?.courseId ?: 0
                    )
                findNavController().navigate(action)
            }
            MediaType.TEXT -> {
                val action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToLessonTextFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition),
                        lectureId = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.lessonList?.get(viewModel.sectionChildPosition)?.lectureId
                            ?: 0,
                        type = Constant.CLICK_EDIT,
                        childPosition = viewModel.sectionChildPosition,
                        courseId = viewModel.courseData.value?.courseId ?: 0

                    )
                findNavController().navigate(action)
            }
            MediaType.QUIZ -> {
                findNavController().navigate(
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAddQuizFragment(
                        viewModel.courseData.value,
                        true,
                        viewModel.sectionAdapterPosition,
                        viewModel.getSectionList()?.toTypedArray(),
                        viewModel.sectionChildPosition
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
                val action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToDocLessonFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition),
                        lectureId = _lectureId,
                        type = Constant.CLICK_ADD,
                        childPosition = -1,
                        courseId = viewModel.courseData.value?.courseId ?: 0,
                        filePath = p1.toString()

                    )

                //R.id.docLessonFragment, bundleOf(   "sendSectionModel" to viewModel.getSectionList()
                //                    ?.get(viewModel.sectionAdapterPosition),
                //                    "lectureId" to _lectureId,
                //                    "type" to Constant.CLICK_ADD,
                //                    "childPosition" to  -1,
                //                    "courseId" to (viewModel.courseData.value?.courseId ?: 0),
                //                    "filePath" to  p1.toString()
                //                )
                parentFragment?.findNavController()?.navigate(action)
            }
        }


    }


    private fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize > maxFileSize


    }

    override fun onApiRetry(apiCode: String) {
        //handled in AddCourseBaseFragment
    }
}