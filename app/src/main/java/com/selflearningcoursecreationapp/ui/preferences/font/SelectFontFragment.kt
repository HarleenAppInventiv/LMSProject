package com.selflearningcoursecreationapp.ui.preferences.font

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSelectFontBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

@SuppressLint("NotifyDataSetChanged")
class SelectFontFragment : BaseFragment<FragmentSelectFontBinding>(), BaseAdapter.IViewClick {
    private val viewModel: PreferenceViewModel by viewModels({ if (parentFragment != null) requireParentFragment() else this })

    private var adapter: FontAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_select_font
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        val msg = SpanUtils.with(baseActivity, baseActivity.getString(R.string.select_font)).apply {
            isBold()
            endPos(6)
        }.getSpanString()
        binding.tvTitle.setSpanString(msg)

        if (viewModel.fontListData.value.isNullOrEmpty()) {
            val nameList = baseActivity.resources.getStringArray(R.array.font_name_array)
            val fontArray =
                arrayListOf(R.font.roboto_medium, R.font.ibm_medium, R.font.worksans_medium)
            val list = ArrayList<CategoryData>()
            for (i in nameList.indices) {
                val data = CategoryData(
                    id = i + 1,
                    name = nameList[i],
                    codeId = fontArray[i],
                    isSelected = (i + 1) == viewModel.userProfile?.font?.id
                )
                list.add(data)
            }
            viewModel.fontListData.value = list

        }

        setAdapter()

    }

    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = FontAdapter(viewModel.fontListData.value!!)
            binding.rvFont.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    viewModel.fontListData.value = viewModel.fontListData.value?.apply {
                        forEachIndexed { index, themeData ->
                            themeData.isSelected = position == index
                        }

                    }
                    setAdapter()
                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }


}