package com.selflearningcoursecreationapp.ui.preferences.language

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSelectLanguageBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant


class SelectLanguageFragment : BaseFragment<FragmentSelectLanguageBinding>(),
    BaseAdapter.IViewClick, View.OnClickListener {
    private var adapter: LanguageAdapter? = null

    private val viewModel: PreferenceViewModel by viewModels({ if (parentFragment != null) requireParentFragment() else this })

    override fun getLayoutRes(): Int {
        return R.layout.fragment_select_language
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = parentFragment


        initUi()
    }

//    private fun initLanguageList() {
//        if (viewModel.languageListLiveData.value.isNullOrEmpty()) {
//            val nameList =
//                baseActivity.resources.getStringArray(R.array.language_array)
//
//            val list = ArrayList<ThemeData>()
//            for (i in nameList.indices) {
//                list.add(
//                    ThemeData(
//                        nameList[i],
//                        0,
//                        i + 1 == LANGUAGE_CONSTANT.ENGLISH,
//                        i + 1
//                    )
//                )
//            }
//
//            viewModel.languageListLiveData.value = list
//        }
//    }

    private fun initUi() {
//        initLanguageList()
        binding.tvTitle.setSpanString(
            baseActivity.getString(R.string.select_language),
            endPos = 6,
            isBold = true
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
                    viewModel.languageListLiveData.value?.apply {
                        forEachIndexed { index, themeData ->
                            themeData.isSelected = position == index
                        }
                    }
                    setAdapter()
                }
            }
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
//            R.id.btn_apply -> {
//                if (parentFragment is NavHostFragment) {
//                    viewModel.saveLanguage()
//                    baseActivity.changeAppLanguage()
////                        findNavController().navigateUp()
//                    baseActivity.recreate()
//                } else {
//
////                    viewModel.saveLanguage()
////                    baseActivity.changeAppLanguage()
//                }
//            }
        }
    }
}