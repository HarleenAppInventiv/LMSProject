package com.selflearningcoursecreationapp.ui.splash.intro_slider

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.databinding.FragmentSliderBinding
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.setTransparentLightStatusBar
import com.selflearningcoursecreationapp.models.WalkthroughData
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.ui.dialog.ViModeDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.Constants
import com.selflearningcoursecreationapp.utils.THEME_CONSTANT
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SliderFragment : BaseFragment<FragmentSliderBinding>(), View.OnClickListener,
    BaseAdapter.IViewClick {
    var adapter: SlideViewPagerAdapter? = null
    var dotAdapter: DotAdapter? = null
    var dotList: ArrayList<Boolean> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        onClickListners()
        val list = ArrayList<WalkthroughData>()
        val iconList = baseActivity.resources.obtainTypedArray(R.array.walkthrough_icons)
        val titleList = baseActivity.resources.getStringArray(R.array.walkthrough_title)
        val descList = baseActivity.resources.getStringArray(R.array.walkthrough_description)

        for (i in 0 until titleList.size) {
            list.add(
                WalkthroughData(
                    title = titleList[i],
                    descrtion = descList[i],
                    iconList.getResourceId(i, -1)
                )
            )
        }
        dotList.clear()
        dotList.addAll(list.map { false })
        dotList.set(0, true)
        adapter = SlideViewPagerAdapter(list)
        binding.viewpager.adapter = adapter
        adapter!!.setOnAdapterItemClickListener(this)
        iconList.recycle()
        binding.btnGetStarted.setOnClickListener(this)

        lifecycleScope.launch {
            val value =
                lifecycleScope.async { PreferenceDataStore.getInt(Constants.APP_THEME) }.await()
            baseActivity.runOnUiThread {
                binding.svVisualImpared.isChecked = value == THEME_CONSTANT.BLACK
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

    private fun onClickListners() {
        binding.btnGetStarted.setOnClickListener(this)
        binding.svVisualImpared.setOnClickListener(this)
        binding.ivInfo.setOnClickListener(this)
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
//                context.navigationOnly(LoginSingupActivity())
                showLoading()
                baseActivity.saveTheme(if (binding.svVisualImpared.isChecked) THEME_CONSTANT.BLACK else THEME_CONSTANT.BLUE)

                val colorString =
                    if (binding.svVisualImpared.isChecked) getString(R.color.black_theme) else getString(
                        R.color.blue
                    )

                lifecycleScope.launch {
                    lifecycleScope.async {
                        baseActivity.saveThemeFile(baseActivity.getThemeFile(colorString))
                        (getAppContext() as SelfLearningApplication).updatedThemeFile()
                        PreferenceDataStore.saveBoolean(Constants.WALKTHROUGH_DONE, true)
                    }
                        .await()
                }
                hideLoading()
                baseActivity.startActivity(Intent(baseActivity, InitialActivity::class.java))
                baseActivity.finish()

            }
            R.id.iv_info -> {
                val colorString =
                    if (binding.svVisualImpared.isChecked) getString(R.color.black_theme) else getString(
                        R.color.blue
                    )
                ViModeDialog().apply {
                    arguments= bundleOf("colorString" to colorString)
                }.show(childFragmentManager, "")

            }
            R.id.sv_visual_impared -> {

                val colorString =
                    if (binding.svVisualImpared.isChecked) getString(R.color.black_theme) else getString(
                        R.color.blue
                    )

                binding.parentCL.backgroundTintList = null

                binding.parentCL.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(colorString))
                binding.parentCL.backgroundTintMode = PorterDuff.Mode.MULTIPLY
                binding.btnGetStarted.setTextColor(Color.parseColor(colorString))


//                lifecycleScope.launch {
//                    lifecycleScope.async {
//                        PreferenceDataStore.saveInt(Constants.APP_THEME,
//                            if (binding.svVisualImpared.isChecked) THEME_CONSTANT.BLACK else THEME_CONSTANT.BLUE)
//                    }.await()
//                    delay(1000)
//                    lifecycleScope.async {
//                        baseActivity.setAppTheme()
//                    }.await()
//                    delay(1000)
//                    baseActivity.runOnUiThread {
//                        hideLoading()
//                        findNavController().navigate(R.id.action_sliderFragment_self)
//
//                    }
//
//
//                }

//                baseActivity.saveTheme(if (binding.cbVisual.isChecked) THEME_CONSTANT.BLACK else THEME_CONSTANT.BLUE)
//                lifecycleScope.launch {
//                    delay(1000)
//
//                }
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_slider


    override fun onDestroyView() {
        super.onDestroyView()
        baseActivity.setTransparentLightStatusBar()
    }


    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    Log.d("main", "")
                }
            }
        }
    }


}