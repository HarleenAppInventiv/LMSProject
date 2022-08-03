package com.selflearningcoursecreationapp.ui.dashboard

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDashModeartorBinding
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class DashModeratorFragment : BaseFragment<FragmentDashModeartorBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {

        binding.pieChartView.apply {

            val pieEntries: ArrayList<PieEntry> = ArrayList()
            val label = ""
            val typeAmountMap: MutableMap<String, Int> = HashMap()
            typeAmountMap["Main"] = 100
            typeAmountMap["Less"] = 20
            val colors: ArrayList<Int> = ArrayList()
            colors.add(ContextCompat.getColor(baseActivity, R.color.accent_color_2FBF71))
            colors.add(ContextCompat.getColor(baseActivity, R.color.no_earning_chart_bg))
            for (type in typeAmountMap.keys) {
                pieEntries.add(PieEntry(typeAmountMap[type]!!.toFloat(), ""))
            }
            val pieDataSet = PieDataSet(pieEntries, label)
            pieDataSet.valueTextSize = 0f
            pieDataSet.colors = colors
            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(true)
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f


//            chart.setCenterTextTypeface(tfLight)
//            chart.setCenterText(generateCenterSpannableText())

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 40f
            transparentCircleRadius = 40f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = false


//            val l: Legend = getLegend()
//            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
//            l.orientation = Legend.LegendOrientation.VERTICAL
//            l.setDrawInside(false)
//            l.xEntrySpace = 7f
//            l.yEntrySpace = 0f
//            l.yOffset = 0f

            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            invalidate()

        }

        binding.pieChartView2.apply {

            val pieEntries: ArrayList<PieEntry> = ArrayList()
            val label = ""
            val typeAmountMap: MutableMap<String, Int> = HashMap()
            typeAmountMap["off"] = 8
            typeAmountMap["less"] = 5
            typeAmountMap["more"] = 2
            val colors: ArrayList<Int> = ArrayList()
            colors.add(Color.parseColor("#2FBF71"))
            colors.add(Color.parseColor("#FC6D5B"))
            colors.add(Color.parseColor("#FFB800"))
            for (type in typeAmountMap.keys) {
                pieEntries.add(PieEntry(typeAmountMap[type]!!.toFloat()))
            }
            val pieDataSet = PieDataSet(pieEntries, label)
            pieDataSet.valueTextSize = 10f
            pieDataSet.colors = colors
            val pieData = PieData(pieDataSet)
            pieData.setDrawValues(true)
            data = pieData
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            legend.isEnabled = false


//            chart.setCenterTextTypeface(tfLight)
//            chart.setCenterText(generateCenterSpannableText())

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 0f
            transparentCircleRadius = 0f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)


//            val l: Legend = getLegend()
//            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
//            l.orientation = Legend.LegendOrientation.VERTICAL
//            l.setDrawInside(false)
//            l.xEntrySpace = 7f
//            l.yEntrySpace = 0f
//            l.yOffset = 0f

            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            invalidate()

        }

        val list = arrayListOf<Fragment>(
            InProgressFragment(),
            TodoFragment(),
            DoneFragment(),
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.in_progress),
            baseActivity.getString(R.string.todo),
            baseActivity.getString(R.string.done),
        )
        binding.vpCourses.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)


        TabLayoutMediator(binding.tlCourses, binding.vpCourses) { tab, position ->
            tab.text = nameArray[position]


        }.attach()

        binding.tlCourses.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tlCourses.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )

        binding.tlCourses.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT_BOLD
                (tab?.customView as TextView?)?.isAllCaps = false
//                (tab?.customView as TextView?)?.setTextColor(
//                    ThemeUtils.getPrimaryTextColor(
//                        baseActivity
//                    )
//                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT
                (tab?.customView as TextView?)?.isAllCaps = false

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })

    }

    override fun getLayoutRes() = R.layout.fragment_dash_modeartor
    override fun onApiRetry(apiCode: String) {

    }


}