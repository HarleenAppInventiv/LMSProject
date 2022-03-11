package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps


import android.os.Bundle
import android.view.View

import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep3Binding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.ThemeData
import com.selflearningcoursecreationapp.ui.preferences.PreferencesFragment


class Step3Fragment : BaseFragment<FragmentStep3Binding>() {
    var select_logo = false
    var select_course_image = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        binding.tvUploadTheme.setOnClickListener {
            findNavController().navigate(AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToPreferencesFragment(
                PreferencesFragment.TYPE_THEME,
                title = baseActivity.getString(R.string.select_theme),
                screenType = PreferencesFragment.SCREEN_COURSE))
        }
        binding.tvUploadFont.setOnClickListener {
            findNavController().navigate(AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToPreferencesFragment(
                PreferencesFragment.TYPE_FONT,
                title = baseActivity.getString(R.string.select_font),
                screenType = PreferencesFragment.SCREEN_COURSE))
        }
        parentFragment?.setFragmentResultListener("preferenceData",
            listener = { _, bundle ->
                val type = bundle.getInt("type")
                val list = bundle.getParcelableArrayList<ThemeData>("data")
                showToastShort("data received")
                when (type) {
                    PreferencesFragment.TYPE_FONT -> {

                        list?.forEach {
                            binding.tvUploadFont.text = getString(it.themeName)
                            binding.ivUploadFont.setImageDrawable(
                                ContextCompat.getDrawable(
                                    baseActivity,
                                    R.drawable.ic_font_preview
                                )
                            )
                            binding.ivEditFont.visible()
                        }
                    }
                    PreferencesFragment.TYPE_THEME -> {
                        list?.forEach {
                            binding.tvUploadTheme.text = getString(it.themeName)
//                            binding.ivUploadTheme.setImageDrawable(null)
                            binding.ivUploadTheme.setBackgroundColor(
                                ContextCompat.getColor(
                                    baseActivity,
                                    it.themeColor
                                )
                            )
                            binding.ivEditTheme.visible()

                        }
                    }
                }
            })


        binding.ivUploadLogo.setOnClickListener {
            showBottomSheetDialogMedia()
            select_logo = true
        }
        binding.ivSelectedMedia.setOnClickListener {
            showBottomSheetDialogMedia()
            select_course_image = true
        }
    }

    override fun getLayoutRes() = R.layout.fragment_step3




}