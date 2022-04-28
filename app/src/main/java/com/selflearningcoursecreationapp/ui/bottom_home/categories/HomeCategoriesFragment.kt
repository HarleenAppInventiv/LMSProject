package com.selflearningcoursecreationapp.ui.bottom_home.categories

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentHomeCategoriesBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.ui.preferences.category.CategoryAdapter
import com.selflearningcoursecreationapp.utils.Constant

class HomeCategoriesFragment : BaseFragment<FragmentHomeCategoriesBinding>(),
    BaseAdapter.IViewClick {
    private var preferredList: ArrayList<CategoryData> = ArrayList()
    private var allList: ArrayList<CategoryData> = ArrayList()
    private var preferredAdapter: CategoryAdapter? = null
    private var allAdapter: CategoryAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_home_categories
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    private fun initUi() {
        preferredList.clear()
        preferredList.add(CategoryData("Science"))
        preferredList.add(CategoryData("Maths"))
        preferredList.add(CategoryData("Geography"))
        allList.clear()
        allList.add(CategoryData("Chemistry"))
        allList.add(CategoryData("Biology"))
        allList.add(CategoryData("Finance"))
        allList.add(CategoryData("Sports"))
        setPreferredAdapter()
        setAllAdapter()
    }

    private fun setPreferredAdapter() {
        preferredAdapter?.notifyDataSetChanged() ?: kotlin.run {
            preferredAdapter = CategoryAdapter(preferredList, true, 1)
            binding.rvPreferred.adapter = preferredAdapter
            preferredAdapter!!.setOnAdapterItemClickListener(this)
        }
    }

    private fun setAllAdapter() {
        allAdapter?.notifyDataSetChanged() ?: kotlin.run {
            allAdapter = CategoryAdapter(allList, true, 2)
            binding.rvOther.adapter = allAdapter
            allAdapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val clickType = items[0] as Int
            val position = items[1] as Int
            val listType = items[2] as Int
            when (clickType) {
                Constant.CLICK_VIEW -> {
                    allList.forEach {
                        it.isSelected = false
                    }
                    preferredList.forEach {
                        it.isSelected = false
                    }
                    when (listType) {

                        1 -> {

                            preferredList[position].isSelected = true
                        }
                        2 -> {
                            allList[position].isSelected = true
                        }
                    }
                    setAllAdapter()
                    setPreferredAdapter()
                }
            }
        }
    }

}