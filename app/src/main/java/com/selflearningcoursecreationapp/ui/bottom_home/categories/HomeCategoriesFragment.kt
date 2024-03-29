package com.selflearningcoursecreationapp.ui.bottom_home.categories

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentHomeCategoriesBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("NotifyDataSetChanged")
class HomeCategoriesFragment : BaseFragment<FragmentHomeCategoriesBinding>(),
    BaseAdapter.IViewClick {
    private var mAdapter: HomeCategoryAdapter? = null
    private val viewModel: HomeCategoriesVM by viewModel()
    private val categories = ArrayList<CategoryData>()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_home_categories
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        initUi()
    }


    private fun initUi() {

        binding.etSearch.text?.clear()
        reset()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getCategories()
        observeData()


        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            textChangeHandling(text)
        }

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                textChangeHandling(binding.etSearch.content())
            }

            return@setOnEditorActionListener true
        }
        spokenTextLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { value ->
                binding.etSearch.setText(value)
            }
        }

        binding.imgMic.setOnClickListener {
            displaySpeechRecognizer(this)
        }

        binding.imgCross.setOnClickListener {

            binding.etSearch.text?.clear()
            binding.imgCross.gone()
            binding.imgMic.visible()

        }
    }

    private fun textChangeHandling(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            allHandling(viewModel.categoryLiveData.value)
            binding.imgCross.gone()
            binding.imgMic.visible()
        } else {
            binding.imgCross.visible()
            binding.imgMic.gone()
            reset()
            categories.addAll(viewModel.categoryLiveData.value?.filter { categoryData ->
                categoryData.name?.lowercase()?.contains(text.toString().lowercase()) == true
            } as ArrayList)

            uiVisibility()
        }
    }

    private fun observeData() {
        viewModel.categoryLiveData.observe(viewLifecycleOwner, Observer {
            it.forEach {
                if (!it.codeId.isNullOrZero()) {
                    it.name = baseActivity.getString(it.codeId ?: 0)
                }
            }
            textChangeHandling(binding.etSearch.content())
        })
    }


    private fun allHandling(it: ArrayList<CategoryData>?) {
        reset()
        categories.addAll(it ?: ArrayList())
        uiVisibility()

    }


    private fun uiVisibility() {


        binding.rvList.visibleView(!categories.isNullOrEmpty())
        binding.noDataTV.visibleView(categories.isNullOrEmpty() && !viewModel.isFirst)
        setAdapter()
    }

    private fun reset() {
        categories.clear()
        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.selectedList.clear()

    }


    private fun setAdapter() {

        mAdapter?.notifyDataSetChanged() ?: kotlin.run {
            mAdapter = HomeCategoryAdapter(categories)
            binding.rvList.adapter = mAdapter
            mAdapter?.setOnAdapterItemClickListener(this)
        }

    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val clickType = items[0] as Int
            val position = items[1] as Int
            when (clickType) {
                Constant.CLICK_VIEW -> {

                    findNavController().navigateTo(
                        R.id.action_homeCategoriesFragment_to_categoryWiseCoursesFragment, bundleOf(
                            "id" to (categories[position].id ?: 0),
                            "name" to (categories[position].name ?: "")
                        )
                    )


                }
            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_HOME_ALL_CATEGORIES -> {

            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

}