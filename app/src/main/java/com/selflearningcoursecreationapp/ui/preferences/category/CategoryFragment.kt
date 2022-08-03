package com.selflearningcoursecreationapp.ui.preferences.category

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCategoryBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.SpanUtils

@SuppressLint("NotifyDataSetChanged")
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(), BaseAdapter.IViewClick {
    private val viewModel: PreferenceViewModel by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private var adapter: CategoryAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_category
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        if (viewModel.categoryListLiveData.value.isNullOrEmpty()) {

            viewModel.getApiResponse().observe(viewLifecycleOwner, this)
            viewModel.getCategories()
        }

        binding.tvTitle.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.select_categories))
                .endPos(6).isBold().getSpanString()

        )

        viewModel.categoryListLiveData.observe(viewLifecycleOwner, {

            adapter?.notifyDataSetChanged()
            adapter = null
            setCategoryAdapter()
        })
        adapter?.notifyDataSetChanged()
        adapter = null
        setCategoryAdapter()
    }

    private fun setCategoryAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = CategoryAdapter(viewModel.categoryListLiveData.value!!)
            binding.rvCategory.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }

    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    viewModel.categoryListLiveData.value =
                        viewModel.categoryListLiveData.value?.apply {
                            forEachIndexed { index, data ->
                                if (index == position) {
                                    data.isSelected = !data.isSelected
                                }
                            }


                        }
                    setCategoryAdapter()
                }
            }
        }

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_GET_CATEGORIES -> {
                if (baseActivity is HomeActivity) {
                    viewModel.getMyCategories()
                }

            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }
}