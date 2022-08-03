package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModCourseDetailsBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.moderator.courseDetails.content.ModCourseContentFragment
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModCourseDetailsFragment : BaseFragment<FragmentModCourseDetailsBinding>() {
    private val viewModel: ModCourseDetailVM by viewModel()
    private var menu: Menu? = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_mod_course_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        initToolbar()
        initViewPager()
        scrollHandling()
        binding.viewModel = viewModel
        binding.tvTime.text =
            viewModel.courseData.value?.createdDate?.changeDateFormat(outputFormat = "dd MMM yyyy")
        setData(viewModel.courseData.value)

    }

    private fun scrollHandling() {

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            showLog("VERTICAL_OFFSET", "$verticalOffset")

            if (verticalOffset >= -725) {

                binding.toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        baseActivity,
                        android.R.color.transparent
                    )
                )
                binding.toolbar.title = ""
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(baseActivity, R.drawable.ic_arrow_left_white)
                menu?.findItem(R.id.action_read)?.icon?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(baseActivity, R.color.white),
                    PorterDuff.Mode.SRC_IN
                )
                menu?.findItem(R.id.action_edit)?.icon?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(baseActivity, R.color.white),
                    PorterDuff.Mode.SRC_IN
                )
            } else {

                binding.toolbar.title = viewModel.courseData.value?.courseTitle ?: ""
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(baseActivity, R.drawable.ic_arrow_left)

                binding.toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        baseActivity,
                        R.color.white
                    )
                )

                menu?.findItem(R.id.action_read)?.icon?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(baseActivity, R.color.text_color_black_131414),
                    PorterDuff.Mode.SRC_IN
                )
                menu?.findItem(R.id.action_edit)?.icon?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(baseActivity, R.color.text_color_black_131414),
                    PorterDuff.Mode.SRC_IN
                )
            }
        })
    }

    private fun initToolbar() {
        menu = binding.toolbar.menu
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun setData(data: CourseData?) {
        data?.let { courseData ->
            binding.ivCourseImage.loadImage(
                courseData.courseBannerUrl,
                R.drawable.ic_dummy_course
            )
            binding.rvAuthor.adapter =
                CourseAuthorAdapter(courseData.courseCoAuthors ?: ArrayList())
            binding.tvLevel.setComplexityLevel(data.courseComplexityId)

            binding.tvSectionCount.text =
                baseActivity.getQuantityString(R.plurals.section_quantity, courseData.totalSections)

            binding.tvDuration.text = courseData.courseDuration.getTime(baseActivity)
            ResizeableUtils.builder(binding.tvDescription)
                .isUnderline(false)
                .setFullText(courseData.getDescription())
                .setFullText(R.string.read_more)
                .showDots(true)
                .build()

        }
    }


    private fun initViewPager() {


        val list = arrayListOf<Fragment>(
            ModCourseContentFragment(),
            ModCourseAssessmentFragment()
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.course_content),
            baseActivity.getString(R.string.course_assessment)
        )
        binding.viewpager.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        TabLayoutMediator(binding.tlHeader, binding.viewpager) { tab, position ->
            tab.text = nameArray[position]


        }.attach()

        binding.tlHeader.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tlHeader.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )
        binding.tlHeader.setCustomTabs(nameArray)
        binding.tlHeader.getTabAt(0)?.customView?.isSelected = true
        binding.tlHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_SEMI_BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_MEDIUM)

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })
    }

    override fun onApiRetry(apiCode: String) {
        when (apiCode) {

        }

    }

}