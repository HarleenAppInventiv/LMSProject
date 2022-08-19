package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentContentCourseDetailBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.course_details.certificate.CertificateFragment
import com.selflearningcoursecreationapp.ui.course_details.info.CourseInfoFragment
import com.selflearningcoursecreationapp.ui.course_details.lessons.LessonListingFragment
import com.selflearningcoursecreationapp.ui.course_details.ratings.ReviewsFragment
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CourseStatus
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class ContentCourseDetailFragment : BaseFragment<FragmentContentCourseDetailBinding>() {

    private var menu: Menu? = null
    private val viewModel: CourseDetailVM by viewModel()

    override fun getLayoutRes() = R.layout.fragment_content_course_detail
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

        baseActivity.setToolbar(showToolbar = false)
        menu = binding.toolbar.menu
        menu?.findItem(R.id.action_share)?.setOnMenuItemClickListener {
            baseActivity.shareIntent("Share course data")
            return@setOnMenuItemClickListener true
        }
        initViewPager()
        scrollHandling()
//        binding.tvOldPrice.paintFlags =
//            binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btEnroll.setOnClickListener {
            findNavController().navigate(
                R.id.action_contentCourseDetailFragment_to_addCourseBaseFragment,
                bundleOf(
                    "courseId" to viewModel.courseData.value?.courseId
                )
            )
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
        viewModel.courseData.observe(viewLifecycleOwner) {
            setData(it)
        }
    }

    private fun setData(data: CourseData?) {
        data?.let { courseData ->
            binding.parentCL.visibleView(true)
            binding.ivCourseImage.loadImage(
                courseData.courseBannerUrl,
                R.drawable.ic_home_default_banner, 3
            )
            binding.tvDescription.text = data.courseDescription
//            binding.tvDuration.text= data.courseDuration.toString()
            binding.tvCategory.text = data.categoryName
            binding.tvLanguage.text = data.languageName
            binding.tvCourseName.text = data.courseTitle

            binding.recyclerCoauthor.adapter = CoAuthorListAdapter(data.courseCoAuthors)
//            binding.bottomCL.visibleView(courseData.userCourseStatus.isNullOrZero())
//            binding.progressG.visibleView(courseData.userCourseStatus == 1)
//            binding.ivProfileImage.loadImage(courseData.profileUrl,
//                R.drawable.ic_default_user_grey)
//            binding.ivLogo.loadImage(courseData.courseLogoUrl, R.drawable.ic_logo_default)
//            binding.btEnroll.text = baseActivity.getButtonText(
//                courseData.courseTypeId,
//                courseData.userCourseStatus
//            )

//            binding.btEnroll.setCourseButton(
//                courseData.courseTypeId,
//                courseData.userCourseStatus,
//                courseData.paymentStatus,z
//                1
//            )
            binding.tvSectionCount.text =
                baseActivity.getQuantityString(
                    R.plurals.section_quantity,
                    courseData.totalSections
                )
            binding.tvReviews.text = data.totalReviews.getReviewCount()
            binding.tvLevel.setComplexityLevel(data.courseComplexityId)
            binding.tvDuration.text = courseData.courseDuration.getTime(baseActivity)
//            binding.tvCoin.text = data.rewardPoints + " Points"

            binding.tvNewPrice.text =
                String.format("%s %s", data.currencySymbol, data.courseFee)

            ResizeableUtils.builder(binding.tvDescription).isBold(false)
                .isUnderline(false)
                .setFullText(data.getDescription())
                .setFullText(R.string.read_more_arrow)
                .setLessText(R.string.read_less_arrow)
                .setSpanSize(0.9f)
                .showDots(true)
                .build()

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
        val data = bundleOf(
            "courseId" to viewModel.courseId,
//            "userCourseStatus" to viewModel.courseData.value?.userCourseStatus
        )
        viewModel.courseContentType = true

        val fragOne = CourseInfoFragment()
        fragOne.arguments = data
        val fragTwo = LessonListingFragment()
        fragTwo.arguments = data
        val fragThree = CertificateFragment()
        fragThree.arguments = data
        val fragFragment = ReviewsFragment()
        fragFragment.arguments = data


        val list = arrayListOf<Fragment>(
            CourseInfoFragment(),
            LessonListingFragment(),
//            CertificateFragment(),
            ReviewsFragment(),
            RevenueFragment()
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.info),
            baseActivity.getString(R.string.lessons),
//            baseActivity.getString(R.string.certificate),
            baseActivity.getString(R.string.reviews),
            baseActivity.getString(R.string.revenue_text)

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
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
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

            binding.toolbar.title = viewModel.courseData.value?.courseTitle ?: ""
            binding.toolbar.scrollHandling(-1204, verticalOffset)


        })
    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
//     if (isAdded && isVisible)  ((binding.viewpager.adapter as ScreenSlidePagerAdapter).list[binding.viewpager.currentItem] as BaseFragment<*>).onResponseSuccess(value,apiCode)
        when (apiCode) {
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

        }
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