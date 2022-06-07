package com.selflearningcoursecreationapp.ui.dialog.multipleChoice

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomDialogSingleChoiceBinding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.models.SingleClickResponse
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.DialogType
import org.koin.androidx.viewmodel.ext.android.viewModel

class MultipleChoiceBottomDialog : BaseBottomSheetDialog<BottomDialogSingleChoiceBinding>(),
    BaseAdapter.IViewClick {
    private val viewModel: SingleChoiceVM by viewModel()
    private var list: ArrayList<SingleChoiceData> = ArrayList()
    private var selectedList: ArrayList<SingleChoiceData> = ArrayList()
    private var type: Int = 0
    private var adapter: MultipleChoiceAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.bottom_dialog_single_choice

    }

    @SuppressLint("ResourceType")
    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.btDone.visible()
        binding.ivClose.setOnClickListener {

            dismiss()
        }
        binding.btDone.setOnClickListener {
            onDialogClick(
                type,
                (list.filter { it.isSelected == true } as? ArrayList<SingleChoiceData>)
                    ?: ArrayList<SingleChoiceData>())
            dismiss()
        }

        arguments?.let {
            type = it.getInt("type")
            if (it.containsKey("selectedIds")) {
                selectedList =
                    it.getParcelableArrayList<SingleChoiceData>("selectedIds") ?: ArrayList()
            }
            if (it.containsKey("list")) {
                list.clear()
                list.addAll(it.getParcelableArrayList<SingleChoiceData>("list") ?: ArrayList())
                list.forEach { it.isSelected = false }
                updateSelectedData()
            }
            binding.tvDialogTitle.text = it.getString("title")
        }

        when (type) {


            DialogType.PROFESSION -> {
                if (list.isNullOrEmpty()) {
                    viewModel.getProfession()
                } else {
                    setAdapter()
                }
            }

        }

    }

    private fun updateSelectedData() {
        selectedList.forEach { selected ->
            list.forEach { data ->
                if (data.id == selected.id) {
                    data.isSelected = true
                }
            }
        }
    }


    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = MultipleChoiceAdapter(list)
            binding.rvList.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }


    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val position = items[1] as Int
            list[position].isSelected = !(list[position].isSelected ?: false)
            setAdapter()
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_PROFESSION -> {
                list = (value as BaseResponse<SingleClickResponse>)?.resource?.list ?: ArrayList()
                updateSelectedData()
                setAdapter()
            }
        }
    }

}