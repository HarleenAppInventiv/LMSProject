package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentContentCourseDetailBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.course_details.certificate.CertificateFragment
import com.selflearningcoursecreationapp.ui.course_details.info.CourseInfoFragment
import com.selflearningcoursecreationapp.ui.course_details.lessons.LessonListingFragment
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorDetailsData
import com.selflearningcoursecreationapp.ui.course_details.ratings.ReviewsFragment
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.HtmlResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ContentCourseDetailFragment : BaseFragment<FragmentContentCourseDetailBinding>(),
    BaseAdapter.IViewClick {

    private var goToReview: Boolean = false
    private var menu: Menu? = null
    private val viewModel: CourseDetailVM by viewModel()
    private var mAdapter: CoAuthorListAdapter? = null
    var authorProfilePos = -1

    override fun getLayoutRes() = R.layout.fragment_content_course_detail
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        binding.viewModel = viewModel
        arguments?.let {
            viewModel.courseId = it.getInt("courseId")
            viewModel.courseContentType = it.getString("status").toString()
            if (it.containsKey("goToReview")) {
                goToReview = it.getBoolean("goToReview")
            }
            if (it.containsKey("ModeratorRequestId")) {
                viewModel.moderatorRequestId = it.getString("ModeratorRequestId").toString()
            }
        }

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeData()
        viewModel.getCourseDetail(viewModel.courseContentType)

        baseActivity.setToolbar(showToolbar = false)
//        if (viewModel.courseData.value?.status != CreatedCourseStatus.PUBLISHED) {
        menu = binding.toolbar.menu

//        menu?.findItem(R.id.action_share).
        menu?.findItem(R.id.action_share)?.setOnMenuItemClickListener {
//            menu?.findItem(R.id.action_share)?.isEnabled = false
//            baseActivity.shareIntent(getString(R.string.share_course_data))
//            lifecycleScope.launch {
//                delay(1000)
//                baseActivity.runOnUiThread {
//                    menu?.findItem(R.id.action_share)?.isEnabled = true
//
//                }
//            }
            viewModel.getCourseShareLinkUrl()
            return@setOnMenuItemClickListener true
        }

        menu?.findItem(R.id.action_read)?.setOnMenuItemClickListener {
            baseActivity.checkAccessibilityService()
            return@setOnMenuItemClickListener true
        }

        scrollHandling()
//        binding.tvOldPrice.paintFlags =
//            binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btEnroll.setOnClickListener {
            if (viewModel.courseData.value?.status == CreatedCourseStatus.PARTIALREJECTED) {
                viewModel.editToDraft(viewModel.courseData.value?.courseId ?: 0)
            } else {
                findNavController().navigateTo(
                    R.id.action_contentCourseDetailFragment_to_addCourseBaseFragment,
                    bundleOf(
                        "courseId" to viewModel.courseData.value?.courseId
                    )
                )
            }
        }

        binding.llRatings.setOnClickListener {
            binding.viewpager.setCurrentItem(3)
        }


//        binding.btEnroll.setOnClickListener {
////            updateButton()
//        }
    }

