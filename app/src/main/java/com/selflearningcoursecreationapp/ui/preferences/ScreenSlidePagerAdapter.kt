package com.selflearningcoursecreationapp.ui.preferences

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ScreenSlidePagerAdapter(
    fragmentActivity: FragmentManager,
    var list: ArrayList<Fragment>,
    lifecycleOwner: Lifecycle
) :
    FragmentStateAdapter(fragmentActivity, lifecycleOwner) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]

    }
}