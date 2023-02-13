package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.*
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentPopularBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.AllCoursesAdapter
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.FilterBottomDialog
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockCourseDialog
import com.selflearningcoursecreationapp.ui.payment.CheckoutBottomSheet
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AllCoursesFragment : BaseFragment<FragmentPopularBinding>(), BaseAdapter.IViewClick,
    BaseBottomSheetDialog.IDialogClick, BaseAdapter.IListEnd, BaseDialog.IDialogClick {
    private var mAdapter: AllCoursesAdapter? = null
    private val viewModel: AllCoursesVM by viewModel()
    private val sharedHomeModel: HomeVM by sharedViewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()
    }


    private fun initUI() {
        arguments?.let {
            viewModel.searchLiveData.value = it.getString("searchData")
            if (it.containsKey("filterData")) {
                viewModel.selectedFilters =
                    it.getParcelableArrayList<SelectedFilterData>("filterData") as ArrayList<SelectedFilterData?>

            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.reset()
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
            viewModel.getCourses()
        }


        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        binding.viewModel = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        binding.imgCross.setOnClickListener {
            viewModel.searchLiveData.value = ""
            binding.etSearch.text?.clear()
            binding.imgCross.gone()
            binding.ivMic.visible()

        }

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isEmpty() && viewModel.isTextSearch) {
                viewModel.isTextSearch = false
                binding.imgCross.gone()
                binding.ivMic.visible()
                viewModel.reset()
                mAdapter?.notifyDataSetChanged()
                mAdapter = null
                viewModel.getCourses()
            } else if (text.toString().isEmpty()) {
                binding.imgCross.gone()
                binding.ivMic.visible()
            } else {
                binding.imgCross.visible()
                binding.ivMic.gone()
            }

        }

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.isTextSearch = true
                viewModel.reset()
                mAdapter?.notifyDataSetChanged()
                mAdapter = null
                viewModel.getCourses()
            }

            return@setOnEditorActionListener true
        }



        arguments?.let {
            if (it.containsKey("selectedList")) {
                viewModel.selectedCategory.clear()
                viewModel.selectedCategory.addAll(
                    it.getParcelableArrayList<CategoryData>("selectedList") ?: ArrayList()
                )

            }
            if (it.containsKey("courseType")) {
                viewModel.courseType = it.getInt("courseType")
                binding.filter.gone()
            } else {
                binding.filter.visible()

            }


            if (it.containsKey("fromSearch")) {
                binding.etSearch.requestFocus()
                binding.etSearch.showKeyBoard()
                if (it.containsKey("searchText")) {
                    viewModel.searchLiveData.value = it.getString("searchText")
                    viewModel.getCourses()
                }
            } else {
                viewModel.getCourses()
            }
        }

        binding.filter.setOnClickListener {
            FilterBottomDialog().apply {
                arguments = bundleOf("selectedFilters" to viewModel.selectedFilters)
                setOnDialogClickListener(this@AllCoursesFragment)
            }.show(childFragmentManager, "")
        }
        observeData()
        observeWishlist()
        speechToTextHandling()

    }

    private fun speechToTextHandling() {
        spokenTextLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { value ->
                viewModel.isTextSearch = true
                binding.etSearch.setText(value)
                viewModel.reset()
                mAdapter?.notifyDataSetChanged()
                mAdapter = null
                viewModel.getCourses()
            }


        }

        binding.ivMic.setOnClickListener {
            displaySpeechRecognizer(this)


        }
    }

    private fun observeData() {
        viewModel.courseLiveData.observe(viewLifecycleOwner, Observer {
            setAdapter()
        })
    }

    private fun setAdapter() {
        binding.recyclerCoursesView.visibleView(!viewModel.courseLiveData.value.isNullOrEmpty())
        binding.noDataTV.visibleView(!viewModel.isFirst && viewModel.courseLiveData.value.isNullOrEmpty())
        if (viewModel.courseLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter = AllCoursesAdapter(
                    viewModel.courseLiveData.value ?: ArrayList(),
                    baseActivity.isViOn()
                )
                binding.recyclerCoursesView.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
                mAdapter?.setOnPageEndListener(this)
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_popular
    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int

        when (type) {
            Constant.CLICK_DETAILS -> {
                findNavController().navigateTo(
                    R.id.action_popularFragment_to_courseDetailsFragment,
                    bundleOf("courseId" to viewModel.courseLiveData.value?.get(position)?.courseId)
                )
            }
            Constant.CLICK_BOOKMARK -> {
                if (baseActivity.tokenFromDataStore().isEmpty()) {
                    baseActivity.guestUserPopUp()
                } else {
                    viewModel.adapterPosition = position
                    viewModel.addWishlist()
                }
            }
            Constant.CLICK_BUYBUTTON -> {
                if (baseActivity.tokenFromDataStore().isEmpty()) {
                    baseActivity.guestUserPopUp()
                } else {
//                val innerPosition = items[2] as Int
                    viewModel.adapterPosition = position
//                viewModel.childPosition = innerPosition

                    viewModel.courseLiveData.value?.get(position).let {
                        if (it?.userCourseStatus == CourseStatus.ENROLLED || it?.userCourseStatus == CourseStatus.IN_PROGRESS ||
                            it?.userCourseStatus == CourseStatus.COMPELETD
                        ) {
                            findNavController().navigateTo(
                                R.id.action_popularFragment_to_courseDetailsFragment,
                                bundleOf("courseId" to viewModel.courseLiveData.value?.get(position)?.courseId)
                            )

                        } else {
                            when (it?.courseTypeId) {
                                CourseType.FREE -> {
                                    viewModel.purchaseCourse()
                                }
                                CourseType.PAID -> {

                                    CheckoutBottomSheet().apply {
                                        arguments = bundleOf(
                                            "courseFee" to viewModel.courseLiveData.value?.get(
                                                position
                                            )?.courseFee,
                                        )
                                        setOnDialogClickListener(this@AllCoursesFragment)

                                    }.show(childFragmentManager, "")

                                }
                                CourseType.RESTRICTED -> {
                                    UnlockCourseDialog().apply {
                                        arguments = bundleOf(
                                            "courseId" to it.courseId,
                                            "courseType" to it.courseTypeId
                                        )
                                        setOnDialogClickListener(this@AllCoursesFragment)
                                    }.show(childFragmentManager, "")
                                }
                                CourseType.REWARD_POINTS -> {
                                    val desc = String.format(
                                        baseActivity.getString(R.string.to_buy_this_course),
                                        viewModel.courseLiveData.value?.get(position)?.rewardPoints
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
                                    showToastShort(getString(R.string.course_type_not_added))

                                }
                            }

                        }
                    }
                }
            }

        }
    }

    override fun onApiRetry(apiCode: String) {
        binding.swipeRefresh.isRefreshing = false
        viewModel.onApiRetry(apiCode)
    }

    override fun onLoading(message: String, apiCode: String?) {
        when (apiCode) {
            ApiEndPoints.API_ALL_COURSES, ApiEndPoints.API_GUEST_ALL_COURSES -> {
                if (!binding.swipeRefresh.isRefreshing) {
                    super.onLoading(message, apiCode)

                }
            }
            ApiEndPoints.API_WISHLIST -> {

            }
            else -> {
                super.onLoading(message, apiCode)

            }
        }
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        binding.swipeRefresh.isRefreshing = false
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
                            viewModel.reset()
                            mAdapter?.notifyDataSetChanged()
                            mAdapter = null
                            viewModel.getCourses()
                        }
                    }.build()
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)
            }
        }

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        binding.swipeRefresh.isRefreshing = false
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
                            val resource = (value as BaseResponse<OrderData>)
                            sharedHomeModel.updateCourse(resource.resource?.course?.courseId)
                            findNavController().navigateTo(
                                R.id.action_popularFragment_to_courseDetailsFragment,
                                bundleOf("courseId" to resource.resource?.course?.courseId)
                            )
                        }
                    }
                    .build()


            }
            ApiEndPoints.API_HOME_WISHLIST -> {
                sharedHomeModel.setWishlist((value as Pair<Int?, Boolean>?))

            }

        }

    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                DialogType.PAYMENT -> {
                    viewModel.stateId = items[1] as String
                    viewModel.buyRazorPayCourse()

                }
                DialogType.HOME_FILTER -> {
                    viewModel.selectedFilters.clear()
                    viewModel.selectedFilters.addAll(items[1] as ArrayList<SelectedFilterData?>)
                    viewModel.reset()
                    viewModel.getCourses()
                }
                Constant.CLICK_VIEW -> {
                    val courseId = items[1] as Int

//                    val otp = items[1] as String
//                    viewModel.otp = otp
//                    viewModel.purchaseCourse()
                    findNavController().navigateTo(
                        R.id.action_popularFragment_to_courseDetailsFragment,
                        bundleOf("courseId" to courseId)
                    )
                }

            }
        }
    }


    override fun onPageEnd(vararg items: Any) {
        viewModel.getCourses()
    }

    private fun observeWishlist() {
        viewModel.wishlistLiveData.observe(viewLifecycleOwner) {
            if (it.success.equals("true")) {
                mAdapter?.notifyDataSetChanged()
//                viewModel.courseLiveData.value
//                binding.rvList.adapter = HomeAdapter(it)
//                (binding.rvList.adapter as? HomeAdapter)?.setOnAdapterItemClickListener(this)
            }

        }

    }


}