package com.selflearningcoursecreationapp.ui.dashboard

import LearnerDashboardDataList
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentTodoBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseStatus
import com.selflearningcoursecreationapp.utils.ModeratorDashboard
import org.koin.androidx.viewmodel.ext.android.viewModel


class TodoFragment() : BaseFragment<FragmentTodoBinding>(), BaseAdapter.IViewClick,
    BaseAdapter.IListEnd {
    private val viewModel: LearnerDashboardVM by viewModel()
    private val parentVM: LearnerDashboardVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private var mAdapter: LearnerDashAdapter? = null
    var isFirstTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    override fun onResume() {
        super.onResume()
        binding.getRoot().requestLayout();
        isFirstTime = true

    }

    private fun initUI() {
        observeChanges()

        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.courseType = ModeratorDashboard.TODO_FRAGMENT

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setCoursesObserver()

//        viewModel.getCourses()
    }

    private fun observeChanges() {
        parentVM.refreshData.observe(viewLifecycleOwner, Observer {
            viewModel.minDate = parentVM.minDate
            viewModel.maxDate = parentVM.maxDate
            viewModel.filterType = parentVM.filterType
            viewModel.selectedDay = parentVM.selectedDay
            viewModel.reset()
            viewModel.courseType = ModeratorDashboard.TODO_FRAGMENT
            viewModel.getCourses()


        })
    }

    private fun setCoursesObserver() {
        viewModel.courseLiveData.observe(viewLifecycleOwner) {
            setAdapter()

        }
    }


    override fun getLayoutRes() = R.layout.fragment_todo
    private fun setAdapter() {
        binding.recyclerCourses.visibleView(!viewModel.courseLiveData.value.isNullOrEmpty())
        binding.llNoWishlist.visibleView(viewModel.courseLiveData.value.isNullOrEmpty())


        if (viewModel.courseLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            var list = viewModel.courseLiveData.value?.distinctBy { it.courseId }
            mAdapter =
                LearnerDashAdapter(
                    (viewModel.courseLiveData.value
                        ?: ArrayList()) as ArrayList<LearnerDashboardDataList>
                )
            binding.recyclerCourses.adapter = mAdapter
            mAdapter?.setOnAdapterItemClickListener(this)
            mAdapter?.setOnPageEndListener(this)
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_BUYBUTTON, Constant.CLICK_VIEW -> {
                var item = viewModel.courseLiveData.value?.get(position)
                if (item?.userCourseStatus == CourseStatus.ENROLLED) {
                    findNavController().navigateTo(
                        R.id.action_dashboardBaseFragment_to_courseDetailsFragment,
                        bundleOf("courseId" to item.courseId)
                    )
                }
            }
        }
    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getCourses()
    }


}