package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseBaseFragmentDirections
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.ui.dialog.SectionMoreDialog
import com.selflearningcoursecreationapp.ui.dialog.UploadDocOptionsDialog
import com.selflearningcoursecreationapp.utils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.Lecture
import com.selflearningcoursecreationapp.utils.MEDIA
import java.util.*


class AddSectionOrLectureFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentAddSectionOrLectureBinding>(),
    BaseAdapter.IViewClick, BaseBottomSheetDialog.IDialogClick {
    private var adapter: AddSectionAdapter? = null
    private var adapterPosition: Int = -1
    private var childPosition: Int = -1
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment() })

    var lesson = 1
    var mediaType = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        binding.llNoSection.visible()
        binding.addSectionLecture = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        fragmentResultListener()
        handleInitList()
        binding.btnAddSection.setOnClickListener {
            binding.llNoSection.gone()
            viewModel.addSection()
        }

        binding.btAddSection.setOnClickListener {
            viewModel.addSection()
            viewModel.users[adapterPosition].expandedItemPos = 1
            adapter?.notifyDataSetChanged()
        }

        observeAddSection()
        observeDeleteSection()
        observeDeleteLecture()

        observeAddLecture()

        viewModel.addPatchSectionLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                if (it == true) {
                    viewModel.users[adapterPosition].expandedItemPos = -1
                    adapter?.notifyDataSetChanged()
                    binding.btAddSection.visible()
                }

            }
        }
    }

    private fun observeAddLecture() {
        viewModel.addLectureLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                if (it != 0) {
                    when (mediaType) {

                        3 -> {
                            val action =
                                AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToDocLessonFragment(
                                    viewModel.users[adapterPosition].sectionId!!,
                                    it,
                                    viewModel.courseData.value?.courseId ?: 0
                                )
                            findNavController().navigate(action)
                        }
                        4 -> {
                            val action =
                                AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToLessonTextFragment(
                                    viewModel.users[adapterPosition].sectionId!!,
                                    it,
                                    viewModel.courseData.value?.courseId ?: 0
                                )
                            findNavController().navigate(action)
                        }

                    }


                }
            }
        }
    }

    private fun observeDeleteLecture() {
        viewModel.deleteLectureLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                if (it == true) {
                    viewModel.users[adapterPosition].lessonList.removeAt(childPosition)
                    binding.rvSections.run {
                        binding.rvSections.invalidate()
                        binding.rvSections.adapter?.notifyDataSetChanged()

                    }
                    handleInitList()
                }
            }
        }
    }

    private fun observeDeleteSection() {
        viewModel.deleteSectionLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                if (it == true) {
                    //                    viewModel.users.removeAt(adapterPosition)
                    if (viewModel.users.size == 0) {
                        binding.btAddSection.gone()
                    }
                    binding.rvSections.run {
                        binding.rvSections.invalidate()
                        binding.rvSections.adapter?.notifyDataSetChanged()

                    }
                    handleInitList()
                }
            }
        }
    }

    private fun observeAddSection() {
        viewModel.addSectionLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                if (it != 0) {
                    binding.btAddSection.gone()
                    setAdapter(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    override fun getLayoutRes() = R.layout.fragment_add_section_or_lecture

    private fun setAdapter(id: Int) {
        if (id != 0) {
            binding.rvSections.visible()
            viewModel.users.add(
                SectionModel(
                    sectionId = id,
                    lessonList = arrayListOf()
                )
            )
            handleSwipAndNotifyAdapter()

        } else {

            handleSwipAndNotifyAdapter()
        }
    }

    fun handleSwipAndNotifyAdapter() {
        val touchHelper = object : TouchHelper() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {

                val recyclerviewAdapter = recyclerView.adapter as AddSectionAdapter
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
//                recyclerviewAdapter.moveItem(fromPosition, toPosition)


                Collections.swap(viewModel.users, fromPosition, toPosition)

                recyclerviewAdapter.notifyItemMoved(fromPosition, toPosition)

                val previous =
                    if (toPosition == 0) 0 else viewModel.users[toPosition - 1].sectionId
                val last =
                    if (toPosition == viewModel.users.size - 1) 0 else viewModel.users[toPosition + 1].sectionId
                viewModel.dragAndDropSection(
                    viewModel.users[fromPosition].sectionId,
                    previous,
                    last
                )
                return false
            }

        }

        adapter?.notifyDataSetChanged() ?: kotlin.run {
            val itemTouchHelper = ItemTouchHelper(touchHelper)
            itemTouchHelper.attachToRecyclerView(binding.rvSections)
            adapter = AddSectionAdapter(viewModel.users)
            binding.rvSections.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }


    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                Constant.CLICK_ADD -> {
                    showToastShort("add")
                }
                Constant.CLICK_DELETE -> {
//                    Log.d("varun", "onDialogClick: ${users.toString()}")
                    CommonAlertDialog.builder(baseActivity)
                        .title(getString(R.string.are_you_sure))
                        .description(getString(R.string.sure_delete_section))
                        .positiveBtnText(getString(R.string.yes))
                        .icon(R.drawable.ic_fogot_password)
                        .getCallback {
                            if (it) {
                                viewModel.deleteSection(
                                    viewModel.users[adapterPosition].sectionId,
                                    adapterPosition
                                )

                            }
                        }.build()
                }
                Constant.CLICK_EDIT -> {
                    showToastShort("edit")
                }
                Lecture.CLICK_LESSON_DOCS -> {
                    mediaType = MEDIA.DOC
                    viewModel.addLecture(viewModel.users[adapterPosition].sectionId, mediaType)
                }
                Lecture.CLICK_LESSON_TEXT -> {
                    mediaType = MEDIA.TEXT
                    viewModel.addLecture(viewModel.users[adapterPosition].sectionId, mediaType)
                }
                Lecture.CLICK_LESSON_QUIZ -> {
                    mediaType = MEDIA.QUIZ
                    findNavController().navigate(
                        R.id.action_addCourseBaseFragment_to_addQuizFragment,
                        bundleOf(
                            "courseId" to viewModel.courseData.value?.courseId,
                            "sectionId" to viewModel.users[adapterPosition].sectionId
                        )
                    )

                }
                Lecture.CLICK_LESSON_VIDEO -> {
                    showToastShort("Video")

                }
                Lecture.CLICK_LESSON_AUDIO -> {
                    showToastShort("Audio")

                }
                Lecture.CLICK_LESSON_SCREEN_RECORD -> {
                    showToastShort("record")

                }
            }
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            adapterPosition = items[1] as Int

            when (type) {

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
                    childPosition = items[2] as Int

                    CommonAlertDialog.builder(baseActivity)
                        .title(getString(R.string.are_you_sure))
                        .description(getString(R.string.sure_delete_lecture))
                        .positiveBtnText(getString(R.string.yes))
                        .icon(R.drawable.ic_fogot_password)
                        .getCallback {
                            if (it) {
                                viewModel.deleteLecture(
                                    viewModel.users[adapterPosition].sectionId,
                                    viewModel.users[adapterPosition].lessonList[childPosition].lectureId
                                )
                            }
                        }.build()

                }
                Constant.CLICK_SWAP -> {
                    val fromPosition = items[2] as Int
                    val toPosition = items[3] as Int

                    val previous =
                        if (toPosition == 0) 0 else viewModel.users[adapterPosition].lessonList[toPosition - 1].lectureId
                    val last =
                        if (toPosition == viewModel.users[adapterPosition].lessonList.size - 1) 0 else viewModel.users[adapterPosition].lessonList[toPosition + 1].lectureId
                    viewModel.dragAndDropLecture(
                        viewModel.users[adapterPosition].lessonList[fromPosition].lectureId,
                        previous,
                        last
                    )
                }
                Constant.CLICK_SAVE -> {
                    val title = items[2] as String
                    val desc = items[3] as String
                    viewModel.addPatchSection(
                        viewModel.users[adapterPosition].sectionId,
                        title,
                        desc
                    )
                }
            }
        }
    }

    fun handleInitList() {
        adapter?.notifyDataSetChanged()
        adapter = null
        if (!viewModel.users.isEmpty()) {
            binding.rvSections.visible()
            binding.llNoSection.gone()
            setAdapter(0)
        } else {
            binding.rvSections.gone()
            binding.llNoSection.visible()
        }
    }


    private fun fragmentResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            "response",
            viewLifecycleOwner
        ) { _, bundle ->
            bundle.getParcelable<UserProfile>("value")?.let {
                viewModel.users.get(adapterPosition).lessonList.add(
                    ChildModel(
                        lesson, it.lectureId,
                        it.lectureTitle, it.mediaType
                    )
                )
                setAdapter(0)

                Log.d("varun", "fragmentResultListener: ${viewModel.users}")
            }


        }
    }
}