package com.selflearningcoursecreationapp.ui.profile.edit_profile

import android.text.Editable
import android.text.TextWatcher
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogCourceCateBinding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.StateModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import java.util.*

class StateListDialog() : BaseBottomSheetDialog<BottomDialogCourceCateBinding>(),
    BaseAdapter.IViewClick {
    lateinit var adapterState: AdapterState

    //    var listStates = ArrayList<StateModel>()
    private var list = ArrayList<StateModel>()
    override fun getLayoutRes() = R.layout.bottom_dialog_cource_cate

    override fun initUi() {

        arguments?.let {
            list = it.getParcelableArrayList<StateModel>("data")!!

            binding.etSearch.visible()
            setStateAdapter(list)
        }
        binding.etSearch.hint = baseActivity.getString(R.string.search_state)
        binding.tvTitle.text = baseActivity.getString(R.string.select_state)
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    setStateAdapter(list)

                } else {
                    arguments?.let {
                        val dataList = it.getParcelableArrayList<StateModel>("data")?.filter {
                            it.stateName?.toLowerCase()
                                ?.contains(p0.toString().toLowerCase()) == true
                        } as ArrayList
                        setStateAdapter(dataList)

                    }
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    fun setStateAdapter(stateList: ArrayList<StateModel>) {
        binding.recyclerCourceCategory.apply {
            adapterState = AdapterState(stateList)
            adapter = adapterState
        }
        adapterState.setOnAdapterItemClickListener(this)
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val value = items[1]

            when (type) {
                Constant.CLICK_VIEW -> {
                    dismiss()
                    onDialogClick(DialogType.STATE, value)
                }
            }
        }
    }

}