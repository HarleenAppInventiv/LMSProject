package com.selflearningcoursecreationapp.ui.course_details.ratings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentReviewsBinding
import com.selflearningcoursecreationapp.extensions.getQuantityString
import com.selflearningcoursecreationapp.extensions.isNullOrNegative
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.course_details.ratings.filters.RatingFilterAdapter
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SocketReviewsResponse
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SortingField
import com.selflearningcoursecreationapp.ui.profile.bookmark.WishListViewModel
import com.selflearningcoursecreationapp.ui.search.LikeDislikeSocketManager
import com.selflearningcoursecreationapp.ui.search.MessageListener
import com.selflearningcoursecreationapp.ui.splash.ReviewsRequest
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseStatus
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.concurrent.thread


class ReviewsFragment : BaseFragment<FragmentReviewsBinding>(), BaseBottomSheetDialog.IDialogClick,
    MessageListener, (Int, CourseData, Int) -> Unit, BaseAdapter.IListEnd {
    private val viewModel: CourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private val reviewViewModel: WishListViewModel by viewModel()
    private var model = GetReviewsRequestModel()
    private val sharedHomeModel: HomeVM by sharedViewModel()
    private var reviewsNewAdapter: ReviewNewAdapter? = null


    override fun getLayoutRes() = R.layout.fragment_reviews
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {
        reviewViewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeCourseData()
//        ReviewPagingDataSource.clear()

        arguments?.let {
            viewModel.courseId = it.getInt("courseId")
        }
        model.courseId = viewModel.courseId




        reviewsNewAdapter = ReviewNewAdapter(

            reviewViewModel.reviewList.value ?: ArrayList(),
            baseActivity.tokenFromDataStore(), this
        )
        binding.recyclerRatingList.adapter = reviewsNewAdapter
        reviewsNewAdapter?.setOnPageEndListener(this)


        binding.recyclerRatingList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastVisible() && (reviewViewModel.reviewList.value?.size
                        ?: 8) <= reviewViewModel.currentPage.times(8)
                ) {

                    if (reviewViewModel.currentPage <= reviewViewModel.totalPage) {
                        model.pageNumber = reviewViewModel.currentPage
                        reviewViewModel.getReviewsData(model)

                    }
                }
            }


        })

        callReviewsApi()



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

    private fun callReviewsApi() {
        reset()
        reviewViewModel.getReviewsData(model)

        reviewViewModel.reviewResponse.observe(viewLifecycleOwner) {
            binding.tvTotalView.text = baseActivity.getQuantityString(
                R.plurals.review_quantity,
                reviewViewModel.reviewResponse.value?.totalReviews
            )
            binding.tvAverageRating.text =
                reviewViewModel.reviewResponse.value?.averageReview.toString()

            sharedHomeModel.updateCourseRating(
                viewModel.courseId,
                reviewViewModel.reviewResponse.value?.averageReview.toString(),
                reviewViewModel.reviewResponse.value?.totalCount?.toLong()
            )

            binding.btRateThisCourse.visibility =
                if ((viewModel.courseData.value?.percentageCompleted ?: 0.0) >= 50.00) {
                    if (reviewViewModel.reviewResponse.value?.userAlreadyRated == true) View.GONE else View.VISIBLE
                } else {
                    View.GONE
                }
        }
        reviewViewModel.reviewList.observe(viewLifecycleOwner) {
            setAdapter()
        }

    }


    private fun setAdapter() {
        binding.noDataTV.visibleView(reviewViewModel.reviewList.value.isNullOrEmpty())
        binding.recyclerRatingList.visibleView(!reviewViewModel.reviewList.value.isNullOrEmpty())
//        reviewsNewAdapter?.notifyDataSetChanged()
//        reviewsNewAdapter = null


        if (reviewsNewAdapter == null) {
            reviewsNewAdapter = ReviewNewAdapter(
                reviewViewModel.reviewList.value ?: ArrayList(),
                baseActivity.tokenFromDataStore(), this
            )
            binding.recyclerRatingList.adapter = reviewsNewAdapter
            reviewsNewAdapter?.setOnPageEndListener(this)
        } else {
            reviewsNewAdapter?.setListData(reviewViewModel.reviewList.value ?: ArrayList())
            reviewsNewAdapter?.notifyDataSetChanged()
        }


    }

    private fun reset() {
        reviewViewModel.currentPage = 1
        reviewViewModel.totalPage = 2
        model.pageNumber = 1
        model.pageSize = 8
        reviewViewModel.reviewList.value?.clear()
        reviewsNewAdapter?.notifyDataSetChanged()
        reviewsNewAdapter = null
        binding.recyclerRatingList.adapter = null
    }


    override fun onApiRetry(apiCode: String) {


    }

    fun handleLoading(loading: Boolean) {

        if (loading) {
            showLoading()
        } else {
            hideLoading()


        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()

        if (context != null) {
            thread {
                kotlin.run {
                    LikeDislikeSocketManager.init(
                        "${ApiEndPoints.WEB_SOCKET_ADD_LIKE_DISLIKE}?Token=${baseActivity.tokenFromDataStore()}&LanguageId=${1}&ChannelId=2",
                        this,
                    )
                    LikeDislikeSocketManager.connect()


                }
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
                    binding.ivRed.visibleView(viewModel.selectedFilters.isNotEmpty())

                    callReviewsApi()
                }

                DialogType.RATE_COURSE -> {
                    callReviewsApi()

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
                    if ((viewModel.courseData.value?.percentageCompleted ?: 0.0) >= 50.00) {
                        if (reviewViewModel.reviewResponse.value?.userAlreadyRated == true) View.GONE else View.VISIBLE
                    } else {
                        View.GONE
                    }

            }
            ApiEndPoints.API_REPORT_COMMENT -> {
                (value as BaseResponse<CourseData>).let {
                    callReviewsApi()
                    showToastShort(it.message)
                }
            }

        }
    }


    private fun observeCourseData() {
        viewModel.purchaseCourseLiveData.observe(viewLifecycleOwner) {
            binding.btRateThisCourse.visibility =
                if ((viewModel.courseData.value?.percentageCompleted ?: 0.00) >= 50.00) {
                    if (reviewViewModel.reviewResponse.value?.userAlreadyRated == true) View.GONE else View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    override fun onConnectSuccess() {
        showLog("websocket", "onConnectSuccess")
    }

    override fun onConnectFailed() {
        showLog("websocket", "onConnectFailed")

    }

    override fun onClose() {
        showLog("websocket", "onClose")

    }

    override fun onMessage(text: String?) {

        val data = Gson().fromJson(text, SocketReviewsResponse::class.java)
        Log.d("checkValue", "onMessage: $data")
        Handler(Looper.getMainLooper()).postDelayed({
            reviewViewModel.reviewList.value?.forEachIndexed { index, courseData ->
                if (data.data?.courseId == courseData.courseId && data.data?.reviewId == courseData.reviewId) {
                    courseData.totalLikes =
                        if (data.data?.totalLikes.isNullOrNegative()) 0 else data.data?.totalLikes
                            ?: 0
                    courseData.totalDislikes =
                        if (data.data?.totalDislikes.isNullOrNegative()) 0 else data.data?.totalDislikes
                            ?: 0
                    reviewViewModel.reviewList.value?.set(index, courseData)
                    reviewsNewAdapter?.notifyDataSetChanged()
                }
            }
        }, 2000)
    }


    override fun onPause() {
        super.onPause()
        if (LikeDislikeSocketManager.isConnect())
            LikeDislikeSocketManager.close()


    }

    fun onRefreshData() {
        if (isAdded && isVisible) {
            callReviewsApi()
        }
    }

    override fun invoke(type: Int, courseData: CourseData, position: Int) {

        when (type) {
            -1 -> {
                baseActivity.guestUserPopUp()
            }
            1 -> {
                LikeDislikeSocketManager.sendMessage(
                    Gson().toJson(
                        ReviewsRequest(
                            courseData.courseId ?: 0,
                            courseData.reviewId ?: 0,
                            type
                        )
                    )
                )
                performLikeDislike(type, courseData, position)
            }
            2 -> {

                LikeDislikeSocketManager.sendMessage(
                    Gson().toJson(
                        ReviewsRequest(
                            courseData.courseId ?: 0,
                            courseData.reviewId ?: 0,
                            type
                        )
                    )
                )
                performLikeDislike(type, courseData, position)
            }
            Constant.CLICK_REPORT -> {
                if (baseActivity.tokenFromDataStore().isNotEmpty()) {
                    if (viewModel.courseData.value?.createdById != viewModel.userProfile?.id && viewModel.courseData.value?.userCourseStatus == CourseStatus.NOT_ENROLLED) {
                        showToastShort(baseActivity.getString(R.string.this_feature_is_accessible_after_you_enroll_course))
                    } else {
                        CommonAlertDialog.builder(baseActivity)
                            .title(getString(R.string.alerte))
                            .description(getString(R.string.do_you_really_want_to_report_comment))
                            .positiveBtnText(getString(R.string.yes))
                            .negativeBtnText(getString(R.string.no))
                            .icon(R.drawable.ic_alert)
                            .getCallback {
                                if (it) {
                                    reviewViewModel.reportComment(
                                        courseData.reviewId,
                                        courseData.courseId,
                                    )
                                }
                            }.build()
                    }
                } else {
                    baseActivity.guestUserPopUp()
                }
            }


        }


    }

    private fun performLikeDislike(
        type: Int,
        clickData: CourseData,
        position: Int
    ) {

        val ins = clickData

        if (clickData.reviewId == reviewViewModel.reviewList.value?.get(position)?.reviewId) {

            if (type == 1) {

                if (clickData.userLiked == 0 && clickData.userDisLiked == 0) {

                    ins.totalLikes = ins.totalLikes?.plus(1) ?: 1
                    ins.userDisLiked = 0
                    ins.userLiked = 1

                } else if (clickData.userLiked == 1 && clickData.userDisLiked == 0) {
                    ins.totalLikes =
                        if (ins.totalLikes.isNullOrNegative()) 0 else ins.totalLikes?.minus(1)
                    ins.userDisLiked = 0
                    ins.userLiked = 0
                } else if (clickData.userLiked == 0 && clickData.userDisLiked == 1) {

                    ins.totalLikes = ins.totalLikes?.plus(1)
                    ins.totalDislikes =
                        if (ins.totalDislikes.isNullOrNegative()) 0 else ins.totalDislikes?.minus(1)
                    ins.userDisLiked = 0
                    ins.userLiked = 1
                }

            } else {
                if (clickData.userDisLiked == 0 && clickData.userLiked == 0
                ) {

                    ins.totalDislikes = ins.totalDislikes?.plus(1) ?: 1
                    ins.userDisLiked = 1
                    ins.userLiked = 0
                } else if (clickData.userDisLiked == 1 && clickData.userLiked == 0) {
                    ins.totalDislikes =
                        if (ins.totalDislikes.isNullOrNegative()) 0 else ins.totalDislikes?.minus(1)
                    ins.userDisLiked = 0
                    ins.userLiked = 0

                } else if (clickData.userDisLiked == 0 && clickData.userLiked == 1) {
                    ins.totalLikes =
                        if (ins.totalLikes.isNullOrNegative()) 0 else ins.totalLikes?.minus(1)
                    ins.totalDislikes = ins.totalDislikes?.plus(1) ?: 1
                    ins.userDisLiked = 1
                    ins.userLiked = 0

                }


            }
//        reviewsNewAdapter?.setData(ins, position)
            reviewViewModel.reviewList.value?.set(position, ins)
//            reviewViewModel.reviewList.value?.let {
//                it.forEachIndexed { index, courseData ->
//                    if (index == position) {
//                        courseData.apply {
//
//                            this.userLiked = ins.userLiked
//                            this.notifyPropertyChanged(BR.courseWishlisted)
//                            this.notifyChange()
//
//                        }
//
//                    }
//                }
//            }
        }


        reviewsNewAdapter?.notifyDataSetChanged()
//        reviewList.set(position, ins)


    }


    override fun onPageEnd(vararg items: Any) {
    }

    fun isLastVisible(): Boolean {
        val layoutManager: LinearLayoutManager =
            binding.recyclerRatingList.layoutManager as LinearLayoutManager
        val pos: Int = layoutManager.findLastCompletelyVisibleItemPosition()
        val numItems: Int = binding.recyclerRatingList.adapter?.itemCount ?: 0
        return pos >= numItems - 1
    }


//    private  fun setList(){
//        reviewsNewAdapter = ReviewNewAdapter(
//
//            reviewViewModel.reviewList.value ?: ArrayList(),
//            baseActivity.tokenFromDataStore(), this
//        )
//        binding.recyclerRatingList.adapter = reviewsNewAdapter
//        reviewsNewAdapter?.setOnPageEndListener(this)
//    }
}
