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
import com.selflearningcoursecreationapp.models.StateModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import org.koin.androidx.viewmodel.ext.android.viewModel

class StateListDialog : BaseBottomSheetDialog<BottomDialogCourceCateBinding>(),
    BaseAdapter.IViewClick {
    private lateinit var adapterState: AdapterState
    private val viewModel: EditProfileViewModel by viewModel()

    //    var listStates = ArrayList<StateModel>()
    private var list = ArrayList<StateModel>()
    override fun getLayoutRes() = R.layout.bottom_dialog_cource_cate

    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getStateList()

        binding.parentCL.visible()
        binding.etSearch.gone()

        binding.etSearch.hint = baseActivity.getString(R.string.search_state)
        binding.tvTitle.text = baseActivity.getString(R.string.select_state)
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                setStateAdapter(list)

            } else {
                arguments?.let {
                    val dataList = list.filter { state ->
                        state.stateName?.lowercase()
                            ?.contains(text.toString().lowercase()) == true
                    } as ArrayList
                    setStateAdapter(dataList)

                }
            }
        }

        binding.tvNoData.text = baseActivity.getString(R.string.no_state_found)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        val response = value as? BaseResponse<*>
        when (apiCode) {
            ApiEndPoints.API_GET_ALL_STATES -> {
                list = response?.resource as java.util.ArrayList<StateModel>

                binding.etSearch.visibleView(!list.isNullOrEmpty())
                setStateAdapter(list)

            }
        }
    }


    private fun setStateAdapter(stateList: ArrayList<StateModel>) {
        binding.recyclerCourceCategory.visibleView(!stateList.isNullOrEmpty())
        binding.tvNoData.visibleView(stateList.isNullOrEmpty())
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

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

}