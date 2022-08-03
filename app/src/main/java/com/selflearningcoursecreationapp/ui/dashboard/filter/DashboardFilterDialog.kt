package com.selflearningcoursecreationapp.ui.dashboard.filter

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogFilterBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant

@SuppressLint("NotifyDataSetChanged")
class DashboardFilterDialog : BaseBottomSheetDialog<BottomDialogFilterBinding>(),
    BaseAdapter.IViewClick {

    private var selectedId: Int = 0
    private var professionList: ArrayList<CategoryData> = ArrayList()
    private var ageList: ArrayList<CategoryData> = ArrayList()
    private var professionAdapter: FilterAdapter? = null
    private var ageAdapter: FilterAdapter? = null
    override fun getLayoutRes() = R.layout.bottom_dialog_filter
    override fun initUi() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        arguments?.let {

            if (it.containsKey("id")) {
                selectedId = it.getInt("id")
            }

        }
        professionList.clear()
        professionList.add(CategoryData("All(400)"))
        professionList.add(CategoryData("Student(80)"))
        professionList.add(CategoryData("Working Professional (150)"))
        professionList.add(CategoryData("Pursuing Graduation (170)"))
        ageList.clear()
        ageList.add(CategoryData("18-20 yrs old"))
        ageList.add(CategoryData("20-22 yrs old"))
        ageList.add(CategoryData("22-24 yrs old"))
        ageList.add(CategoryData("25+ yrs old"))
        setProfessionAdapter()
        setAgeAdapter()

        binding.btnApply.setOnClickListener {
            dismiss()
        }

    }


    private fun setProfessionAdapter() {
        professionAdapter?.notifyDataSetChanged() ?: kotlin.run {
            professionAdapter = FilterAdapter(professionList, 1)
            binding.rvProfession.adapter = professionAdapter
            professionAdapter!!.setOnAdapterItemClickListener(this)
        }
    }

    private fun setAgeAdapter() {
        ageAdapter?.notifyDataSetChanged() ?: kotlin.run {
            ageAdapter = FilterAdapter(ageList, 2)
            binding.rvAge.adapter = ageAdapter
            ageAdapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {

        if (items.isNotEmpty()) {
            val clickType = items[0] as Int
            val position = items[1] as Int
            val listType = items[2] as Int
            when (clickType) {
                Constant.CLICK_VIEW -> {


                    when (listType) {

                        1 -> {
                            professionList.forEach {
                                it.isSelected = false
                            }
                            professionList[position].isSelected = true
                            setProfessionAdapter()
                        }
                        2 -> {
                            ageList.forEach {
                                it.isSelected = false
                            }
                            ageList[position].isSelected = true
                            setAgeAdapter()
                        }
                    }

                }
            }
        }
    }
}