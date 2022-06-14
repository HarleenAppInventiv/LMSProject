package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.annotation.RequiresApi
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
import com.selflearningcoursecreationapp.utils.*
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*


class AddSectionOrLectureFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentAddSectionOrLectureBinding>(),
    BaseAdapter.IViewClick, BaseBottomSheetDialog.IDialogClick, (String?) -> Unit {
    private var adapter: AddSectionAdapter? = null
    private var adapterPosition: Int = -1
    private var childPosition: Int = -1
    private val imagePickUtils: ImagePickUtils by inject()
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment() })
    private var mediaFrom = 0
    private var filePath = ""
    private var mOrderChanged = false
    private var fromPosition: Int = -1
    private var toPosition: Int = -1
    var mediaType = 0
    private var _lectureId = -1

    private val touchHelper = object : TouchHelper() {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {

            fromPosition = viewHolder.adapterPosition
            toPosition = target.adapterPosition
            mOrderChanged = true

            return false
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
                mOrderChanged = false
                val recyclerviewAdapter = binding.rvSections.adapter as AddSectionAdapter
                Collections.swap(viewModel.getSectionList(), toPosition, fromPosition)

                Log.d("varun", "onMove: ${toPosition} ${fromPosition}")

                val previous =
                    if (toPosition == 0 || toPosition == viewModel.getSectionList()?.size!! - 1) 0 else viewModel.getSectionList()
                        ?.get(toPosition - 1)?.sectionId

                val last =
                    if (toPosition == viewModel.getSectionList()?.size!! - 1 || toPosition == 0) 0 else viewModel.getSectionList()
                        ?.get(toPosition + 1)?.sectionId
                viewModel.dragAndDropSection(
                    viewModel.getSectionList()?.get(fromPosition)?.sectionId,
                    previous,
                    last
                )
                recyclerviewAdapter.notifyItemMoved(fromPosition, toPosition)
                recyclerviewAdapter.notifyDataSetChanged()
//                    binding.rvSections.layoutManager.run {
//                        binding.rvSections.adapter?.notifyItemChanged(adapterPosition)
//                    }
                fromPosition = -1
                toPosition = -1
            }
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.itemView?.alpha = 0.5f
            }

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
        viewModel.sectionUpdationData.observe(
            viewLifecycleOwner,
            { event ->
                event.getContentIfNotHandled()?.let {
                    viewModel.courseData.value?.notifyPropertyChanged(BR.allDataEntered)
                    when (it) {
                        Constant.CLICK_UPLOAD -> {
                            viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                            viewModel.addSection()
                            viewModel.getSectionList()?.forEach { it.isVisible = false }
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
                            viewModel.getSectionList()?.get(adapterPosition)?.isVisible = false
                            viewModel.getSectionList()?.get(adapterPosition)?.isSaved = true
                            viewModel.getSectionList()?.get(adapterPosition)?.changesMade = false
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

            })
    }

    private fun observeAddLecture() {
        viewModel.addLectureLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { lectureId ->
                if (lectureId != 0) {
                    _lectureId = lectureId
                    when (mediaType) {

                        MEDIA_TYPE.DOC -> {

                            imagePickUtils.openDocs(
                                baseActivity,
                                this,
                                registry = requireActivity().activityResultRegistry
                            )
                        }

                        MEDIA_TYPE.TEXT -> {
                            val action =
                                AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToLessonTextFragment(
                                    sendSectionModel = viewModel.getSectionList()
                                        ?.get(adapterPosition),
                                    lectureId = lectureId,
                                    type = Constant.CLICK_ADD,
                                    childPosition = -1,
                                    courseId = viewModel.courseData.value?.courseId ?: 0

                                )
                            findNavController().navigate(action)
                        }
                        MEDIA_TYPE.AUDIO -> {
                            if (mediaFrom == 1) {
                                val action =
                                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToRecordAudioFragment(
                                        sendSectionModel = viewModel.getSectionList()
                                            ?.get(adapterPosition),
                                        courseId = viewModel.courseData.value?.courseId
                                            ?: 0,
                                        lectureId = lectureId!!,
                                        childPosition = childPosition!!,
                                        type = Constant.CLICK_ADD,
                                        from = mediaFrom

                                    )
                                findNavController().navigate(action)
                            } else {
                                var action =
                                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAudioLectureFragment(
                                        sendSectionModel = viewModel.getSectionList()
                                            ?.get(adapterPosition),
                                        lectureId = lectureId,
                                        type = Constant.CLICK_ADD,
                                        childPosition = -1,
                                        courseId = viewModel.courseData.value?.courseId ?: 0,
                                        filePath = filePath

                                    )
                                findNavController().navigate(action)
                            }
                        }
                        MEDIA_TYPE.VIDEO -> {
                            var action =
                                AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToVideoLectureFragment(
                                    sendSectionModel = viewModel.getSectionList()
                                        ?.get(adapterPosition),
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
                    viewModel.getSectionList()!!,
                    viewModel.userProfile?.id ?: 0,
                    viewModel.isCreator.value == true,
                    viewModel.courseData?.value?.createdById ?: 0
                )
                binding.rvSections.adapter = adapter
                adapter!!.setOnAdapterItemClickListener(this)
            }
        }
        if (scrollTo && !viewModel.getSectionList().isNullOrEmpty()) {
            binding.rvSections.smoothScrollToPosition(viewModel.getSectionList()?.size ?: 0)

        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                Constant.CLICK_DELETE -> {
                    deleteSectionFunctionality()
                }
                Constant.CLICK_EDIT -> {
                    showToastShort("edit")
                }
                Lecture.CLICK_LESSON_TEXT -> {
                    mediaType = MEDIA_TYPE.TEXT
                    viewModel.addLecture(
                        viewModel.getSectionList()?.get(adapterPosition)?.sectionId,
                        mediaType
                    )
                }
                Lecture.CLICK_LESSON_QUIZ -> {
                    mediaType = MEDIA_TYPE.QUIZ
                    findNavController().navigate(
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAddQuizFragment(
                            viewModel.courseData.value,
                            true,
                            adapterPosition,
                            viewModel.getSectionList()?.toTypedArray()
                        )
                    )
                }
                MEDIA_TYPE.AUDIO -> {
                    filePath = items[1] as String
                    if (isFileLessThan5MB(File(filePath))) {
                        showToastShort("File size is more than 5 MB, not able to upload. Please select another file")
                    } else {
                        mediaType = MEDIA_TYPE.AUDIO
                        mediaFrom = 2

                        viewModel.addLecture(
                            viewModel.getSectionList()
                                ?.get(adapterPosition)?.sectionId,
                            mediaType
                        )
                    }
                }
                Lecture.CLICK_LESSON_DOCS -> {
                    mediaType = MEDIA_TYPE.DOC
                    viewModel.addLecture(
                        viewModel.getSectionList()?.get(adapterPosition)?.sectionId,
                        mediaType
                    )

                }
                Lecture.CLICK_LESSON_AUDIO -> {
                    mediaType = MEDIA_TYPE.AUDIO
                    mediaFrom = 1
                    viewModel.addLecture(
                        viewModel.getSectionList()
                            ?.get(adapterPosition)?.sectionId,
                        mediaType
                    )
                }
                MEDIA_TYPE.VIDEO -> {
                    filePath = items[1] as String

                    if (isFileLessThan5MB(File(filePath))) {
                        showToastShort("File size is more than 5 MB, not able to upload. Please select another file")
                    } else {
                        mediaType = MEDIA_TYPE.VIDEO
                        viewModel.addLecture(
                            viewModel.getSectionList()
                                ?.get(adapterPosition)?.sectionId,
                            mediaType
                        )

                    }
                }
            }
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            adapterPosition = items[1] as Int

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
                    childPosition = items[2] as Int

                    deleteLessonFunctionality()

                }
                Constant.CLICK_SWAP -> {
                    val fromPosition = items[2] as Int
                    val toPosition = items[3] as Int


                    swapFunctionality(toPosition, fromPosition)
                }
                Constant.CLICK_SAVE -> {
                    viewModel.updateSection(adapterPosition)
                }
                Constant.CLICK_EDIT -> {
                    childPosition = items[2] as Int

                    editLessonFunctionality()

                }
            }
        }
    }

    private fun swapFunctionality(toPosition: Int, fromPosition: Int) {
        val previous =
            if (toPosition == 0 || toPosition == viewModel.getSectionList()
                    ?.get(adapterPosition)?.lessonList?.size!! - 1
            ) 0 else viewModel.getSectionList()
                ?.get(adapterPosition)?.lessonList?.get(toPosition - 1)?.lectureId
        val last =
            if (toPosition == viewModel.getSectionList()
                    ?.get(adapterPosition)?.lessonList?.size!! - 1 || toPosition == 0
            ) 0 else viewModel.getSectionList()?.get(adapterPosition)?.lessonList?.get(
                toPosition + 1
            )?.lectureId

        viewModel.dragAndDropLecture(
            viewModel.getSectionList()?.get(adapterPosition)?.lessonList?.get(
                fromPosition
            )?.lectureId,
            previous,
            last
        )
    }

    private fun editLessonFunctionality() {
        when (viewModel.getSectionList()?.get(adapterPosition)?.lessonList?.get(
            childPosition
        )?.mediaType) {
            MEDIA_TYPE.VIDEO -> {
                var action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToVideoLectureFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(adapterPosition),
                        lectureId = viewModel.getSectionList()
                            ?.get(adapterPosition)?.lessonList?.get(childPosition)?.lectureId!!,
                        type = Constant.CLICK_EDIT,
                        childPosition = childPosition,
                        courseId = viewModel.courseData.value?.courseId ?: 0
                    )
                findNavController().navigate(action)
            }
            MEDIA_TYPE.AUDIO -> {
                val action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAudioLectureFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(adapterPosition),
                        lectureId = viewModel.getSectionList()
                            ?.get(adapterPosition)?.lessonList?.get(childPosition)?.lectureId!!,
                        type = Constant.CLICK_EDIT,
                        childPosition = childPosition,
                        courseId = viewModel.courseData.value?.courseId ?: 0
                    )
                findNavController().navigate(action)
            }
            MEDIA_TYPE.DOC -> {

                val action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToDocLessonFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(adapterPosition),
                        lectureId = viewModel.getSectionList()
                            ?.get(adapterPosition)?.lessonList?.get(childPosition)?.lectureId!!,
                        type = Constant.CLICK_EDIT,
                        childPosition = childPosition,
                        courseId = viewModel.courseData.value?.courseId ?: 0
                    )
                findNavController().navigate(action)
            }
            MEDIA_TYPE.TEXT -> {
                val action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToLessonTextFragment(
                        sendSectionModel = viewModel.getSectionList()
                            ?.get(adapterPosition),
                        lectureId = viewModel.getSectionList()
                            ?.get(adapterPosition)?.lessonList?.get(childPosition)?.lectureId!!,
                        type = Constant.CLICK_EDIT,
                        childPosition = childPosition,
                        courseId = viewModel.courseData.value?.courseId ?: 0

                    )
                findNavController().navigate(action)
            }
            MEDIA_TYPE.QUIZ -> {
                findNavController().navigate(
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAddQuizFragment(
                        viewModel.courseData.value,
                        true,
                        adapterPosition,
                        viewModel.getSectionList()?.toTypedArray(),
                        childPosition
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
                    viewModel.deleteLecture(
                        adapterPosition,
                        childPosition
                    )
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
                    viewModel.deleteSection(
                        viewModel.getSectionList()?.get(adapterPosition)?.sectionId,
                        adapterPosition
                    )

                }
            }.build()
    }

    fun handleInitList() {
        adapter?.notifyDataSetChanged()
        adapter = null
        setAdapter()

    }

    override fun invoke(p1: String?) {
        when (mediaType) {
            MEDIA_TYPE.DOC -> {
                Log.d("varun", "invoke: ${p1}")
                val action =
                    AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToDocLessonFragment(
                        sendSectionModel = viewModel.getSectionList()?.get(adapterPosition),
                        lectureId = _lectureId,
                        type = Constant.CLICK_ADD,
                        childPosition = -1,
                        courseId = viewModel.courseData.value?.courseId ?: 0,
                        filePath = p1.toString()

                    )
                findNavController().navigate(action)
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


}