//    private fun updateButton() {
//        if (baseActivity.tokenFromDataStore() == "") {
//            baseActivity.guestUserPopUp()
//        } else {
//            viewModel.courseData.value?.let {
//                when (it.courseTypeId) {
//                    CourseType.FREE -> {
//                        viewModel.purchaseCourse()
//
//                    }
//                    CourseType.PAID -> {
//                        viewModel.buyRazorPayCourse()
//                    }
//                    CourseType.RESTRICTED -> {
//                        UnlockCourseDialog().apply {
//                            arguments = bundleOf("courseId" to viewModel.courseId,
//                                "courseType" to viewModel.courseData.value?.courseTypeId)
//                            setOnDialogClickListener(this@ContentCourseDetailFragment)
//                        }.show(childFragmentManager, "")
//                    }
//                    CourseType.REWARD_POINTS -> {
//                        val desc = String.format(
//                            baseActivity.getString(R.string.to_buy_this_course),
//                            viewModel.courseData.value?.rewardPoints
//                        )
//                        CommonAlertDialog.builder(requireContext())
//                            .title(getString(R.string.pay_by_reward))
//                            .description(desc)
//                            .icon(R.drawable.ic_coin_icon)
//                            .hideNegativeBtn(false)
//                            .positiveBtnText(getString(R.string.continues))
//                            .negativeBtnText(getString(R.string.cancel))
//                            .getCallback {
//                                if (it) {
//                                    viewModel.purchaseCourse()
//                                }
//                            }
//                            .build()
//                    }
//                    else -> {
//
//                    }
//
//                }
//            }
//        }
//    }

    private fun observeData() {
        viewModel.shareLinkUrlLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                if (it.url != null) {
                    baseActivity.shareIntent("${getString(R.string.below_is_the_link_for_course_detail)}  \n" + it.url + "\n")
                }
            }

        }

        viewModel.courseData.observe(viewLifecycleOwner) {
            viewModel.courseTypeId = it?.courseTypeId
            setData(it)
        }
    }

    private fun setData(data: CourseData?) {
        data?.let { courseData ->
            viewModel.courseType = courseData.courseTypeId ?: 0
            initViewPager()

            menu?.findItem(R.id.action_share)?.isVisible =
                data.status == CreatedCourseStatus.PUBLISHED
            binding.llRatings.visibleView(!viewModel.courseContentType.equals("moderatorComments"))
            binding.parentCL.visibleView(true)
            binding.ivCourseImage.loadImage(
                courseData.courseBannerUrl,
                R.drawable.ic_home_default_banner, 3
            )
//            binding.tvDescription.text = data.courseDescription
//            binding.tvDuration.text= data.courseDuration.toString()
            binding.tvCategory.text = data.categoryName
            binding.tvLanguage.text = data.languageName
//            binding.tvCourseName.text = data.courseTitle

            ResizeableUtils.builder(binding.tvCourseName).isBold(false)
                .isUnderline(false)
                .setLimit(38)
                .setFullText(courseData.courseTitle)
                .setFullText(R.string.read_more_arrow)
                .setLessText(R.string.read_less_arrow)
                .setSpanSize(0.9f)
                .showDots(true)
                .build()

            binding.tvPostedDate.text =
                data.createdDate.changeDateFormat(outputFormat = "dd MMM yyyy")
            binding.tvDuration.text =
                if (data.courseDuration.isNullOrZero()) "00:00:00" else data.courseDuration.getTime(
                    baseActivity,
                    true,
                    true
                )

            binding.tvCourseName.contentDescription =
                "${getString(R.string.course_name_is)}  ${courseData.courseTitle}"
//            binding.tvTime.contentDescription= "Course created on ${ courseData.createdDate.changeDateFormat(outputFormat = "dd MMM yyyy")}"
//            binding.ivProfileImage.contentDescription= "Course creator image"
//            binding.tvAuthorName.contentDescription= "Course author name is ${courseData.createdByName}"

            binding.tvCategory.contentDescription =
                "${getString(R.string.course_category_is)} ${courseData.categoryName}"

            binding.tvLanguage.contentDescription =
                "${getString(R.string.course_language_is)} ${courseData.languageName}"


//            binding.tvDuration.contentDescription= "Course duration is ${courseData.courseDuration}"
            binding.tvSectionCount.contentDescription =
                "${getString(R.string.total_sections_are)} ${
                    baseActivity.getQuantityString(
                        R.plurals.section_quantity,
                        courseData.totalSections
                    )
                }"


            mAdapter = CoAuthorListAdapter(data.courseCoAuthors)
            binding.recyclerCoauthor.adapter = mAdapter
            mAdapter?.setOnAdapterItemClickListener(this)
            binding.tvRejectionReason.apply {
                setSpanString(
                    SpanUtils.with(
                        context,
                        context.getString(R.string.reason_of_reject) + " " + data.previousModeratorComment
                    ).endPos(
                        21
                    ).isBold().getSpanString()
                )
//                text = context.getString(R.string.reason_of_reject) +" "+ data.previousModeratorComment

                visibleView(
                    (data.previousModeratorComment?.isNotEmpty() == true) && (data.status != CreatedCourseStatus.SUBMIT || viewModel.courseContentType.equals(
                        "moderatorComments"
                    ))
                )
            }


            data.courseComments?.forEach { commentData ->

                when (commentData.commentType) {
                    Constant.COMMENT_TITLE -> {
                        binding.tvTitleComment.setSpanString(
                            context?.let {
                                SpanUtils.with(
                                    it,
                                    "${getString(R.string.mod_comment)} ${commentData.comment}"
                                ).endPos(21).isBold().getSpanString()
                            }
                        )
                        binding.tvTitleComment.visibleView(
                            (commentData.comment?.isNotEmpty() == true) && (data.status != CreatedCourseStatus.SUBMIT || viewModel.courseContentType.equals(
                                "moderatorComments"
                            ))
                        )

                    }

                    Constant.COMMENT_DESC -> {
                        binding.tvDescComment.setSpanString(
                            context?.let {
                                SpanUtils.with(
                                    it,
                                    "${getString(R.string.mod_comment)} ${commentData.comment}"
                                ).endPos(21).isBold().getSpanString()
                            }
                        )
                        binding.tvDescComment.visibleView(
                            (commentData.comment?.isNotEmpty() == true) && (data.status != CreatedCourseStatus.SUBMIT || viewModel.courseContentType.equals(
                                "moderatorComments"
                            ))
                        )
                    }
                }
            }

            binding.btEnroll.setCreateCourseText(data.status)

            binding.tvSectionCount.text =
                baseActivity.getQuantityString(
                    R.plurals.section_quantity,
                    courseData.totalSections
                )
            binding.tvReviews.text = data.totalReviews.getReviewCount()
            binding.tvLevel.setComplexityLevel(data.courseComplexityId)
            binding.tvLevel.text = data.courseComplexityName


            when (data?.courseTypeId) {
                CourseType.REWARD_POINTS -> {
                    binding.tvNewPrice.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_coin_yellow,
                        0,
                        0,
                        0
                    )
                    binding.tvNewPrice.text = " ${
                        baseActivity.getQuantityString(
                            R.plurals.point_quantity,
                            data.rewardPoints?.toIntOrNull() ?: 0
                        )
                    }"


                    binding.tvNewPrice.setTextColor(baseActivity.getAttrResource(R.attr.blackTextColor))

                }
                CourseType.PAID -> {
                    binding.tvNewPrice.text =
                        String.format("%s %s", data.currencySymbol, data.courseFee)
                    binding.tvNewPrice.setTextColor(baseActivity.getAttrResource(R.attr.accentColor_Green))

                }
                CourseType.FREE -> {
                    binding.tvNewPrice.text = baseActivity.getString(R.string.free)
                    binding.tvNewPrice.setTextColor(baseActivity.getAttrResource(R.attr.blackTextColor))
                }
                else -> {
                    binding.tvNewPrice.text = baseActivity.getString(R.string.restricted)
                    binding.tvNewPrice.setTextColor(baseActivity.getAttrResource(R.attr.blackTextColor))

                    binding.tvNewPrice.visible()
                    binding.tvPriceTitle.invisible()

                }
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


