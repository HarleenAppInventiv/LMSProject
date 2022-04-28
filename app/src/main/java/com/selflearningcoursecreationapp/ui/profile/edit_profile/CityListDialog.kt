package com.selflearningcoursecreationapp.ui.profile.edit_profile

import android.text.Editable
import android.text.TextWatcher
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogCourceCateBinding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.CityModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType

class CityListDialog : BaseBottomSheetDialog<BottomDialogCourceCateBinding>(),
    BaseAdapter.IViewClick {
    lateinit var adapterCity: AdapterCity
    private var list = ArrayList<CityModel>()
    override fun getLayoutRes() = R.layout.bottom_dialog_cource_cate

    override fun initUi() {

        arguments?.let {
            list = it.getParcelableArrayList("data")!!

            binding.etSearch.visible()
            setStateAdapter(list)
        }
        binding.etSearch.hint = baseActivity.getString(R.string.search_city)
        binding.tvTitle.text = baseActivity.getString(R.string.select_city)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    setStateAdapter(list)

                } else {
                    arguments?.let {
                        val dataList = it.getParcelableArrayList<CityModel>("data")?.filter {
                            it.cityName?.toLowerCase()
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

    fun setStateAdapter(cityList: ArrayList<CityModel>) {
        binding.recyclerCourceCategory.apply {
            adapterCity = AdapterCity(cityList)
            adapter = adapterCity
        }
        adapterCity.setOnAdapterItemClickListener(this)
    }

    override fun onItemClick(vararg items: Any) {
        dismiss()
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val value = items[1]

            when (type) {
                Constant.CLICK_VIEW -> {
                    onDialogClick(DialogType.CITY, value)
                }
            }
        }
    }

}