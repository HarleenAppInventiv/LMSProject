package com.selflearningcoursecreationapp.ui.moderator.courseDetails.content

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModCourseContentBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.moderator.courseDetails.ModCourseDetailVM
import com.selflearningcoursecreationapp.ui.moderator.dialog.AddCommentDialogue
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog

class ModCourseContentFragment : BaseFragment<FragmentModCourseContentBinding>(),
    BaseAdapter.IViewClick, BaseBottomSheetDialog.IDialogClick {
    private val viewModel: ModCourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private var adapter: ModCourseSectionAdapter? = null
    var clickedInnerPos = -1

    override fun getLayoutRes(): Int {
        return R.layout.fragment_mod_course_content
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        adapter?.notifyDataSetChanged()
        adapter = null
//viewModel.getLessonList()

//        viewModel.sectionData.observe(viewLifecycleOwner, Observer {
//            setAdapter()
//        })


        viewModel.commentStatus.observe(viewLifecycleOwner, Observer { commentStatus ->
            viewModel.courseData.value?.let { setAdapter(it) }
        })
//        showToastShort(viewModel.courseData.value?.sectionData?.get(0)?.sectionTitle.toString())
//        setAdapter(courseData)
        viewModel.courseData.observe(viewLifecycleOwner) { courseData ->
            setAdapter(courseData)

        }
//        viewModel.requestType.observe(viewLifecycleOwner, Observer {
//            setAdapter()
//            adapter?.changeStatus(it)
//        })

    }

    private fun setAdapter(courseData: CourseData) {
        binding.noDataTV.visibleView(courseData.sectionData.isNullOrEmpty())
        if (courseData.sectionData.isNullOrEmpty()) {
            adapter?.notifyDataSetChanged()
            adapter = null
        } else {
            adapter = ModCourseSectionAdapter(
                courseData.sectionData ?: ArrayList(),
                courseData.createdById,
                viewModel.requestType.value ?: 0,
                viewModel.requestType.value ?: 0

            )
            binding.rvLessons.adapter = adapter
            adapter?.setOnAdapterItemClickListener(this)
        }

    }

    override fun onApiRetry(apiCode: String) {
    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_ADD -> {
//                AddCommentDialogue(position, Constant.CLICK_ADD_COMMENT).apply {
//                    setOnDialogClickListener(this@ModCourseContentFragment)
//                }.show(childFragmentManager, "")
                openCommentDialog(Constant.CLICK_ADD_COMMENT, position, -1)
            }
            Constant.CLICK_VIEW -> {

            }
            Constant.CLICK_EDIT -> {
                openCommentDialog(Constant.CLICK_EDIT_COMMENT, position, -1)

            }
            Constant.CLICK_DELETE -> {

                CommonAlertDialog.builder(baseActivity)
                    .title(getString(R.string.are_you_sure))
                    .description(getString(R.string.do_you_really_want_to_delete_your_comment))
                    .positiveBtnText(getString(R.string.delete_acc))
                    .icon(R.drawable.ic_fogot_password)
                    .getCallback {
                        if (it) {
                            viewModel.addComment(position, "", Constant.CLICK_DELETE_COMMENT)
                        }
                    }.build()


            }

            Constant.CLICK_ADD_COMMENT_LEC -> {
                val innerPosition = items[2] as Int
                clickedInnerPos = innerPosition
                openCommentDialog(Constant.CLICK_ADD_COMMENT_LEC, position, innerPosition)
            }

            Constant.CLICK_EDIT_COMMENT_LEC -> {
                val innerPosition = items[2] as Int
                clickedInnerPos = innerPosition
                openCommentDialog(Constant.CLICK_EDIT_COMMENT_LEC, position, innerPosition)
            }

            Constant.CLICK_DELETE_COMMENT_LEC -> {
                val innerPosition = items[2] as Int
                clickedInnerPos = innerPosition
                CommonAlertDialog.builder(baseActivity)
                    .title(getString(R.string.are_you_sure))
                    .description(getString(R.string.do_you_really_want_to_delete_your_comment))
                    .positiveBtnText(getString(R.string.delete_acc))
                    .icon(R.drawable.ic_fogot_password)
                    .getCallback {
                        if (it) {
                            viewModel.addCommentLec(
                                position,
                                "",
                                Constant.CLICK_DELETE_COMMENT_LEC,
                                innerPosition
                            )
                        }
                    }.build()

            }

            Constant.CLICK_PLAY -> {
                if (viewModel.requestType.value == ModHomeConst.PENDING) {
                    if (!baseActivity.tokenFromDataStore()
                            .isEmpty()
                    ) {
                        val innerPosition = items[2] as Int
                        when (viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.mediaType) {
                            MediaType.QUIZ -> {
                                findNavController().navigateTo(
                                    R.id.action_global_quiZListForModCreatorFragment,
                                    bundleOf(
                                        "quizId" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.quizId,
                                        "title" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureTitle,
                                        "courseId" to viewModel.courseData.value?.courseId
                                    )
                                )
                            }
                            MediaType.DOC -> {
                                findNavController().navigateTo(
                                    R.id.action_modCourseDetailsFragment_to_pdfReaderFragment,
                                    bundleOf(
                                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                                        "courseId" to viewModel.courseData.value?.courseId,
                                        "sectionId" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.sectionId,
                                        "type" to MODTYPE.MODERATOR,
                                        "lessonId" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureId,
                                        "title" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureTitle,
                                        "name" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureTitle,
                                        "duration" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureContentDuration?.toInt()
                                    )
                                )
                            }
                            MediaType.TEXT -> {

                                findNavController().navigateTo(
                                    R.id.action_modCourseDetailsFragment_to_pdfReaderFragment,
                                    bundleOf(
                                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                                        "courseId" to viewModel.courseData.value?.courseId,
                                        "type" to MODTYPE.MODERATOR,
                                        "sectionId" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.sectionId,
                                        "lessonId" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureId,
                                        "name" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureTitle,
                                        "title" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureTitle,
                                        "duration" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureContentDuration?.toInt()
                                    )
                                )
                            }
                            MediaType.VIDEO, MediaType.AUDIO -> {
                                findNavController().navigateTo(
                                    R.id.action_modCourseDetailsFragment_to_videoBaseFragment,
                                    bundleOf(
                                        "title" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureTitle,
                                        "courseId" to viewModel.courseData.value?.courseId,
                                        "sectionId" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.sectionId,
                                        "title" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureTitle,
                                        "lectureId" to viewModel.courseData.value?.sectionData?.get(
                                            position
                                        )?.lessonList?.get(
                                            innerPosition
                                        )?.lectureId
                                    )
                                )


                            }
                        }
                    } else {
                        baseActivity.guestUserPopUp()

                    }
                }
            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        val desc = items[2] as String
        when (type) {
            Constant.CLICK_ADD_COMMENT -> {
                viewModel.addComment(position, desc, Constant.CLICK_ADD_COMMENT)
            }
            Constant.CLICK_EDIT_COMMENT -> {
                viewModel.addComment(position, desc, Constant.CLICK_EDIT_COMMENT)
            }

            Constant.CLICK_ADD_COMMENT_LEC -> {
                viewModel.addCommentLec(
                    position,
                    desc,
                    Constant.CLICK_ADD_COMMENT_LEC,
                    innerposition = items[3] as Int
                )
            }
            Constant.CLICK_EDIT_COMMENT_LEC -> {
                viewModel.addCommentLec(
                    position,
                    desc,
                    Constant.CLICK_EDIT_COMMENT_LEC,
                    innerposition = items[3] as Int
                )
            }

        }
    }

    fun openCommentDialog(type: Int, position: Int, innerPosition: Int) {
        when (type) {
            Constant.CLICK_ADD_COMMENT, Constant.CLICK_EDIT_COMMENT -> {
                AddCommentDialogue(position, type).apply {
                    arguments =
                        bundleOf("comment" to viewModel.courseData.value?.sectionData?.get(position)?.moderatorComment)
                    setOnDialogClickListener(this@ModCourseContentFragment)
                }.show(childFragmentManager, "")
            }

            Constant.CLICK_ADD_COMMENT_LEC, Constant.CLICK_EDIT_COMMENT_LEC -> {
                AddCommentDialogue(position, type, innerPosition = innerPosition).apply {
                    arguments =
                        bundleOf(
                            "comment" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.lectureComment
                        )
                    setOnDialogClickListener(this@ModCourseContentFragment)
                }.show(childFragmentManager, "")
            }
        }

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COMMENT_LECTURE -> {
//                val data = value as BaseResponse<ChildModel>
//                showToastShort(data.message)
            }
            ApiEndPoints.API_COMMENT_SECTION -> {
//                adapter?.notifyDataSetChanged()
//                showToastShort("yes")
//                setAdapter()
            }
        }
    }


}