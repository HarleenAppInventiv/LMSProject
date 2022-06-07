package com.selflearningcoursecreationapp.ui.create_course.review

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCourseReviewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant


class CourseReviewFragment : BaseFragment<FragmentCourseReviewBinding>(), BaseAdapter.IViewClick {
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment() })
    private var adapter: CourseReviewAdapter? = null
    private var keywordAdapter: CourseKeywordAdapter? = null
    private var suggestionAdapter: KeywordSuggestionAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_course_review
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    //    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        viewModel.getCourseSections()
//    }
    private fun initUi() {
        adapter?.notifyDataSetChanged()
        adapter = null
        keywordAdapter?.notifyDataSetChanged()
        keywordAdapter = null
        viewModel.courseData.value?.keywords?.removeAll { it == null }
        val manager = FlexboxLayoutManager(baseActivity, FlexDirection.ROW, FlexWrap.WRAP)
        binding.rvKeywords.layoutManager = manager
        binding.tvSections.text = baseActivity.getQuantityString(
            R.plurals.section_course_quantity,
            viewModel.courseData.value?.sectionData?.size
        )
        binding.etKeywords.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                keywordsFunctionality(binding.etKeywords.content())

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }


        binding.etKeywords.doOnTextChanged { text, start, before, count ->
            if (text.toString().contains(" ")) {
                keywordsFunctionality(binding.etKeywords.content())
            } else if (text.toString().length >= 2) {
                viewModel.getKeywords(text.toString())
            } else if (text.toString().isNullOrEmpty()) {
                suggestionAdapter?.notifyDataSetChanged()
                suggestionAdapter = null
                binding.clSuggestion.gone()
//                binding.etKeywords.setAdapter(null)

            }
        }
        setAdapter()
        setKeywordAdapter()

        observeKeywordData()
        binding.etKeywords.setOnFocusChangeListener { view, b ->
//            if (!b && !binding.rvKeywords.hasFocus()) {
//                suggestionAdapter?.notifyDataSetChanged()
//                suggestionAdapter = null
//                binding.clSuggestion.gone()
//
//            }
        }
        observeSectionData()

//        binding.parentNSV.setOnTouchListener { view, motionEvent ->
//            if (!binding.etKeywords.hasFocus()) {
//                suggestionAdapter?.notifyDataSetChanged()
//                suggestionAdapter = null
//                binding.clSuggestion.gone()
//
//            }
//            return@setOnTouchListener false
//
//        }

    }


    private fun observeSectionData() {
//        viewModel.getCourseSections()

        viewModel.reviewUpdationData.observe(
            viewLifecycleOwner,
            { event ->
                event.getContentIfNotHandled()?.let {
                    viewModel.courseData.value?.notifyPropertyChanged(BR.allDataEntered)
                    when (it) {
                        Constant.CLICK_DETAILS -> {
//                            binding.btAddSection.gone()
                            adapter?.notifyDataSetChanged()
                            adapter = null
                            setAdapter()
                            viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                        }

                    }
                }

            })
    }

    private fun keywordsFunctionality(keyword: String) {
        if (!keyword.isBlank() && viewModel.courseData.value?.keywords?.size ?: 0 < 10) {
            if (viewModel.courseData.value?.keywords.isNullOrEmpty()) {
                viewModel.courseData.value?.keywords = ArrayList()
            }
            viewModel.courseData.value?.keywords!!.add(keyword)
            binding.etKeywords.text?.clear()

            setKeywordAdapter()
        } else if (!keyword.isBlank()) {
            showToastShort(baseActivity.getString(R.string.max_keywords_limit))
        }
    }


    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = CourseReviewAdapter(
                viewModel.courseData.value?.sectionData ?: ArrayList(),
                viewModel.courseData.value?.createdById ?: 0
            )
            binding.rvSections.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }

    private fun setKeywordAdapter() {
        binding.etKeywords.isEnabled = viewModel.courseData.value?.keywords?.size ?: 0 < 10
        keywordAdapter?.notifyDataSetChanged() ?: kotlin.run {
            if (!viewModel.courseData.value?.keywords.isNullOrEmpty()) {
                keywordAdapter =
                    CourseKeywordAdapter(viewModel.courseData.value?.keywords ?: ArrayList())
                binding.rvKeywords.adapter = keywordAdapter
                keywordAdapter!!.setOnAdapterItemClickListener(this)
            }
        }
    }

    private fun observeKeywordData() {
        viewModel.keywordsData.observe(viewLifecycleOwner, Observer {

            binding.rvSuggestions.adapter?.notifyDataSetChanged()
            suggestionAdapter?.notifyDataSetChanged()
            suggestionAdapter = null

            if (it.isNotEmpty()) {
                setKeywordSuggestionAdapter(it)
            }
            binding.clSuggestion.visibleView(!it.isNullOrEmpty() && !binding.etKeywords.isBlank())

        })
    }

    private fun setKeywordSuggestionAdapter(arrayList: ArrayList<SingleChoiceData>) {
        val titleList = arrayList.map { it.title + " " } as ArrayList<String>
//     val suggestionAdapter= ArrayAdapter<String>(baseActivity,android.R.layout.simple_list_item_1,titleList.toTypedArray())
//        binding.etKeywords.setAdapter(suggestionAdapter)
//        suggestionAdapter.setNotifyOnChange(true)

        suggestionAdapter?.notifyDataSetChanged() ?: kotlin.run {
            suggestionAdapter = KeywordSuggestionAdapter(arrayList)
            binding.rvSuggestions.adapter = suggestionAdapter
            suggestionAdapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    val data = items[1] as SingleChoiceData
                    keywordsFunctionality(data.title ?: "")
                }
                Constant.CLICK_DELETE -> {
                    val position = items[1] as Int
                    viewModel.courseData.value?.keywords?.removeAt(position)
                    setKeywordAdapter()
                }
            }
        }
    }

    override fun onLoading(message: String, apiCode: String?) {
        if (!apiCode.equals(ApiEndPoints.API_GET_KEYWORDS))
            super.onLoading(message, apiCode)
    }

}

