package com.selflearningcoursecreationapp.ui.splash.intro_slider

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.databinding.FragmentSliderBinding
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.setTransparentLightStatusBar
import com.selflearningcoursecreationapp.models.WalkThroughData
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.ui.dialog.ViModeDialog
import com.selflearningcoursecreationapp.ui.splash.SplashVM
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.Constants
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


@SuppressLint("NotifyDataSetChanged")
class SliderFragment : BaseFragment<FragmentSliderBinding>(), View.OnClickListener {
    var adapter: SlideViewPagerAdapter? = null
    private var dotAdapter: DotAdapter? = null
    var dotList: ArrayList<Boolean> = ArrayList()
    private val viewModel: SplashVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }


    fun init() {
        onClickListeners()
        initViewPager()
        binding.btnGetStarted.setOnClickListener(this)

//        binding.svReadingMode.isChecked = baseActivity.getAccessibilityService()


        autoSlider()

    }

    private fun initViewPager() {
        val list = ArrayList<WalkThroughData>()
        val iconList = baseActivity.resources.obtainTypedArray(R.array.walkthrough_icons)
        val titleList = baseActivity.resources.getStringArray(R.array.walkthrough_title)
        val descList = baseActivity.resources.getStringArray(R.array.walkthrough_description)

        for (i in titleList.indices) {
            list.add(
                WalkThroughData(
                    title = titleList[i],
                    description = descList[i],
                    iconList.getResourceId(i, -1)
                )
            )
        }
        dotList.clear()
        dotList.addAll(list.map { false })
        dotList[0] = true
        adapter = SlideViewPagerAdapter(list)
        binding.viewpager.adapter = adapter
        iconList.recycle()
        setDotAdapter()


        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                dotList.forEachIndexed { index, _ ->
                    dotList[index] = index == position
                }
                setDotAdapter()
            }
        })
    }

    private fun autoSlider() {
        lifecycleScope.launch {
            delay(3000)
            baseActivity.runOnUiThread {

                if (binding.viewpager.currentItem >= 2) {
                    binding.viewpager.currentItem = 0
                } else {
                    binding.viewpager.currentItem += 1
                }

                autoSlider()

            }
        }
    }

    private fun onClickListeners() {
        binding.btnGetStarted.setOnClickListener(this)
        binding.svVisualImpared.setOnClickListener(this)
        binding.svReadingMode.setOnClickListener(this)
        binding.ivInfo.setOnClickListener(this)
        binding.ivInfoReadingMode.setOnClickListener(this)
    }

    private fun setDotAdapter() {
        dotAdapter?.notifyDataSetChanged() ?: kotlin.run {
            dotAdapter = DotAdapter(Constant.TYPE_LINE, dotList)
            binding.dotRV.adapter = dotAdapter
        }
    }

    @SuppressLint("ResourceType")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnGetStarted -> {
                getStarted()


            }
            R.id.iv_info -> {

                ViModeDialog().apply {
                    arguments = bundleOf("colorString" to getColor())
                }.show(childFragmentManager, "")

            }
            R.id.sv_visual_impared -> {


                viModeChanges()


            }

            R.id.sv_reading_mode -> {
                binding.svReadingMode.isChecked = !binding.svReadingMode.isChecked
                baseActivity.checkAccessibilityService()

            }
            R.id.iv_info_reading_mode -> {

                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_visually_impaired)
                    .title(getString(R.string.screen_reading_mode))
                    .spannedText(
                        SpanUtils.with(
                            baseActivity,
                            getString(R.string.enable_screen_reading_mode_desc)
                        )
                            .startPos(80)
                            .textColor().isBold().getSpanString()
                    )
                    .positiveBtnText(getString(R.string.close))
                    .hideNegativeBtn(true)
                    .setPositiveInCaps(false)
//                    .setPositiveButtonTheme(bgColor = color)
//                    .setVectorIconColor(iconColor, secondaryColor)
                    .build()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        binding.svReadingMode.isChecked = baseActivity.getAccessibilityService()

    }

    private fun viModeChanges() {
        showLoading()
        SelfLearningApplication.isViOn = binding.svVisualImpared.isChecked
        baseActivity.setDayNightTheme()

        lifecycleScope.launch {
            delay(500)
        }
        hideLoading()

        binding.parentCL.changeBackground(ThemeConstants.TYPE_BACKGROUND_TINT)

        binding.viewpager.adapter?.notifyDataSetChanged()
        binding.btnGetStarted.changeBtnBackground(if (binding.svVisualImpared.isChecked) ThemeConstants.TYPE_PRIMARY else ThemeConstants.TYPE_SECONDARY)
        binding.btnGetStarted.changeTextColor(if (binding.svVisualImpared.isChecked) ThemeConstants.TYPE_PRIMARY else ThemeConstants.TYPE_SECONDARY)
        if (binding.svVisualImpared.isChecked) {
            binding.svVisualImpared.buttonDrawable =
                ContextCompat.getDrawable(baseActivity, R.drawable.sw_intro_selector)
            binding.svReadingMode.buttonDrawable =
                ContextCompat.getDrawable(baseActivity, R.drawable.sw_intro_selector)
        } else {
            binding.svVisualImpared.buttonDrawable =
                ContextCompat.getDrawable(baseActivity, R.drawable.sw_small_selector)
            binding.svReadingMode.buttonDrawable =
                ContextCompat.getDrawable(baseActivity, R.drawable.sw_small_selector)
        }

    }

    private fun getStarted() {
        showLoading()
        viewModel.saveViMode(binding.svVisualImpared.isChecked)



        lifecycleScope.launch {
            withContext(lifecycleScope.coroutineContext) {
                viewModel.saveThemeFile(viewModel.getThemeFile(getColor()))
                (getAppContext() as SelfLearningApplication).updatedThemeFile()
                PreferenceDataStore.saveBoolean(Constants.WALKTHROUGH_DONE, true)
            }
            delay(2000)
            baseActivity.runOnUiThread {
                SelfLearningApplication.isViOn = binding.svVisualImpared.isChecked

                hideLoading()
                baseActivity.startActivity(
                    Intent(
                        baseActivity,
                        InitialActivity::class.java
                    )
                )
                baseActivity.finish()
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_slider


    override fun onDestroyView() {
        super.onDestroyView()
        baseActivity.setTransparentLightStatusBar()
    }


    override fun onApiRetry(apiCode: String) {

    }

    @SuppressLint("ResourceType")
    fun getColor(): String {
        return if (binding.svVisualImpared.isChecked) getString(R.color.black_theme) else getString(
            R.color.primaryColor
        )
    }
}

