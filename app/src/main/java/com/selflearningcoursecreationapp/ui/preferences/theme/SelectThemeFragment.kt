package com.selflearningcoursecreationapp.ui.preferences.theme

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSelectThemeBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils


@SuppressLint("NotifyDataSetChanged")
class SelectThemeFragment : BaseFragment<FragmentSelectThemeBinding>(), BaseAdapter.IViewClick {

    private var adapter: ThemeAdapter? = null
    private val viewModel: PreferenceViewModel by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    override fun getLayoutRes(): Int {
        return R.layout.fragment_select_theme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }



    private fun initUi() {
        if (viewModel.themeListLiveData.value.isNullOrEmpty()) {

            viewModel.getApiResponse().observe(viewLifecycleOwner, this)
            viewModel.getThemeData()


        }
        binding.tvTitle.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.select_theme))
                .endPos(6)
                .isBold().getSpanString()

        )
        viewModel.themeListLiveData.observe(viewLifecycleOwner) {
            adapter?.notifyDataSetChanged()
            adapter = null
            setAdapter()

        }
        setAdapter()
    }

    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = ThemeAdapter(viewModel.themeListLiveData.value!!)
            binding.rvTheme.adapter = adapter
            adapter?.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    viewModel.themeListLiveData.value = viewModel.themeListLiveData.value?.apply {
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