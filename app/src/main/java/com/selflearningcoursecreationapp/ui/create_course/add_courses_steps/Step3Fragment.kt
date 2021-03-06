package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep3Binding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.PREFERENCES


class Step3Fragment : BaseFragment<FragmentStep3Binding>(), BaseBottomSheetDialog.IDialogClick,
    HandleClick {

    override fun getLayoutRes() = R.layout.fragment_step3


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        binding.handleClick = this

        parentFragment?.setFragmentResultListener("preferenceData",
            listener = { _, bundle ->
                val type = bundle.getInt("type")
                val list = bundle.getParcelableArrayList<CategoryData>("data")
                showToastShort("data received")
                when (type) {
                    PREFERENCES.TYPE_FONT -> {

                        list?.forEach {
                            binding.tvUploadFont.text = it.name
                            binding.ivUploadFont.setImageDrawable(
                                ContextCompat.getDrawable(
                                    baseActivity,
                                    R.drawable.ic_font_preview
                                )
                            )
                            binding.ivEditFont.visible()
                        }
                    }
                    PREFERENCES.TYPE_THEME -> {
                        list?.forEach {
                            binding.tvUploadTheme.text = it.name
                            binding.ivUploadTheme.setBackgroundColor(
                                Color.parseColor(it.code)
                            )
                            binding.ivEditTheme.visible()

                        }
                    }
                }
            })

    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
//                Constant.CLICK_LOGO -> {
//                    binding.ivLogoBg.setImageURI(Uri.parse(items[1] as String))
//                    binding.ivUploadLogo.gone()
//                    binding.ivEditLogo.visible()
//
//                }
//                Constant.CLICK_BANNER -> {
//                    binding.ivBanner.setImageURI(Uri.parse(items[1] as String))
//                    binding.tvUploadImage.gone()
//                }
            }
        }
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
//                R.id.tv_upload_image, R.id.iv_banner -> {
//                    UploadImageOptionsDialog().apply {
////                        arguments = bundleOf("type" to Constant.CLICK_BANNER)
//                        setOnDialogClickListener(this@Step3Fragment)
//                    }.show(childFragmentManager, "")
//                }
//
//                R.id.iv_logo_bg, R.id.iv_upload_logo, R.id.iv_edit_logo -> {
//                    UploadImageOptionsDialog().apply {
////                        arguments = bundleOf("type" to Constant.CLICK_LOGO)
//                        setOnDialogClickListener(this@Step3Fragment)
//                    }.show(childFragmentManager, "")
//                }

                R.id.iv_theme_bg, R.id.iv_upload_theme, R.id.iv_edit_theme -> {
                    findNavController().navigate(
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToPreferencesFragment(
                            PREFERENCES.TYPE_THEME,
                            title = baseActivity.getString(R.string.select_theme),
                            screenType = PREFERENCES.SCREEN_COURSE
                        )
                    )
                }

                R.id.iv_font_bg, R.id.iv_upload_font, R.id.iv_edit_font -> {
                    findNavController().navigate(
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToPreferencesFragment(
                            PREFERENCES.TYPE_FONT,
                            title = baseActivity.getString(R.string.select_font),
                            screenType = PREFERENCES.SCREEN_COURSE
                        )
                    )
                }
            }
        }
    }


}