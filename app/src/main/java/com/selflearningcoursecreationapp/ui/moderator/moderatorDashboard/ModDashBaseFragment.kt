package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

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
import com.selflearningcoursecreationapp.databinding.FragmentModeratorDashboardBaseBinding
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class ModDashBaseFragment : BaseFragment<FragmentModeratorDashboardBaseBinding>() {

    override fun getLayoutRes() = R.layout.fragment_moderator_dashboard_base
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {


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
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            invalidate()

        }

        val list = arrayListOf<Fragment>(
            ReviewedFragment(),
            PendingFragment(),
            RejectedModFragment(),
            AddCommentFragment()
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.reviewed),
            baseActivity.getString(R.string.pending),
            baseActivity.getString(R.string.rejected),
            baseActivity.getString(R.string.addedComments),
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

    override fun onApiRetry(apiCode: String) {
    }

}