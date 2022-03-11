package com.selflearningcoursecreationapp.ui.preferences.font

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSelectFontBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant

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
        binding.tvTitle.setSpanString(
            baseActivity.getString(R.string.select_font),
            endPos = 6,
            isBold = true
        )

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


}