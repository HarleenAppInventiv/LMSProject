package com.selflearningcoursecreationapp.ui.bottom_course

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentMyCourseTabBinding
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class MyCourseTabFragment : BaseFragment<FragmentMyCourseTabBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_course_tab
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initUi()
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


    private fun initUi() {
        val list = arrayListOf<Fragment>(
            MyCourseFragment(),
            CompletedCourseFragment(),
            CreatedCourseFragment(),
            RequestedFragment()
        )
        val nameArray = arrayListOf<String>(
            baseActivity.getString(R.string.ongoing),
            baseActivity.getString(R.string.completed),
            baseActivity.getString(R.string.created),
            baseActivity.getString(R.string.requestes)
        )
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