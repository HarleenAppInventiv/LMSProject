package com.selflearningcoursecreationapp.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDashCreaterBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.CreatorDashboardVM
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorCourseUserCountWithFilter
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.model.CreatorTotalEarnings
import com.selflearningcoursecreationapp.ui.dashboard.filter.DashboardFilterDialog
import com.selflearningcoursecreationapp.utils.DASHBOARD_FILTER_TYPE
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import com.selflearningcoursecreationapp.utils.getDotsAfterTwentyChars
import com.selflearningcoursecreationapp.utils.isLessThan9
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class DashCreatorFragment : BaseFragment<FragmentDashCreaterBinding>(),
    BaseBottomSheetDialog.IDialogClick {

    private val viewModel: CreatorDashboardVM by viewModel()
    var inActiveCourseCount = 0
    var completedCount = 0
    var inProgressCount = 0
    var filterList = arrayListOf<String>("Day", "Week", "Month", "All")
    var earningFilterList = arrayListOf<String>("Day", "Week", "Month")
    var adapter: ArrayAdapter<Any?>? = null
    var earningAdapter: ArrayAdapter<Any?>? = null

    var agePositionInFilter = -1
    var professionPosInFilter = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {
        adapter?.notifyDataSetChanged()
        adapter = null

        earningAdapter?.notifyDataSetChanged()
        earningAdapter?.notifyDataSetChanged()
        earningAdapter = null

        changeFromAndTo()
        changeEarningFromAndTo()

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setObservers()
        viewModel.audienceStateCount()
        viewModel.getCourseUserCount()
        viewModel.getFilteredCourseUsersCount()
        viewModel.totalEarnings()
        setFilterAdapter()
        setEarningFilterAdapter()


        //viewModel.userProfile.

        binding.tvFilter.setOnClickListener {
            viewModel.reset()
            DashboardFilterDialog().apply {
                arguments = bundleOf(
                    "filterData" to viewModel.ageProfessionFilterData,
                    "professionPos" to professionPosInFilter,
                    "agePos" to agePositionInFilter
                )
                setOnDialogClickListener(this@DashCreatorFragment)
            }.show(childFragmentManager, "")
        }


        setClickListeners()
    }

    private fun setClickListeners() {
        binding.tvFrom.setOnClickListener {
            var calendarMax = Calendar.getInstance()

            if (viewModel.filterType == 2) calendarMax.time =
                getCurrentDate().createDate("yyyy-MM-dd")

            baseActivity.openDatePickerDialog(setMaxDate = true, maxDate = calendarMax) {
                viewModel.minDate = it.time.getStringDate("yyyy-MM-dd")

                if (viewModel.filterType == DASHBOARD_FILTER_TYPE.DAY) {
                    viewModel.selectedDay = it.time.getStringDate("yyyy-MM-dd")
                    binding.tvFrom.text = viewModel.selectedDay
                } else if (viewModel.filterType == DASHBOARD_FILTER_TYPE.WEEK) {
                    viewModel.maxDate = getNextXDays(it.time.getStringDate("yyyy-MM-dd"), 7)
                    binding.tvFrom.setText(viewModel.minDate)
                } else if (viewModel.filterType == DASHBOARD_FILTER_TYPE.MONTH) {
                    viewModel.maxDate = getNextMOnthDate(it.time.getStringDate("yyyy-MM-dd"))
                    binding.tvFrom.setText(viewModel.minDate)
                }

                binding.tvTo.setText(viewModel.maxDate)
                viewModel.reset()
                viewModel.getFilteredCourseUsersCount()
                viewModel.getCourseUserCount()
            }
        }

        binding.tvFromEarning.setOnClickListener {
            var calendarMax = Calendar.getInstance()

            if (viewModel.earningFilterType == 2) calendarMax.time =
                getCurrentDate().createDate("yyyy-MM-dd")

            baseActivity.openDatePickerDialog(setMaxDate = true, maxDate = calendarMax) {
                viewModel.earningMinDate = it.time.getStringDate("yyyy-MM-dd")

                if (viewModel.earningFilterType == DASHBOARD_FILTER_TYPE.DAY) {
                    viewModel.earningSelectedDay = it.time.getStringDate("yyyy-MM-dd")
                    binding.tvFromEarning.text = viewModel.earningSelectedDay
//                    binding.tvDate.text= "${viewModel.earningSelectedDay.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")} "
                } else if (viewModel.earningFilterType == DASHBOARD_FILTER_TYPE.WEEK) {
                    viewModel.earningMaxDate = getNextXDays(it.time.getStringDate("yyyy-MM-dd"), 7)
//                    binding.tvDate.text= "${viewModel.earningMinDate.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")} To ${viewModel.earningMaxDate.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")}"
                    binding.tvFromEarning.setText(viewModel.earningMinDate)
                } else if (viewModel.earningFilterType == DASHBOARD_FILTER_TYPE.MONTH) {
                    viewModel.earningMaxDate = getNextMOnthDate(it.time.getStringDate("yyyy-MM-dd"))
//                    binding.tvDate.text= "${viewModel.earningMinDate.changeDateFormat("yyyy-MM-dd", "MMM yyyy")} To ${viewModel.earningMaxDate.changeDateFormat("yyyy-MM-dd", "MMM yyyy")}"
                    binding.tvFromEarning.setText(viewModel.earningMinDate)
                }

                binding.tvToEarning.setText(viewModel.earningMaxDate)

                viewModel.totalEarnings()
            }
        }
    }

    private fun setFilterAdapter() {
        adapter = ArrayAdapter<Any?>(
            baseActivity,
            android.R.layout.simple_list_item_1,
            filterList as List<Any?>
        )
        binding.tvSelectDay.setAdapter(adapter)
        binding.tvSelectDay.setOnTouchListener { view, motionEvent ->
            binding.tvSelectDay.showDropDown()
            return@setOnTouchListener false
        }
        binding.tvSelectDay.setDropDownBackgroundResource(R.drawable.edt_white_bg)
        binding.tvSelectDay.setOnItemClickListener { adapterView, view, i, l ->

            viewModel.selectedDay = getCurrentDate()
            viewModel.filterType = i
            viewModel.reset()
            changeFromAndTo()
            viewModel.getCourseUserCount()
            viewModel.getFilteredCourseUsersCount()

        }
    }

    private fun setEarningFilterAdapter() {
        earningAdapter = ArrayAdapter<Any?>(
            baseActivity,
            android.R.layout.simple_list_item_1,
            earningFilterList as List<Any?>
        )
        binding.tvEarningDay.setAdapter(earningAdapter)
        binding.tvEarningDay.setOnTouchListener { view, motionEvent ->
            binding.tvEarningDay.showDropDown()
            return@setOnTouchListener false
        }
        binding.tvEarningDay.setDropDownBackgroundResource(R.drawable.edt_white_bg)
        binding.tvEarningDay.setOnItemClickListener { adapterView, view, i, l ->

            viewModel.earningSelectedDay = getCurrentDate()
            viewModel.earningFilterType = i

            changeEarningFromAndTo()
            viewModel.totalEarnings()

        }
    }


    private fun changeEarningFromAndTo() {
        when (viewModel.earningFilterType) {
            DASHBOARD_FILTER_TYPE.DAY -> {
                binding.tvFromEarning.visible()
                binding.tvToEarning.gone()
                binding.tvDay.text = getString(R.string.day) + " :"
                binding.tvFromEarning.text = viewModel.selectedDay
            }

            DASHBOARD_FILTER_TYPE.WEEK -> {
                binding.tvDay.text = getString(R.string.week) + " :"
                binding.tvFromEarning.visible()
                binding.tvToEarning.visible()
                viewModel.earningMinDate =
                    getLastWeekDate(getCurrentDate()).createDate("yyyy-MM-dd")
                        .getStringDate("yyyy-MM-dd")
                viewModel.earningMaxDate =
                    (getCurrentDate().createDate("yyyy-MM-dd").getStringDate("yyyy-MM-dd"))
                binding.tvToEarning.text = viewModel.earningMaxDate
                binding.tvFromEarning.text = viewModel.earningMinDate
            }

            DASHBOARD_FILTER_TYPE.MONTH -> {
                binding.tvDay.text = getString(R.string.month) + " :"
                binding.tvFromEarning.visible()
                binding.tvToEarning.visible()
                viewModel.earningMinDate =
                    getLastMOnthDate(getCurrentDate()).createDate("yyyy-MM-dd")
                        .getStringDate("yyyy-MM-dd")
                viewModel.earningMaxDate =
                    (getCurrentDate().createDate("yyyy-MM-dd").getStringDate("yyyy-MM-dd"))
                binding.tvToEarning.text = viewModel.earningMaxDate
                binding.tvFromEarning.text = viewModel.earningMinDate
            }

        }

        //            binding.tvDate.text= "${viewModel.earningMinDate.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")} To " +
//                    "${viewModel.earningMaxDate.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")}"


    }


    private fun changeFromAndTo() {

        when (viewModel.filterType) {
            DASHBOARD_FILTER_TYPE.DAY -> {
                binding.tvFrom.visible()
                binding.tvTo.gone()
                binding.tvFrom.text = viewModel.selectedDay
            }

            DASHBOARD_FILTER_TYPE.WEEK -> {
                binding.tvFrom.visible()
                binding.tvTo.visible()
                viewModel.minDate = getLastWeekDate(getCurrentDate()).createDate("yyyy-MM-dd")
                    .getStringDate("yyyy-MM-dd")
                viewModel.maxDate =
                    (getCurrentDate().createDate("yyyy-MM-dd").getStringDate("yyyy-MM-dd"))
                binding.tvTo.text = viewModel.maxDate
                binding.tvFrom.text = viewModel.minDate
            }

            DASHBOARD_FILTER_TYPE.MONTH -> {
                binding.tvFrom.visible()
                binding.tvTo.visible()
                viewModel.minDate = getLastMOnthDate(getCurrentDate()).createDate("yyyy-MM-dd")
                    .getStringDate("yyyy-MM-dd")
                viewModel.maxDate =
                    (getCurrentDate().createDate("yyyy-MM-dd").getStringDate("yyyy-MM-dd"))
                binding.tvTo.text = viewModel.maxDate
                binding.tvFrom.text = viewModel.minDate
            }

            DASHBOARD_FILTER_TYPE.ALL -> {
                binding.tvFrom.gone()
                binding.tvTo.gone()
            }

        }
    }


    private fun setObservers() {

        viewModel.totalEarningLiveData.observe(viewLifecycleOwner) {

            binding.tvTotalEarnings.text = "₹ ${it.totalEarning}"
            var todayEarnings = 0f
            it.courseEarningDates?.forEach { todayEarnings = todayEarnings + it.todayEarning }
            binding.tvDate.text = "₹ ${it.totalRangeEarning}"
            initEarningPieChart(it)
        }

        viewModel.audienceCountLiveData.observe(viewLifecycleOwner) {

            binding.tvUserCount.text = isLessThan9(it.totaluseR_COUNT ?: 0)
            binding.tvCourseCount.text = isLessThan9(it.totalcoursE_COUNT ?: 0)
            binding.tvVideoCount.text = isLessThan9(it.totalvideoS_COUNT ?: 0)

            binding.clCourse.contentDescription = "Total courses are ${it.totalcoursE_COUNT}"
            binding.clUser.contentDescription = "Total users are ${it.totaluseR_COUNT}"
            binding.clVideo.contentDescription = "Total videos are ${it.totalvideoS_COUNT}"
        }

        viewModel.courseUserCountLiveData.observe(viewLifecycleOwner) {
            inActiveCourseCount = it.inactivecoursE_COUNT ?: 0
            completedCount = it.completedcoursE_COUNT ?: 0
            inProgressCount = it.inprogresscoursE_COUNT ?: 0
            binding.tvCourseUsers.text =
                "${isLessThan9(inActiveCourseCount + completedCount + inProgressCount)}"
            binding.tvInactive.text =
                "${getString(R.string.inactive)} (${isLessThan9(it.inactivecoursE_COUNT ?: 0)})"
            binding.tvCompleted.text =
                "${getString(R.string.completed)} (${isLessThan9(it.completedcoursE_COUNT ?: 0)})"
            binding.tvInprogress.text =
                "${getString(R.string.in_progress)} (${isLessThan9(it.inprogresscoursE_COUNT ?: 0)})"

            initUsersPieChart()


        }

        viewModel.filteredCourseUserLiveData.observe(viewLifecycleOwner) {

            initBarChart(it)
        }
    }


    private fun resetChart() {
        binding.barChart.apply {
            fitScreen()
            data?.clearValues()
            xAxis.valueFormatter = null
            notifyDataSetChanged()
            clear()
            invalidate()
        }

    }

    private fun initBarChart(barData: CreatorCourseUserCountWithFilter) {
        setData(5, 500f, barData)
        if (!barData.list.isNullOrEmpty()) {
            binding.tvConstBarInner.visible()
            binding.tvNoUsersEnrolled.gone()
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
//            xAxis.granularity = 1f // only intervals of 1 day
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);

                xAxis.labelCount = 7
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {

                        return ""
                    }

                    override fun getBarLabel(barEntry: BarEntry?): String {
                        return super.getBarLabel(barEntry)

                    }
                }

                val leftAxis: YAxis = axisLeft
//            leftAxis.typeface = tfLight
//            leftAxis.setLabelCount(6, false)
                leftAxis.granularity = 1f
                leftAxis.isGranularityEnabled = true

                leftAxis.valueFormatter = object : ValueFormatter() {

                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        return "${value.toInt()}"
                    }


                }
                leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
                leftAxis.spaceTop = 15f
                leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


