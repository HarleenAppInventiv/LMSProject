package com.selflearningcoursecreationapp.ui.bottom_home.categories

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentHomeCategoriesBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("NotifyDataSetChanged")
class HomeCategoriesFragment : BaseFragment<FragmentHomeCategoriesBinding>(),
    BaseAdapter.IViewClick {
    //    private var preferredAdapter: CategoryAdapter? = null
//    private var allAdapter: CategoryAdapter? = null
    private var mAdapter: HomeCategoryAdapter? = null
    private val viewModel: HomeCategoriesVM by viewModel()

    //    private val preferredCategories = ArrayList<CategoryData>()
//    private val otherCategories = ArrayList<CategoryData>()
    private val categories = ArrayList<CategoryData>()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_home_categories
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    private fun initUi() {

        binding.etSearch.text?.clear()
        reset()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getCategories()
        observeData()
//        binding.btCourses.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_homeCategoriesFragment_to_popularFragment,
//                bundleOf(
//                    "subTitle" to "",
//                    "title" to "All courses",
//                    "selectedList" to viewModel.selectedList
//                )
//            )
//        }

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            textChangeHandling(text)
        }


        spokenTextLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { value ->
                binding.etSearch.setText(value)

            }


        }

        binding.imgMic.setOnClickListener {
            displaySpeechRecognizer(this)


        }
    }

    private fun textChangeHandling(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            allHandling(viewModel.categoryLiveData.value)

        } else {

            reset()
            categories.addAll(viewModel.categoryLiveData.value?.filter { categoryData ->
                categoryData.name?.lowercase()?.contains(text.toString().lowercase()) == true
            } as ArrayList)

//            preferredCategories.addAll(viewModel.categoryLiveData.value?.preferredCategories?.filter { categoryData ->
//                categoryData.name?.lowercase()?.contains(text.toString().lowercase()) == true
//            } as ArrayList)
//            otherCategories.addAll(viewModel.categoryLiveData.value?.otherCategories?.filter { categoryData ->
//                categoryData.name?.lowercase()?.contains(text.toString().lowercase()) == true
//            } as ArrayList)
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

//    private fun allHandling(it: HomeAllCategoryResponse?) {
//        reset()
//        it?.let {
//            preferredCategories.addAll(it.preferredCategories ?: ArrayList())
//            otherCategories.addAll(it.otherCategories ?: ArrayList())
//        }
//
//        uiVisibility()
//
//    }

    private fun allHandling(it: ArrayList<CategoryData>?) {
        reset()
        categories.addAll(it ?: ArrayList())
        uiVisibility()

    }


    private fun uiVisibility() {
//        binding.rvPreferred.visibleView(preferredCategories.isNotEmpty())
//        binding.tvPreferred.visibleView(preferredCategories.isNotEmpty())
//        binding.rvOther.visibleView(otherCategories.isNotEmpty())
//        binding.tvOther.visibleView(otherCategories.isNotEmpty())
//
//        setPreferredAdapter()
//        setAllAdapter()

        binding.rvList.visibleView(!categories.isNullOrEmpty())
        binding.noDataTV.visibleView(categories.isNullOrEmpty() && !viewModel.isFirst)
        setAdapter()
    }

    private fun reset() {
        categories.clear()
        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.selectedList?.clear()
//        preferredCategories.clear()
//        preferredAdapter?.notifyDataSetChanged()
//        preferredAdapter = null
//
//        otherCategories.clear()
//        allAdapter?.notifyDataSetChanged()
//        allAdapter = null

    }

    //    private fun setPreferredAdapter() {
//
//        preferredAdapter?.notifyDataSetChanged() ?: kotlin.run {
//            preferredAdapter = CategoryAdapter(preferredCategories, true, 1)
//            binding.rvPreferred.adapter = preferredAdapter
//            preferredAdapter!!.setOnAdapterItemClickListener(this)
//        }
//
//    }
//
//    private fun setAllAdapter() {
//
//        allAdapter?.notifyDataSetChanged() ?: kotlin.run {
//            allAdapter = CategoryAdapter(otherCategories, true, 2)
//            binding.rvOther.adapter = allAdapter
//            allAdapter!!.setOnAdapterItemClickListener(this)
//        }
//
//    }
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
//                    if (!categories[position].id.isNullOrZero()) {
//                        viewModel.selectedList.add(categories.get(position))
//                    }
//                    findNavController().navigate(
//                        R.id.action_homeCategoriesFragment_to_popularFragment,
//                        bundleOf(
//                            "title" to categories[position].name,
//                            "selectedList" to viewModel.selectedList
//                        )
//                    )

                    findNavController().navigate(
                        R.id.action_homeCategoriesFragment_to_categoryWiseCoursesFragment, bundleOf(
                            "id" to (categories[position].id ?: 0),
                            "name" to (categories[position].name ?: baseActivity.getString(
                                categories[position].codeId!!
                            ))
                        )
                    )


//                    when (listType) {
//
//                        1 -> {
//
//                            preferredCategories.get(position).isSelected =
//                                !preferredCategories.get(position).isSelected
//
//                            if (preferredCategories.get(position).isSelected) {
//                                viewModel.selectedList.add(preferredCategories.get(position))
//                            } else {
//                                viewModel.selectedList.removeAll { category ->
//                                    category.id == preferredCategories.get(
//                                        position
//                                    ).id
//                                }
//                            }
//                        }
//                        2 -> {
//                            otherCategories.get(position).isSelected =
//                                !otherCategories.get(position).isSelected
//                            if (otherCategories.get(position).isSelected) {
//                                viewModel.selectedList.add(otherCategories.get(position))
//                            } else {
//                                viewModel.selectedList.removeAll { category ->
//                                    category.id == otherCategories.get(
//                                        position
//                                    ).id
//                                }
//                            }
//                        }
//                    }
//                    setAllAdapter()
//                    setPreferredAdapter()
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

    override fun onResume() {
        super.onResume()
//        binding.etSearch.text?.clear()
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

}