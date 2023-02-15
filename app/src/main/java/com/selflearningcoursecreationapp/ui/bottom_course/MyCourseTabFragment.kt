package com.selflearningcoursecreationapp.ui.bottom_course

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentMyCourseTabBinding
import com.selflearningcoursecreationapp.extensions.setCustomTabs
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MyCourseTabFragment : BaseFragment<FragmentMyCourseTabBinding>() {
    private val viewModel: MyCourseVM by viewModel()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_course_tab
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        initUi()
    }


    private fun initUi() {
        if (arguments?.containsKey("fromProfile") == true && arguments?.getBoolean("fromProfile") == true) {
            (baseActivity as HomeActivity).bottomBarVisibility(false)

        } else {
            baseActivity.setToolbar(
                baseActivity.getString(R.string.my_course),
                showBackIcon = false
            )

        }
        initViewPager()

        if (arguments?.containsKey("tabPosition") == true) {
            lifecycleScope.launch {
                delay(500)
                baseActivity.runOnUiThread {
                    binding.viewpager.currentItem = arguments?.getInt("tabPosition") ?: 0
                }
            }
        }

        viewModel.viewPagerScroll.observe(viewLifecycleOwner, Observer {
            binding.viewpager.isUserInputEnabled = it
        })

    }

    private fun initViewPager() {
        val list = arrayListOf<Fragment>(
            MyCourseFragment(),
            CompletedCourseFragment(),
            CreatedCourseFragment(),
            //            RequestedFragment()
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.ongoing),
            baseActivity.getString(R.string.completed),
            baseActivity.getString(R.string.created)
        )
        binding.viewpager.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)

        TabLayoutMediator(binding.tlHeader, binding.viewpager) { tab, position ->
            tab.text = nameArray[position]


        }.attach()

        binding.tlHeader.setCustomTabs(nameArray)
        binding.tlHeader.getTabAt(0)?.customView?.isSelected = true
        binding.tlHeader.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tlHeader.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )
        binding.tlHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_SEMI_BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_MEDIUM)

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })
    }

    fun onClickBack(): Boolean {
        if (arguments?.containsKey("fromProfile") == true && arguments?.getBoolean("fromProfile") == true) {
            findNavController()?.navigateUp()
            return true
        } else {
            return false
        }
    }

    override fun onApiRetry(apiCode: String) {
//handle api retry callback
    }

//    fun refreshData() {
//
//    }
}