package com.selflearningcoursecreationapp.ui.preferences.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCategoryBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant


class CategoryFragment : BaseFragment<FragmentCategoryBinding>(), BaseAdapter.IViewClick {
    private val viewModel: PreferenceViewModel by viewModels({ if (parentFragment != null) requireParentFragment() else this })

    private var adapter: CategoryAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_category
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.tvTitle.setSpanString(
            baseActivity.getString(R.string.select_categories),
            endPos = 6,
            isBold = true
        )

        viewModel.categoryListLiveData.observe(viewLifecycleOwner, Observer {
            setCategoryAdapter()
        })
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
                                    data.isSelected =  ! data.isSelected
                                }
                            }


                        }
                    setCategoryAdapter()
                }
            }
        }

    }

}