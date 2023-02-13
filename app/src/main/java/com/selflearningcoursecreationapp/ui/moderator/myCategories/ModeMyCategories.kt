package com.selflearningcoursecreationapp.ui.moderator.myCategories

import android.os.Bundle
import android.view.View
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentMyCategoriesModeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ModeMyCategories : BaseFragment<FragmentMyCategoriesModeBinding>(),
    BaseAdapter.IViewClick {
    private val viewModel: MyCategoriesVM by viewModel()
    private var adapter: ModeMyCategoriesAdapter? = null
    private var languageAdapter: ModeMyLanguagesAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_categories_mode
    }

    fun refreshData() {
        viewModel.myCategories()
    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun onResume() {
        super.onResume()
        init()

    }

    private fun init() {
        val manager = FlexboxLayoutManager(baseActivity, FlexDirection.ROW, FlexWrap.WRAP)
        binding.rvLanguage.layoutManager = manager
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observe()
        viewModel.myCategories()
        callMenu()
    }

    private fun observe() {
        viewModel.myCategoriesLiveData.observe(viewLifecycleOwner) {
            setAdapter(it)
        }
    }

    private fun setAdapter(data: ModeratorMyCategories?) {
        adapter = ModeMyCategoriesAdapter(data?.categories as ArrayList<Categories>)
        binding.rvCategory.adapter = adapter

        languageAdapter = ModeMyLanguagesAdapter(data.languages as ArrayList<Languages>)
        binding.rvLanguage.adapter = languageAdapter
    }

    override fun onItemClick(vararg items: Any) {

    }


}