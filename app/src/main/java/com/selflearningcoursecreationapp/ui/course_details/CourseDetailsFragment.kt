package com.selflearningcoursecreationapp.ui.course_details

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentCourseDetailsBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.ui.course_details.certificate.CertificateFragment
import com.selflearningcoursecreationapp.ui.course_details.info.CourseInfoFragment
import com.selflearningcoursecreationapp.ui.course_details.lessons.LessonListingFragment
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorDetailsData
import com.selflearningcoursecreationapp.ui.course_details.ratings.ReviewsFragment
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockCourseDialog
import com.selflearningcoursecreationapp.ui.payment.CheckoutBottomSheet
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.ImageViewBuilder
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class CourseDetailsFragment : BaseFragment<FragmentCourseDetailsBinding>(),
    BaseDialog.IDialogClick, BaseBottomSheetDialog.IDialogClick {

    private var menu: Menu? = null
    private val viewModel: CourseDetailVM by viewModel()
    private val sharedHomeModel: HomeVM by sharedViewModel()
    var prevLink = ""


    override fun getLayoutRes(): Int {
        return R.layout.fragment_course_details
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        binding.viewModel = viewModel

        arguments?.let {
            viewModel.courseId = it.getInt("courseId")
        }


        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeData()
        viewModel.getCourseDetail()
        menu = binding.toolbar.menu
        menu?.findItem(R.id.action_share)?.setOnMenuItemClickListener {

            viewModel.getCourseShareLinkUrl()

//            baseActivity.shareIntent("Below is the link for course details  \n"+ viewModel.shareUrl+"\n")

            return@setOnMenuItemClickListener true
        }
        menu?.findItem(R.id.action_read)?.setOnMenuItemClickListener {
            baseActivity.checkAccessibilityService()
            return@setOnMenuItemClickListener true
        }
        initViewPager()
        scrollHandling()
        binding.tvOldPrice.paintFlags = binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


        binding.toolbar.setNavigationOnClickListener {
            it.contentDescription = getString(R.string.back_button)
            findNavController().navigateUp()

        }

        binding.btEnroll.setOnClickListener {
            updateButton()
        }

        viewModel.viewPagerScroll.observe(viewLifecycleOwner) {
            binding.viewpager.isUserInputEnabled = it
        }

        binding.ivProfileImage.setOnClickListener {
            navigateToAuthorDetail()
        }

        binding.tvAuthorName.setOnClickListener { navigateToAuthorDetail() }
        binding.ivLogo.setOnClickListener { navigateToAuthorDetail() }

        binding.llRatings.setOnClickListener {
            binding.viewpager.currentItem = 3
        }

    }

    private fun navigateToAuthorDetail() {
        if (baseActivity.tokenFromDataStore() == "") {
            baseActivity.guestUserPopUp()
        } else {
            viewModel.authorUserId = viewModel.courseData.value?.courseCoAuthors?.get(0)?.id ?: 0
            viewModel.getAuthorDetails()


        }
    }

    private fun updateButton() {
        if (baseActivity.tokenFromDataStore() == "") {
            baseActivity.guestUserPopUp()
        } else {
            viewModel.courseData.value?.let {
                when (it.courseTypeId) {
                    CourseType.FREE -> {
                        viewModel.purchaseCourse()

                    }
                    CourseType.PAID -> {
                        CheckoutBottomSheet().apply {
                            arguments = bundleOf(
                                "courseFee" to viewModel.courseData.value?.courseFee,
                            )
                            setOnDialogClickListener(this@CourseDetailsFragment)

                        }.show(childFragmentManager, "")

                    }
                    CourseType.RESTRICTED -> {
                        UnlockCourseDialog().apply {
                            arguments = bundleOf(
                                "courseId" to viewModel.courseId,
                                "courseType" to viewModel.courseData.value?.courseTypeId
                            )
                            setOnDialogClickListener(this@CourseDetailsFragment)
                        }.show(childFragmentManager, "")
                    }
                    CourseType.REWARD_POINTS -> {
                        val desc = String.format(
                            baseActivity.getString(R.string.to_buy_this_course),
                            viewModel.courseData.value?.rewardPoints
                        )
                        CommonAlertDialog.builder(baseActivity)
                            .title(getString(R.string.pay_by_reward))
                            .description(desc)
                            .icon(R.drawable.ic_coin_icon)
                            .hideNegativeBtn(false)
                            .positiveBtnText(getString(R.string.continues))
                            .negativeBtnText(getString(R.string.cancel))
                            .getCallback {
                                if (it) {
                                    viewModel.purchaseCourse()
                                }
                            }
                            .build()
                    }
                    else -> {

                    }

                }
            }
        }
    }

    private fun observeData() {
        viewModel.shareLinkUrlLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                if (it.url != null) {
                    baseActivity.shareIntent("${getString(R.string.below_is_the_link_for_course_detail)} \n" + it.url + "\n")

                }
            }


        }

        viewModel.courseData.observe(viewLifecycleOwner) {

            setData(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(data: CourseData?) {
        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

        data?.let { courseData ->
            binding.parentCL.visibleView(true)
//            binding.ivCourseImage.loadImage(
//                courseData.courseBannerUrl,
//                R.drawable.ic_home_default_banner, courseData.courseBannerHash
//            )
            ImageViewBuilder.builder(binding.ivCourseImage)
                .setImageUrl(courseData.courseBannerUrl)
//                .blurhash(courseData.courseBannerHash)
                .placeHolder(R.drawable.ic_home_default_banner)
                .colorIndex(3)
                .loadImage()

            binding.bottomCL.visibleView(courseData.userCourseStatus.isNullOrZero())
            binding.progressG.visibleView(courseData.userCourseStatus == 1)
            binding.ivProfileImage.loadImage(
                courseData.profileUrl,
                R.drawable.ic_default_user_grey,
                data.profileBlurHash
            )

            binding.ivLogo.loadImage(
                courseData.courseLogoUrl,
                R.drawable.ic_logo_default,
                data.courseLogoHash
            )

//            binding.btEnroll.text = baseActivity.getButtonText(
//                courseData.courseTypeId,
//                courseData.userCourseStatus
//            )

            binding.btEnroll.setCourseButton(
                courseData.courseTypeId,
                courseData.userCourseStatus,
                courseData.paymentStatus,
                1
            )
            binding.tvSectionCount.text =
                baseActivity.getQuantityString(R.plurals.section_quantity, courseData.totalSections)
            binding.tvReviews.apply {
                text = data.totalReviews.getReviewCount()
                contentDescription = "$text user have already given the rating"
            }
            binding.tvLevel.setComplexityLevel(data.courseComplexityId)
            binding.tvDuration.text = courseData.courseDuration.getTime(baseActivity)
            binding.tvCoin.text = data.rewardPoints + getString(R.string.points_small)
            sharedHomeModel.updateCourseRating(
                viewModel.courseId,
                data.averageRating,
                data.totalReviews
            )


//            var desc= "asadasadasadasd<br>a<br>a<br>a<br>a<br>a<br>a<br>aa<br>a<br>"
            var desc = courseData.courseDescription ?: ""
            var readMoreStatus = getMoreText(desc.toString())

            showLog("SECOND_STRING", "" + readMoreStatus.second)
            if (readMoreStatus.first) {
                displayDataToWeb(readMoreStatus.second, binding.tvDescription)
                binding.tvReadMore.visible()
            } else {
                displayDataToWeb(desc.toString(), binding.tvDescription)
                binding.tvReadMore.gone()
            }



            binding.tvReadMore.setOnClickListener {
                if (binding.tvReadMore.content()
                        .equals(baseActivity.getString(R.string.read_more_arrow))
                ) {
                    displayDataToWeb(desc.toString().replace("\n", "<br>"), binding.tvDescription)
                    binding.tvReadMore.text = baseActivity.getString(R.string.read_less_arrow)
                } else {
                    displayDataToWeb(readMoreStatus.second, binding.tvDescription)

                    binding.tvReadMore.text = baseActivity.getString(R.string.read_more_arrow)
                }
            }

//            if(courseData?.courseDescription?.trim()?.replace("<p><br></p>", "\n")?.length ?: 0 >150)
//            {
//                binding.tvReadMore.visible()
//                val newT= checkUnclosedTags(courseData?.courseDescription?.trim()?.replace("<p><br></p>", "\n")?.substring(0,150)+"...")
//                displayDataToWeb(newT, binding.tvDescription)
//
//            }else {
//                courseData.courseDescription?.let { displayDataToWeb(it, binding.tvDescription) }
//                binding.tvReadMore.gone()
//            }
//
//
//
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


//            ResizeableUtils.builder(binding.tvDescription).isBold(false)
//                .isUnderline(false)
//                .setFullText(courseData.getDescription())
//                .setFullText(R.string.read_more_arrow)
//                .setLessText(R.string.read_less_arrow)
//                .setSpanSize(0.9f)
//                .showDots(true)
//                .build()


            ResizeableUtils.builder(binding.tvCourseName).isBold(false)
                .isUnderline(false)
                .setLimit(38)
                .setFullText(courseData.courseTitle)
                .setFullText(R.string.read_more_arrow)
                .setLessText(R.string.read_less_arrow)
                .setSpanSize(0.9f)
                .showDots(true)
                .build()

            binding.grpUnlock.gone()

            binding.tvCourseName.contentDescription = "Course name is ${courseData.courseTitle}"
//            binding.tvTime.contentDescription= "Course created on ${ courseData.createdDate.changeDateFormat(outputFormat = "dd MMM yyyy")}"
            binding.ivProfileImage.contentDescription = "Course creator image"
            binding.ivLogo.contentDescription = getString(R.string.course_logo_image)
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



            if (data.userCourseStatus == CourseStatus.NOT_ENROLLED) {
                when (data.courseTypeId) {
                    CourseType.REWARD_POINTS -> {
                        binding.tvCoin.visible()
                        binding.tvPriceTitle2.visible()

                    }
                    CourseType.RESTRICTED -> {
                        binding.grpUnlock.gone()
                    }
                    CourseType.PAID -> {
                        binding.tvNewPrice.visible()
                        binding.tvPriceTitle.visible()
//                        binding.tvOldPrice.visible()
                        binding.tvNewPrice.text =
                            String.format("%s %s", data.currencySymbol, data.courseFee)
                        binding.tvNewPrice.setTextColor(
                            baseActivity.getAttrResource(R.attr.accentColor_Green)
                        )
                    }
                    CourseType.FREE -> {
                        binding.tvNewPrice.visible()
                        binding.tvPriceTitle.visible()
                        binding.tvNewPrice.text = baseActivity.getString(R.string.free)
                        binding.tvNewPrice.changeTextColor(ThemeConstants.TYPE_PRIMARY)

                    }
                    else -> {}
                }
            } else {
                binding.progressG.visibleView((data.percentageCompleted ?: 0.0) > 0.0)

                binding.tvProgress.text =
                    if ((data.percentageCompleted ?: 0.0) > 0 && (data.percentageCompleted
                            ?: 0.0) < 1
                    ) {
                        (data.percentageCompleted).toString() + "% " + getString(R.string.completed)

                    } else {
                        (data.percentageCompleted)?.toInt()
                            .toString() + "% " + getString(R.string.completed)

                    }
//                binding.pbProgress.apply {
//                    max = 100
//                    progress = data.percentageCompleted?.toInt() ?: 0
//                    /*  if (data.percentageCompleted?.toInt() == 100) data.courseDuration?.toInt() ?: 0
//                      else data.courseDuration?.toInt()
//                          ?: 0.minus(data.totalDurationLeft ?: 0)*/
//                }

                binding.pbProgress.apply {

                    max = 100
                    progress =
                        if ((data.percentageCompleted ?: 0.0) > 0 && (data.percentageCompleted
                                ?: 0.0) < 1
                        ) 1
                        else
                            data.percentageCompleted?.toInt() ?: 0

                }

            }
        }
    }

    private fun checkDescForLimitedText(
        splitList: List<String>?,
        content: String?,
        courseDescription: String?
    ): Boolean {
        if ((splitList?.size ?: 0) > 4) {
            var concattedText = ""
            for (i in 0 until 4) {
                concattedText += ((splitList?.get(i) ?: "") + "<br>")
            }

            binding.tvReadMore.visible()
            val newText = checkUnclosedTags(
                concattedText + "...", courseDescription ?: ""
            )
            displayDataToWeb(newText, binding.tvDescription)

//            if((concattedText?.length ?: 0) > Constant.DESC_CHAR_COUNT_MAX)
//            {
//                binding.tvReadMore.visible()
//                displayDataToWeb(concattedText?.substring(0,Constant.DESC_CHAR_COUNT_MAX)+"...", binding.tvDescription)
//
//            }
//            else{
//                binding.tvReadMore.visible()
//                displayDataToWeb(concattedText+"...", binding.tvDescription)
//            }
            return true
        } else if ((content?.length ?: 0) > Constant.DESC_CHAR_COUNT_MAX) {
            binding.tvReadMore.visible()
            val newT = checkUnclosedTags(
                content?.substring(0, Constant.DESC_CHAR_COUNT_MAX)?.replace("\n", "<br>") + "...",
                content ?: ""
            )
            displayDataToWeb(newT, binding.tvDescription)
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCourseDetail()
    }

    private fun initViewPager() {
        val data = bundleOf(
            "courseId" to viewModel.courseId,
//            "visible" to (viewModel.courseData.value?.totalSections == viewModel.courseData.value?.totalSectionsCompleted)

//            "userCourseStatus" to viewModel.courseData.value?.userCourseStatus
        )

        val fragOne = CourseInfoFragment()
        fragOne.arguments = data
        val fragTwo = LessonListingFragment()
        fragTwo.arguments = data

        val fragThree = CertificateFragment()
        fragThree.arguments = data

        val fragFragment = ReviewsFragment()
        fragFragment.arguments = data


        val list = arrayListOf<Fragment>(
            fragOne,
            fragTwo,
            fragThree,
            fragFragment
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.info),
            baseActivity.getString(R.string.lessons),
            baseActivity.getString(R.string.certificate),
            baseActivity.getString(R.string.reviews)
        )
        binding.viewpager.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        TabLayoutMediator(binding.tlHeader, binding.viewpager) { tab, position ->
            tab.text = nameArray[position]
        }.attach()

        binding.tlHeader.setCustomTabs(nameArray)
        binding.tlHeader.getTabAt(0)?.customView?.isSelected = true
        binding.tlHeader.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tlHeader.setTabTextColors(
            baseActivity.getAttrResource(R.attr.hintColor),
            ThemeUtils.getPrimaryTextColor(baseActivity)
        )
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
            binding.appBar.post {
                binding.toolbar.title = viewModel.courseData.value?.courseTitle ?: ""
                binding.toolbar.scrollHandling(-1352, verticalOffset, -200)

            }


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
        when (apiCode) {
            ApiEndPoints.API_COURSE_AUTHOR_DETAIL -> {
                var data = value as BaseResponse<AuthorDetailsData>
                if (data.statusCode == HTTPCode.USER_NOT_FOUND) {
                    CommonAlertDialog.builder(baseActivity)
                        .description(data.message)
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
                        R.id.action_courseDetailsFragment_to_authorDetailsFragment, bundleOf(
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
                CommonAlertDialog.builder(baseActivity)
                    .title(getString(R.string.congrats))
                    .description(getString(R.string.you_have_succesully_enroled_inthis))
                    .icon(R.drawable.ic_checked_logo)
                    .hideNegativeBtn(true)
                    .notCancellable(false)
                    .positiveBtnText(getString(R.string.okay))
                    .getCallback {
                        if (it) {
                            viewModel.userCourseStatus.value = CourseStatus.ENROLLED
                            sharedHomeModel.updateCourse(viewModel.courseId)
                            binding.bottomCL.visibleView(false)
                        }
                    }
                    .build()
            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        val type = items[0] as Int
        when (type) {
            DialogType.PAYMENT -> {
                viewModel.stateId = items[1] as String
                viewModel.buyRazorPayCourse()
            }

            Constant.CLICK_VIEW -> {
                binding.bottomCL.visibleView(false)
                viewModel.courseData.value?.userCourseStatus = CourseStatus.ENROLLED
                viewModel.purchaseCourseLiveData.value = OrderData()
//                val otp = items[1] as String
//                viewModel.otp = otp
                //  viewModel.purchaseCourse()
            }
        }

    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (exception.statusCode) {
            HTTPCode.USER_NOT_FOUND -> {
                hideLoading()
                if (apiCode.equals(ApiEndPoints.API_COURSE_SHARE_URL)) {
                    super.onException(isNetworkAvailable, exception, apiCode)

                } else {
                    CommonAlertDialog.builder(baseActivity)
                        .description(exception.message ?: "")
                        .positiveBtnText(baseActivity.getString(R.string.okay))
                        .icon(R.drawable.ic_alert)
                        .title("")
                        .notCancellable(true)
                        .hideNegativeBtn(true)
                        .getCallback {
                            if (it) {
                                if (!apiCode.equals(ApiEndPoints.API_COURSE_AUTHOR_DETAIL)) {
                                    baseActivity.onBackPressed()
                                }

                            }
                        }.build()
                }
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)
            }
        }

    }
}



