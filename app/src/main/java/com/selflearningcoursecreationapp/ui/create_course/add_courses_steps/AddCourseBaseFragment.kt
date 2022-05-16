package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentAddCourseBaseBinding
import com.selflearningcoursecreationapp.extensions.invisible
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.AddSectionOrLectureFragment
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.ui.splash.intro_slider.DotAdapter
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddCourseBaseFragment : BaseFragment<FragmentAddCourseBaseBinding>() {
    private var authorMenu: MenuItem? = null
    private var dotList: ArrayList<Boolean> = ArrayList()
    private var dotAdapter: DotAdapter? = null
    private val viewModel: AddCourseViewModel by viewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        activityResultListener()
        resetDot()
        setDotAdapter()
        initViewPager()
        setHasOptionsMenu(true)
        binding.lifecycleOwner = parentFragment
        binding.viewModel = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getMasterData()
        binding.btContinue.setOnClickListener {
//            findNavController().navigate(R.id.action_addCourseBaseFragment_to_addQuizFragment)
            when (binding.vpAddCourses.currentItem) {
                0 -> viewModel.step1Validation()
                1 -> viewModel.step2Validation()
                else -> showToastShort("Implementation pending")
            }
        }
        binding.tvPrevious.setOnClickListener {
            if (binding.vpAddCourses.currentItem != 0) {
                binding.vpAddCourses.currentItem -= 1

                binding.tvPrevious.apply { if (binding.vpAddCourses.currentItem != 0) visible() else invisible() }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
        authorMenu = menu.findItem(R.id.add_co_author)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_co_author -> {
                showToastShort("types")
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun getLayoutRes() = R.layout.fragment_add_course_base
    private fun initViewPager() {
        val list = ArrayList<Fragment>()
        val fragList = arrayListOf(
            Step1Fragment(),
            Step2Fragment(),
            AddSectionOrLectureFragment(),

            )

        list.addAll(fragList)


        val adapter = ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        binding.vpAddCourses.adapter = adapter
        dotList.clear()
        resetDot()


        binding.vpAddCourses.isUserInputEnabled = false

        dotList.addAll(list.map { false })
        showLog("DOT", "selected one >> ${binding.vpAddCourses.currentItem}")
        dotList.set(binding.vpAddCourses.currentItem, true)
        setDotAdapter()
        binding.vpAddCourses.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.courseData.value =
                    viewModel.courseData.value?.apply { currentPage = position }
                authorMenu?.isVisible = position == 0

                dotList.forEachIndexed { index, _ ->
                    dotList[index] = index == position
                }
                lifecycleScope.launch {
                    delay(500)
                    baseActivity.runOnUiThread {
                        setDotAdapter()
                        binding.tvPrevious.apply { if (position != 0) visible() else invisible() }
                        binding.group.visibleView(position == 2)
                        setSectionCount()
                    }
                }


            }
        })
    }

    private fun resetDot() {

        dotAdapter?.notifyDataSetChanged()
        dotAdapter = null
    }

    private fun setDotAdapter() {
        dotList.forEachIndexed { index, b ->
            showLog("DOT_LIST", "position, $index >>> $b")

        }
        dotAdapter?.notifyDataSetChanged() ?: kotlin.run {
            dotAdapter = DotAdapter(Constant.TYPE_ROUND, dotList)
            binding.rvPagerDot.adapter = dotAdapter
        }
    }

    //    override fun onResume() {
//        super.onResume()
//        resetDot()
//        setDotAdapter()
//    }
    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_CRE_STEP_1 -> {
                (value as BaseResponse<UserProfile>)

                binding.tvPrevious.visible()
                binding.vpAddCourses.currentItem += 1

            }
            ApiEndPoints.API_CRE_STEP_2 -> {
                binding.vpAddCourses.currentItem += 1
            }
            ApiEndPoints.API_ADD_SECTION_POST -> {
                setSectionCount()
            }


        }
    }

    private fun setSectionCount() {
        val count: String =
            baseActivity.resources.getQuantityString(
                R.plurals.section_quantity,
                viewModel.users.size,
                viewModel.users.size
            )
        binding.tvSelectedValue.text = count
    }

    private fun activityResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            "valueHTML",
            viewLifecycleOwner
        ) { _, bundle ->
            val value = bundle.getString("value")
            val type = bundle.getInt("type")
            when (type) {
                Constant.DESC -> {
                    viewModel.courseData.value?.courseDescription = value ?: ""

                }
                Constant.KEY_TAKEAWAY -> {
                    viewModel.courseData.value?.keyTakeaways = value ?: ""

                }
            }
            viewModel.notifyData()
        }
    }

}