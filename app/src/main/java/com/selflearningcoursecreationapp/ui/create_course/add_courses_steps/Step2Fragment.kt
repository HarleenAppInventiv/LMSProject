package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep2Binding
import com.selflearningcoursecreationapp.models.ThemeData
import com.selflearningcoursecreationapp.ui.preferences.PreferencesFragment


class Step2Fragment : BaseFragment<FragmentStep2Binding>() {


    override fun getLayoutRes(): Int {
        return R.layout.fragment_step2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.tvSelectedLanguage.setOnClickListener {
            findNavController().navigate(AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToPreferencesFragment(
                PreferencesFragment.TYPE_LANGUAGE,
                title = baseActivity.getString(R.string.select_language),
                screenType = PreferencesFragment.SCREEN_COURSE))
        }
        binding.tvSelectedCategory.setOnClickListener {
            findNavController().navigate(AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToPreferencesFragment(
                PreferencesFragment.TYPE_CATEGORY,
                title = baseActivity.getString(R.string.select_categories),
                screenType = PreferencesFragment.SCREEN_COURSE))
        }
        parentFragment?.setFragmentResultListener("preferenceData", listener = { _, bundle ->
            val type = bundle.getInt("type")
            val list = bundle.getParcelableArrayList<ThemeData>("data")
            showToastShort("data received")
            when (type) {
                PreferencesFragment.TYPE_CATEGORY -> {
                    list?.map { it.languageId }?.let {
                        binding.tvSelectedCategory.text = it.joinToString()
                    }
                }
                PreferencesFragment.TYPE_LANGUAGE -> {
                    list?.map { getString(it.themeName) }?.let {
                        binding.tvSelectedLanguage.text = it.joinToString()
                    }
                }
            }
        })
    }
}