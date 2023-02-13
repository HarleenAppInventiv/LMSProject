package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

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
import com.selflearningcoursecreationapp.databinding.FragmentRejectedModBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ModeratorDashboard
import org.koin.androidx.viewmodel.ext.android.viewModel

class RejectedModFragment() : BaseFragment<FragmentRejectedModBinding>(), BaseAdapter.IViewClick,
    BaseAdapter.IListEnd {

    private val viewModel: ModDashboardVM by viewModel()
    private val parentVM: ModDashboardVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private var mAdapter: ModeratorDashAdapter? = null


    override fun getLayoutRes() = R.layout.fragment_rejected_mod

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onResume() {
        super.onResume()
        binding.getRoot().requestLayout();
    }

    private fun initUI() {
        observeChanges()

        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.moderatorStatus = ModeratorDashboard.REJECTED_FRAGMENT

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
            viewModel.moderatorStatus = ModeratorDashboard.REJECTED_FRAGMENT
            viewModel.getCourses()
        })
    }


    private fun setCoursesObserver() {
        viewModel.courseLiveData.observe(viewLifecycleOwner) {
            setAdapter()

        }
    }

    private fun setAdapter() {
        binding.rvRejectedList.visibleView(!viewModel.courseLiveData.value.isNullOrEmpty())
        binding.llNoWishlist.visibleView(viewModel.courseLiveData.value.isNullOrEmpty())
        if (viewModel.courseLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    ModeratorDashAdapter(
                        Constant.ACCEPTED_COURSES,
                        viewModel.courseLiveData.value ?: ArrayList()
                    )
                binding.rvRejectedList.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
                mAdapter?.setOnPageEndListener(this)
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                findNavController().navigateTo(
                    R.id.action_global_modCourseDetailsFragment,
                    bundleOf(
                        "courseId" to viewModel.courseLiveData.value?.get(position)?.courseId
                    )
                )
            }
        }
    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getCourses()
    }


}
