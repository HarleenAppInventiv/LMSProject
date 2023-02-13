package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogFilterBottomBinding
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.utils.DialogType
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt


class FilterBottomDialog : BaseBottomSheetDialog<DialogFilterBottomBinding>(),
    BaseAdapter.IViewClick, View.OnClickListener {
    private var adapter: AdapterFilter? = null

    private var buttonLayoutParams: ConstraintLayout.LayoutParams? = null
    private var collapsedMargin //Button margin in collapsed state
            = 0
    private var buttonHeight = 0
    private var expandedHeight //Height of bottom sheet in expanded state
            = 0

    private val viewModel: HomeFilterVM by viewModel()
    override fun getLayoutRes() = R.layout.dialog_filter_bottom


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onApiRetry(apiCode: String) {
        super.onApiRetry(apiCode)
        viewModel.getFilters()
    }

    override fun initUi() {


        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getFilters()
        observeData()
        binding.btReset.setOnClickListener(this)
        binding.btApply.setOnClickListener(this)


//        dialog?.setOnShowListener { dialog -> // In a previous life I used this method to get handles to the positive and negative buttons
//            // of a dialog in order to change their Typeface. Good ol' days.
//            val d = dialog as BottomSheetDialog
////            val bottomSheet =
////                d.findViewById<FrameLayout>(com.selflearningcoursecreationapp.R.id.design_bottom_sheet)
//            // This is gotten directly from the source of BottomSheetDialog
//            // in the wrapInBottomSheet() method
//            val bottomSheet =
//                d.findViewById<View>(R.id.design_bottom_sheet)
//
//            // Right here!
//            if (bottomSheet != null) {
//                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_HALF_EXPANDED
//            }
//        }

//        dialog?.setOnShowListener { dialogInterface -> setupRatio((dialogInterface as BottomSheetDialog)) }

        //        Log.d("varun", "initUi: ${mainList}")


    }

    private fun observeData() {
        viewModel.filterLiveData.observe(viewLifecycleOwner, Observer {
            binding.tvTitle.text = it?.filterName ?: ""
            setBundleData(it?.list)
            setAdapter(it?.list)
        })
    }

    private fun setBundleData(list: ArrayList<FilterTypeData>?) {
        arguments?.let { bundle ->
            if (bundle.containsKey("selectedFilters")) {
                val resultList =
                    bundle.getParcelableArrayList<SelectedFilterData?>("selectedFilters")
                resultList?.forEach { resultData ->
                    list?.forEach { filterTypeData ->
                        if (filterTypeData.filterName.equals(resultData?.filterName)) {
                            filterTypeData.filterOptionData?.forEach { filterOptionData ->
                                if (filterOptionData.filterOptionValue.equals(resultData?.filterOptionValue)) {
                                    filterOptionData.isSelected = true
                                }
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
                adapter = AdapterFilter(list)
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
                    filterName = filterTypeData.filterName,
                    filterOptionValue = selectedData.filterOptionValue,
                    filterOptionOperatorId = selectedData.filterOptionOperatorId,
                    filterOptionId = selectedData.filterOptionId
                )
            }
        } as ArrayList?)

        resultList?.removeAll { data -> data == null }

        resultList?.forEach {
            showLog(
                "FILTER_DATA",
                " value >> ${it?.filterName} ... ${it?.filterOptionValue}"
            )
        }

        onDialogClick(DialogType.HOME_FILTER, resultList ?: ArrayList<SelectedFilterData>())
        dismiss()
    }


    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<FrameLayout>(com.selflearningcoursecreationapp.R.id.design_bottom_sheet)
                ?: return

        //Retrieve button parameters
        buttonLayoutParams = binding.btApply.getLayoutParams() as ConstraintLayout.LayoutParams

        //Retrieve bottom sheet parameters
        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
        val bottomSheetLayoutParams = bottomSheet.layoutParams
        bottomSheetLayoutParams.height = getBottomSheetDialogDefaultHeight()
        expandedHeight = bottomSheetLayoutParams.height
        val peekHeight =
            (expandedHeight / 1.3)  //Peek height to 70% of expanded height (Change based on your view)

        //Setup bottom sheet
        bottomSheet.layoutParams = bottomSheetLayoutParams
        BottomSheetBehavior.from(bottomSheet).skipCollapsed = false
        BottomSheetBehavior.from(bottomSheet).peekHeight = peekHeight.roundToInt()
        BottomSheetBehavior.from(bottomSheet).isHideable = true

        //Calculate button margin from top
        buttonHeight =
            binding.btApply.getHeight() + 40 //How tall is the button + experimental distance from bottom (Change based on your view)
        collapsedMargin =
            (peekHeight - buttonHeight).roundToInt() //Button margin in bottom sheet collapsed state
        buttonLayoutParams?.topMargin = collapsedMargin
        binding.btApply.setLayoutParams(buttonLayoutParams)

        //OPTIONAL - Setting up recyclerview margins
        val recyclerLayoutParams: ConstraintLayout.LayoutParams =
            binding.recyclerFilter.getLayoutParams() as ConstraintLayout.LayoutParams
        val k: Int =
            (buttonHeight - 60) / buttonHeight  //60 is amount that you want to be hidden behind button
        recyclerLayoutParams.bottomMargin =
            (k * buttonHeight) //Recyclerview bottom margin (from button)
        binding.recyclerFilter.setLayoutParams(recyclerLayoutParams)
    }

    //Calculates height for 90% of fullscreen
    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 90 / 100
    }

    //Calculates window height for fullscreen use
    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (baseActivity as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

}