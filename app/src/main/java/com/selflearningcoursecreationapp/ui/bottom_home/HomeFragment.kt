package com.selflearningcoursecreationapp.ui.bottom_home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.FragmentHomeBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.AllCoursesAdapter
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockCourseDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import com.selflearningcoursecreationapp.utils.recyclerView.ScrollStateHolder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>(), HandleClick, BaseAdapter.IViewClick,
    BaseDialog.IDialogClick {
    val gson: Gson? = null
    private val viewModel: HomeVM by sharedViewModel()
    private var mAdapter: HomeAdapterTest? = null
    private var mOtherAdapter: AllCoursesAdapter? = null
    private lateinit var scrollStateHolder: ScrollStateHolder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollStateHolder = ScrollStateHolder(savedInstanceState)
        initUI()

        if (viewModel.isFirst) {
            viewModel.categories.value?.clear()
            binding.recyclerCourseType.adapter?.notifyDataSetChanged()
            binding.recyclerCourseType.adapter = null
//            viewModel.homeCategories()

            callApi()
        } else {
            reset()
            binding.recyclerCourseType.adapter?.notifyDataSetChanged()
            binding.recyclerCourseType.adapter = null
        }

        observeData()
        observeCategoriesData()
        observeWishlist()
    }


    fun initUI() {

        setUserData()
        binding.viewModel = viewModel
        observeOtherCourseData()
        binding.swipeRefresh.setOnRefreshListener {
            callApi()
        }

        binding.tvSeeAll.backgroundTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.purple_700
                )
            )


        spokenTextLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { value ->

                binding.etSearch.setText(value)
//                viewModel.reset()
                reset()
                viewModel.reset()
                viewModel.getCourses()
                viewModel.getOtherCourses()
//
//                findNavController().navigate(
//                    R.id.action_homeFragment_to_popularFragment,
//                    bundleOf("fromSearch" to true, "searchText" to value)
//                )

            }
        }
//
//        binding.etSearch.setOnTouchListener { view, motionEvent ->
//            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
//                findNavController().navigate(
//                    R.id.action_homeFragment_to_popularFragment,
//                    bundleOf("fromSearch" to true, "searchText" to "")
//                )
//            }
//            return@setOnTouchListener true
//        }

        binding.etSearch.setOnClickListener {
            findNavController().navigateTo(R.id.action_homeFragment_to_fragment_search)
        }
//        binding.etSearch.doOnTextChanged { text, start, before, count ->
//            if (text.toString().isEmpty() && viewModel.isTextSearch) {
//                viewModel.isTextSearch = false
//                viewModel.reset()
//                reset()
//                viewModel.getCourses()
//                viewModel.getOtherCourses()
//            }
//        }

//        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
//            if (i == EditorInfo.IME_ACTION_SEARCH) {
//                viewModel.isTextSearch = true
//                viewModel.reset()
//                reset()
//                viewModel.getCourses()
//                viewModel.getOtherCourses()
//            }
//
//            return@setOnEditorActionListener true
//        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.homefrag = this


        val color = ThemeUtils.getAppColor(baseActivity)
        binding.toolbarLayout.setContentScrimColor(color)
        binding.toolbarLayout.setBackgroundColor(color)
        binding.toolbarLayout.setStatusBarScrimColor(color)
        binding.appBar.setBackgroundColor(color)

        binding.ivNotification.setOnClickListener {
//            InviteCoAuthorDialog().show(childFragmentManager, "")
//            downloadDataToInternalStorage()
//            throw RuntimeException("Test Crash"); // Force a crash

        }

        binding.imgMic.setOnClickListener {
            displaySpeechRecognizer(this)
        }
        pagination()


    }

    private fun callApi() {
        binding.tvOther.gone()
        viewModel.reset()
        reset()
        viewModel.callCombinedApis()

//        viewModel.getCourses()
//        viewModel.getOtherCourses()
    }

    private fun pagination() {
        binding.nestedScroll.viewTreeObserver
            .addOnScrollChangedListener {
                val view =
                    binding.nestedScroll.getChildAt(binding.nestedScroll.childCount - 1) as View
                val diff: Int =
                    view.bottom - (binding.nestedScroll.height + binding.nestedScroll
                        .scrollY)
                if (diff == 0) {
                    viewModel.getOtherCourses()
                    // your pagination code
                }
            }
    }

    private fun reset() {
        binding.otherG.gone()
        mAdapter?.notifyDataSetChanged()
        mAdapter = null

        mOtherAdapter?.notifyDataSetChanged()
        mOtherAdapter = null


    }


