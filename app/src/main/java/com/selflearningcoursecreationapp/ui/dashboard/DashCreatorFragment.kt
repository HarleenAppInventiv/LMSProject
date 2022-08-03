package com.selflearningcoursecreationapp.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDashCreaterBinding
import com.selflearningcoursecreationapp.ui.dashboard.filter.DashboardFilterDialog
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class DashCreatorFragment : BaseFragment<FragmentDashCreaterBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {
        initEarningPieChart()
        initUsersPieChart()
        initBarChart()

        binding.tvFilter.setOnClickListener {
            DashboardFilterDialog().show(childFragmentManager, "")
        }
    }

    private fun initBarChart() {
        binding.barChart.apply {
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)

            description.isEnabled = false
            // drawn
            setMaxVisibleValueCount(60)

            // scaling can now only be done on x- and y-axis separately
            setPinchZoom(false)

            setDrawGridBackground(false)
            val rightAxis: YAxis = axisRight
            rightAxis.setLabelCount(6, false)
            rightAxis.setDrawGridLines(false)
            rightAxis.textSize = 0f
            rightAxis.textColor = ContextCompat.getColor(baseActivity, R.color.white)
            val xAxis: XAxis = xAxis
            xAxis.position = XAxisPosition.BOTTOM
//            xAxis.typeface = tfLight
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f // only intervals of 1 day

            xAxis.labelCount = 7
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return "Course ${value.toInt()}"
                }

                override fun getBarLabel(barEntry: BarEntry?): String {
                    return super.getBarLabel(barEntry)

                }
            }

            val leftAxis: YAxis = axisLeft
//            leftAxis.typeface = tfLight
            leftAxis.setLabelCount(6, false)

            leftAxis.valueFormatter = object : ValueFormatter() {

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return "${value.toInt()}"
                }


            }
            leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
            leftAxis.spaceTop = 15f
            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


        }
        setData(5, 500f)
    }

    private fun initUsersPieChart() {
        binding.usersPieChart.apply {

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


            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            invalidate()

        }
    }

    private fun initEarningPieChart() {

        binding.earningPieChart.apply {

            val pieEntries: ArrayList<PieEntry> = ArrayList()
            val label = ""
            val typeAmountMap: MutableMap<String, Int> = HashMap()
            typeAmountMap["Main"] = 100
            typeAmountMap["Less"] = 20
            val colors: ArrayList<Int> = ArrayList()
            colors.add(Color.parseColor("#2FBF71"))
            colors.add(Color.parseColor("#D1D4D9"))
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



            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            invalidate()

        }
    }

    private fun setData(count: Int, range: Float) {
        val start = 1f
        val values: ArrayList<BarEntry> = ArrayList()
        var i = start.toInt()
        while (i < start + count) {
            val value = (Math.random() * (range + 1)).toFloat()


            values.add(BarEntry(i.toFloat(), value))
            i++
        }
        val set1: BarDataSet
        if (binding.barChart.data != null &&
            binding.barChart.data.dataSetCount > 0
        ) {
            set1 = binding.barChart.data.getDataSetByIndex(0) as BarDataSet

            set1.values = values
            binding.barChart.data.notifyDataChanged()
            binding.barChart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "")
            set1.setDrawIcons(false)
            set1.color = ThemeUtils.getAppColor(baseActivity)
            val dataSets: ArrayList<IBarDataSet> = ArrayList()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueTextSize(10f)
//            data.setValueTypeface(tfLight)
            data.barWidth = 0.5f
            binding.barChart.data = data
        }
    }

    override fun getLayoutRes() = R.layout.fragment_dash_creater
    override fun onApiRetry(apiCode: String) {

    }

}