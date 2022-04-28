package com.selflearningcoursecreationapp.ui.bottom_home

import android.util.Log
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterHomeBinding
import com.selflearningcoursecreationapp.utils.Constant


class HomeAdapter(private var list: ArrayList<String>) : BaseAdapter<AdapterHomeBinding>() {

    override fun getLayoutRes() = R.layout.adapter_home

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterHomeBinding
        binding.rvCourses.adapter = HomeCoursesAdapter()
        binding.tvTitle.text = list[position]

        binding.tvSeeAll.setOnClickListener {
            onItemClick(Constant.CLICK_SEE_ALL, position)
            Log.d("varun", "onBindViewHolder: okkokkokoko")
        }
    }

    override fun getItemCount() = list.size


}