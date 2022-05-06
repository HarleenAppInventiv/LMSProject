package com.selflearningcoursecreationapp.ui.bottom_home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentHomeBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>(), HandleClick, BaseAdapter.IViewClick {
    val gson: Gson? = null
    private val viewModel: HomeVM by viewModel()
    private var homeList = ArrayList<String>()

    var list = arrayListOf<CategoryData>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity.supportActionBar?.hide()

    }

    override fun onStart() {
        super.onStart()
        baseActivity.supportActionBar?.hide()

    }

    fun initUI() {
        binding.homefrag = this
        list.clear()

        homeList.clear()
        homeList.apply {
            add("Popular Courses")
            add("Recent Courses")

        }
        binding.recyclerCourseType.apply {
            list.add(CategoryData("Arts", isSelected = true))
            list.add(CategoryData("Science", isSelected = false))
            list.add(CategoryData("Biology", isSelected = false))
            list.add(CategoryData("Commerce", isSelected = false))
            list.add(CategoryData("Design", isSelected = false))


            adapter = MyAdapter(this@HomeFragment).also {
                it.submitList(list)
                it.notifyDataSetChanged()
            }

        }
        // adapterCourseType.setOnAdapterItemClickListener(this)

        binding.rvList.adapter = HomeAdapter(homeList)
        (binding.rvList.adapter as HomeAdapter).setOnAdapterItemClickListener(this)


        val color = ThemeUtils.getAppColor(baseActivity)
        binding.toolbarLayout.setContentScrimColor(color);
        binding.toolbarLayout.setBackgroundColor(color)
        binding.toolbarLayout.setStatusBarScrimColor(color)
        binding.appBar.setBackgroundColor(color)
    }


    override fun getLayoutRes() = R.layout.fragment_home


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.iv_user_image -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileThumbFragment)
                }
                R.id.tv_user_name -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileThumbFragment)

                }
                R.id.tv_see_all -> {
                    findNavController().navigate(R.id.action_homeFragment_to_homeCategoriesFragment)

                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getUserData()
        binding.tvUserName.text = viewModel.userProfile?.name

    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int

            when (type) {
                Constant.CLICK_VIEW -> {
                    list.forEach {
                        it.isSelected = false
                    }
                    list[position].isSelected = true
                    binding.recyclerCourseType?.run {
                        adapter?.notifyDataSetChanged()

                    }
//                    showToastShort(text)
                }
                Constant.CLICK_SEE_ALL -> {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_popularFragment,
                        bundleOf("subTitle" to "Science . Chemistry", "title" to homeList[position])
                    )
                }
            }

        }
    }


}
