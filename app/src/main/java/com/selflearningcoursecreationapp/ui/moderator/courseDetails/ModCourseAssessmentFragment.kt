package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentModCourseAssessmentBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.moderator.dialog.AddCommentDialogue
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ModHomeConst
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils


class ModCourseAssessmentFragment() : BaseFragment<FragmentModCourseAssessmentBinding>(),
    View.OnClickListener, BaseBottomSheetDialog.IDialogClick {
    private val viewModel: ModCourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    override fun getLayoutRes() = R.layout.fragment_mod_course_assessment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeQuizData()
        if (viewModel.courseData.value?.assessmentId.isNullOrZero()) {
            binding.noDataTV.visible()
            binding.cvTest.gone()
        } else {
            viewModel.getAssessment()
        }

        setCommentTabVisibility()

        binding.btReview.setOnClickListener {
            if (viewModel.requestType.value == ModHomeConst.PENDING) {
                findNavController().navigateTo(
                    R.id.action_modCourseDetailsFragment_to_modAssessmentFragment,
                    bundleOf(
                        "list" to viewModel.assessmentData.value,
                        "title" to viewModel.assessmentData.value?.assessmentName
                    )
                )
            }
        }


        binding.ivComment.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
        binding.ivEdit.setOnClickListener(this)
        observeRequestValue()
    }

    private fun setCommentTabVisibility() {
        var assessmentComment =
            viewModel.courseData.value?.courseComments?.filter { it.commentType == Constant.COMMENT_ASSESSMENT }
        if (assessmentComment?.size ?: 0 > 0) {
            binding.ivComment.visibleView(assessmentComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
//            binding.constDescComment.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.tvTime.text =
                assessmentComment?.get(0)?.courseCommentCreatedDate.changeDateFormat()
            binding.tvComment.setSpanString(
                context?.let {
                    SpanUtils.with(
                        it,
                        "${getString(R.string.comments_semicolon)} ${assessmentComment?.get(0)?.comment}"
                    ).endPos(7).isBold().getSpanString()
                }
            )
            binding.commentG.visibleView(!assessmentComment?.get(0)?.comment.isNullOrEmpty())
            binding.constComment.visibleView(!assessmentComment?.get(0)?.comment.isNullOrEmpty())
            binding.ivEdit.visibleView(!assessmentComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.ivDelete.visibleView(!assessmentComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)


        } else {
            binding.constComment.visibleView(false && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.ivComment.visibleView(true && viewModel.requestType.value == ModHomeConst.PENDING)
        }
    }

    private fun observeRequestValue() {
        viewModel.requestType.observe(viewLifecycleOwner, Observer {
            when (it) {
                ModHomeConst.PENDING -> {
                    binding.btReview.setBtnDisabled(true)
                    binding.btReview.isEnabled = true
                }
                else -> {
                    binding.btReview.setBtnDisabled(false)
                    binding.btReview.isEnabled = false
                }
            }
        })
    }
//    action_courseDetailsFragment_to_quizBaseFragment

    private fun observeQuizData() {


        viewModel.commentStatus.observe(viewLifecycleOwner, {
            setCommentTabVisibility()
        })

        viewModel.quizData.observe(viewLifecycleOwner)
        { quizata ->
            quizata?.let {

                binding.cvTest.visible()
                ResizeableUtils.builder(binding.tvTitle).isBold(false)
                    .isUnderline(false)
                    .setFullText(it?.assessmentName)
                    .setFullText(R.string.read_more_arrow)
                    .setLessText(R.string.read_less_arrow)
                    .setLimit(28)
                    .setSpanSize(0.9f)
                    .showDots(true)
//            .setSpanColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
                    .build()

//                binding.tvTitle.text = it?.assessmentName
                binding.tvQuesCount.setText(it?.totalQues.toString() + " " + getString(R.string.questiona))
                it?.totalAssessmentTime?.let { duration ->
                    binding.tvQuesTime.text = duration.getTime(baseActivity)
                }
            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COMMENT_MISC -> {
                val data = value as BaseResponse<ChildModel>
                showToastShort(data.message)
                viewModel.getCourseDetail()
            }

        }
    }

    override fun onApiRetry(apiCode: String) {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_comment -> {
                openCommentDialog(Constant.COMMENT_ASSESSMENT, Constant.CLICK_ADD_COMMENT)
            }
            R.id.iv_delete -> {
                openDeletePopup(Constant.COMMENT_ASSESSMENT)
            }
            R.id.iv_edit -> {
                openCommentDialog(Constant.COMMENT_ASSESSMENT, Constant.CLICK_EDIT_COMMENT)
            }
        }
    }

    private fun openDeletePopup(typeOfComment: Int) {
        CommonAlertDialog.builder(baseActivity)
            .title(getString(R.string.are_you_sure))
            .description(getString(R.string.do_you_really_want_to_delete_your_comment))
            .positiveBtnText(getString(R.string.delete_acc))
            .icon(R.drawable.ic_fogot_password)
            .getCallback {
                if (it) {
                    var filteredList =
                        viewModel.courseData.value?.courseComments?.filter { it.commentType == typeOfComment }

                    if (filteredList?.size ?: 0 > 0) {
                        viewModel.courseData.value?.courseComments?.forEachIndexed { index, courseComments ->
                            if (courseComments.commentType == typeOfComment) {
                                viewModel.addCommentMisc(
                                    index,
                                    "",
                                    Constant.CLICK_DELETE_COMMENT,
                                    commentType = -1 as Int
                                )
                            }
                        }


                    }
                }
            }.build()
    }

    fun openCommentDialog(commentType: Int, type: Int) {
        var filteredList =
            viewModel.courseData.value?.courseComments?.filter { it.commentType == commentType }

        if (filteredList?.size ?: 0 > 0) {
            viewModel.courseData.value?.courseComments?.forEachIndexed { index, courseComments ->
                if (courseComments.commentType == commentType) {
                    AddCommentDialogue(index, type = type, commentType = commentType).apply {
                        arguments =
                            bundleOf("comment" to courseComments.comment)
                        setOnDialogClickListener(this@ModCourseAssessmentFragment)
                    }.show(childFragmentManager, "")
                }
            }


        } else {
            AddCommentDialogue(-1, type = type, commentType = commentType).apply {
                arguments =
                    bundleOf("comment" to null)
                setOnDialogClickListener(this@ModCourseAssessmentFragment)
            }.show(childFragmentManager, "")
        }

    }

    override fun onDialogClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        val desc = items[2] as String
        when (type) {
            Constant.CLICK_ADD_COMMENT -> {
                viewModel.addCommentMisc(
                    position,
                    desc,
                    Constant.CLICK_ADD_COMMENT,
                    commentType = items[4] as Int
                )
            }
            Constant.CLICK_EDIT_COMMENT -> {
                viewModel.addCommentMisc(
                    position,
                    desc,
                    Constant.CLICK_EDIT_COMMENT,
                    commentType = items[4] as Int
                )
            }


        }
    }

}