package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModeratorBaseBinding
import com.selflearningcoursecreationapp.extensions.setCustomTabs
import com.selflearningcoursecreationapp.ui.moderator.dialog.ModerateFilterDialogue
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class ModeratorBaseFragment : BaseFragment<FragmentModeratorBaseBinding>(), HandleClick {

    override fun getLayoutRes() = R.layout.fragment_moderator_base

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.supportActionBar?.hide()
        initUI()
    }

    private fun initUI() {
        binding.handleClick = this
        initViewPager()
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
        }
        val color = ThemeUtils.getAppColor(baseActivity)
        binding.toolbarLayout.setContentScrimColor(color)
        binding.toolbarLayout.setBackgroundColor(color)
        binding.toolbarLayout.setStatusBarScrimColor(color)
        binding.appBar.setBackgroundColor(color)

        binding.filter.setOnClickListener {
            ModerateFilterDialogue().show(childFragmentManager, "")
        }

        binding.ivNotification.setOnClickListener {
            findNavController().navigate(R.id.action_moderatorBaseFragment_to_notificationFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        baseActivity.supportActionBar?.hide()

    }

    private fun initViewPager() {
        val list = ArrayList<Fragment>()
        list.add(RequestFragment())
        list.add(ApprovedFragment())
        list.add(RejectedFragment())

        val nameArray = ArrayList<String>()
        nameArray.add("Request")
        nameArray.add("Approved")
        nameArray.add("Rejected")
        binding.vpModeratorHome.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.vpModeratorHome) { tab, position ->
            tab.text = nameArray[position]
        }.attach()
        binding.tabLayout.setCustomTabs(nameArray)
        binding.tabLayout.getTabAt(0)?.customView?.isSelected = true

        binding.tabLayout.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

    override fun onApiRetry(apiCode: String) {
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.iv_user_image -> {
                    if (baseActivity.tokenFromDataStore() == "") {
                        baseActivity.guestUserPopUp()
                    } else {
                        findNavController().navigate(R.id.action_moderatorBaseFragment_to_nav_profile_graph)
                    }

                }
                R.id.tv_user_name -> {
                    if (baseActivity.tokenFromDataStore() == "") {
                        baseActivity.guestUserPopUp()
                    } else {
                        findNavController().navigate(R.id.action_moderatorBaseFragment_to_nav_profile_graph)
                    }

                }

            }

        }
    }

}