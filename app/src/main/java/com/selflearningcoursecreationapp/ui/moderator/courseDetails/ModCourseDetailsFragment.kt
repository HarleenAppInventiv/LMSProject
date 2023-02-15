package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentModCourseDetailsBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.moderator.courseDetails.content.ModCourseContentFragment
import com.selflearningcoursecreationapp.ui.moderator.dialog.AddCommentDialogue
import com.selflearningcoursecreationapp.ui.moderator.dialog.ReasonForRejectionDialogue
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CoAuthorStatus
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ModHomeConst
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.HtmlResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModCourseDetailsFragment : BaseFragment<FragmentModCourseDetailsBinding>(),
    View.OnClickListener, BaseBottomSheetDialog.IDialogClick {
    private val viewModel: ModCourseDetailVM by viewModel()
    private var menu: Menu? = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_mod_course_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onResume() {
        super.onResume()
        Log.e("onresume", "calling")
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        arguments?.let {
            viewModel.courseId = it.getInt("courseId")
            if (viewModel.requestType.value.isNullOrZero())
                viewModel.requestType.value = it.getInt("requestType")
//            viewModel.status = it.getInt("status")
            viewModel.id = it.getInt("id")
        }

        initToolbar()
        observeData()
        observeRequestData()
        viewModel.getCourseDetail()
        menu = binding.toolbar.menu


        initViewPager()
        scrollHandling()
        binding.viewModel = viewModel
        binding.tvTime.text =
            viewModel.courseData.value?.createdDate?.changeDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "dd MMM yyyy"
            )
//        setData(viewModel.courseData.value)


        setDataStatus()
        binding.btApprove.setOnClickListener(this)
        binding.btReject.setOnClickListener(this)
        binding.ivCommentDesc.setOnClickListener(this)
        binding.ivComment.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
        binding.ivEdit.setOnClickListener(this)
        binding.ivDeleteDesc.setOnClickListener(this)
        binding.ivEditDesc.setOnClickListener(this)


//        Constant.CLICK_ACCEPT -> {
//            viewModel.updateRequestStatus(CoAuthorStatus.ACCEPT, data)
//        }
//        Constant.CLICK_REJECT -> {
//            viewModel.updateRequestStatus(CoAuthorStatus.REJECT, data)
//
//
//        }
    }

    private fun setDataStatus() {
        binding.bottomV.visible()

        when (viewModel.requestType.value) {
            ModHomeConst.REQUEST -> {
                binding.toolbar.menu.findItem(R.id.action_edit).isVisible = false
                binding.btApprove.visible()
                binding.btReject.visible()
                binding.btApprove.text = baseActivity.getString(R.string.i_can_moderate)
                binding.btReject.text = baseActivity.getString(R.string.i_am_busy)
            }
            ModHomeConst.PENDING -> {
                binding.toolbar.menu.findItem(R.id.action_edit).isVisible = true
                binding.btApprove.visible()
                binding.btReject.visible()
                binding.btApprove.text = baseActivity.getString(R.string.approve)
                binding.btReject.text = baseActivity.getString(R.string.reject)
            }
            else -> {
                binding.toolbar.menu.findItem(R.id.action_edit).isVisible = false
                binding.btApprove.gone()
                binding.bottomV.gone()
                binding.btReject.gone()
            }
        }
    }

    private fun scrollHandling() {

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.toolbar.title = viewModel.courseData.value?.courseTitle ?: ""

            binding.toolbar.scrollHandling(-1204, verticalOffset, -200)
        })

    }

    private fun initToolbar() {
        menu = binding.toolbar.menu
        binding.toolbar.setNavigationOnClickListener {
            baseActivity.onBackPressed()

        }
        binding.toolbar.navigationContentDescription = getString(R.string.back_button)

        menu?.findItem(R.id.action_read)?.isVisible = true
        binding.toolbar.menu.findItem(R.id.action_edit).setOnMenuItemClickListener {
            findNavController().navigateTo(
                R.id.action_modCourseDetailsFragment_to_modEditCourseFragment,
                bundleOf(
                    "courseData" to viewModel.courseData.value,
                )
            )
            return@setOnMenuItemClickListener true
        }
        binding.toolbar.menu.findItem(R.id.action_read).setOnMenuItemClickListener {
            baseActivity.checkAccessibilityService()
            return@setOnMenuItemClickListener true
        }
    }

    private fun observeData() {
        viewModel.courseData.observe(viewLifecycleOwner) {
            setData(it)
        }

        viewModel.commentStatus.observe(viewLifecycleOwner) { commentStatus ->
            setData(viewModel.courseData.value)
        }
    }

    private fun observeRequestData() {
        viewModel.requestType.observe(viewLifecycleOwner) {
            setDataStatus()

        }
    }

    private fun setData(data: CourseData?) {
        data?.let { courseData ->

            setTitleDefaultVisibility(data)
            setDescDefaultVisibility(data)


            binding.ivCourseImage.loadImage(
                courseData.courseBannerUrl,
                R.drawable.ic_home_default_banner, 2
            )
            if (courseData.courseCoAuthors?.find { it.id == data.createdById } == null) {
                courseData.courseCoAuthors?.add(0, UserProfile(id = data.createdById).apply {
                    name = data.createdByName ?: ""
                    profileUrl = data.profileUrl
                    courseLogoURL = data.courseLogoUrl

                })
            }
            binding.rvAuthor.adapter =
                CourseAuthorAdapter(courseData.courseCoAuthors ?: ArrayList())

//            data.courseComments?.forEach { commentData->
//
//                when(commentData.commentType){
//                    Constant.OMMENT_TITLE->{
//                        binding.tvTimeComment.text = commentData.courseCommentCreatedDate.changeDateFormat()
//                        binding.tvComment.setSpanString(
//                            context?.let {
//                                SpanUtils.with(
//                                    it,
//                                    "Comment: ${commentData.comment}"
//                                ).endPos(7).isBold().getSpanString()
//                            }
//                        )
//
//                        binding.ivComment.visibleView(commentData.comment.isNullOrEmpty())
//                        binding.constTitleComment.visibleView(!commentData.comment.isNullOrEmpty())
//
//                    }
//
//                    Constant.COMMENT_DESC ->{
//                        binding.tvTimeCommentDesc.text = commentData.courseCommentCreatedDate.changeDateFormat()
//                        binding.tvCommentDesc.setSpanString(
//                            context?.let {
//                                SpanUtils.with(
//                                    it,
//                                    "Comment: ${commentData.comment}"
//                                ).endPos(7).isBold().getSpanString()
//                            }
//                        )
//
//                        binding.ivCommentDesc.visibleView(commentData.comment.isNullOrEmpty())
//                        binding.constDescComment.visibleView(!commentData.comment.isNullOrEmpty())
//                    }
//                }
//            }


            binding.tvLevel.setComplexityLevel(data.courseComplexityId)
            binding.ivModImage.loadImage(
                viewModel.userProfile?.profileUrl,
                R.drawable.ic_default_user_grey
            )
            binding.tvPreviousModName.text = viewModel.userProfile?.name
            binding.tvModTime.text = courseData.previousModeratorTimeConsumed.getTime(baseActivity)
            binding.clPreviousMod.visibleView(viewModel.requestType.value == ModHomeConst.APPROVED || viewModel.requestType.value == ModHomeConst.REJECTED)
//            binding.tvPreviousModName.text = courseData.previousModeratorName
            binding.tvAuthorName.text = courseData.createdByName
            Log.e("createddate in ", "detail" + courseData.createdDate)
            binding.tvTime.text =
                    /* if (viewModel.requestType.value == ModHomeConst.REQUEST)*/
                courseData.createdDate.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", "dd MMM yyyy")
            /*else courseData.modifiedDate.changeDateFormat(outputFormat = "dd MMM yyyy")*/
            binding.ivProfileImage.loadImage(courseData.profileUrl, R.drawable.ic_default_user_grey)
//            binding.tvModTime.text = context?.getTimeInChar(courseData.previousModeratorTimeConsumed)
//                (requireContext(),showHms = true)
            binding.tvCategory.text = courseData.categoryName
            binding.tvLanguage.text = courseData.languageName
            binding.tvSectionCount.text =
                baseActivity.getQuantityString(R.plurals.section_quantity, courseData.totalSections)

            ResizeableUtils.builder(binding.tvCourseName).isBold(false)
                .isUnderline(false)
                .setLimit(38)
                .setFullText(courseData.courseTitle)
                .setFullText(R.string.read_more_arrow)
                .setLessText(R.string.read_less_arrow)
                .setSpanSize(0.9f)
                .showDots(true)
                .build()

            binding.tvCourseName.contentDescription = "Course name is ${courseData.courseTitle}"
            binding.tvTime.contentDescription =
                "Course created on ${courseData.createdDate.changeDateFormat(outputFormat = "dd MMM yyyy")}"
            binding.ivProfileImage.contentDescription = "Course creator image"
            binding.tvAuthorName.contentDescription =
                "Course author name is ${courseData.createdByName}"
            binding.tvCategory.contentDescription = "Course category is${courseData.categoryName}"
            binding.tvLanguage.contentDescription = "Course language is ${courseData.languageName}"
//            binding.tvDuration.contentDescription= "Course duration is ${courseData.courseDuration}"
            binding.tvSectionCount.contentDescription = "Total sections are ${
                baseActivity.getQuantityString(
                    R.plurals.section_quantity,
                    courseData.totalSections
                )
            }"

            binding.tvRejectionReason.apply {
                setSpanString(
                    SpanUtils.with(
                        context,
                        context.getString(R.string.reason_of_reject) + " " + data.previousModeratorComment
                    ).endPos(
                        21
                    ).isBold().getSpanString()
                )
                visibleView((data.previousModeratorComment?.isNotEmpty() == true))
            }

            val htmlBuilder = HtmlResizeableUtils.builder(binding.tvDescription)
                .fullContent(courseData.courseDescription ?: "")
                .getCallback {
                    if (it) {
//                        displayDataToWeb(readMoreStatus.second, binding.tvDescription)
                        binding.tvReadMore.visible()
                    } else {
//                        displayDataToWeb(desc.toString(), binding.tvDescription)
                        binding.tvReadMore.gone()
                    }
                }
                .build()

//            var readMoreStatus = checkForReadMoreDesc(courseData.courseDescription)
//
//            showLog("SECOND_STRING", "" + readMoreStatus.second)
//            if (readMoreStatus.first) {
//                displayDataToWeb(readMoreStatus.second, binding.tvDescription)
//                binding.tvReadMore.visible()
//            } else {
//                displayDataToWeb(courseData.courseDescription.toString(), binding.tvDescription)
//                binding.tvReadMore.gone()
//            }


            binding.tvReadMore.setOnClickListener {
                if (binding.tvReadMore.content()
                        .equals(baseActivity.getString(R.string.read_more_arrow))
                ) {
//                    displayDataToWeb(courseData.courseDescription.toString(), binding.tvDescription)
                    htmlBuilder.showFullContent()
                    binding.tvReadMore.text = baseActivity.getString(R.string.read_less_arrow)
                } else {
//                    displayDataToWeb(readMoreStatus.second, binding.tvDescription)
                    htmlBuilder.showLessContent()
                    binding.tvReadMore.text = baseActivity.getString(R.string.read_more_arrow)
                }
            }


            binding.tvDuration.text = courseData.courseDuration.getTime(baseActivity)
//            ResizeableUtils.builder(binding.tvDescription)
//                .isBold(false)
//                .isUnderline(false)
//                .setFullText(courseData.getDescription())
//                .setFullText(R.string.read_more_arrow)
//                .setLessText(R.string.read_less_arrow)
//                .setSpanSize(0.9f)
//                .showDots(true)
//                .build()

            binding.clParent.visible()


        }


    }



    private fun setTitleDefaultVisibility(data: CourseData) {
        var titleComment = data.courseComments?.filter { it.commentType == Constant.COMMENT_TITLE }
        if (titleComment?.size ?: 0 > 0) {
            binding.ivComment.visibleView(titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
//            binding.constTitleComment.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.tvTimeComment.text =
                titleComment?.get(0)?.courseCommentCreatedDate.changeDateFormat()
            binding.tvComment.setSpanString(
                context?.let {
                    SpanUtils.with(
                        it,
                        "Comment: ${titleComment?.get(0)?.comment}"
                    ).endPos(7).isBold().getSpanString()
                }
            )

            binding.commentGTitle.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty())
            binding.constTitleComment.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty())
            binding.ivEdit.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.ivDelete.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)


        } else {
            binding.ivComment.visibleView(true && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.constTitleComment.visibleView(false && viewModel.requestType.value == ModHomeConst.PENDING)
        }


    }

    private fun setDescDefaultVisibility(data: CourseData) {
        var titleComment = data.courseComments?.filter { it.commentType == Constant.COMMENT_DESC }
        if (titleComment?.size ?: 0 > 0) {
            binding.ivCommentDesc.visibleView(titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
//            binding.constDescComment.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.tvTimeCommentDesc.text =
                titleComment?.get(0)?.courseCommentCreatedDate.changeDateFormat()
            binding.tvCommentDesc.setSpanString(
                context?.let {
                    SpanUtils.with(
                        it,
                        "Comment: ${titleComment?.get(0)?.comment}"
                    ).endPos(7).isBold().getSpanString()
                }
            )
            binding.commentGDesc.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty())
            binding.constDescComment.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty())
            binding.ivEditDesc.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.ivDeleteDesc.visibleView(!titleComment?.get(0)?.comment.isNullOrEmpty() && viewModel.requestType.value == ModHomeConst.PENDING)


        } else {
            binding.constDescComment.visibleView(false && viewModel.requestType.value == ModHomeConst.PENDING)
            binding.ivCommentDesc.visibleView(true && viewModel.requestType.value == ModHomeConst.PENDING)
        }


    }


    private fun initViewPager() {

        val list = arrayListOf<Fragment>(
            ModCourseContentFragment(),
            ModCourseAssessmentFragment()
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.course_content),
            baseActivity.getString(R.string.course_assessment)
        )

        binding.viewpager.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        TabLayoutMediator(binding.tlHeader, binding.viewpager) { tab, position ->
            tab.text = nameArray[position]


        }.attach()

        binding.tlHeader.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tlHeader.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )
        binding.tlHeader.setCustomTabs(nameArray)
        binding.tlHeader.getTabAt(0)?.customView?.isSelected = true
        binding.tlHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_SEMI_BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_MEDIUM)

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {

        }

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COMMENT_MISC -> {
                val data = value as BaseResponse<ChildModel>
                showToastShort(data.message)


//                viewModel.getCourseDetail()
            }
            ApiEndPoints.API_COMMENT_SECTION -> {
                val data = value as BaseResponse<CourseData>
                showToastShort(data.message)
            }


            ApiEndPoints.API_UPDATE_MOD_COURSE_STATUS -> {

                val data = value as BaseResponse<CourseData>
                showToastShort(data.message)
                setFragmentResult(
                    "modRequestData",
                    bundleOf(
                        "requestType" to viewModel.requestType.value,
                        "status" to data.statusCode
                    )
                )

                baseActivity.onBackPressed()
//                when (data.statusCode) {
//                    CoAuthorStatus.ACCEPT -> {
//                        viewModel.requestType.value = ModHomeConst.APPROVED
//                    }
//                    else -> {
//                        viewModel.requestType.value = ModHomeConst.REJECTED
//
//                    }
//                }
//                setDataStatus()

            }
            ApiEndPoints.API_UPDATE_MOD_REQUEST -> {
                val data = value as BaseResponse<CourseData>
                showToastShort(data.message)
                setFragmentResult(
                    "modRequestData",
                    bundleOf(
                        "requestType" to viewModel.requestType.value,
                        "status" to data.statusCode
                    )
                )
//                findNavController().navigateUp()
                baseActivity.onBackPressed()
//                when (data.statusCode) {
//                    CoAuthorStatus.ACCEPT -> {
//                        viewModel.requestType.value = ModHomeConst.PENDING
//                        setDataStatus()
//                    }
//                    else -> {
//                        viewModel.requestType.value = ModHomeConst.REJECTED
//                        baseActivity.onBackPressed()
//                    }
//                }

            }
        }
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
                        setOnDialogClickListener(this@ModCourseDetailsFragment)
                    }.show(childFragmentManager, "")
                }
            }


        } else {
            AddCommentDialogue(-1, type = type, commentType = commentType).apply {
                arguments =
                    bundleOf("comment" to null)
                setOnDialogClickListener(this@ModCourseDetailsFragment)
            }.show(childFragmentManager, "")
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_comment -> {
                openCommentDialog(Constant.COMMENT_TITLE, Constant.CLICK_ADD_COMMENT)
            }
            R.id.iv_delete -> {

                openDeletePopup(Constant.COMMENT_TITLE)
            }
            R.id.iv_edit -> {
                openCommentDialog(Constant.COMMENT_TITLE, Constant.CLICK_EDIT_COMMENT)
            }

            R.id.iv_comment_desc -> {
                openCommentDialog(Constant.COMMENT_DESC, Constant.CLICK_ADD_COMMENT)
            }
            R.id.iv_delete_desc -> {
                openDeletePopup(Constant.COMMENT_DESC)

            }
            R.id.iv_edit_desc -> {
                openCommentDialog(Constant.COMMENT_DESC, Constant.CLICK_EDIT_COMMENT)
            }
            R.id.bt_approve -> {
                when (viewModel.requestType.value) {
                    ModHomeConst.REQUEST -> {
                        viewModel.updateRequestStatus(
                            CoAuthorStatus.ACCEPT,
                        )
                    }
                    else -> {

                        if (viewModel.courseData.value?.sectionData?.filter { !it.moderatorComment.isNullOrEmpty() }
                                .isNullOrEmpty() && (viewModel.courseData.value?.courseComments?.isNullOrEmpty() == true) &&
                            viewModel.courseData.value?.sectionData?.filter {
                                !it.lessonList.filter { !it.lectureComment.isNullOrEmpty() }
                                    .isNullOrEmpty()
                            }
                                .isNullOrEmpty()
                        ) {

                            viewModel.updateCourseStatus(
                                CoAuthorStatus.ACCEPT,
                            )
                        } else {
                            showToastShort(baseActivity.getString(R.string.you_cant_approve_commented_course))
                        }
                    }
                }
            }

            R.id.bt_reject -> {
                when (viewModel.requestType.value) {
                    ModHomeConst.REQUEST -> {
                        viewModel.updateRequestStatus(
                            CoAuthorStatus.REJECT,
                        )
                    }
                    else -> {
                        ReasonForRejectionDialogue().apply {
                            setOnDialogClickListener(object : BaseBottomSheetDialog.IDialogClick {
                                override fun onDialogClick(vararg items: Any) {
                                    val comment = items[0] as String
                                    viewModel.updateCourseStatus(
                                        CoAuthorStatus.REJECT,
                                        comment
                                    )

                                }

                            })
                        }.show(childFragmentManager, "")
                    }
                }
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

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {

        when (exception.statusCode) {
            HTTPCode.USER_NOT_FOUND -> {
                hideLoading()
                CommonAlertDialog.builder(baseActivity)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .icon(R.drawable.ic_alert)
                    .title("")
                    .notCancellable(true)
                    .hideNegativeBtn(true)
                    .getCallback {
                        if (it) {
                            baseActivity.onBackPressed()
                        }
                    }.build()
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
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