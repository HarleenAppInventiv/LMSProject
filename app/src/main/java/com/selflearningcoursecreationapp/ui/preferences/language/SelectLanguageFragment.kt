package com.selflearningcoursecreationapp.ui.preferences.language

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSelectLanguageBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.PREFERENCES
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils


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
            viewModel.getMasterData()

//            val nameList =
//                baseActivity.resources.getStringArray(R.array.language_array)
//            val codeArray = arrayListOf(
//                LanguageConstant.ENGLISH,
//                LanguageConstant.HINDI,
//                LanguageConstant.TELUGU,
//                LanguageConstant.TAMIL,
//                LanguageConstant.KANNADA,
//                LanguageConstant.BENGALI
//            )
//            val list = ArrayList<CategoryData>()
//            for (i in nameList.indices) {
//                list.add(
//                    CategoryData(
//                        nameList[i],
//                        codeArray[i],
//                        id = i + 1,
//                        isSelected = if (viewModel.screenType == PREFERENCES.SCREEN_SELECT) viewModel.preferenceArgData?.selectedValues?.contains(
//                            i + 1
//                        ) ?: false else i + 1 == viewModel.userProfile?.language?.id
//                    )
//                )
//            }
//
//            viewModel.languageListLiveData.value = list
        }

        viewModel.languageListLiveData.observe(viewLifecycleOwner, Observer {
            setAdapter()

        })

        if (viewModel.screenType == PREFERENCES.SCREEN_SELECT) {
            binding.tvSubheading.text = baseActivity.getString(R.string.select_language_desc)
        } else {
            binding.tvSubheading.text = baseActivity.getString(R.string.select_language_subtext)
        }

    }

    private fun initUi() {
//        initLanguageList()
        binding.tvTitle.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.select_language)).endPos(6)
                .isBold().getSpanString()

        )
    }

    private fun setAdapter() {
        if (!viewModel.languageListLiveData.value.isNullOrEmpty()) {
            adapter?.notifyDataSetChanged() ?: kotlin.run {
                adapter =
                    LanguageAdapter(
                        viewModel.languageListLiveData.value!!,
                        viewModel.languageSingleSelection,
                        viewModel.from
                    )
                binding.rvLanguage.adapter = adapter
                adapter!!.setOnAdapterItemClickListener(this)
            }
        } else {
            adapter?.notifyDataSetChanged()
            adapter = null
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
                                if (viewModel.languageSingleSelection) {
                                    themeData.isSelected = position == index
                                } else if (position == index) {
                                    themeData.isSelected = !themeData.isSelected
                                }
                            }
                        }

                    setAdapter()
//                    if (parentFragment is NavHostFragment) {
//
//                        val bundle = arguments ?: Bundle()
//                        bundle.putString(
//                            "language_code",
//                            viewModel.languageListLiveData.value?.singleOrNull { it.isSelected }?.code
//                        )
//
//                        setFragmentResult("languageData", bundle)
//                        findNavController().navigateUp()
//                    }
                }
            }
        }

    }

    override fun onApiRetry(apiCode: String) {

    }


}