//            legend.isEnabled=false
            }
        } else {
            binding.tvConstBarInner.gone()
            binding.tvNoUsersEnrolled.visible()
        }

    }

    private fun initUsersPieChart() {
        val pieEntries: java.util.ArrayList<PieEntry> = java.util.ArrayList()
        val label = ""

        val colors: java.util.ArrayList<Int> = java.util.ArrayList()
        if (completedCount > 0 || inActiveCourseCount > 0 || inProgressCount > 0) {
            binding.usersPieChart.apply {

                binding.tvNoUsersChart.gone()
                binding.usersPieChart.visible()

                if (inActiveCourseCount > 0) {
                    colors.add(baseActivity.getAttrResource(R.attr.graph_red))
                    pieEntries.add(PieEntry((inActiveCourseCount).toFloat()))
                }

                if (completedCount > 0) {
                    colors.add(baseActivity.getAttrResource(R.attr.graph_green))
                    pieEntries.add(PieEntry((completedCount).toFloat()))
                }

                if (inProgressCount > 0) {
                    colors.add(baseActivity.getAttrResource(R.attr.graph_yellow))
                    pieEntries.add(PieEntry((inProgressCount).toFloat()))
                }
                val pieDataSet = PieDataSet(pieEntries, label)
                pieDataSet.valueTextSize = 10f
                pieDataSet.colors = colors
                pieDataSet.valueTextColor = context.getAttrResource(R.attr.whiteTextColor)
                val pieData = PieData(pieDataSet)
                pieData.setDrawValues(true)
                val vf: ValueFormatter = object : ValueFormatter() {
                    //value format here, here is the overridden method
                    override fun getFormattedValue(value: Float): String {
                        return "" + isLessThan9(value.toInt())
                    }
                }
                pieData.setValueFormatter(vf)
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
        } else {
            binding.usersPieChart.clear()
            pieEntries.clear()
            colors.clear()
            binding.tvNoUsersChart.visible()
            binding.usersPieChart.gone()
        }

    }

    private fun initEarningPieChart(creatorTotalEarnings: CreatorTotalEarnings) {

        var todayEarnings = 0f
        creatorTotalEarnings.courseEarningDates?.forEach {
            todayEarnings = todayEarnings + it.todayEarning
        }

        if ((creatorTotalEarnings.totalEarning ?: 0f) > 0f) {
            binding.tvNoEranings.gone()
            binding.earningPieChart.visible()
            binding.earningPieChart.apply {

                val pieEntries: ArrayList<PieEntry> = ArrayList()
                val label = ""
                val typeAmountMap: MutableMap<String, Int> = HashMap()
                typeAmountMap["Main"] = creatorTotalEarnings.totalRangeEarning?.toInt() ?: 0
                typeAmountMap["Less"] =
                    (creatorTotalEarnings.totalEarning?.toInt()
                        ?: 0) - (creatorTotalEarnings.totalRangeEarning?.toInt() ?: 0)
                val colors: ArrayList<Int> = ArrayList()
                colors.add(baseActivity.getAttrResource(R.attr.graph_green))
                colors.add(Color.parseColor("#D1D4D9"))
                for (type in typeAmountMap.keys) {
                    pieEntries.add(PieEntry(typeAmountMap[type]?.toFloat() ?: 0f, ""))
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
        } else {
            binding.tvNoEranings.visible()
            binding.earningPieChart.gone()
        }
    }

    private fun setData(count: Int, range: Float, barData: CreatorCourseUserCountWithFilter) {

        resetChart()
        val colors: java.util.ArrayList<Int> = java.util.ArrayList()
        context?.let { colors.add(it.getAttrResource(R.attr.graph_red)) }
        context?.let { colors.add(it.getAttrResource(R.attr.graph_yellow)) }
        context?.let { colors.add(it.getAttrResource(R.attr.graph_green)) }
        context?.let { colors.add(it.getAttrResource(R.attr.colorVariant_DarkPink)) }
        context?.let { colors.add(it.getAttrResource(R.attr.colorVariant_DarkBlue)) }


        val dataSets: ArrayList<IBarDataSet> = ArrayList()


        barData.list?.distinctBy { element -> element.courseId }?.forEachIndexed { index, element ->
            val values: ArrayList<BarEntry> = ArrayList()
            values.clear();
            values.add(BarEntry(index.toFloat(), element.totalEnrolledUser.toFloat()))
            var set1 = BarDataSet(values, "")
            set1.setDrawIcons(false)
            set1.setDrawValues(false)
            if (index < colors.size)
                set1.color = colors[index]
            else
                set1.color = ThemeUtils.getAppColor(baseActivity)
            dataSets.add(set1)
        }

//        values2.add(BarEntry(1.toFloat(),4.toFloat()))
//
//
//
//        var  set2 = BarDataSet(values2, "")
//        set2.setDrawIcons(false)
//        set2.setDrawValues(false)
//        set2.color = context?.getAttrResource(R.attr.accentColor_Black) ?: ThemeUtils.getAppColor(baseActivity)


        val data = BarData(dataSets)
        data.setValueTextSize(10f)
//            data.setValueTypeface(tfLight)
        data.barWidth = 0.4f


        val l: Legend = binding.barChart.getLegend()
        l.isEnabled = true
        var legendEntry = arrayListOf<LegendEntry>()

        barData.list?.distinctBy { element -> element.courseId }?.forEachIndexed { index, element ->
            var color: Int
            if (index < colors.size)
                color = colors[index]
            else
                color = ThemeUtils.getAppColor(baseActivity)

            legendEntry.add(
                LegendEntry(
                    element.courseTitle.getDotsAfterTwentyChars(),
                    Legend.LegendForm.DEFAULT,
                    Float.Companion.NaN,
                    Float.Companion.NaN,
                    null,
                    color
                )
            )
        }

        l.setCustom(legendEntry)
        l.setWordWrapEnabled(true);

        binding.barChart.data = data
    }

    override fun getLayoutRes() = R.layout.fragment_dash_creater
    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                DialogType.CREATOR_DASH_FILTER -> {

                    agePositionInFilter = items[3] as Int
                    professionPosInFilter = items[2] as Int
                    viewModel.ageProfessionFilterData = items[1] as GetReviewsRequestModel
                    binding.ivRed.visibleView(!viewModel.ageProfessionFilterData.searchFields.isNullOrEmpty())
                    viewModel.getFilteredCourseUsersCount()
                    viewModel.getCourseUserCount()
                    viewModel.totalEarnings()

                }
            }
        }
    }

}