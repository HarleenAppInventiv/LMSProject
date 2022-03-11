package com.selflearningcoursecreationapp.ui.bottom_home

import android.os.Bundle

import android.view.View
import androidx.navigation.fragment.findNavController

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentHomeBinding
import com.selflearningcoursecreationapp.utils.HandleClick


class HomeFragment : BaseFragment<FragmentHomeBinding>(), HandleClick {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        binding.homefrag = this
        baseActivity.supportActionBar?.hide()

        binding.tlCourseType.apply {
            addTab(this.newTab().setText("Arts"))
            addTab(this.newTab().setText("Science"))
            addTab(this.newTab().setText("Biology"))
            addTab(this.newTab().setText("Commerce"))
            addTab(this.newTab().setText("Design"))

        }

        binding.contentHome.rvPopularCourses.adapter = HomeScreenAdapter()
        binding.contentHome.rvRecentCourses.adapter = HomeScreenAdapter()


    }

    override fun getLayoutRes() = R.layout.fragment_home


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.iv_user -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileThumbFragment)
                }
                R.id.tv_user_name -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileThumbFragment)

                }
            }

        }
    }

}