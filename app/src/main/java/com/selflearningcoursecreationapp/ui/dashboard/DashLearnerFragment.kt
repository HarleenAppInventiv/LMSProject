package com.selflearningcoursecreationapp.ui.dashboard

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDashLearnerBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.DASHBOARD_FILTER_TYPE
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import com.selflearningcoursecreationapp.utils.isLessThan9
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit


class DashLearnerFragment : BaseFragment<FragmentDashLearnerBinding>() {

    private val viewModel: LearnerDashboardVM by viewModel()
    var todoCount = 0
    var doneCount = 0
    var inProgressCount = 0
    var filterList = arrayListOf<String>("Day", "Week", "Month", "All")
    var adapter: ArrayAdapter<Any?>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {

        adapter?.notifyDataSetChanged()
        adapter = null

        changeFromAndTo()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeCourseStatCount()
        viewModel.requestCount()
//        viewModel.getActivityTrackTime()
        odserveFilterCount()

        viewModel.getFilteredCount()
        setFilterAdapter()

        setTabLayout()
//        setUpUpperChart(it.totalRequestedHours, it.totalActivityHours)


        binding.tvFrom.setOnClickListener {
//            var calendarMin= Calendar.getInstance()
//
//            if(selectedFilter==1) calendarMin.time=  getLastWeekDate(getCurrentDate()).createDate("yyyy-MM-dd")
//            else calendarMin.time=  getLastMOnthDate(getCurrentDate()).createDate("yyyy-MM-dd")

            val calendarMax = Calendar.getInstance()

            if (viewModel.filterType == 2) calendarMax.time =
                getCurrentDate().createDate("yyyy-MM-dd")



            baseActivity.openDatePickerDialog(setMaxDate = true, maxDate = calendarMax) {
                viewModel.minDate = it.time.getStringDate("yyyy-MM-dd")

                if (viewModel.filterType == DASHBOARD_FILTER_TYPE.DAY) {
                    viewModel.selectedDay = it.time.getStringDate("yyyy-MM-dd")
                    binding.tvFrom.text = viewModel.selectedDay
                    binding.tvDate.text =
                        "${viewModel.selectedDay.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")} "
                    viewModel.maxDate = viewModel.selectedDay
                    viewModel.getActivityTrackTime(false)
//                    viewModel.selectedDay= LocalToGMT(it.time)

                } else if (viewModel.filterType == DASHBOARD_FILTER_TYPE.WEEK) {
                    viewModel.maxDate = getNextXDays(it.time.getStringDate("yyyy-MM-dd"), 7)
                    binding.tvDate.text = "${
                        viewModel.minDate.changeDateFormat(
                            "yyyy-MM-dd",
                            "dd MMM yyyy"
                        )
                    } To ${viewModel.maxDate.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")}"
                    binding.tvFrom.setText(viewModel.minDate)
                    viewModel.getActivityTrackTime(false)

                } else {
                    viewModel.maxDate = getNextMOnthDate(it.time.getStringDate("yyyy-MM-dd"))
                    binding.tvDate.text = "${
                        viewModel.minDate.changeDateFormat(
                            "yyyy-MM-dd",
                            "MMM yyyy"
                        )
                    } To ${viewModel.maxDate.changeDateFormat("yyyy-MM-dd", "MMM yyyy")}"
                    binding.tvFrom.setText(viewModel.minDate)
                    viewModel.getActivityTrackTime(false)
                }

                binding.tvTo.setText(viewModel.maxDate)

