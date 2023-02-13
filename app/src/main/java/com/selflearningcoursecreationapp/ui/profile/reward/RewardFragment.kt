package com.selflearningcoursecreationapp.ui.profile.reward

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRewardBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.setCustomTabs
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.ui.profile.reward.viewModel.RewardViewModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class RewardFragment : BaseFragment<FragmentRewardBinding>() {
    private val viewModel: RewardViewModel by viewModel()
    var availableRewards = 0
    var learnerRewards = 0
    var creatorRewards = 0
    var totalSpends = 0
    override fun getLayoutRes() = R.layout.fragment_reward
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()

    }

    private fun initUI() {

        val nameArray = arrayListOf(getString(R.string.my_earning), getString(R.string.my_purchase))
        val adapter = ScreenSlidePagerAdapter(
            childFragmentManager,
            arrayListOf(MyEarningsFragment(), MyPurchaseFragment()),
            this.lifecycle
        )

        binding.viewpager.adapter = adapter

        TabLayoutMediator(binding.tlHeader, binding.viewpager) { tab, position ->
            tab.text = nameArray[position]
        }.attach()
        binding.tlHeader.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tlHeader.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )
        binding.tlHeader.setCustomTabs(nameArray)
        binding.tlHeader.getTabAt(0)?.customView?.isSelected = true
        binding.ivInfoIcon.setOnClickListener {
            CommonAlertDialog.builder(baseActivity)
                .icon(R.drawable.ic_info)
                .title(getString(R.string.alerte))
                .description(getString(R.string.these_reward_points_are_not_applicable_to_purchase_any_course))
                .positiveBtnText(getString(R.string.close))
                .hideNegativeBtn(true)
                .setPositiveInCaps(false)
//                    .setPositiveButtonTheme(bgColor = color)
//                    .setVectorIconColor(iconColor, secondaryColor)
                .build()
        }

        binding.tlHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_MEDIUM)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_REGULAR)

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })
        viewModel.totalSpendRewards.observe(viewLifecycleOwner) {
            binding.tvTotalCount.text = it.toString()
            binding.ivFavouriteCourse.contentDescription = "Total spend $it reward point"

            totalSpends = it.toInt()
//            binding.tvAvailableRewards.text= availableRewards.toString()
            totalSpends = it.toInt()
            availableRewards = learnerRewards - totalSpends
            binding.tvAvailableRewards.text = availableRewards.toString()
        }

        viewModel.totalEarnAsALearnerRewards.observe(viewLifecycleOwner) {
            binding.ivCompletedCourse.contentDescription = "Learner $it reward point"
            learnerRewards = it.toInt()
//            binding.tvAvailableRewards.text= availableRewards.toString()
            binding.tvLearnerCount.text = it.toString()
            availableRewards = learnerRewards - totalSpends
            binding.tvAvailableRewards.text = availableRewards.toString()
        }


        viewModel.totalEarnAsACreatorRewards.observe(viewLifecycleOwner) {
            binding.tvCreatorCount.text = it.toString()
            binding.ivEnrolledCourse.contentDescription = "Creator $it reward point"

            creatorRewards = it.toInt()
            availableRewards = learnerRewards - totalSpends
            binding.tvAvailableRewards.text = availableRewards.toString()
            binding.ivAvailable.contentDescription = "Available $availableRewards reward point"

        }




        binding.ivUserLogo.loadImage(
            viewModel.userProfile?.profileUrl,
            R.drawable.ic_default_user_grey,
            viewModel.userProfile?.profileBlurHash
        )

        binding.tvName.text = viewModel.userProfile?.name
        binding.tvName.contentDescription = "User name is ${viewModel.userProfile?.name}"
//        binding.cvPopularCourse.contentDescription = "Info related to you reward point"

    }


    override fun onApiRetry(apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_REWARDS_POINTS -> {
                if (binding.viewpager.currentItem == 0) {
                    ((binding.viewpager.adapter as ScreenSlidePagerAdapter).list[0] as MyEarningsFragment).onRefreshData()
                } else if (binding.viewpager.currentItem == 1) {
                    ((binding.viewpager.adapter as ScreenSlidePagerAdapter).list[0] as MyPurchaseFragment).onRefreshData()
                }
            }
            else -> {
                viewModel.onApiRetry(apiCode)

            }
        }

    }

    fun refreshData() {
        binding.viewpager.currentItem = 0
    }

}