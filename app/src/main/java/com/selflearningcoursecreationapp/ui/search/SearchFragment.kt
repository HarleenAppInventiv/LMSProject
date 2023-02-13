package com.selflearningcoursecreationapp.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSearchBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import com.selflearningcoursecreationapp.utils.screenRecorder.PreferenceHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>(), BaseAdapter.IViewClick,
    BaseAdapter.IListEnd {

    var searchKeywordList: ArrayList<String>? = null
    private var mAdapter: SearchAdapter? = null
    private var popularSearchAdapter: PopularSearchesAdapter? = null
    private val viewModel: SearchFragmentVM by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_search
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
//        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getUserData()
        setObserver()

        elasticSearchFunctionality()

        baseActivity.setToolbar(showToolbar = false)
        val color = ThemeUtils.getAppColor(baseActivity)
        binding.toolbar.setBackgroundColor(color)
        mAdapter?.notifyDataSetChanged()
        mAdapter = null

        popularSearchAdapter?.notifyDataSetChanged()
        popularSearchAdapter = null

        searchKeywordList = arrayListOf<String>()

        PreferenceHelper(baseActivity).apply {

            searchKeywordList =
                getSearchList("" + viewModel.userProfile?.id + " " + context?.getString(R.string.pref_key_search_list))

            if (searchKeywordList == null) {
                searchKeywordList = arrayListOf()
            }
            setAdapter()
        }

        binding.imgBack.setOnClickListener {

            findNavController().popBackStack()
        }

        binding.tvClearAll.setOnClickListener {
            searchKeywordList?.clear()
            setAdapter()
            updateSharedPrefList()
        }

        binding.imgMic.setOnClickListener {
            displaySpeechRecognizer(this)
        }


    }

    private fun elasticSearchFunctionality() {
        binding.imgCross.setOnClickListener {
            viewModel.reset()
            binding.etSearch.text?.clear()
            binding.imgCross.gone()
            binding.imgMic.visible()
            setPopularSearchAdapter()

        }

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH && textView.text.toString().isNotEmpty()) {
                addElementToLocal(textView.text.toString())
            }

            return@setOnEditorActionListener true
        }

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (text.toString().isNotEmpty() && text.toString().length > 1) {
                viewModel.isEmpty = false
                viewModel.searchKeyword = text.toString()
                binding.imgCross.visible()
                binding.imgMic.gone()
                viewModel.reset()
                viewModel.searchListLiveData.postValue(ArrayList())
                viewModel.getElasticSearchData()

            }
//            else if (text.toString().isNotEmpty() && text.toString().length<=3) {
//                binding.imgCross.visible()
//                binding.imgMic.gone()
//                viewModel.reset()
//                setPopularSearchAdapter()
//            }
            else if (text.toString().isEmpty()) {
                viewModel.isEmpty = true
                viewModel.searchKeyword = ""
                binding.imgCross.gone()
                binding.imgMic.visible()
                viewModel.reset()
                viewModel.searchListLiveData.postValue(ArrayList())
                viewModel.getElasticSearchData()
            }


        }
    }

    private fun setObserver() {
        viewModel.searchListLiveData.observe(viewLifecycleOwner) {
            setPopularSearchAdapter()
        }

        spokenTextLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { value ->

                binding.etSearch.setText(value)
                viewModel.searchKeyword = value.toString()
                binding.imgCross.visible()
                binding.imgCross.gone()
                viewModel.reset()
                viewModel.searchListLiveData.postValue(ArrayList())
                viewModel.getElasticSearchData()

            }
        }

    }

    private fun setPopularSearchAdapter() {
        var searchList = viewModel.searchListLiveData.value ?: ArrayList()
        binding.rvPopularSearch.visibleView(!viewModel.searchListLiveData.value.isNullOrEmpty())
        binding.tvPopularSearch.visibleView(!viewModel.searchListLiveData.value.isNullOrEmpty())
        binding.view2.visibleView(!viewModel.searchListLiveData.value.isNullOrEmpty())


        if (viewModel.searchListLiveData.value.isNullOrEmpty()) {
            popularSearchAdapter?.notifyDataSetChanged()
            popularSearchAdapter = null
        } else {

            popularSearchAdapter =
                PopularSearchesAdapter(
                    (searchList?.distinctBy { it?.id ?: 0 }
                        ?: ArrayList()) as ArrayList<SearchList>)
            binding.rvPopularSearch.adapter = popularSearchAdapter
            popularSearchAdapter?.setOnAdapterItemClickListener(this)
            popularSearchAdapter?.setOnPageEndListener(this)

        }
    }

    private fun navigateToNext(searchData: String) {
        findNavController().navigateTo(
            R.id.action_searchFragment_to_categoryWiseCoursesFragment,
            bundleOf(
                "searchData" to searchData,
                "name" to getString(R.string.search_results),
                "viewType" to "search"
            )
        )
    }

    private fun setAdapter() {
        binding.rvSearch.visibleView(!searchKeywordList.isNullOrEmpty())
        binding.tvClearAll.visibleView(!searchKeywordList.isNullOrEmpty())
        binding.view.visibleView(!searchKeywordList.isNullOrEmpty())
        binding.tvNoData.visibleView(searchKeywordList.isNullOrEmpty())


        if (searchKeywordList.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    SearchAdapter(
                        searchKeywordList ?: ArrayList()
                    )

                binding.rvSearch.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
            }
        }
    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                navigateToNext(searchKeywordList?.get(position) ?: "")
            }

            Constant.CLICK_SUGGESTION -> {
                addElementToLocal(viewModel.searchListLiveData.value?.get(position)?.title)
            }

            Constant.SEARCH_CROSS -> {
                searchKeywordList?.removeAt(position)
                mAdapter?.notifyDataSetChanged()
                setAdapter()
                updateSharedPrefList()
            }

        }
    }

    private fun addElementToLocal(title: String?) {

        var searchListLen = searchKeywordList?.size

        title?.let { navigateToNext(it) }

        if (searchListLen != null && searchKeywordList?.contains(title) == false) {

            if (searchListLen >= 5) {
                searchKeywordList?.removeAt(0)

            }

            title?.let { searchKeywordList?.add(it) }
            setAdapter()
            updateSharedPrefList()

//                binding.etSearch.text?.clear()
        }


    }

    private fun updateSharedPrefList() {
        PreferenceHelper(baseActivity).apply {
            setSearchList(
                "" + viewModel.userProfile?.id + " " + context?.getString(R.string.pref_key_search_list),
                searchKeywordList
            )
//            searchList= searchKeywordList
        }
    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getElasticSearchData()
    }


}