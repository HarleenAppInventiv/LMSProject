package com.selflearningcoursecreationapp.ui.preferences

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter

class ScreenSlidePagerAdapter(
    fragmentActivity: FragmentManager,
    private var list: ArrayList<Fragment>,
    var lifecycleOwner: Lifecycle,
) :
    FragmentStateAdapter(fragmentActivity, lifecycleOwner) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]

    }
}