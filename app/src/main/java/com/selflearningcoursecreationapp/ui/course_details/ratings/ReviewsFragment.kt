package com.selflearningcoursecreationapp.ui.course_details.ratings

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentReviewsBinding
import com.selflearningcoursecreationapp.extensions.getQuantityString
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.course_details.ratings.filters.RatingFilterAdapter
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SortingField
import com.selflearningcoursecreationapp.ui.profile.bookmark.WishListViewModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.SignalR
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReviewsFragment : BaseFragment<FragmentReviewsBinding>(), BaseBottomSheetDialog.IDialogClick {
    private val viewModel: CourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private val reviewViewModel: WishListViewModel by viewModel()
    private var model = GetReviewsRequestModel()

    private val reviewListAdapter by lazy {
        ReviewsListAdapter(reviewViewModel) { type, wishListItem, _ ->
            when (type) {
                0 -> {
//                    if (baseActivity.tokenFromDataStore().isNullOrEmpty())
//                    {
//                        baseActivity.guestUserPopUp()
//                    }else{
                    SignalR.addLikeDisLike(
                        SignalR.HUB_ADD_LIKE,
                        wishListItem.courseId ?: 0,
                        wishListItem.reviewId ?: 0
                    )
//                }
                }

                1 -> {
//                    if (baseActivity.tokenFromDataStore().isNullOrEmpty())
//                    {
//                        baseActivity.guestUserPopUp()
//                    }else{
                    SignalR.addLikeDisLike(
                        SignalR.HUB_ADD_DISLIKE,
                        wishListItem.courseId ?: 0,
                        wishListItem.reviewId ?: 0
                    )
//                }
                }
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_reviews
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        reviewViewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeCourseData()
        ReviewPagingDataSource.clear()


//
//        SignalR.turnOnTheSignalR()
//        SignalR.listenDataForAddLike {
//
//            val data = CourseData()
//            data.courseId = it.courseId
//            data.courseRating = it.courseRating
//            data.contentDescription = it.description
//            data.totalLikes = it.totalLikes
//            data.totalDislikes = it.userDisLiked
//            data.createdDate = it.createdDate
//            data.userLiked = it.userLiked
//            data.userDisLiked = it.userDisLiked
//            data.reviewId = it.reviewId
//            reviewViewModel.onViewEvent(PagerViewEvents.EditListenData(data))
//
//
//        }

        arguments?.let {
            viewModel.courseId = it.getInt("courseId")
        }
        model.courseId = viewModel.courseId



        viewLifecycleOwner.lifecycleScope.launch {
            reviewViewModel.getReviewList(model).observe(viewLifecycleOwner) {
                reviewListAdapter.submitData(lifecycle, it)


            }
        }



        binding.recyclerRatingList.adapter = reviewListAdapter


        reviewListAdapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && reviewListAdapter.itemCount == 0
            binding.noDataTV.isVisible = isListEmpty

            // Only show the list if refresh succeeds.

            // Show loading spinner during initial load or refresh.
            handleLoading(loadState.source.refresh is LoadState.Loading)

            // Show the retry state if initial load or refresh fails.
            // binding.btnRetry.isVisible = loadState.source.refresh is LoadState.Error

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

            }
        }


        binding.btRateThisCourse.setOnClickListener {
            if (baseActivity.tokenFromDataStore() == "") {
                baseActivity.guestUserPopUp()
            } else {
                val rate = RateCourseDialog()
                rate.arguments = bundleOf(
                    "courseId" to viewModel.courseId,
                    "courseData" to viewModel.courseData.value
                )
                rate.show(childFragmentManager, "")
                rate.setOnDialogClickListener(this)
            }
        }

        binding.filter.setOnClickListener {
            RatingFilterAdapter().apply {
                arguments = bundleOf(
                    "selectedFilters" to viewModel.selectedFilters,
                    "courseId" to viewModel.courseId
                )
                setOnDialogClickListener(this@ReviewsFragment)
            }.show(childFragmentManager, "")
        }


    }

    override fun onApiRetry(apiCode: String) {

    }

    fun handleLoading(loading: Boolean) {

        if (loading) {
            showLoading()
        } else {
            hideLoading()
            binding.tvTotalView.text = baseActivity.getQuantityString(
                R.plurals.review_quantity,
                ReviewPagingDataSource.count
            )
            binding.tvAverageRating.text = ReviewPagingDataSource.rating.toString()
            binding.btRateThisCourse.visibility =
                if (viewModel.courseData.value?.userCourseStatus == 1) {
                    if (ReviewPagingDataSource.userAlreadyRated) View.GONE else View.VISIBLE
                } else {
                    View.GONE
                }


        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                DialogType.HOME_FILTER -> {
                    viewModel.selectedFilters.clear()
                    viewModel.selectedFilters.addAll(items[1] as ArrayList<SelectedFilterData?>)
                    model.searchFields = ArrayList()
                    viewModel.selectedFilters.forEach {
                        if (it?.filterType == 1) {
                            model.searchFields?.add(
                                SearchFieldsItem(
                                    it.filterName,
                                    it.filterOptionOperatorId,
                                    ArrayList<String?>().apply {
                                        add(it.filterOptionValue)
                                    })
                            )

                        } else {

                            model.sortingField = SortingField(it?.filterName, it?.filterOptionValue)


                        }


                    }

//                    viewLifecycleOwner.lifecycleScope.launch {
//                        reviewViewModel.getReviewList(model).observe(viewLifecycleOwner) {
//                            reviewListAdapter.submitData(lifecycle, it)
//
//
//                        }
//                    }
                    reviewListAdapter.refresh()
                }

                DialogType.RATE_COURSE -> {
                    reviewListAdapter.refresh()

                }
            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.GUEST_LOGIN -> {
                baseActivity.guestUserPopUp()
            }
            ApiEndPoints.API_PURCHASE_COURSE -> {
                binding.btRateThisCourse.visibility =
                    if (viewModel.courseData.value?.userCourseStatus == 1) {
                        if (ReviewPagingDataSource.userAlreadyRated) View.GONE else View.VISIBLE
                    } else {
                        View.GONE
                    }

            }
        }
    }


    private fun observeCourseData() {
        viewModel.purchaseCourseLiveData.observe(viewLifecycleOwner) {
            binding.btRateThisCourse.visibility =
                if (viewModel.courseData.value?.userCourseStatus == 1) {
                    if (ReviewPagingDataSource.userAlreadyRated) View.GONE else View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }
}