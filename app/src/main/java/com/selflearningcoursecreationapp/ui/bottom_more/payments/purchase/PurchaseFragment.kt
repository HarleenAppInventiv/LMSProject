package com.selflearningcoursecreationapp.ui.bottom_more.payments.purchase

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEarningBinding

class PurchaseFragment : BaseFragment<FragmentEarningBinding>(), BaseAdapter.IViewClick {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_earning
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    private fun initUi() {
        binding.rvList.adapter = PurchaseAdapter().apply {
            setOnAdapterItemClickListener(this@PurchaseFragment)
        }
    }

    override fun onItemClick(vararg items: Any) {
        findNavController().navigate(R.id.action_paymentsFragment_to_paymentDetailsFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    override fun onApiRetry(apiCode: String) {

    }


}