//    private fun downloadDataToInternalStorage() {
//        writeData()
//    }

//    private fun writeData() {
//        try {
//            val fos: FileOutputStream =
//                getAppContext().openFileOutput("demoFile.txt", Context.MODE_PRIVATE)
//            val data: String = "Demo File"
//            fos.write(data.toByteArray())
//            fos.flush()
//            fos.close()
//            Log.e("data", "writing to file " + "demoFile.txt" + "completed...")
//
//            readData()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//
//    }

//    private fun readData() {
//        try {
//            val fin: FileInputStream = getAppContext().openFileInput("demoFile.txt")
//            var a: Int
//            val temp = StringBuilder()
//            while (fin.read().also { a = it } != -1) {
//                temp.append(a.toChar())
//            }
//
//            // setting text from the file.
//            Log.e("data reading", "" + temp.toString())
//            fin.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        Log.e("data", "reading to file " + "demoFile.txt".toString() + " completed..")
//    }


    override fun getLayoutRes() = R.layout.fragment_home


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.iv_user_image -> {
                    if (baseActivity.tokenFromDataStore() == "") {
                        baseActivity.guestUserPopUp()
                    } else {
                        findNavController().navigateTo(R.id.action_homeFragment_to_profileThumbFragment)
                    }

                }
                R.id.tv_user_name -> {
                    if (baseActivity.tokenFromDataStore() == "") {
                        baseActivity.guestUserPopUp()
                    } else {
                        findNavController().navigateTo(R.id.action_homeFragment_to_profileThumbFragment)
                    }

                }
                R.id.tv_see_all -> {
                    findNavController().navigateTo(R.id.action_homeFragment_to_homeCategoriesFragment)

                }
            }

        }
    }

    private fun setUserData() {
        viewModel.getUserData()

        binding.tvUserName.apply {

            val value = viewModel.userProfile?.name ?: "Guest"
            if (value.length > 12) {
                val str = value.substring(0, 12)
                text = "${str}..."
            } else {
                text = value
            }

        }
        Glide.with(requireActivity()).load(viewModel.userProfile?.profileUrl)
            .placeholder(R.drawable.ic_default_user_grey)
            .into(binding.ivUserImage)
    }


    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int

            when (type) {
                Constant.CLICK_VIEW -> {
                    findNavController().navigateTo(
                        R.id.action_homeFragment_to_categoryWiseCoursesFragment, bundleOf(
                            "id" to viewModel.categories.value?.get(position)?.id,
                            "name" to viewModel.categories.value?.get(position)?.name
                        )
                    )


                }
                Constant.CLICK_SEE_ALL -> {
                    findNavController().navigateTo(
                        R.id.action_homeFragment_to_popularFragment,
                        bundleOf(
                            "title" to viewModel.courseLiveData.value?.get(position)?.title,
                            "courseType" to viewModel.courseLiveData.value?.get(position)?.coursesType,
                            "searchData" to viewModel.searchLiveData.value,
                            "filterData" to viewModel.selectedFilters
                        )
                    )
                }
                Constant.CLICK_DETAILS -> {

                    if (items.size > 2) {
                        viewModel.fromOther = false
                        val childPos = items[2] as Int
                        findNavController().navigateTo(
                            R.id.action_homeFragment_to_courseDetailsFragment,
                            bundleOf(
                                "courseId" to viewModel.courseLiveData.value?.get(position)?.courses?.get(
                                    childPos
                                )?.courseId
                            )
                        )
                    } else {
                        viewModel.fromOther = true

                        findNavController().navigateTo(
                            R.id.action_homeFragment_to_courseDetailsFragment,
                            bundleOf(
                                "courseId" to viewModel.otherCourseLiveData.value?.get(position)?.courseId
                            )
                        )
                    }

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
                            viewModel.courseLiveData.value?.get(position)?.courses?.get(
                                innerPosition
                            )
                        } else {
                            viewModel.fromOther = true
                            viewModel.otherCourseLiveData.value?.get(position)

                        }
                        if (course?.userCourseStatus == 1) {

                            findNavController().navigateTo(
                                R.id.action_homeFragment_to_courseDetailsFragment,
                                bundleOf(
                                    "courseId" to course.courseId
                                )
                            )
                        } else {
                            buyCourseCheck(
                                course?.courseTypeId,
                                course?.courseId,
                                course?.rewardPoints
                            )
                        }


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
                    setOnDialogClickListener(this@HomeFragment)
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

    override fun onApiRetry(apiCode: String) {
        binding.swipeRefresh.isRefreshing = false
        viewModel.onApiRetry(apiCode)
    }

    private fun observeData() {
        viewModel.courseLiveData.observe(viewLifecycleOwner) {
            setAdapter()
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
                mAdapter = HomeAdapterTest(
                    viewModel.courseLiveData.value ?: ArrayList(),
                    scrollStateHolder
                )
                binding.rvList.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
//                mAdapter!!.setOnPageEndListener(this)
            }
        }
    }

    private fun observeCategoriesData() {
        binding.tvSeeAll.backgroundTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    if (baseActivity.isViOn()) R.color.ViSecondaryColor else R.color.purple_700
                )
            )
        viewModel.categories.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
