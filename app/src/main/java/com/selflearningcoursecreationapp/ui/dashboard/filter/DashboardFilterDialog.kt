package com.selflearningcoursecreationapp.ui.dashboard.filter

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogFilterBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.HomeFilterVM
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.dashboard.creator_dashboard.CreatorDashFilterVM
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.PROFESSION_FILTER
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("NotifyDataSetChanged")
class DashboardFilterDialog : BaseBottomSheetDialog<BottomDialogFilterBinding>(),
    BaseAdapter.IViewClick {
    private val viewModel2: HomeFilterVM by viewModel()
    private val viewModel: CreatorDashFilterVM by viewModel()
    private var selectedId: Int = 0
    private var professionList: ArrayList<CategoryData> = ArrayList()
    private var ageList: ArrayList<CategoryData> = ArrayList()
    private var professionAdapter: FilterAdapter? = null
    private var ageAdapter: FilterAdapter? = null

    var ageSelected = -1
    var professionSelected = -1

    override fun getLayoutRes() = R.layout.bottom_dialog_filter
    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getMasterData()
        observeData()

        binding.ivClose.setOnClickListener {
            dismiss()
        }
        arguments?.let {

            if (it.containsKey("id")) {
                selectedId = it.getInt("id")
            }

        }

        arguments?.let {
            viewModel.filterRequestModel =
                it.getParcelable("filterData") ?: GetReviewsRequestModel()
            professionSelected = it.getInt("professionPos")
            ageSelected = it.getInt("agePos")
        }






        binding.btnApply.setOnClickListener {
            generatePayloads()
            onDialogClick(
                DialogType.CREATOR_DASH_FILTER,
                viewModel.filterRequestModel,
                professionSelected,
                ageSelected
            )
            dismiss()
        }

        binding.btnReset.setOnClickListener {
            resetFields()
            onDialogClick(
                DialogType.CREATOR_DASH_FILTER,
                viewModel.filterRequestModel,
                professionSelected,
                ageSelected
            )
            dismiss()
        }

    }

    private fun generatePayloads() {
        ageList.forEach { element ->
            if (element.isSelected) {
                viewModel.createAgePayload(element)
            }
        }

        professionList.forEach { element ->
            if (element.isSelected) {
                viewModel.createProfessionPayload(element)
            }
        }
    }

    private fun resetFields() {
        professionList.forEach {
            it.isSelected = false
        }

        ageList.forEach {
            it.isSelected = false
        }
        professionSelected = -1
        ageSelected = -1
        setAgeAdapter()
        setProfessionAdapter()
        viewModel.resetFilterPayloads()
    }


    private fun setProfessionAdapter() {
        professionAdapter?.notifyDataSetChanged() ?: kotlin.run {
            professionAdapter = FilterAdapter(professionList, 1)
            binding.rvProfession.adapter = professionAdapter
            professionAdapter?.setOnAdapterItemClickListener(this)
        }
    }

    private fun setAgeAdapter() {
        ageAdapter?.notifyDataSetChanged() ?: kotlin.run {
            ageAdapter = FilterAdapter(ageList, 2)
            binding.rvAge.adapter = ageAdapter
            ageAdapter?.setOnAdapterItemClickListener(this)
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
                            professionSelected = position
                            setProfessionAdapter()
                        }
                        2 -> {
                            ageList.forEach {
                                it.isSelected = false
                            }
                            ageList[position].isSelected = true
                            ageSelected = position
                            setAgeAdapter()
                        }
                    }

                }
            }
        }
    }


    private fun observeData() {
        professionList.add(CategoryData("All Level", id = PROFESSION_FILTER.ALL))
        viewModel.masterData.observe(viewLifecycleOwner, Observer { response ->
            //        professionList.add(CategoryData(getString(R.string.all), id= PROFESSION_FILTER.ALL))
//        professionList.add(CategoryData(getString(R.string.student), id= PROFESSION_FILTER.STUDENT))
//        professionList.add(CategoryData(getString(R.string.working_professional), id=PROFESSION_FILTER.WORKING_PROFESSIONAL))
//        professionList.add(CategoryData(getString(R.string.pursuing_graduation), id=PROFESSION_FILTER.PURSUING_GRADUATION))

//            response?.list?.forEach {list->
//                 list.filterOptionData?.forEach {
//                     professionList.add(CategoryData(it.filterOptionDisplayName,id= it.filterOptionId))
//                 }
//            }

            response.professions?.list?.forEach { filterList ->

                professionList.add(CategoryData(filterList.title, id = filterList.id))

            }

//            response?.list?.get(0)?.filterOptionData?.forEach {
//                professionList.add(CategoryData(it.filterOptionDisplayName, id = it.filterOptionId))
//            }

            professionList.forEachIndexed() { index, data ->
                if (professionSelected == index)
                    professionList[index].isSelected = true
            }

            ageList.clear()
            ageList.add(
                CategoryData(
                    getString(R.string.age_between_18_20),
                    id = 0,
                    minAgeFilter = "18",
                    maxAgeFilter = "20"
                )
            )
            ageList.add(
                CategoryData(
                    getString(R.string.age_between_20_22),
                    id = 1,
                    minAgeFilter = "20",
                    maxAgeFilter = "22"
                )
            )
            ageList.add(
                CategoryData(
                    getString(R.string.age_between_22_24),
                    id = 2,
                    minAgeFilter = "22",
                    maxAgeFilter = "24"
                )
            )
            ageList.add(
                CategoryData(
                    getString(R.string.age_greater_than_25),
                    id = 3,
                    minAgeFilter = "25"
                )
            )
            ageList.forEachIndexed() { index, data ->
                if (ageSelected == index)
                    ageList[index].isSelected = true
            }
            setProfessionAdapter()
            setAgeAdapter()
        })
    }


}