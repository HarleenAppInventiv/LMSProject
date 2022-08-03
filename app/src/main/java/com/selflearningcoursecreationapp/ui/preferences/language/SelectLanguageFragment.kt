package com.selflearningcoursecreationapp.ui.preferences.language

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSelectLanguageBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.LanguageConstant
import com.selflearningcoursecreationapp.utils.SpanUtils


@SuppressLint("NotifyDataSetChanged")
class SelectLanguageFragment : BaseFragment<FragmentSelectLanguageBinding>(),
    BaseAdapter.IViewClick {
    private var adapter: LanguageAdapter? = null

    private val viewModel: PreferenceViewModel by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    override fun getLayoutRes(): Int {
        return R.layout.fragment_select_language
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = parentFragment
        initLanguageList()

        initUi()
    }

    private fun initLanguageList() {
        if (viewModel.languageListLiveData.value.isNullOrEmpty()) {
            val nameList =
                baseActivity.resources.getStringArray(R.array.language_array)
            val codeArray = arrayListOf(
                LanguageConstant.ENGLISH,
                LanguageConstant.HINDI,
                LanguageConstant.TELUGU,
                LanguageConstant.TAMIL,
                LanguageConstant.KANNADA,
                LanguageConstant.BENGALI
            )
            val list = ArrayList<CategoryData>()
            for (i in nameList.indices) {
                list.add(
                    CategoryData(
                        nameList[i],
                        codeArray[i],
                        id = i + 1,
                        isSelected = i + 1 == viewModel.userProfile?.language?.id
                    )
                )
            }

            viewModel.languageListLiveData.value = list
        }
    }

    private fun initUi() {
//        initLanguageList()
        binding.tvTitle.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.select_language)).endPos(6)
                .isBold().getSpanString()

        )
        setAdapter()
    }

    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = LanguageAdapter(viewModel.languageListLiveData.value!!)
            binding.rvLanguage.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    viewModel.languageListLiveData.value =
                        viewModel.languageListLiveData.value?.apply {
                            forEachIndexed { index, themeData ->
                                themeData.isSelected = position == index
                            }
                        }

                    setAdapter()
                    if (parentFragment is NavHostFragment) {

                        val bundle = arguments ?: Bundle()
                        bundle.putString(
                            "language_code",
                            viewModel.languageListLiveData.value?.singleOrNull { it.isSelected }?.code
                        )

                        setFragmentResult("languageData", bundle)
                        findNavController().navigateUp()
                    }
                }
            }
        }

    }

    override fun onApiRetry(apiCode: String) {

    }


}