//                it.sortBy { it.name }
                binding.recyclerCourseType.adapter = AdapterCourseType(it, baseActivity.isViOn())
                (binding.recyclerCourseType.adapter as? AdapterCourseType)?.setOnAdapterItemClickListener(
                    this
                )

                binding.tvSeeAll.visible()

            }
        }
    }


    private fun observeWishlist() {
        viewModel.wishlistLiveData.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let {
//                if (it.success.equals("true")) {

//mAdapter?.callNotifyChange(viewModel.adapterPosition)
                    mAdapter?.notifyDataSetChanged()
                    mOtherAdapter?.notifyDataSetChanged()
//                }
            }
        }
    }

    override fun onLoading(message: String, apiCode: String?) {
        when (apiCode) {


            ApiEndPoints.API_HOME_COURSES, ApiEndPoints.API_HOME_GUESTCOURSES, ApiEndPoints.API_ALL_COURSES, ApiEndPoints.API_GUEST_ALL_COURSES -> {
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
                (value as BaseResponse<OrderData>).let {
                    showToastShort((it.message))
                    viewModel.updateCourse(it.resource?.course?.courseId)
                    findNavController().navigateTo(
                        R.id.action_homeFragment_to_courseDetailsFragment,
                        bundleOf("courseId" to it.resource?.course?.courseId)
                    )

                }

            }
            ApiEndPoints.API_HOME_WISHLIST -> {
                viewModel.setWishlist((value as Pair<Int?, Boolean>?))
                mAdapter?.notifyDataSetChanged()
                mOtherAdapter?.notifyDataSetChanged()
            }
//            ApiEndPoints.API_HOME_WISHLIST + "/false" -> {
//                viewModel.setWishlist((value as Pair<Int?, Boolean>?))
//                mAdapter?.notifyDataSetChanged()
//                mOtherAdapter?.notifyDataSetChanged()
//            }
        }
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
            binding.tvOther.gone()
        } else {
            binding.tvOther.visible()
            mOtherAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mOtherAdapter =
                    AllCoursesAdapter(
                        viewModel.otherCourseLiveData.value as ArrayList<CourseData>,
                        baseActivity.isViOn()
                    )
                binding.rvOther.adapter = mOtherAdapter
                mOtherAdapter?.setOnAdapterItemClickListener(this)
            }
        }
    }


    override fun onDialogClick(vararg items: Any) {
        val type = items[0] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                val courseId = items[1] as Int
                findNavController().navigateTo(
                    R.id.action_homeFragment_to_courseDetailsFragment,
                    bundleOf("courseId" to courseId)
                )
            }
        }
    }
}