//            var desc = courseData.courseDescription
//            var readMoreStatus = checkForReadMoreDesc(desc)
//
//            showLog("SECOND_STRING", "" + readMoreStatus.second)
//            if (readMoreStatus.first) {
//                displayDataToWeb(readMoreStatus.second, binding.tvDescription)
//                binding.tvReadMore.visible()
//            } else {
//                displayDataToWeb(desc.toString(), binding.tvDescription)
//                binding.tvReadMore.gone()
//            }


            binding.tvReadMore.setOnClickListener {
                if (binding.tvReadMore.content()
                        .equals(baseActivity.getString(R.string.read_more_arrow))
                ) {
//                    displayDataToWeb(desc.toString(), binding.tvDescription)
                    htmlBuilder.showFullContent()
                    binding.tvReadMore.text = baseActivity.getString(R.string.read_less_arrow)
                } else {
//                    displayDataToWeb(readMoreStatus.second, binding.tvDescription)
                    htmlBuilder.showLessContent()
                    binding.tvReadMore.text = baseActivity.getString(R.string.read_more_arrow)
                }
            }

//            binding.tvReadMore.setOnClickListener {
//                if (binding.tvReadMore.content()
//                        .equals(baseActivity.getString(R.string.read_more_arrow))
//                ) {
//                    courseData.courseDescription?.let { displayDataToWeb(it, binding.tvDescription) }
//                    binding.tvReadMore.text = baseActivity.getString(R.string.read_less_arrow)
//                } else {
//                    val newT= checkUnclosedTags(courseData?.courseDescription?.trim()?.replace("<p><br></p>", "\n")?.substring(0,150)+"...")
//                    displayDataToWeb(newT, binding.tvDescription)
//                    binding.tvReadMore.text = baseActivity.getString(R.string.read_more_arrow)
//                }
//            }

