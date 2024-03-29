package com.selflearningcoursecreationapp.ui.profile.bookmark

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentMyCourseBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockCourseDialog
import com.selflearningcoursecreationapp.ui.payment.CheckoutBottomSheet
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class WishlistCoursesFragment : BaseFragment<FragmentMyCourseBinding>(), BaseDialog.IDialogClick,
    BaseBottomSheetDialog.IDialogClick {

    private val viewModel: WishListViewModel by viewModel()
    private val homeViewModel: HomeVM by viewModel()
    private val sharedHomeModel: HomeVM by sharedViewModel()
    private lateinit var pagingList: PagingData<CourseData>


    private var itemposition = 0
    lateinit var itemWishlisted: CourseData
    private val wishListListAdapter by lazy {
        WishListAdapter { type, wishListItem, position ->
            itemposition = position
            itemWishlisted = wishListItem

            when (type) {
                Constant.CLICK_BOOKMARK -> {
                    viewModel.onViewEvent(PagerViewEvents.Remove(wishListItem))
                    homeViewModel.courseId = wishListItem.courseId ?: 0
                    homeViewModel.unmarkWishlist()
                }
                Constant.CLICK_VIEW -> {
                    findNavController().navigateTo(
                        R.id.action_bookmarkedCoursesFragment_to_courseDetailsFragment,
                        bundleOf("courseId" to wishListItem.courseId)
                    )
                }
                Constant.CLICK_BUYBUTTON -> {
                    if (wishListItem.userCourseStatus == CourseStatus.ENROLLED || wishListItem.userCourseStatus == CourseStatus.IN_PROGRESS
                        || wishListItem.userCourseStatus == CourseStatus.COMPELETD
                    ) {
                        findNavController().navigateTo(
                            R.id.action_bookmarkedCoursesFragment_to_courseDetailsFragment,
                            bundleOf("courseId" to wishListItem.courseId)
                        )
                    } else {
                        when (wishListItem.courseTypeId) {
                            CourseType.FREE -> {
                                viewModel.purchaseCourse(wishListItem)
                            }
                            CourseType.PAID -> {
                                CheckoutBottomSheet().apply {
                                    arguments = bundleOf(
                                        "courseFee" to wishListItem.courseFee,
                                    )
                                    setOnDialogClickListener(this@WishlistCoursesFragment)

                                }.show(childFragmentManager, "")

//                                viewModel.buyRazorPayCourse(wishListItem)
                            }
                            CourseType.RESTRICTED -> {
//                                UnlockCourseDialog().show(childFragmentManager, "")
                                UnlockCourseDialog().apply {
                                    arguments = bundleOf(
                                        "courseId" to wishListItem.courseId,
                                        "courseType" to wishListItem.courseTypeId
                                    )
                                    setOnDialogClickListener(this@WishlistCoursesFragment)
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
                                            viewModel.purchaseCourse(wishListItem)
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
//            var courseId: Int,
//    var reviewId: Int,
//    var userId: Int,
//    var courseRating: Int,
//    var description: Int,
//    var totalLikes: Int,
//    var totalDislikes: Int,
//    var userLiked: Int,
//    var name: String,
//    var createdDate: String,


        }


    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_course
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        callMenu()
    }


    private fun initUi() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getWishList().observe(viewLifecycleOwner) {
                wishListListAdapter.submitData(lifecycle, it)
                binding.swipeRefresh.isRefreshing = false
                pagingList = it

            }
        }


        binding.tvNoData.text = getString(R.string.wishlist_empty)
        binding.tvNoDataDesc.text =
            getString(R.string.you_do_not_have_any_courses_in_your_wishlist_yet_add_your_favourite_courses_to_the_list)
        binding.llNoWishlist.gone()
        binding.rvCourse.visible()

        binding.tvNoData.setOnClickListener {
            wishListListAdapter.retry()

        }


        binding.rvCourse.adapter = wishListListAdapter
        binding.swipeRefresh.setOnRefreshListener {

            wishListListAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
        wishListListAdapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && wishListListAdapter.itemCount == 0
            binding.llNoWishlist.isVisible = isListEmpty

            // Only show the list if refresh succeeds.
            binding.rvCourse.isVisible = loadState.source.refresh is LoadState.NotLoading

            // Show loading spinner during initial load or refresh.
            handleLoading(loadState.source.refresh is LoadState.Loading)

            // Show the retry state if initial load or refresh fails.
//            binding.btnRetry.isVisible = loadState.source.refresh is LoadState.Error

            /**
             * loadState.refresh - represents the load state for loading the PagingData for the first time.
             * loadState.prepend - represents the load state for loading data at the start of the list.
             * loadState.append - represents the load state for loading data at the end of the list.
             * */
            // If we have an error, show a toast
            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            errorState?.let {
                binding.llNoWishlist.visible()
                binding.swipeRefresh.isRefreshing = false
                handlePagingError(it, ApiEndPoints.API_HOME_WISHLIST)
            }
        }

        observeWishlist()
        observePurchaseCourseData()
    }

    private fun observeWishlist() {
        homeViewModel.wishlistLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
//                if (it.success.equals("true")) {
//                wishListListAdapter.refresh()
                if (wishListListAdapter.itemCount == 0) {
                    binding.llNoWishlist.visible()
                    binding.swipeRefresh.isRefreshing = false

                }
                sharedHomeModel.setWishlist(it)
//                }
            }
        }
    }

    private fun observePurchaseCourseData() {
        viewModel.purchaseCourseLiveData.observe(viewLifecycleOwner) {

        }
    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_HOME_WISHLIST -> {
                wishListListAdapter.refresh()
            }
            else -> {
                viewModel.onApiRetry(apiCode = apiCode)
                homeViewModel.onApiRetry(apiCode = apiCode)

            }
        }


    }

    private fun handleLoading(loading: Boolean) {

        if (loading) {
            showLoading()
        } else {
            hideLoading()


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
                    .notCancellable(false)
                    .positiveBtnText(getString(R.string.okay))
                    .getCallback {
                        if (it) {
                            val resource = (value as BaseResponse<OrderData>)
                            sharedHomeModel.updateCourse(resource.resource?.course?.courseId)
                            findNavController().navigateTo(
                                R.id.action_bookmarkedCoursesFragment_to_courseDetailsFragment,
                                bundleOf("courseId" to resource.resource?.course?.courseId)
                            )
                        }
                    }
                    .build()


            }

        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                DialogType.PAYMENT -> {
                    viewModel.stateId = items[1] as String
                    viewModel.buyRazorPayCourse(itemWishlisted)

                }

                Constant.CLICK_VIEW -> {
//                    val otp = items[1] as String
//                    viewModel.otp = otp
//                    viewModel.purchaseCourse()
                    val courseId = items[1] as Int

                    findNavController().navigateTo(
                        R.id.action_bookmarkedCoursesFragment_to_courseDetailsFragment,
                        bundleOf("courseId" to courseId)
                    )
                }

            }

        }
    }
}