                viewModel.getFilteredCount()
//                binding.vpCourses.currentItem = 2
                viewModel.refreshData.value = true
            }
        }

    }

    private fun odserveFilterCount() {
        viewModel.filteredRequestCountLiveData.observe(viewLifecycleOwner) {
            todoCount = it.todocoursE_COUNT!!
            doneCount = it.donecoursE_COUNT!!
            inProgressCount = it.inprogresscoursE_COUNT!!
            binding.tvTodo.text = "${getString(R.string.todo)} (${isLessThan9(todoCount)})"
            binding.tvDone.text = "${getString(R.string.done)} (${isLessThan9(doneCount)})"
            binding.tvInProgress.text =
                "${getString(R.string.in_progress)} (${isLessThan9(inProgressCount)})"
            binding.tvTotalEnrolledCourse.text =
                (isLessThan9(todoCount!! + doneCount!! + inProgressCount!!))

            val pieEntries: ArrayList<PieEntry> = ArrayList()
            val label = ""

            val colors: ArrayList<Int> = ArrayList()
            if (todoCount > 0 || doneCount > 0 || inProgressCount > 0) {
                binding.tvNoChart.gone()
                binding.pieChartView2.visible()
                binding.pieChartView2.apply {


                    if (todoCount > 0) {
                        colors.add(baseActivity.getAttrResource(R.attr.graph_red))
                        pieEntries.add(PieEntry((todoCount).toFloat()))
                    }

                    if (doneCount > 0) {
                        colors.add(baseActivity.getAttrResource(R.attr.graph_green))
                        pieEntries.add(PieEntry((doneCount).toFloat()))
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
                            return "" + value.toInt().formatCountString()
                        }
                    }
                    pieData.setValueFormatter(vf)

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
                    setEntryLabelTextSize(12f)
                    rotationAngle = 0f
                    isRotationEnabled = true
                    isHighlightPerTapEnabled = true
                    animateY(1400, Easing.EaseInOutQuad)

                    setEntryLabelColor(Color.WHITE)
                    setEntryLabelTextSize(12f)
                    invalidate()

                }
            } else {
                binding.pieChartView2.clear()
                pieEntries.clear()
                colors.clear()
                binding.tvNoChart.visible()
                binding.pieChartView2.gone()
            }


        }
    }


    override fun onResume() {
        super.onResume()
//        viewModel.refreshData.value = true
        setFilterAdapter()

    }


    private fun setFilterAdapter() {
        adapter = ArrayAdapter<Any?>(
            baseActivity,
            android.R.layout.simple_list_item_1,
            filterList as List<Any?>
        )
        binding.tvSelectDay.setAdapter(adapter)
        binding.tvSelectDay.setSelection(3)
        binding.tvSelectDay.setOnTouchListener { view, motionEvent ->
            binding.tvSelectDay.showDropDown()
            return@setOnTouchListener false
        }
        binding.tvSelectDay.setDropDownBackgroundResource(R.drawable.edt_white_bg)
        binding.tvSelectDay.setOnItemClickListener { adapterView, view, i, l ->

            viewModel.selectedDay = getCurrentDate()
            viewModel.filterType = i
            changeFromAndTo()
            viewModel.getFilteredCount()
            viewModel.refreshData.value = true


        }
    }

    private fun changeFromAndTo() {
        when (viewModel.filterType) {
            DASHBOARD_FILTER_TYPE.DAY -> {
                binding.tvFrom.visible()
                binding.tvTo.gone()
                binding.tvDay.text = getString(R.string.day) + " :"
                binding.tvFrom.text = viewModel.selectedDay
                binding.tvDate.text =
                    viewModel.selectedDay.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")
                viewModel.minDate = viewModel.selectedDay
                viewModel.maxDate = viewModel.selectedDay
                viewModel.getActivityTrackTime(false)

            }

            DASHBOARD_FILTER_TYPE.WEEK -> {
                binding.tvDay.text = getString(R.string.week) + " :"
                binding.tvFrom.visible()
                binding.tvTo.visible()
                viewModel.minDate = getLastWeekDate(getCurrentDate()).createDate("yyyy-MM-dd")
                    .getStringDate("yyyy-MM-dd")
                viewModel.maxDate =
                    (getCurrentDate().createDate("yyyy-MM-dd").getStringDate("yyyy-MM-dd"))
                binding.tvTo.text = viewModel.maxDate
                binding.tvFrom.text = viewModel.minDate
                binding.tvDate.text =
                    "${viewModel.minDate.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")} To " +
                            "${viewModel.maxDate.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")}"

                viewModel.getActivityTrackTime(false)

            }
            DASHBOARD_FILTER_TYPE.MONTH -> {
                binding.tvDay.text = getString(R.string.month) + " :"
                binding.tvFrom.visible()
                binding.tvTo.visible()
                viewModel.minDate = getLastMOnthDate(getCurrentDate()).createDate("yyyy-MM-dd")
                    .getStringDate("yyyy-MM-dd")
                viewModel.maxDate =
                    (getCurrentDate().createDate("yyyy-MM-dd").getStringDate("yyyy-MM-dd"))
                binding.tvTo.text = viewModel.maxDate
                binding.tvFrom.text = viewModel.minDate
                binding.tvDate.text = "${
                    viewModel.minDate.changeDateFormat(
                        "yyyy-MM-dd",
                        "MMM yyyy"
                    )
                } To ${viewModel.maxDate.changeDateFormat("yyyy-MM-dd", "MMM yyyy")}"

                viewModel.getActivityTrackTime(false)

            }
            DASHBOARD_FILTER_TYPE.ALL -> {
                binding.tvFrom.gone()
                binding.tvTo.gone()
                binding.tvDay.text = getString(R.string.all) + " :"
                viewModel.getActivityTrackTime(isToday = false, isAllSelected = true)

            }
        }


    }


    private fun setUpUpperChart(totalRequestedHours: Long?, totalActivityHours: Long?) {

        val hms = String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalActivityHours ?: 0),
            TimeUnit.MILLISECONDS.toMinutes(totalActivityHours ?: 0) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    totalActivityHours ?: 0
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(totalActivityHours ?: 0) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    totalActivityHours ?: 0
                )
            )
        )
        binding.tvActivityValue.text = hms
        binding.tvHours.text = "$hms"
        binding.pieChartView.apply {

            val pieEntries: ArrayList<PieEntry> = ArrayList()
            val label = ""
            val typeAmountMap: MutableMap<String, Float> = HashMap()

            val f = totalActivityHours?.toFloat()?.div(totalRequestedHours?.toFloat() ?: 0f)
                ?.times(100.0)?.toFloat() ?: 0f
            typeAmountMap["Main"] = f
            typeAmountMap["Less"] = 100f - f
            val colors: ArrayList<Int> = ArrayList()
            colors.add(
                baseActivity.getAttrResource(R.attr.accentColor_Green)
            )
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
    }

    private fun setTabLayout() {
        var bundle = Bundle()
        bundle.putString("minDate", viewModel.minDate)
        bundle.putString("maxDate", viewModel.maxDate)
        bundle.putInt("filterType", viewModel.filterType)
        bundle.putString("selectedDay", viewModel.selectedDay)

        var inProgressFrag = InProgressFragment()
        var todoFrag = TodoFragment()
        var doneFrag = DoneFragment()

        inProgressFrag.arguments = bundle
        todoFrag.arguments = bundle
        doneFrag.arguments = bundle

        val list = arrayListOf<Fragment>(
            inProgressFrag, todoFrag, doneFrag
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.in_progress),
            baseActivity.getString(R.string.todo),
            baseActivity.getString(R.string.done),
        )
        binding.vpCourses.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)