//            binding.grpUnlock.gone()


//            if (data.userCourseStatus == CourseStatus.NOT_ENROLLED) {
//                when (data.courseTypeId) {
//                    CourseType.REWARD_POINTS -> {
//                        binding.tvCoin.visible()
//                        binding.tvPriceTitle2.visible()
//
//                    }
//                    CourseType.RESTRICTED -> {
//                        binding.grpUnlock.gone()
//                    }
//                    CourseType.PAID -> {
//                        binding.tvNewPrice.visible()
//                        binding.tvPriceTitle.visible()
////                        binding.tvOldPrice.visible()
//                        binding.tvNewPrice.text =
//                            String.format("%s %s", data.currencySymbol, data.courseFee)
//                        binding.tvNewPrice.setTextColor(
//                            ContextCompat.getColor(
//                                baseActivity,
//                                R.color.accent_color_2FBF71
//                            )
//                        )
//                    }
//                    CourseType.FREE -> {
//                        binding.tvNewPrice.visible()
//                        binding.tvPriceTitle.visible()
//                        binding.tvNewPrice.text = baseActivity.getString(R.string.free)
//                        binding.tvNewPrice.changeTextColor(ThemeConstants.TYPE_PRIMARY)
//
//                    }
//                }
//            }
        }
    }



    private fun initViewPager() {

        var revenueType = 1
        when (viewModel.courseType) {
            CourseType.REWARD_POINTS -> {
                revenueType = RevenueType.REWARD_POINTS

            }
            CourseType.PAID -> {
                revenueType = RevenueType.PURCHASED
            }

        }

        val data = bundleOf(
            "courseId" to viewModel.courseId,
            "RevenueType" to revenueType,
//            "creator" to "creator"
        )
//        viewModel.courseContentType = getString(R.string.creator)

        val revenueFrag = RevenueFragment()
        revenueFrag.arguments = data

        val list = ArrayList<Fragment>()
        list.add(CourseInfoFragment())
        list.add(LessonListingFragment())
        list.add(CertificateFragment())


        val nameArray = arrayListOf<String>()
        nameArray.add(baseActivity.getString(R.string.info))
        nameArray.add(baseActivity.getString(R.string.lessons))
        nameArray.add(baseActivity.getString(R.string.certificate))

        if (!viewModel.courseContentType.equals("moderatorComments")) {
            list.add(ReviewsFragment())
            nameArray.add(baseActivity.getString(R.string.reviews))
            if (viewModel.courseType == CourseType.PAID || viewModel.courseType == CourseType.REWARD_POINTS) {
                nameArray.add(baseActivity.getString(R.string.revenue_text))
                list.add(revenueFrag)
            }

        }

        binding.viewpager.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        TabLayoutMediator(binding.tlHeader, binding.viewpager) { tab, position ->
            tab.text = nameArray[position]
        }.attach()
        binding.tlHeader.setCustomTabs(nameArray)
        binding.tlHeader.getTabAt(0)?.customView?.isSelected = true
        binding.tlHeader.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tlHeader.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )
        if (goToReview) {
            lifecycleScope.launch {
                delay(500)
                binding.viewpager.currentItem = 3

            }
        }
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

    private fun scrollHandling() {
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->

            binding.toolbar.title = viewModel.courseData.value?.courseTitle ?: ""
            binding.toolbar.scrollHandling(-1204, verticalOffset, -200)


        })
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_GET_REVIEW -> {
                if (binding.viewpager.currentItem == 3) {
                    ((binding.viewpager.adapter as ScreenSlidePagerAdapter).list[3] as ReviewsFragment).onRefreshData()
                }
            }
            else -> {
                viewModel.onApiRetry(apiCode)

            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
//     if (isAdded && isVisible)  ((binding.viewpager.adapter as ScreenSlidePagerAdapter).list[binding.viewpager.currentItem] as BaseFragment<*>).onResponseSuccess(value,apiCode)
        when (apiCode) {
            ApiEndPoints.API_COURSE_AUTHOR_DETAIL -> {
                var data = value as BaseResponse<AuthorDetailsData>
                if (data.statusCode == HTTPCode.USER_NOT_FOUND) {
                    CommonAlertDialog.builder(baseActivity)
                        .description(data.message ?: "")
                        .positiveBtnText(baseActivity.getString(R.string.okay))
                        .icon(R.drawable.ic_alert)
                        .title("")
                        .notCancellable(true)
                        .hideNegativeBtn(true)
                        .getCallback {
                        }.build()
                }

                if (data.statusCode == HTTPCode.SUCCESS) {
                    findNavController().navigateTo(
                        R.id.action_contentCourseDetailFragment_to_authorDetailsFragment,
                        bundleOf(
                            "userId" to viewModel.authorUserId
                        )
                    )
                }
            }
            ApiEndPoints.GUEST_LOGIN -> {
                baseActivity.guestUserPopUp()
            }
            ApiEndPoints.API_RAZORPAY_COURSE -> {
                baseActivity.startRazorpayPayment((value as BaseResponse<OrderData>).resource)
            }
            ApiEndPoints.API_PURCHASE_COURSE -> {
                showToastShort((value as BaseResponse<OrderData>).message)
                viewModel.userCourseStatus.value = CourseStatus.ENROLLED
                binding.bottomCL.visibleView(false)

            }
            ApiEndPoints.API_EDIT_TO_DRAFT -> {

                findNavController().navigateTo(
                    R.id.addCourseBaseFragment,
                    bundleOf(
                        "courseId" to viewModel.courseId,
                        "fromEdit" to true
                    )
                )
            }

        }
    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_AUTHOR_PROFILE -> {

                viewModel.authorUserId =
                    viewModel.courseData.value?.courseCoAuthors?.get(position)?.id ?: 0
                viewModel.getAuthorDetails()

            }
        }
    }

    fun refreshData(goToReview: Boolean) {
        this.goToReview = goToReview
        viewModel.getCourseDetail(viewModel.courseContentType)

    }

//    override fun onDialogClick(vararg items: Any) {
//        val type = items[0] as Int
//        when (type) {
//            Constant.CLICK_VIEW -> {
//                binding.bottomCL.visibleView(false)
//                viewModel.courseData.value?.userCourseStatus = CourseStatus.ENROLLED
//                viewModel.purchaseCourseLiveData.value = OrderData()
////                val otp = items[1] as String
////                viewModel.otp = otp
//                //  viewModel.purchaseCourse()
//            }
//        }
//
//    }


}