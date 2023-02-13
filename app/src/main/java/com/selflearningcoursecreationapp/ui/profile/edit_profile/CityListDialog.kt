package com.selflearningcoursecreationapp.ui.profile.edit_profile

import androidx.core.widget.doOnTextChanged
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomDialogCourceCateBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CityModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import org.koin.androidx.viewmodel.ext.android.viewModel

class CityListDialog : BaseBottomSheetDialog<BottomDialogCourceCateBinding>(),
    BaseAdapter.IViewClick {
    private lateinit var adapterCity: AdapterCity
    private var list = ArrayList<CityModel>()
    override fun getLayoutRes() = R.layout.bottom_dialog_cource_cate
    private val viewModel: EditProfileViewModel by viewModel()

    override fun initUi() {
        arguments.let {
            viewModel.stateId = it?.getInt("stateId") ?: 0
        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getCityList()

        binding.parentCL.visible()
        binding.etSearch.gone()

        binding.etSearch.hint = baseActivity.getString(R.string.search_city)
        binding.tvTitle.text = baseActivity.getString(R.string.select_city)



        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                setStateAdapter(list)

            } else {
                arguments?.let {
                    val dataList = list.filter { city ->
                        city.cityName?.lowercase()
                            ?.contains(text.toString().lowercase()) == true
                    } as ArrayList
                    setStateAdapter(dataList)

                }
            }
        }


        binding.tvNoData.text = baseActivity.getString(R.string.no_city_found)

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        val response = value as? BaseResponse<*>
        when (apiCode) {
            ApiEndPoints.API_GET_ALL_CITY -> {
                list = response?.resource as ArrayList<CityModel>

                binding.etSearch.visibleView(!list.isNullOrEmpty())
                setStateAdapter(list)

            }
        }
    }


    private fun setStateAdapter(cityList: ArrayList<CityModel>) {
        binding.recyclerCourceCategory.visibleView(!cityList.isNullOrEmpty())
        binding.tvNoData.visibleView(cityList.isNullOrEmpty())
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

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)

    }
}