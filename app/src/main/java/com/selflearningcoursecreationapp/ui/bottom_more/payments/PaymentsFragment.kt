package com.selflearningcoursecreationapp.ui.bottom_more.payments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPaymentsBinding
import com.selflearningcoursecreationapp.ui.bottom_more.payments.purchase.PurchaseFragment
import com.selflearningcoursecreationapp.ui.bottom_more.payments.earnings.EarningFragment
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class PaymentsFragment : BaseFragment<FragmentPaymentsBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_payments
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {

        binding.btHistory.setOnClickListener {
            findNavController().navigate(R.id.action_paymentsFragment_to_walletFragment)
        }
        val list = ArrayList<Fragment>()

          list.add(  EarningFragment())
        list.add(    PurchaseFragment())


        val nameArray= arrayListOf("My Earning","My Purchase")
        binding.viewpager.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)

        TabLayoutMediator(binding.tlHeader, binding.viewpager) { tab, position ->
            tab.text = nameArray[position]


        }.attach()
        binding.tlHeader.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))

        binding.tlHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT_BOLD
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })
    }

}