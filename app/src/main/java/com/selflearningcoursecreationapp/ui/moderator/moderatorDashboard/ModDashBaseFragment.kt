package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

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
import com.selflearningcoursecreationapp.databinding.FragmentModeratorDashboardBaseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.DASHBOARD_FILTER_TYPE
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import com.selflearningcoursecreationapp.utils.isLessThan9
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class ModDashBaseFragment() : BaseFragment<FragmentModeratorDashboardBaseBinding>() {

    private val viewModel: ModDashboardVM by viewModel()
    var acceptedCount = 0
    var rejectedCount = 0
    var pendingCount = 0

    //    var filterList = arrayListOf<String>("Day","Week","Month","All")
//    var filterList =resources.getStringArray(R.array.filter_array)
    private var filterList: ArrayList<String> = ArrayList()
    var adapter: ArrayAdapter<Any?>? = null

    override fun getLayoutRes() = R.layout.fragment_moderator_dashboard_base
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()
    }

    override fun onResume() {
        super.onResume()
        setFilterAdapter()
    }

    private fun initUI() {
        adapter?.notifyDataSetChanged()
        adapter = null
        changeFromAndTo()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeCourseStatCount()
        viewModel.requestCount()
        odserveFilterCount()

        resources.getStringArray(R.array.filter_array).forEach {
            filterList?.add(it)
        }
        viewModel.getFilteredCount()
        setFilterAdapter()
        setTabLayout()

        binding.tvFrom.setOnClickListener {
            var calendarMax = Calendar.getInstance()

            if (viewModel.filterType == DASHBOARD_FILTER_TYPE.MONTH) calendarMax.time =
                getCurrentDate().createDate("yyyy-MM-dd")

            baseActivity.openDatePickerDialog(setMaxDate = true, maxDate = calendarMax) {
                viewModel.minDate = it.time.getStringDate("yyyy-MM-dd")

                if (viewModel.filterType == DASHBOARD_FILTER_TYPE.DAY) {
                    viewModel.selectedDay = it.time.getStringDate("yyyy-MM-dd")
                    binding.tvFrom.text = viewModel.selectedDay
                } else if (viewModel.filterType == DASHBOARD_FILTER_TYPE.WEEK) {
                    viewModel.maxDate = getNextXDays(it.time.getStringDate("yyyy-MM-dd"), 7)
                    binding.tvFrom.setText(viewModel.minDate)
                } else {
                    viewModel.maxDate = getNextMOnthDate(it.time.getStringDate("yyyy-MM-dd"))
                    binding.tvFrom.setText(viewModel.minDate)
                }

                binding.tvTo.setText(viewModel.maxDate)

                viewModel.getFilteredCount()
                viewModel.refreshData.value = true

            }
        }

    }

    private fun odserveFilterCount() {

        viewModel.filteredRequestCountLiveData.observe(viewLifecycleOwner) {
            acceptedCount = it.acceptedcoursE_COUNT ?: 0
            pendingCount = it.pendingcoursE_COUNT ?: 0
            rejectedCount = it.rejectedcoursE_COUNT ?: 0
            binding.tvAccepted.text =
                "${getString(R.string.accepted)} (${isLessThan9(acceptedCount)})"
            binding.tvRejected.text =
                "${getString(R.string.rejected)} (${isLessThan9(rejectedCount)})"
            binding.tvPending.text = "${getString(R.string.pending)} (${isLessThan9(pendingCount)})"



            binding.tvCourseUsers.text = (isLessThan9(
                (it.acceptedcoursE_COUNT ?: 0) + (it.rejectedcoursE_COUNT
                    ?: 0) + (it.pendingcoursE_COUNT ?: 0)
            ))

            val pieEntries: ArrayList<PieEntry> = ArrayList()
            val label = ""

            val colors: ArrayList<Int> = ArrayList()

            if (acceptedCount > 0 || rejectedCount > 0 || pendingCount > 0) {
                binding.tvNoChart.gone()
                binding.pieChartView2.visible()
                binding.pieChartView2.apply {


                    if (acceptedCount > 0) {
                        colors.add(baseActivity.getAttrResource(R.attr.graph_green))
                        pieEntries.add(PieEntry((acceptedCount).toFloat()))
                    }

                    if (pendingCount > 0) {
                        colors.add(baseActivity.getAttrResource(R.attr.graph_yellow))
                        pieEntries.add(PieEntry((pendingCount).toFloat()))
                    }

                    if (rejectedCount > 0) {
                        colors.add(baseActivity.getAttrResource(R.attr.graph_red))
                        pieEntries.add(PieEntry((rejectedCount).toFloat()))
                    }


                    val pieDataSet = PieDataSet(pieEntries, label)
                    pieDataSet.valueTextSize = 15f
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
//                dragDecelerationFrictionCoef = 0.95f
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

    private fun setTabLayout() {
        var bundle = Bundle()
        bundle.putString("minDate", viewModel.minDate)
        bundle.putString("maxDate", viewModel.maxDate)
        bundle.putInt("filterType", viewModel.filterType)
        bundle.putString("selectedDay", viewModel.selectedDay)

        val accepted = AcceptedFragment()
        val pending = PendingFragment()
        val rejected = RejectedModFragment()

        accepted.arguments = bundle
        pending.arguments = bundle
        rejected.arguments = bundle
        val list = arrayListOf<Fragment>(
            accepted,
            pending,
            rejected
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.accepted),
            baseActivity.getString(R.string.pending),
            baseActivity.getString(R.string.rejected)
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

    private fun setFilterAdapter() {

//        var arrayAdapter= ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_dropdown_item_1line, filterList as List<Any?>)
        adapter = ArrayAdapter(
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
            changeFromAndTo()
            viewModel.refreshData.value = true
            viewModel.getFilteredCount()

        }

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

    private fun observeCourseStatCount() {
        viewModel.requestCountLiveData.observe(viewLifecycleOwner) {


            binding.tvTotalAccepted.text = it.acceptedcoursE_COUNT?.formatCountString()
            binding.tvTotalPending.text = it.pendingcoursE_COUNT?.formatCountString()
            binding.tvTotalRejected.text = it.rejectedcoursE_COUNT?.formatCountString()

            binding.ivCompletedCourse.contentDescription =
                "Total accepted courses are ${it.acceptedcoursE_COUNT}"
            binding.ivEnrolledCourse.contentDescription =
                "Total pending courses are ${it.pendingcoursE_COUNT}"
            binding.ivFavouriteCourse.contentDescription =
                "Total rejected courses are ${it.rejectedcoursE_COUNT}"

        }
    }

    private fun Int.formatCountString(): String {
        if (this < 10) return "0${this}"
        else return "${this}"

    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}