//        binding.vpCourses.offscreenPageLimit=2


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


    private fun observeCourseStatCount() {

        viewModel.requestCountLiveData.observe(viewLifecycleOwner) {

            binding.LMSTextView5.text = it.completecoursE_COUNT?.formatCountString()
            binding.LMSTextView8.text = it.enrolledcoursE_COUNT?.formatCountString()
            binding.LMSTextView11.text = it.favouritecoursE_COUNT?.formatCountString()

            binding.ivEnrolledCourse.contentDescription =
                "Total enrolled courses are ${it.enrolledcoursE_COUNT?.formatCountString()}"
            binding.ivFavouriteCourse.contentDescription =
                "Total favourite courses are ${it.favouritecoursE_COUNT?.formatCountString()}"
            binding.ivCompletedCourse.contentDescription =
                "Total complete courses are ${it.completecoursE_COUNT?.formatCountString()}"


        }

        viewModel.activityTracerLiveData.observe(viewLifecycleOwner) {
            when (viewModel.filterType) {

                DASHBOARD_FILTER_TYPE.ALL -> {
                    binding.tvDate.text =
                        "${
                            it.startDate?.let { it1 ->
                                getNextDay(
                                    it1,
                                    sourceFormat = "yyyy-MM-dd'T'hh:mm:ss"
                                ).changeDateFormat(outputFormat = "dd MMM yyyy")
                            }
                        } To ${
                            it.endDate.changeDateFormat(outputFormat = "dd MMM yyyy")
                        }"

                }
            }
            setUpUpperChart(it.totalRequestedHours, it.totalActivityHours)


        }


    }

    private fun Int.formatCountString(): String {
        if (this < 10) return "0${this}"
        else return "${this}"

    }

    override fun getLayoutRes() = R.layout.fragment_dash_learner
    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}