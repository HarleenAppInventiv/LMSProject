package com.selflearningcoursecreationapp.ui.search

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSearchBinding
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class SearchFragment : BaseFragment<FragmentSearchBinding>() {


    override fun getLayoutRes(): Int {
        return R.layout.fragment_search
    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearch.adapter = SearchAdapter()
        binding.rvPopularSearch.adapter = PopularSearchesAdapter()
        init()
    }

    private fun init() {
        baseActivity.setToolbar(showToolbar = false)
        val color = ThemeUtils.getAppColor(baseActivity)
        binding.toolbar.setBackgroundColor(color)
        binding.imgBack.setOnClickListener {

            findNavController().popBackStack()
        }

    }


}