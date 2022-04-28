package com.selflearningcoursecreationapp.ui.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogCourceCateBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.models.ProfessionModel
import com.selflearningcoursecreationapp.ui.authentication.login_signup.AdapterProfession
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType

class ProfessionOptionDialog : BaseBottomSheetDialog<BottomDialogCourceCateBinding>(),
    BaseAdapter.IViewClick {
    lateinit var adapterCourseCategories: AdapterProfession

    override fun getLayoutRes() = R.layout.bottom_dialog_cource_cate
    override fun initUi() {
        binding.ivClose.setOnClickListener {

            dismiss()
        }

        arguments?.let {
            val listAllProfessions: ArrayList<ProfessionModel> = it.getParcelableArrayList("data")!!
            binding.etSearch.gone()
            binding.tvTitle.setText(getString(R.string.select_pro))

            setProfessionalAdapter(listAllProfessions)
        }

    }

    fun setProfessionalAdapter(list: ArrayList<ProfessionModel>) {
        binding.recyclerCourceCategory.apply {
            adapterCourseCategories = AdapterProfession(list)
            adapter = adapterCourseCategories
        }
        adapterCourseCategories.setOnAdapterItemClickListener(this)
    }

    override fun onItemClick(vararg items: Any) {
        dismiss()
        if (items.isNotEmpty()) {
            val click = items[0] as Int
            val value = items[1]
            when (click) {
                Constant.CLICK_VIEW -> {
                    onDialogClick(DialogType.PROFESSION, value)
                }
            }

        }
    }


}

