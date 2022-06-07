package com.selflearningcoursecreationapp.ui.dialog.singleChoice

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomDialogSingleChoiceBinding
import com.selflearningcoursecreationapp.extensions.visible
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
            if (it.containsKey("list")) {
                list.clear()
                list.addAll(it.getParcelableArrayList<SingleChoiceData>("list") ?: ArrayList())
                list.forEach { data ->
                    if (data.id == selectedId) {
                        data.isSelected = true
                    }
                }
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
//                setProfessionData()
            }

            DialogType.CLICK_QUIZ_TYPE -> {
                setQuizTypeData()
            }
            else -> {

                setAdapter()
            }
        }

    }

    private fun setQuizTypeData() {
        baseActivity?.resources.getStringArray(R.array.quiz_option_array)
            ?.forEachIndexed { index, s ->
                list.add(SingleChoiceData(index + 1, (index + 1) == selectedId, title = s))
            }

        setAdapter()
    }



    private fun setAdapter() {
        binding.parentCL.visible()
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = SingleChoiceAdapter(list, type == DialogType.CLICK_QUIZ_TYPE)
            binding.rvList.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }


    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val position = items[1] as Int
            list.forEach { it.isSelected = false }
            list[position].isSelected = true
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