package com.selflearningcoursecreationapp.ui.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogCourceCateBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.models.GenderModel
import com.selflearningcoursecreationapp.ui.profile.edit_profile.AdapterSelectGender
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType

class ChooseGenderDialog : BaseBottomSheetDialog<BottomDialogCourceCateBinding>(),
    BaseAdapter.IViewClick {
    lateinit var adapterSelectGender: AdapterSelectGender
    var list = ArrayList<GenderModel>()
    private var selectedId: Int = 0

    override fun getLayoutRes() = R.layout.bottom_dialog_cource_cate
    override fun initUi() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        arguments?.let {

            if (it.containsKey("id")) {
                selectedId = it.getInt("id")
            }

        }
        list.add(GenderModel(1, "Male", selectedId == 1))
        list.add(GenderModel(2, "Female", selectedId == 2))
        list.add(GenderModel(3, "Other", selectedId == 3))

        binding.etSearch.gone()
        binding.tvTitle.setText(getString(R.string.gender))

        setProfessionalAdapter(list)

    }

    fun setProfessionalAdapter(list: ArrayList<GenderModel>) {
        binding.recyclerCourceCategory.apply {
            adapterSelectGender = AdapterSelectGender(list)
            adapter = adapterSelectGender
        }
        adapterSelectGender.setOnAdapterItemClickListener(this)
    }

    override fun onItemClick(vararg items: Any) {
        dismiss()
        if (items.isNotEmpty()) {
            val click = items[0] as Int
            val value = items[1]
            when (click) {
                Constant.CLICK_VIEW -> {
                    onDialogClick(DialogType.GENDER, value)
                }
            }

        }
    }
}