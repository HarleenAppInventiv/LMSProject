package com.selflearningcoursecreationapp.ui.bottom_course

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.FragmentMyCourseBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseScreenType
import org.koin.androidx.viewmodel.ext.android.viewModel


class MyCourseFragment : BaseFragment<FragmentMyCourseBinding>(), BaseAdapter.IViewClick,
    BaseAdapter.IListEnd {
    private val viewModel: MyCourseVM by viewModel()
    private var mAdapter: MyCourseAdapter? = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_course
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
//        setHasOptionsMenu(true)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.course_menu, menu)
//    }

    private fun initUI() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.reset()
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
            viewModel.getCourses(CourseScreenType.ONGOINGCOURSES)
        }

        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.tvNoData.text = getString(R.string.course_list_empty)
        binding.tvNoDataDesc.text = getString(R.string.no_course_data_yet)

//        binding.tvNoData.setOnClickListener {
//            binding.llNoWishlist.gone()
//            binding.rvCourse.visible()
//            binding.rvCourse.adapter = MyCourseAdapter(Constant.COURSE_IN_PROGRESS)
//
//        }
        onGoingDataObserver()
        viewModel.getCourses(CourseScreenType.ONGOINGCOURSES)
    }

    private fun setAdapter() {
        binding.rvCourse.visibleView(!viewModel.courseLiveData.value.isNullOrEmpty())
        binding.llNoWishlist.visibleView(viewModel.courseLiveData.value.isNullOrEmpty())
        if (viewModel.courseLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    MyCourseAdapter(
                        Constant.COURSE_IN_PROGRESS,
                        viewModel.courseLiveData.value ?: ArrayList()
                    )
                binding.rvCourse.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
                mAdapter?.setOnPageEndListener(this)
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        binding.swipeRefresh.isRefreshing = false
        viewModel.onApiRetry(apiCode)
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        super.onException(isNetworkAvailable, exception, apiCode)
        binding.swipeRefresh.isRefreshing = false

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

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                findNavController().navigate(
                    R.id.action_myCourseTabFragment_to_courseDetailsFragment,
                    bundleOf(
                        "courseId" to viewModel.courseLiveData.value?.get(position)?.courseId
                    )
                )
            }
        }

    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getCourses(CourseScreenType.ONGOINGCOURSES)

    }

    private fun onGoingDataObserver() {
        viewModel.courseLiveData.observe(viewLifecycleOwner) {
            setAdapter()

        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ALL_COURSES, ApiEndPoints.API_GUEST_ALL_COURSES -> {
                if (!binding.swipeRefresh.isRefreshing) {
                    super.onResponseSuccess(value, apiCode)

                } else {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
            else -> {
                super.onResponseSuccess(value, apiCode)

            }
        }

    }
}