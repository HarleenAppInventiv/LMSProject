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
//
////    override fun saveState(): Parcelable? {
////        return null
////    }
//
//    override fun canRestoreState(): Boolean {
//        return super.canRestoreState()
//    }
//    fun restoreState(state: Parcelable?, loader: ClassLoader?) {
//        try {
//            super.restoreState(state!!)
//        } catch (e: Exception) {
//            Log.e("TAG", "Error Restore State of Fragment : " + e.message, e)
//        }
//    }
//    override fun setStateRestorationPolicy(strategy: StateRestorationPolicy) {
//        super.setStateRestorationPolicy(strategy)
//    }


}