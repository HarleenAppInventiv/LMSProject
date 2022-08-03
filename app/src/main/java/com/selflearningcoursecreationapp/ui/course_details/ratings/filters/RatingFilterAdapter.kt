package com.selflearningcoursecreationapp.ui.course_details.ratings.filters

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomRatingFilterBinding
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.AdapterFilter
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.FilterTypeData
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.HomeFilterVM
import com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter.SelectedFilterData
import com.selflearningcoursecreationapp.utils.DialogType
import org.koin.androidx.viewmodel.ext.android.viewModel

class RatingFilterAdapter : BaseBottomSheetDialog<BottomRatingFilterBinding>(),
    BaseAdapter.IViewClick, View.OnClickListener {
    private var adapter: AdapterFilter? = null

    private val viewModel: HomeFilterVM by viewModel()
    override fun getLayoutRes() = R.layout.bottom_rating_filter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onApiRetry(apiCode: String) {
        super.onApiRetry(apiCode)
        viewModel.getFilters(false, arguments?.getInt("courseId") ?: 0)
    }

    override fun initUi() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getFilters(false, arguments?.getInt("courseId") ?: 0)
        observeData()
        binding.btReset.setOnClickListener(this)
        binding.btApply.setOnClickListener(this)

//        Log.d("varun", "initUi: ${mainList}")


    }

    private fun observeData() {
        viewModel.filterLiveData.observe(viewLifecycleOwner, Observer {
            binding.tvTitle.text = it.filterName
            setBundleData(it.list)
            setAdapter(it.list)
        })
    }

    private fun setBundleData(list: ArrayList<FilterTypeData>?) {
        arguments?.let { bundle ->
            if (bundle.containsKey("selectedFilters")) {
                val resultList =
                    bundle.getParcelableArrayList<SelectedFilterData?>("selectedFilters")
                resultList?.forEach { resultData ->
                    list?.forEach { filterTypeData ->

                        filterTypeData.filterOptionData?.forEach { filterOptionData ->
                            if (filterOptionData.filterOptionId == resultData?.filterOptionId) {
                                filterOptionData.isSelected = true
                            }
                        }


                    }

                }

            }

        }
    }

    private fun setAdapter(list: ArrayList<FilterTypeData>?) {
        if (list.isNullOrEmpty()) {
            adapter?.notifyDataSetChanged()
            adapter = null
        } else {
            adapter?.notifyDataSetChanged() ?: kotlin.run {
                adapter = AdapterFilter(list, true)
                binding.recyclerFilter.adapter = adapter
                adapter?.setOnAdapterItemClickListener(this)
            }
        }
    }

    override fun onItemClick(vararg items: Any) {

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_reset -> {
                viewModel.filterLiveData.value?.list?.forEach { filterTypeData ->
                    filterTypeData.filterOptionData?.forEach {
                        it.isSelected = false
                    }

                }
                adapter?.notifyDataSetChanged()
                getFilters()
            }
            R.id.bt_apply -> {
                getFilters()
            }
        }
    }

    private fun getFilters() {
        val resultList = (viewModel.filterLiveData.value?.list?.map { filterTypeData ->


            val selectedData = filterTypeData.filterOptionData?.find { it.isSelected }
            selectedData?.let {
                SelectedFilterData(
                    filterName = selectedData.filterName,
                    filterOptionValue = selectedData.filterOptionValue,
                    filterOptionOperatorId = selectedData.filterOptionOperatorId,
                    filterOptionId = selectedData.filterOptionId,
                    filterType = filterTypeData.filterType
                )
            }
        } as ArrayList)

        resultList.removeAll { data -> data == null }

        resultList.forEach {
            showLog(
                "FILTER_DATA",
                "value >> ${it?.filterName} ... ${it?.filterOptionValue}"
            )
        }

        onDialogClick(DialogType.HOME_FILTER, resultList)
        dismiss()
    }
}