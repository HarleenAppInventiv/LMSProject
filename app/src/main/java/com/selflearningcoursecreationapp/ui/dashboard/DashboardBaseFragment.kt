package com.selflearningcoursecreationapp.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDashboardBaseBinding
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter


class DashboardBaseFragment : BaseFragment<FragmentDashboardBaseBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callMenu()
        initUI()
    }

    private fun initUI() {

        val screenList = ArrayList<Fragment>()
        screenList.add(DashLearnerFragment())
        screenList.add(DashCreatorFragment())

        binding.vpDashboards.isUserInputEnabled = false
//
//        binding.vpDashboards.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
//
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//                if(position==0){
//                    binding.svChangeDash.isChecked= false
//                }
//                else if(position==1){
//                    binding.svChangeDash.isChecked=true
//                }
//            }
//        })

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
                binding.tvSwitchText.text = getString(R.string.switch_to_learner_dashboard)
                binding.vpDashboards.setCurrentItem(
                    binding.vpDashboards.currentItem + 1,
                    true
                )
            } else {
                binding.tvSwitchText.text = getString(R.string.switch_to_creator_dashboard)
                binding.vpDashboards.setCurrentItem(
                    binding.vpDashboards.currentItem - 1,
                    true
                )
            }

        }


    }


    override fun getLayoutRes() = R.layout.fragment_dashboard_base
    override fun onApiRetry(apiCode: String) {

    }

}