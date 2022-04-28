package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAddCourseBaseBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.ui.splash.intro_slider.DotAdapter
import com.selflearningcoursecreationapp.utils.Constant


class AddCourseBaseFragment : BaseFragment<FragmentAddCourseBaseBinding>() {
    private var type: Int = TYPE_ALL
    private var dotList: ArrayList<Boolean> = ArrayList()
    private var dotAdapter: DotAdapter? = null
    private val viewModel: AddCourseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {

        initViewPager()
        setHasOptionsMenu(true)
        binding.btContinue.setOnClickListener {
            findNavController().navigate(R.id.action_addCourseBaseFragment_to_addSectionOrLectureFragment)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun getLayoutRes() = R.layout.fragment_add_course_base
    private fun initViewPager() {
        val list = ArrayList<Fragment>()
        val fragList = arrayListOf(
            Step1Fragment(),
            Step2Fragment(),
            Step3Fragment(),
        )
        if (type == TYPE_ALL) {
            list.addAll(fragList)
        } else {
            list.add(fragList[type])
        }
        binding.rvPagerDot.visibleView(type == TYPE_ALL)

        val adapter = ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        binding.vpAddCourses.adapter = adapter
        dotList.clear()
        dotList.addAll(list.map { false })
        dotList.set(0, true)


        setDotAdapter()

        binding.vpAddCourses.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                dotList.forEachIndexed { index, _ ->
                    dotList[index] = index == position
                }
                setDotAdapter()


            }
        })
    }

    private fun setDotAdapter() {
        dotAdapter?.notifyDataSetChanged() ?: kotlin.run {
            dotAdapter = DotAdapter(Constant.TYPE_ROUND, dotList)
            binding.rvPagerDot.adapter = dotAdapter
        }
    }


    companion object {
        const val TYPE_CATEGORY = 0
        const val TYPE_THEME = 1
        const val TYPE_FONT = 2
        const val TYPE_LANGUAGE = 3
        const val TYPE_ALL = -1
    }

}