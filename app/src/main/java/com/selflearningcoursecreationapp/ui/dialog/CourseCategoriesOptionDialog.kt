package com.selflearningcoursecreationapp.ui.dialog

import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogCourceCateBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AdapterCourseCategory
import com.selflearningcoursecreationapp.ui.preferences.PreferenceViewModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.LANGUAGE_CONSTANT
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseCategoriesOptionDialog() : BaseBottomSheetDialog<BottomDialogCourceCateBinding>(),
    BaseAdapter.IViewClick {

    private var list = ArrayList<CategoryData>()
    private val viewModel: PreferenceViewModel by viewModel()
    private var mAdapter: AdapterCourseCategory? = null
    private var type: Int = DialogType.CATEGORY
    private var selectedId: Int = 0
    override fun getLayoutRes() = R.layout.bottom_dialog_cource_cate
    override fun initUi() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            type = it.getInt("type")
            if (it.containsKey("selectedId")) {
                selectedId = it.getInt("selectedId") ?: 0
            }
        }

        when (type) {
            DialogType.CATEGORY -> {
                binding.tvTitle.text = baseActivity.getString(R.string.course_categories)
                binding.etSearch.hint = baseActivity.getString(R.string.search_category)
                viewModel.getCategories(false)
                observeCategoryData()
                binding.etSearch.visible()
            }
            DialogType.LANGUAGE -> {
                list.clear()
                list.addAll(arguments?.getParcelableArrayList<CategoryData>("list") ?: ArrayList())
                list.forEach {
                    if (it.id == selectedId) {
                        it.isSelected = true
                    }
                }
                setAdapter(list)
                binding.etSearch.gone()
                binding.tvTitle.text = baseActivity.getString(R.string.course_language)
//                setLanguageData()
            }
        }

//            setProfessionalAdapter(list)
//        }

        searchFunctionality()
    }

    private fun searchFunctionality() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                mAdapter?.notifyDataSetChanged()
                mAdapter = null
                setAdapter(list)

            } else {
                val dataList = ArrayList<CategoryData>()
                list?.forEach {
                    if (it.name?.toLowerCase()
                            ?.contains(text.toString().toLowerCase()) == true
                    ) {
                        dataList.add(it)
                    }
                }
                mAdapter?.notifyDataSetChanged()
                mAdapter = null
                setAdapter(dataList)


            }
        }

    }

    private fun observeCategoryData() {
        viewModel.categoryListLiveData.observe(viewLifecycleOwner, Observer {
            list.addAll(it)
            list.forEach { catData ->
                if (catData.id == selectedId) {
                    catData.isSelected = true
                }
            }
            setAdapter(list)
        })

    }

    private fun setLanguageData() {
        val nameList =
            baseActivity.resources.getStringArray(R.array.language_array)
        val codeArray = arrayListOf(
            LANGUAGE_CONSTANT.ENGLISH,
            LANGUAGE_CONSTANT.HINDI,
            LANGUAGE_CONSTANT.TELUGU,
            LANGUAGE_CONSTANT.TAMIL,
            LANGUAGE_CONSTANT.KANNADA,
            LANGUAGE_CONSTANT.BENGALI
        )
        list = ArrayList<CategoryData>()
        for (i in nameList.indices) {
            list.add(
                CategoryData(
                    nameList[i],
                    codeArray[i],
                    id = i + 1,
                    isSelected = false
                )
            )
        }

        setAdapter(list)
    }

    private fun setAdapter(list: ArrayList<CategoryData>) {
        mAdapter?.notifyDataSetChanged() ?: kotlin.run {
            mAdapter = AdapterCourseCategory(list)
            binding.recyclerCourceCategory.adapter = mAdapter
            mAdapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        dismiss()
        if (items.isNotEmpty()) {
            val click = items[0] as Int
            val value = items[1]
            when (click) {
                Constant.CLICK_VIEW -> {
                    onDialogClick(type, value)
                }
            }

        }
    }


}