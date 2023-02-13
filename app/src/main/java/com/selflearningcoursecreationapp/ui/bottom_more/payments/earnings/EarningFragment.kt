package com.selflearningcoursecreationapp.ui.bottom_more.payments.earnings

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEarningBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.bottom_more.payments.PaymentsVM
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.RevenueType
import org.koin.androidx.viewmodel.ext.android.viewModel

class EarningFragment : BaseFragment<FragmentEarningBinding>(), BaseAdapter.IViewClick,
    BaseAdapter.IListEnd {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_earning
    }

    private val viewModel: PaymentsVM by viewModel()
    private var mAdapter: EarningAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        callMenu()
    }

    override fun onResume() {
        super.onResume()
        binding.getRoot().requestLayout();
    }

    private fun initUi() {
        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setEarningsObserver()

        viewModel.userEarningsData()
    }

    private fun setEarningsObserver() {
        viewModel.userEarningsLiveData.observe(viewLifecycleOwner) {
            setAdapter()
        }
    }

    private fun setAdapter() {
        binding.rvList.visibleView(!viewModel.userEarningsLiveData.value.isNullOrEmpty())
        binding.noDataTV.visibleView(viewModel.userEarningsLiveData.value.isNullOrEmpty())


        if (viewModel.userEarningsLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    EarningAdapter(
                        viewModel.userEarningsLiveData.value ?: ArrayList()
                    )
                binding.rvList.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
                mAdapter?.setOnPageEndListener(this)
            }
        }
    }


    override fun onApiRetry(apiCode: String) {

    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                var revenueType = 1
                when (viewModel.userEarningsLiveData.value?.get(position)?.courseTypeId) {
                    CourseType.REWARD_POINTS -> {
                        revenueType = RevenueType.REWARD_POINTS

                    }
                    CourseType.PAID -> {
                        revenueType = RevenueType.PURCHASED
                    }

                }
                findNavController().navigateTo(
                    R.id.action_global_revenueFragment,
                    bundleOf(
                        "courseId" to viewModel.userEarningsLiveData.value?.get(position)?.courseId,
                        "RevenueType" to revenueType
                    )
                )
            }
        }
    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.userEarningsData()
    }


}