package com.selflearningcoursecreationapp.ui.course_details.authorDetail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.*
import com.selflearningcoursecreationapp.databinding.FragmentAuthorCoursesBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.course_details.AuthorDetailsVM
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockCourseDialog
import com.selflearningcoursecreationapp.ui.payment.CheckoutBottomSheet
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class AuthorCoursesLIstFragment() : BaseFragment<FragmentAuthorCoursesBinding>(),
    BaseAdapter.IViewClick,
    BaseAdapter.IListEnd, BaseDialog.IDialogClick, BaseBottomSheetDialog.IDialogClick {
    private val viewModel: AuthorDetailsVM by viewModel()
    private var mAdapter: AuthorCoursesAdapter? = null

    override fun getLayoutRes() = R.layout.fragment_author_courses
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onResume() {
        super.onResume()
        binding.getRoot().requestLayout();
    }

    private fun initUI() {
        arguments?.let {
            viewModel.authorUserId = it.getInt("authorUserId")
        }
        viewModel.getUserData()
        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setAuthorDetailObserver()
        viewModel.reset()
        viewModel.getAuthorDetails()

    }

    private fun setAuthorDetailObserver() {

        viewModel.authorProfileCoursesLiveData.observe(viewLifecycleOwner) {
            setAdapter()
        }

//        viewModel.authorDetailLiveData.observe(viewLifecycleOwner) {
//            setAdapter()
//        }

        viewModel.wishlistLiveData.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let {
                mAdapter?.notifyDataSetChanged()

            }
        }

        viewModel.purchaseCourseLiveData.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { orderDate ->
                viewModel.authorProfileCoursesLiveData.value?.let {

                    it.forEach { data ->
                        data.apply {
                            if (data.courseId == orderDate.course?.courseId) {
                                data.userCourseStatus = 1
                            }
                        }
                    }
                }
                mAdapter?.notifyDataSetChanged()
            }

        })
    }


    private fun setAdapter() {
        binding.rvAuthorCourses.visibleView(!viewModel.authorProfileCoursesLiveData.value.isNullOrEmpty())
        binding.noDataTV.visibleView(viewModel.authorProfileCoursesLiveData.value.isNullOrEmpty())


        if (viewModel.authorProfileCoursesLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    AuthorCoursesAdapter(
                        viewModel.authorProfileCoursesLiveData.value ?: ArrayList(),
                        viewModel.userProfile?.id ?: 0
                    )
                binding.rvAuthorCourses.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
                mAdapter?.setOnPageEndListener(this)
            }
        }
    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getAuthorDetails()
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


    override fun onItemClick(vararg items: Any) {

        val type = items[0] as Int
        val position = items[1] as Int
        viewModel.adapterPosition = position
        when (type) {
            Constant.CLICK_VIEW -> {
                if (viewModel?.authorProfileCoursesLiveData?.value?.get(position)?.createdById == viewModel.userProfile?.id) {
                    findNavController().navigateTo(
                        R.id.action_global_contentCourseDetailFragment,
                        bundleOf(
                            "courseId" to viewModel.authorProfileCoursesLiveData.value?.get(position)?.courseId
                        )
                    )
                } else {
                    findNavController().navigateTo(
                        R.id.action_global_courseDetailsFragment,
                        bundleOf(
                            "courseId" to viewModel.authorProfileCoursesLiveData.value?.get(position)?.courseId
                        )
                    )
                }

            }
            Constant.CLICK_BOOKMARK -> {
                if (baseActivity.tokenFromDataStore() == "") {
                    baseActivity.guestUserPopUp()
                } else {
                    viewModel.adapterPosition = position
                    viewModel.addWishlist()
                    mAdapter?.notifyDataSetChanged()
                }

            }

            Constant.CLICK_BUYBUTTON -> {
                var wishListItem = viewModel.authorProfileCoursesLiveData.value?.get(position)
                if (wishListItem?.userCourseStatus == CourseStatus.ENROLLED || wishListItem?.userCourseStatus == CourseStatus.IN_PROGRESS
                    || wishListItem?.userCourseStatus == CourseStatus.COMPELETD
                ) {
                    findNavController().navigateTo(
                        R.id.action_global_courseDetailsFragment,
                        bundleOf("courseId" to wishListItem.courseId)
                    )
                } else {
                    when (wishListItem?.courseTypeId) {
                        CourseType.FREE -> {
                            viewModel.purchaseCourse()
                        }
                        CourseType.PAID -> {
                            CheckoutBottomSheet().apply {
                                arguments = bundleOf(
                                    "courseFee" to wishListItem?.courseFee.toString(),
                                )
                                setOnDialogClickListener(this@AuthorCoursesLIstFragment)

                            }.show(childFragmentManager, "")
                        }
                        CourseType.RESTRICTED -> {

                            UnlockCourseDialog().apply {
                                arguments = bundleOf(
                                    "courseId" to wishListItem.courseId,
                                    "courseType" to wishListItem.courseTypeId
                                )
                                setOnDialogClickListener(this@AuthorCoursesLIstFragment)
                            }.show(childFragmentManager, "")
                        }
                        CourseType.REWARD_POINTS -> {
                            val desc = String.format(
                                baseActivity.getString(R.string.to_buy_this_course),
                                wishListItem.rewardPoints
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


    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)


        when (apiCode) {
            ApiEndPoints.API_RAZORPAY_COURSE -> {
                baseActivity.startRazorpayPayment((value as BaseResponse<OrderData>).resource)
            }
            ApiEndPoints.API_PURCHASE_COURSE -> {

                CommonAlertDialog.builder(baseActivity)
                    .title(getString(R.string.congrats))
                    .description(getString(R.string.you_have_succesully_enroled_inthis))
                    .icon(R.drawable.ic_checked_logo)
                    .hideNegativeBtn(true)
                    .positiveBtnText(getString(R.string.okay))
                    .getCallback {
                        if (it) {
                            (value as BaseResponse<OrderData>).let { orderData ->
                                showToastShort((orderData.message))
                                viewModel.authorProfileCoursesLiveData.value?.let {

                                    it.forEach { data ->
                                        data.apply {
                                            if (data.courseId == orderData.resource?.course?.courseId) {
                                                data.userCourseStatus = 1
                                            }
                                        }
                                    }
                                }
                                mAdapter?.notifyDataSetChanged()
                                findNavController().navigateTo(
                                    R.id.action_global_courseDetailsFragment,
                                    bundleOf("courseId" to orderData.resource?.course?.courseId)
                                )

                            }
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
                val courseId = items[1] as Int
                viewModel.authorProfileCoursesLiveData.value?.let {

                    it.forEach { data ->
                        data.apply {
                            if (data.courseId == courseId) {
                                data.userCourseStatus = 1
                            }
                        }
                    }
                }
                mAdapter?.notifyDataSetChanged()
                findNavController().navigateTo(
                    R.id.action_global_courseDetailsFragment,
                    bundleOf("courseId" to courseId)
                )
            }
        }
    }
}