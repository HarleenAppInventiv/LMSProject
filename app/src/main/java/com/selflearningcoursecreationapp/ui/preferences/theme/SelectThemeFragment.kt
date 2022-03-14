package com.selflearningcoursecreationapp.ui.preferences.theme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSelectThemeBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.SpanUtils


class SelectThemeFragment : BaseFragment<FragmentSelectThemeBinding>(), BaseAdapter.IViewClick {

    private var adapter: ThemeAdapter? = null
    private val viewModel: PreferenceViewModel by viewModels({ if (parentFragment != null) requireParentFragment() else this })

    override fun getLayoutRes(): Int {
        return R.layout.fragment_select_theme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
//        initThemeList()
        binding.tvTitle.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.select_theme)).endPos(6)
                .isBold().getSpanString()

        )

      setAdapter()
    }

//    private fun initThemeList() {
//        if (viewModel.themeListLiveData.value.isNullOrEmpty()) {
//            val nameList =
//                baseActivity.resources.getStringArray(R.array.theme_name_array)
//            val iconList =
//                baseActivity.resources.obtainTypedArray(R.array.walkthrough_icons)
//            val list = ArrayList<ThemeData>()
//            for (i in nameList.indices) {
//                list.add(
//                    ThemeData(
//                        nameList[i],
//                        iconList.getResourceId(i, -1),
//                        i + 1 == THEME_CONSTANT.BLUE,
//                        i + 1
//                    )
//                )
//            }
//
//            viewModel.themeListLiveData.value = list
//            iconList.recycle()
//        }
//    }

    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = ThemeAdapter(viewModel.themeListLiveData.value!!)
            binding.rvTheme.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
//                   viewModel.themeListLiveData.value=
                    viewModel.themeListLiveData.value?.apply {
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