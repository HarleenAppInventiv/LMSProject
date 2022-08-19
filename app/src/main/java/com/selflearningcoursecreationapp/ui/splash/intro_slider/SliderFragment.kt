package com.selflearningcoursecreationapp.ui.splash.intro_slider

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
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
import com.selflearningcoursecreationapp.utils.ThemeConstant
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
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
        binding.btnGetStarted.setOnClickListener(this)

        lifecycleScope.launch {
            val value =
                withContext(lifecycleScope.coroutineContext) {
                    PreferenceDataStore.getInt(
                        Constants.APP_THEME
                    )
                }
            baseActivity.runOnUiThread {
                binding.svVisualImpared.isChecked = value == ThemeConstant.BLACK
            }
        }
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

    private fun onClickListeners() {
        binding.btnGetStarted.setOnClickListener(this)
        binding.svVisualImpared.setOnClickListener(this)
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
                showLoading()
                baseActivity.saveTheme(if (binding.svVisualImpared.isChecked) ThemeConstant.BLACK else ThemeConstant.BLUE)



                lifecycleScope.launch {

                    withContext(lifecycleScope.coroutineContext) {
                        viewModel.saveThemeFile(viewModel.getThemeFile(getColor()))
                        (getAppContext() as SelfLearningApplication).updatedThemeFile()
                        PreferenceDataStore.saveBoolean(Constants.WALKTHROUGH_DONE, true)
                    }
                    delay(2000)
                    baseActivity.runOnUiThread {
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
            R.id.iv_info -> {

                ViModeDialog().apply {
                    arguments = bundleOf("colorString" to getColor())
                }.show(childFragmentManager, "")

            }
            R.id.sv_visual_impared -> {


                binding.parentCL.backgroundTintList = null

                binding.parentCL.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(getColor()))
                binding.parentCL.backgroundTintMode = PorterDuff.Mode.MULTIPLY
                binding.btnGetStarted.setTextColor(Color.parseColor(getColor()))


            }
            R.id.iv_info_reading_mode -> {
                val color =
                    if (binding.svVisualImpared.isChecked) R.color.black_theme else R.color.primaryColor
                val iconColor = Color.parseColor(getColor())
                val secondaryColor = Color.parseColor(
                    String.format(
                        "#%02x%02x%02x%02x",
                        80,
                        Color.red(iconColor),
                        Color.green(iconColor),
                        Color.blue(iconColor)
                    )
                )
                CommonAlertDialog.builder(requireContext())
                    .icon(R.drawable.ic_visually_impaired)
                    .title(getString(R.string.screen_reading_mode))
                    .description(getString(R.string.on_screen_read_mode))
                    .positiveBtnText(getString(R.string.close))
                    .hideNegativeBtn(true)
                    .setPositiveInCaps(false)
                    .setPositiveButtonTheme(bgColor = color)
                    .setVectorIconColor(iconColor, secondaryColor)
                    .build()
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