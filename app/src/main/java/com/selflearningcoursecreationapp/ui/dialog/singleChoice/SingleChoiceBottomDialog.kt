package com.selflearningcoursecreationapp.ui.dialog.singleChoice

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomDialogSingleChoiceBinding
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.models.SingleClickResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.DialogType
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleChoiceBottomDialog : BaseBottomSheetDialog<BottomDialogSingleChoiceBinding>(),
    BaseAdapter.IViewClick {
    private val viewModel: SingleChoiceVM by viewModel()
    private var list: ArrayList<SingleChoiceData> = ArrayList()
    private var type: Int = 0
    private var selectedId: Int = 0
    private var adapter: SingleChoiceAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.bottom_dialog_single_choice

    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        arguments?.let {
            type = it.getInt("type")
            if (it.containsKey("id")) {
                selectedId = it.getInt("id")
            }
            binding.tvDialogTitle.text = it.getString("title")
        }

        when (type) {
            DialogType.COURSE_TYPE -> {
                setCourseTypeData()

            }
            DialogType.COURSE_COMPLEXITY -> {
                setCourseComplexityData()

            }

            DialogType.PROFESSION -> {
                viewModel.getProfession()
//                setProfessionData()


            }
            else -> {

            }
        }

    }

    private fun setCourseTypeData() {
        arrayOf("Free", "Paid", "Restricted")?.forEachIndexed { index, s ->
            list.add(SingleChoiceData(index + 1, false, s))
        }
        setAdapter()

    }

    private fun setCourseComplexityData() {
        arrayOf("Beginner", "Intermediate", "Advanced")?.forEachIndexed { index, s ->
            list.add(SingleChoiceData(index + 1, false, s))
        }
        setAdapter()

    }

    private fun setProfessionData() {
        arrayOf(
            "Student",
            "Working Professional",
            "Pursuing Graduation"
        )?.forEachIndexed { index, s ->
            list.add(SingleChoiceData(index + 1, false, s))
        }
        setAdapter()

    }

    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = SingleChoiceAdapter(list)
            binding.rvList.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }


    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val position = items[1] as Int
            onDialogClick(type, list[position])
            dismiss()
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_PROFESSION -> {
                list = (value as BaseResponse<SingleClickResponse>)?.resource?.list ?: ArrayList()
                list.forEach {
                    if (it.id == selectedId) {
                        it.isSelected = true
                    }
                }
                setAdapter()
            }
        }
    }

}