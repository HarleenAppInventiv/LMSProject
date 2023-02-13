package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRevenueBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.RevenueType
import com.selflearningcoursecreationapp.utils.convertPaiseToRs
import org.koin.androidx.viewmodel.ext.android.viewModel


class RevenueFragment() : BaseFragment<FragmentRevenueBinding>(),
    BaseAdapter.IViewClick, BaseAdapter.IListEnd {
    override fun getLayoutRes() = R.layout.fragment_revenue
    private val viewModel: RevenueFragmentVM by viewModel()
    private var mAdapter: RevenueListAdapter? = null
    var courseId = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        initUI()
    }


    override fun onResume() {
        super.onResume()
        binding.getRoot().requestLayout();
    }

    private fun initUI() {
        arguments?.let {
            courseId = it.getInt("courseId")
            viewModel.revenueType = it.getInt("RevenueType")
        }
        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeRevenueData()
        viewModel.getRevenueData(courseId)


    }

    private fun observeRevenueData() {

        viewModel.revenueLiveData.observe(viewLifecycleOwner) {
            if (viewModel.revenueType == RevenueType.PURCHASED) {

                if (it.revenueList.isNullOrEmpty())
                    binding.tvRevenue.text = "₹ " + it.courseRevenue.toString()
                else binding.tvRevenue.text =
                    "${it.revenueList?.get(0)?.currencySymbol.toString()} ${it.courseRevenue?.convertPaiseToRs()}"
            } else if (viewModel.revenueType == RevenueType.REWARD_POINTS) {
                binding.tvRevenue.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_coin_yellow,
                    0,
                    0,
                    0
                )
                binding.tvRevenue.text = " ${it.courseRevenue?.toInt().toString()}"


            }
        }
        viewModel.revenueLiveList.observe(viewLifecycleOwner) {
            setAdapter()
        }
    }

    private fun setAdapter() {
        binding.recyerRevenueList.visibleView(!viewModel.revenueLiveList.value.isNullOrEmpty())
        binding.noDataTV.visibleView(viewModel.revenueLiveList.value.isNullOrEmpty())


        if (viewModel.revenueLiveList.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    RevenueListAdapter(
                        viewModel.revenueLiveList.value ?: ArrayList(), viewModel.revenueType

                    )
                binding.recyerRevenueList.adapter = mAdapter
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
            Constant.CLICK_INVOICE -> {

            }
        }
    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getRevenueData(courseId)
    }


}