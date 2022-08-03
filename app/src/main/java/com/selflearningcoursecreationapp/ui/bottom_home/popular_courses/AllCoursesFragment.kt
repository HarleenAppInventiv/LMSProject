package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.*
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.FragmentPopularBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.showKeyBoard
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.AllCoursesAdapter
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.FilterBottomDialog
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockCourseDialog
import com.selflearningcoursecreationapp.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class AllCoursesFragment : BaseFragment<FragmentPopularBinding>(), BaseAdapter.IViewClick,
    BaseBottomSheetDialog.IDialogClick, BaseAdapter.IListEnd, BaseDialog.IDialogClick {
    private var mAdapter: AllCoursesAdapter? = null
    private val viewModel: AllCoursesVM by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    private fun initUI() {
        arguments?.let {
            viewModel.searchLiveData.value = it?.getString("searchData")
            if (it.containsKey("filterData")) {
                viewModel.selectedFilters =
                    it?.getParcelableArrayList<SelectedFilterData>("filterData") as ArrayList<SelectedFilterData?>

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

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty() && viewModel.isTextSearch) {
                viewModel.isTextSearch = false

                viewModel.reset()
                mAdapter?.notifyDataSetChanged()
                mAdapter = null
                viewModel.getCourses()
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
                mAdapter = AllCoursesAdapter(viewModel.courseLiveData.value ?: ArrayList())
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
                findNavController().navigate(
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
                        if (it?.userCourseStatus == 1) {
                            findNavController().navigate(
                                R.id.action_popularFragment_to_courseDetailsFragment,
                                bundleOf("courseId" to viewModel.courseLiveData.value?.get(position)?.courseId)
                            )

                        } else {
                            when (it?.courseTypeId) {
                                CourseType.FREE -> {
                                    viewModel.purchaseCourse()
                                }
                                CourseType.PAID -> {
                                    viewModel.buyRazorPayCourse()
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
                                    CommonAlertDialog.builder(requireContext())
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
                                    showToastShort("course type not added")

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
            else -> {
                super.onLoading(message, apiCode)

            }
        }
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        super.onException(isNetworkAvailable, exception, apiCode)
        binding.swipeRefresh.isRefreshing = false

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        binding.swipeRefresh.isRefreshing = false
        when (apiCode) {

            ApiEndPoints.API_RAZORPAY_COURSE -> {
                baseActivity.startRazorpayPayment((value as BaseResponse<OrderData>).resource)
            }
            ApiEndPoints.API_PURCHASE_COURSE -> {
                val resource = (value as BaseResponse<OrderData>)
                showToastShort(resource.message)
                findNavController().navigate(
                    R.id.action_popularFragment_to_courseDetailsFragment,
                    bundleOf("courseId" to resource.resource?.course?.courseId)
                )
            }

        }

    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val courseId = items[1] as Int
            when (type) {
                DialogType.HOME_FILTER -> {
                    viewModel.selectedFilters.clear()
                    viewModel.selectedFilters.addAll(items[1] as ArrayList<SelectedFilterData?>)
                    viewModel.reset()
                    viewModel.getCourses()
                }
                Constant.CLICK_VIEW -> {
//                    val otp = items[1] as String
//                    viewModel.otp = otp
//                    viewModel.purchaseCourse()
                    findNavController().navigate(
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