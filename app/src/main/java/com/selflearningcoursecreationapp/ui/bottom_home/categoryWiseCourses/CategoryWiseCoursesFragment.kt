package com.selflearningcoursecreationapp.ui.bottom_home.categoryWiseCourses

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.*
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.FragmentCategoryWiseCoursesBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_home.HomeAdapter
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.AllCoursesAdapter
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.FilterBottomDialog
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockCourseDialog
import com.selflearningcoursecreationapp.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CategoryWiseCoursesFragment : BaseFragment<FragmentCategoryWiseCoursesBinding>(),
    BaseAdapter.IViewClick, BaseBottomSheetDialog.IDialogClick, BaseAdapter.IListEnd,
    BaseDialog.IDialogClick {
    private val viewModel: HomeVM by viewModel()
    private var mAdapter: HomeAdapter? = null
    private var mOtherAdapter: AllCoursesAdapter? = null

    override fun getLayoutRes() = R.layout.fragment_category_wise_courses
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.viewModel = viewModel
        reset()
        arguments?.let {
            if (!it.getInt("id").isNullOrZero()) {

                viewModel.selectedCategory = ArrayList<CategoryData>().apply {
                    add(CategoryData(id = it.getInt("id")))
                }
            }
            viewModel.screenTitle = it.getString("name").toString()

            Log.d("varun", "initUI: ${it.getString("id").toString()}")

        }

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty() && viewModel.isTextSearch) {
                viewModel.isTextSearch = false
                viewModel.reset()
                reset()
                viewModel.getCourses()
                viewModel.getOtherCourses()
            }
        }

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.isTextSearch = true
                viewModel.reset()
                reset()
                viewModel.getCourses()
                viewModel.getOtherCourses()
            }

            return@setOnEditorActionListener true
        }

        binding.filter.setOnClickListener {
            FilterBottomDialog().apply {
                arguments = bundleOf("selectedFilters" to viewModel.selectedFilters)
                setOnDialogClickListener(this@CategoryWiseCoursesFragment)
            }.show(childFragmentManager, "")
        }
        observeData()
        speechToTextHandling()
        observeWishlist()
        observeOtherCourseData()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.reset()
            reset()
            viewModel.getCourses()
            viewModel.getOtherCourses()
        }

        pagination()

    }

    private fun pagination() {
        binding.nestedScroll.getViewTreeObserver()
            .addOnScrollChangedListener(OnScrollChangedListener {
                val view =
                    binding.nestedScroll.getChildAt(binding.nestedScroll.getChildCount() - 1) as View
                val diff: Int =
                    view.bottom - (binding.nestedScroll.getHeight() + binding.nestedScroll
                        .getScrollY())
                if (diff == 0) {
                    viewModel.getOtherCourses()
                    // your pagination code
                }
            })
    }

    override fun onApiRetry(apiCode: String) {

    }

    private fun reset() {
        binding.otherG.gone()
        mAdapter?.notifyDataSetChanged()
        mAdapter = null

        mOtherAdapter?.notifyDataSetChanged()
        mOtherAdapter = null


    }

    override fun onResume() {
        super.onResume()
        viewModel.getCourses()
        viewModel.getOtherCourses()
        baseActivity.setToolbar(viewModel.screenTitle)
    }

    private fun speechToTextHandling() {
        spokenTextLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { value ->
                binding.etSearch.setText(value)
//                viewModel.reset()
                reset()
                viewModel.reset()
                viewModel.getCourses()
                viewModel.getOtherCourses()
            }


        }

        binding.ivMic.setOnClickListener {
            displaySpeechRecognizer(this)


        }
    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int

        when (type) {
            Constant.CLICK_DETAILS -> {
                if (items.size > 2) {
                    viewModel.fromOther = false
                    val childPos = items[2] as Int
                    findNavController().navigate(
                        R.id.action_categoryWiseCoursesFragment_to_courseDetailsFragment,
                        bundleOf(
                            "courseId" to viewModel.courseLiveData.value?.get(position)?.courses?.get(
                                childPos
                            )?.courseId
                        )
                    )
                } else {
                    viewModel.fromOther = true

                    findNavController().navigate(
                        R.id.action_categoryWiseCoursesFragment_to_courseDetailsFragment,
                        bundleOf(
                            "courseId" to viewModel.otherCourseLiveData.value?.get(position)?.courseId
                        )
                    )
                }
            }
//            Constant.CLICK_BOOKMARK -> {
//                if (baseActivity.tokenFromDataStore().isEmpty()) {
//                    baseActivity.guestUserPopUp()
//                } else {
//                    viewModel.adapterPosition = position
//                    viewModel.addWishlist()
//                }
//            }
            Constant.CLICK_SEE_ALL -> {
                val subtitle =
                    if (viewModel.screenTitle.equals(baseActivity.getString(R.string.all_courses))) "" else viewModel.screenTitle

                findNavController().navigate(
                    R.id.action_categoryWiseCoursesFragment_to_popularFragment,
                    bundleOf(
                        "title" to viewModel.courseLiveData.value?.get(position)?.title,
                        "subTitle" to subtitle,
                        "selectedList" to viewModel.selectedCategory,
                        "courseType" to viewModel.courseLiveData.value?.get(position)?.coursesType,
                        "searchData" to viewModel.searchLiveData.value,
                        "filterData" to viewModel.selectedFilters

                    )
                )
            }
            Constant.CLICK_BOOKMARK -> {
                if (baseActivity.tokenFromDataStore() == "") {
                    baseActivity.guestUserPopUp()
                } else {
                    viewModel.adapterPosition = position
                    if (items.size > 2) {
                        viewModel.fromOther = false

                        val innerPosition = items[2] as Int
                        viewModel.childPosition = innerPosition
                    } else {
                        viewModel.fromOther = true

                    }

                    viewModel.addWishlist()

//                    viewModel.courseLiveData.value!![position].courses!![innerPosition].courseWishlisted =
//                        if (viewModel.courseLiveData.value!![position].courses!![innerPosition].courseWishlisted == 0) 1 else 0

                }

            }
            Constant.CLICK_BUYBUTTON -> {
                if (baseActivity.tokenFromDataStore() == "") {
                    baseActivity.guestUserPopUp()
                } else {
                    viewModel.adapterPosition = position
                    val course = if (items.size > 2) {
                        val innerPosition = items[2] as Int
                        viewModel.childPosition = innerPosition
                        viewModel.fromOther = false
                        viewModel.courseLiveData.value?.get(position)?.courses?.get(innerPosition)
                    } else {
                        viewModel.fromOther = true
                        viewModel.otherCourseLiveData.value?.get(position)

                    }
                    if (course?.userCourseStatus == 1) {

                        findNavController().navigate(
                            R.id.action_categoryWiseCoursesFragment_to_popularFragment,
                            bundleOf(
                                "courseId" to course.courseId
                            )
                        )
                    } else {
                        buyCourseCheck(course?.courseTypeId, course?.courseId, course?.rewardPoints)
                    }
                }

            }

        }
    }

    private fun buyCourseCheck(courseType: Int?, courseId: Int?, rewardPoints: String?) {
        when (courseType) {
            CourseType.FREE -> {
                viewModel.purchaseCourse()
            }
            CourseType.PAID -> {
                viewModel.buyRazorPayCourse()
            }
            CourseType.RESTRICTED -> {
                UnlockCourseDialog().apply {
                    arguments = bundleOf("courseId" to courseId, "courseType" to courseType)
                    setOnDialogClickListener(this@CategoryWiseCoursesFragment)
                }.show(childFragmentManager, "")
            }
            CourseType.REWARD_POINTS -> {
                val desc = String.format(
                    baseActivity.getString(R.string.to_buy_this_course),
                    rewardPoints
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
                showToastShort("Course type not added in course")
            }
        }
    }
//    private fun observeData() {
//        viewModel.courseLiveData.observe(viewLifecycleOwner) {
//            if (!it.isNullOrEmpty())
//                binding.rvList.adapter = HomeAdapter(it)
//            (binding.rvList.adapter as? HomeAdapter)?.setOnAdapterItemClickListener(this)
//        }
//    }

    private fun observeData() {
        viewModel.courseLiveData.observe(viewLifecycleOwner, Observer {
            setAdapter()
        })
    }

    private fun observeWishlist() {
        viewModel.wishlistLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it.success.equals("true")) {
                    mOtherAdapter?.notifyDataSetChanged()
                    mAdapter?.notifyDataSetChanged()
//                viewModel.courseLiveData.value
//                binding.rvList.adapter = HomeAdapter(it)
//                (binding.rvList.adapter as? HomeAdapter)?.setOnAdapterItemClickListener(this)
                }
            }

        }
    }

    private fun setAdapter() {
        binding.rvList.visibleView(!viewModel.courseLiveData.value?.filter { !it.courses.isNullOrEmpty() }
            .isNullOrEmpty())
        binding.noDataTV.visibleView(!viewModel.isFirst && viewModel.otherCourseLiveData.value.isNullOrEmpty() && viewModel.courseLiveData.value?.filter { !it.courses.isNullOrEmpty() }
            .isNullOrEmpty())
        if (viewModel.courseLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter = HomeAdapter(viewModel.courseLiveData.value ?: ArrayList())
                binding.rvList.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
//                mAdapter!!.setOnPageEndListener(this)
            }
        }
    }

    override fun onLoading(message: String, apiCode: String?) {
        if (!binding.swipeRefresh.isRefreshing) {
            super.onLoading(message, apiCode)

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
            ApiEndPoints.API_PURCHASE_COURSE -> {
                val resource = (value as BaseResponse<OrderData>)
                showToastShort(resource.message)
                findNavController().navigate(
                    R.id.action_categoryWiseCoursesFragment_to_courseDetailsFragment,
                    bundleOf("courseId" to resource.resource?.course?.courseId)
                )
            }
            ApiEndPoints.API_RAZORPAY_COURSE -> {
                baseActivity.startRazorpayPayment((value as BaseResponse<OrderData>).resource)
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
                    binding.ivRed.visibility =
                        if (viewModel.selectedFilters.isEmpty()) View.GONE else View.VISIBLE
                    viewModel.reset()
                    reset()
                    viewModel.getCourses()
                    viewModel.getOtherCourses()
                }
                Constant.CLICK_VIEW -> {
//                    val otp = items[1] as String
//                    viewModel.otp = otp
//                    viewModel.purchaseCourse()
                    val courseId = items[1] as Int

                    findNavController().navigate(
                        R.id.action_popularFragment_to_courseDetailsFragment,
                        bundleOf("courseId" to courseId)
                    )
                }
            }
        }
    }


    override fun onPageEnd(vararg items: Any) {
//        viewModel.getOtherCourses()
    }

    private fun observeOtherCourseData() {
        viewModel.otherCourseLiveData.observe(viewLifecycleOwner, Observer {
            setOtherAdapter()
        })
    }

    private fun setOtherAdapter() {
        binding.otherG.visibleView(!viewModel.otherCourseLiveData.value.isNullOrEmpty())
        binding.noDataTV.visibleView(!viewModel.isFirst && viewModel.otherCourseLiveData.value.isNullOrEmpty() && viewModel.courseLiveData.value?.filter { !it.courses.isNullOrEmpty() }
            .isNullOrEmpty())
        if (viewModel.otherCourseLiveData.value.isNullOrEmpty()) {
            mOtherAdapter?.notifyDataSetChanged()
            mOtherAdapter = null
        } else {
            mOtherAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mOtherAdapter =
                    AllCoursesAdapter(viewModel.otherCourseLiveData.value ?: ArrayList())
                binding.rvOther.adapter = mOtherAdapter
                mOtherAdapter?.setOnAdapterItemClickListener(this)
                mOtherAdapter?.setOnPageEndListener(this)
            }
        }
    }


}