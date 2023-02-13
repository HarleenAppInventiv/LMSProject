package com.selflearningcoursecreationapp.ui.moderator.dialog.filter

import androidx.lifecycle.lifecycleScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogeModeratorFilterBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.moderator.ModFilterOptionData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.preferences.language.LanguageAdapter
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ModerateFilterDialogue : BaseBottomSheetDialog<DialogeModeratorFilterBinding>() {

    private val viewModel: ModHomeFilterVM by viewModel()
    private lateinit var parentAdapter: ModeratorFilterParentAdapter
    private var childAdapter: LanguageAdapter? = null


    override fun getLayoutRes(): Int {
        return R.layout.dialoge_moderator_filter
    }

    override fun initUi() {
        arguments?.let {
            viewModel.filterRequestModel =
                it.getParcelable("filterData") ?: GetReviewsRequestModel()
            viewModel.setFilterData()
        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getMasterData()
        binding.viewModel = viewModel

        setParentAdapter()
        observeData()


        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.btApply.setOnClickListener {
            viewModel.validate()


        }
        binding.btReset.setOnClickListener {
            viewModel.resetFields()
        }

    }

    private fun observeData() {
        viewModel.languageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            setChildAdapter()
        })
    }

    private fun setChildAdapter() {
        if (!viewModel.languageData.value.isNullOrEmpty()) {
            childAdapter =
                LanguageAdapter(list = viewModel.languageData.value!!, fromModFilter = true).apply {
                    setOnAdapterItemClickListener(object : BaseAdapter.IViewClick {
                        override fun onItemClick(vararg items: Any) {
                            val position = items[1] as Int
                            viewModel.languageData.value =
                                viewModel.languageData.value?.apply {
                                    forEachIndexed { index, themeData ->
                                        themeData.isSelected = position == index

                                    }
                                }

                            setChildAdapter()
                        }

                    })
                }
        }

        binding.recyclerFilterCatItem.adapter = childAdapter
    }

    private fun setParentAdapter() {
        viewModel.catArrayList.apply {
            add(
                ModFilterOptionData(
                    0,
                    baseActivity.getString(R.string.language).uppercase(Locale.getDefault()),
                    true,
                    Constant.TYPE_CATEGORY
                )
            )
            add(
                ModFilterOptionData(
                    0,
                    getString(R.string.request_date),
                    false,
                    Constant.TYPE_REQUEST_DATE_START_DATE
                )
            )
            add(
                ModFilterOptionData(
                    0,
                    getString(R.string.fee_range),
                    false,
                    Constant.TYPE_FEE_RANGE
                )
            )
            add(
                ModFilterOptionData(
                    0,
                    getString(R.string.creator_name),
                    false,
                    Constant.TYPE_CREATOR_NAME
                )
            )
        }



        parentAdapter = ModeratorFilterParentAdapter { type: Int, position: Int ->
            viewModel.catArrayList.forEachIndexed { index, _ ->
                viewModel.catArrayList[index].isSelected = index == position
                parentAdapter.setAdapterList(viewModel.catArrayList)
            }

            handleViews(type)
        }
        parentAdapter.setAdapterList(viewModel.catArrayList)
        binding.recyclerFilterCat.adapter = parentAdapter
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.HOME_SUCCESS -> {
                showLog("FILTER", "onResponseSuccess")
                onDialogClick(DialogType.HOME_FILTER, viewModel.filterRequestModel)
                dismiss()
            }
        }


    }

    private fun handleViews(type: Int) {

        when (type) {
            Constant.TYPE_CATEGORY -> {
                goneAllFields()
                binding.recyclerFilterCatItem.visible()


            }

            Constant.TYPE_REQUEST_DATE_START_DATE -> {
                goneAllFields()
                binding.layoutRequestDate.itemParent.visible()


//                binding.layoutRequestDate.evStartDate.setText(filterData.startDate)
//                binding.layoutRequestDate.etEnterEndDate.setText(filterData.endDate)


                binding.layoutRequestDate.apply {
                    evStartDate.setOnClickListener {
                        evStartDate.isEnabled = false
                        lifecycleScope.launch {
                            delay(1000)
                            baseActivity.runOnUiThread {
                                evStartDate.isEnabled = true

                            }
                        }

                        baseActivity.openDatePickerDialog(setMaxDate = true) {
                            evStartDate.setText(it.time.getStringDate("yyyy-MM-dd"))
                            etEnterEndDate.text?.clear()
//                        filterData.startDate = binding.layoutRequestDate.evStartDate.text.toString()


                        }

                    }

                    etEnterEndDate.setOnClickListener {
                        if (evStartDate.content().isNullOrEmpty()) {
                            showToastShort(getString(R.string.please_select_your_start_date))
                        } else {
                            etEnterEndDate.isEnabled = false
                            lifecycleScope.launch {
                                delay(1000)
                                baseActivity.runOnUiThread {
                                    etEnterEndDate.isEnabled = true

                                }
                            }
                            val startCal = Calendar.getInstance().apply {
                                this@ModerateFilterDialogue.viewModel.filterData.value?.startDate?.createDate(
                                    "yyyy-MM-dd"
                                )?.let {
                                    this@apply.time = it
                                }
                            }
                            baseActivity.openDatePickerDialog(
                                setMinDate = true, minYear = startCal, setMaxDate = true
                            ) {

                                binding.layoutRequestDate.etEnterEndDate.setText(
                                    it.time.getStringDate(
                                        "yyyy-MM-dd"
                                    )
                                )
//                        filterData.endDate =
//                            binding.layoutRequestDate.etEnterEndDate.text.toString()


                            }
                        }

                    }
                }


            }


            Constant.TYPE_CREATOR_NAME -> {
                goneAllFields()
                binding.layoutCreator.itemParent.visible()


            }
            Constant.TYPE_FEE_RANGE -> {
                goneAllFields()
                binding.layoutFeeRange.itemParent.visible()


            }
        }

    }


    private fun goneAllFields() {
        binding.invalidateAll()
        binding.recyclerFilterCatItem.gone()
        binding.layoutRequestDate.itemParent.gone()
        binding.layoutFeeRange.itemParent.gone()
        binding.layoutCreator.itemParent.gone()
    }

}



