package com.selflearningcoursecreationapp.ui.preferences

import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder


class ScreenSlidePagerTestAdapter(
    fragmentActivity: Fragment,
    var list: ArrayList<Fragment>
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]

    }

    fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        try {
            super.restoreState(state!!)
        } catch (e: Exception) {
            Log.e("TAG", "Error Restore State of Fragment : " + e.message, e)
        }
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

    }
}