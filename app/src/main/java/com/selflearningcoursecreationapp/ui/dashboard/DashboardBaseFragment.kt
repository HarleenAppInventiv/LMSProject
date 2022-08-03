package com.selflearningcoursecreationapp.ui.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDashboardBaseBinding
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter

class DashboardBaseFragment : BaseFragment<FragmentDashboardBaseBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initUI()
    }

    private fun initUI() {

        val screenList = ArrayList<Fragment>()
        screenList.add(DashModeratorFragment())
        screenList.add(DashCreatorFragment())

        binding.vpDashboards.apply {

            adapter =
                ScreenSlidePagerAdapter(
                    childFragmentManager,
                    screenList,
                    this@DashboardBaseFragment.lifecycle
                )
        }

        binding.svChangeDash.setOnClickListener {
            if (binding.svChangeDash.isChecked) {
//                binding.tvSwitchText.text="Switch to Learner Dashboard"
                binding.vpDashboards.setCurrentItem(
                    binding.vpDashboards.currentItem + 1,
                    true
                )
            } else {
//                binding.tvSwitchText.text="Switch to Creator Dashboard"
                binding.vpDashboards.setCurrentItem(
                    binding.vpDashboards.currentItem - 1,
                    true
                )
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_read -> {
//                baseActivity.checkAccessibilityService()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun getLayoutRes() = R.layout.fragment_dashboard_base
    override fun onApiRetry(apiCode: String) {

    }

}