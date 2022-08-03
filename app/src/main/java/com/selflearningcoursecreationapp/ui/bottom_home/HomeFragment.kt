package com.selflearningcoursecreationapp.ui.bottom_home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
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
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.AllCoursesAdapter
import com.selflearningcoursecreationapp.ui.dialog.unlockCourse.UnlockCourseDialog
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>(), HandleClick, BaseAdapter.IViewClick,
    BaseDialog.IDialogClick {
    val gson: Gson? = null
    private val viewModel: HomeVM by viewModel()
    var list = ArrayList<ArrayList<CategoryData>>()
    private var mAdapter: HomeAdapter? = null
    private var mOtherAdapter: AllCoursesAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeData()
        observeCategoriesData()
        observeWishlist()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity.supportActionBar?.hide()

    }

    override fun onStart() {
        super.onStart()
        baseActivity.supportActionBar?.hide()
    }

    fun initUI() {
        binding.viewModel = viewModel
        observeOtherCourseData()
        binding.swipeRefresh.setOnRefreshListener {
            binding.tvOther.gone()
            viewModel.reset()
            reset()
            viewModel.getCourses()
            viewModel.getOtherCourses()
        }
        viewModel.getCourses()
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
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.homefrag = this
        list.clear()


        viewModel.homeCategories()


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

    private fun pagination() {
        binding.nestedScroll.getViewTreeObserver()
            .addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
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
                        findNavController().navigate(R.id.action_homeFragment_to_profileThumbFragment)
                    }

                }
                R.id.tv_user_name -> {
                    if (baseActivity.tokenFromDataStore() == "") {
                        baseActivity.guestUserPopUp()
                    } else {
                        findNavController().navigate(R.id.action_homeFragment_to_profileThumbFragment)
                    }

                }
                R.id.tv_see_all -> {
                    findNavController().navigate(R.id.action_homeFragment_to_homeCategoriesFragment)

                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

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

        viewModel.reset()
        reset()
        viewModel.getCourses()
        viewModel.getOtherCourses()
    }


    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int

            when (type) {
                Constant.CLICK_VIEW -> {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_categoryWiseCoursesFragment, bundleOf(
                            "id" to viewModel.categories.value?.get(position)?.id,
                            "name" to viewModel.categories.value?.get(position)?.name
                        )
                    )


                }
                Constant.CLICK_SEE_ALL -> {
                    findNavController().navigate(
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
                        findNavController().navigate(
                            R.id.action_homeFragment_to_courseDetailsFragment,
                            bundleOf(
                                "courseId" to viewModel.courseLiveData.value?.get(position)?.courses?.get(
                                    childPos
                                )?.courseId
                            )
                        )
                    } else {
                        viewModel.fromOther = true

                        findNavController().navigate(
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

                            findNavController().navigate(
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
                mAdapter = HomeAdapter(viewModel.courseLiveData.value ?: ArrayList())
                binding.rvList.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
//                mAdapter!!.setOnPageEndListener(this)
            }
        }
    }

    private fun observeCategoriesData() {
        viewModel.categories.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
//                it.sortBy { it.name }
                binding.recyclerCourseType.adapter = AdapterCourseType(it)
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
                if (it.success.equals("true")) {

//                   mAdapter?.notifyItemChanged(viewModel.adapterPosition)
//                    viewModel.courseLiveData.value?.let {mainList->
//                        mainList.get(viewModel.adapterPosition).
//                    }
//                    binding.rvList.adapter?.notifyDataSetChanged()
//                    binding.rvOther.adapter?.notifyDataSetChanged()
                    mAdapter?.notifyDataSetChanged()
                    mOtherAdapter?.notifyDataSetChanged()
//                viewModel.courseLiveData.value
//                binding.rvList.adapter = HomeAdapter(it)
//                (binding.rvList.adapter as? HomeAdapter)?.setOnAdapterItemClickListener(this)
                }
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
                    findNavController().navigate(
                        R.id.action_homeFragment_to_courseDetailsFragment,
                        bundleOf("courseId" to it.resource?.course?.courseId)
                    )

                }

            }

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
                    AllCoursesAdapter(viewModel.otherCourseLiveData.value as ArrayList<CourseData> /* = java.util.ArrayList<com.selflearningcoursecreationapp.models.course.CourseData> */)
                binding.rvOther.adapter = mOtherAdapter
                mOtherAdapter?.setOnAdapterItemClickListener(this)
            }
        }
    }


    override fun onDialogClick(vararg items: Any) {
        val type = items[0] as Int
        val courseId = items[1] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_courseDetailsFragment,
                    bundleOf("courseId" to courseId)
                )
            }
        }
    }
}
