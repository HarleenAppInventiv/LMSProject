package com.selflearningcoursecreationapp.ui.create_course.review

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import com.selflearningcoursecreationapp.utils.MODTYPE
import com.selflearningcoursecreationapp.utils.MediaType

@SuppressLint("NotifyDataSetChanged")

class CourseReviewFragment : BaseFragment<FragmentCourseReviewBinding>(), BaseAdapter.IViewClick {
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment().requireParentFragment() })
    private var adapter: CourseReviewAdapter? = null
    private var keywordAdapter: CourseKeywordAdapter? = null

    private var suggestionAdapter: KeywordSuggestionAdapter? = null
    private lateinit var mainFragment: Fragment
    override fun getLayoutRes(): Int {
        return R.layout.fragment_course_review
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity.window.statusBarColor =
            ContextCompat.getColor(baseActivity, R.color.white)
        mainFragment = requireParentFragment().requireParentFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        enableFields()
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
        binding.etKeywords.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                keywordsFunctionality(binding.etKeywords.content())

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }


        binding.etKeywords.doOnTextChanged { text, _, _, _ ->
            when {
                /*text.toString().contains(" ") -> {
                    keywordsFunctionality(binding.etKeywords.content())
                }*/
                text.toString().length >= 2 -> {
                    viewModel.getKeywords(text.toString())
                }
                text.toString().isEmpty() -> {
                    suggestionAdapter?.notifyDataSetChanged()
                    suggestionAdapter = null
                    binding.clSuggestion.gone()

                }
            }
        }

        binding.svChangeDash1.isChecked = viewModel.courseData.value?.courseMandatory == true
        binding.svChangeDash1.setOnCheckedChangeListener { _, isChecked ->
            viewModel.courseData.value?.courseMandatory = isChecked
        }

//        binding.svChangeDash1.setOnClickListener {
//            binding.svChangeDash1.isChecked = binding.svChangeDash1.isChecked != true
//            viewModel.freezeContent = binding.svChangeDash1.isChecked
//
//        }
        setAdapter()
        setKeywordAdapter()

        observeKeywordData()

        observeSectionData()
    }

    private fun enableFields() {

        binding.parentNSV.isEnabled = viewModel.courseData.value?.enableFields ?: true
        binding.parentNSV.isClickable = viewModel.courseData.value?.enableFields ?: true

        binding.disableView.visibleView(!(viewModel.courseData.value?.enableFields ?: true))


        binding.parentNSV.alpha =
            if (viewModel.courseData.value?.enableFields != false) 1f else 0.3f
    }

    private fun observeSectionData() {

        viewModel.reviewUpdateData.observe(
            viewLifecycleOwner
        ) { event ->
            event.getContentIfNotHandled()?.let {
                viewModel.courseData.value?.notifyPropertyChanged(BR.allDataEntered)
                when (it) {
                    Constant.CLICK_DETAILS -> {
                        adapter?.notifyDataSetChanged()
                        adapter = null
                        setAdapter()
                        viewModel.courseData.value?.notifyPropertyChanged(BR.sectionDataAdded)
                    }

                }
            }

        }
    }

    private fun keywordsFunctionality(keyword: String) {
        if (keyword.isNotBlank() && viewModel.courseData.value?.keywords?.size ?: 0 < 10) {
            if (viewModel.courseData.value?.keywords.isNullOrEmpty()) {
                viewModel.courseData.value?.keywords = ArrayList()
            }
            if (viewModel.courseData.value?.keywords?.contains(keyword) == true) {
                showToastShort(baseActivity.getString(R.string.already_added))

            } else {
                viewModel.courseData.value?.keywords?.add(keyword)
                binding.etKeywords.text?.clear()
            }
            setKeywordAdapter()
        } else if (keyword.isNotBlank()) {
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
            adapter?.setOnAdapterItemClickListener(this)
        }
    }

    private fun setKeywordAdapter() {
        binding.etKeywords.isEnabled = viewModel.courseData.value?.keywords?.size ?: 0 < 10
        keywordAdapter?.notifyDataSetChanged() ?: kotlin.run {
            if (!viewModel.courseData.value?.keywords.isNullOrEmpty()) {
                keywordAdapter =
                    CourseKeywordAdapter(viewModel.courseData.value?.keywords ?: ArrayList())
                binding.rvKeywords.adapter = keywordAdapter
                keywordAdapter?.setOnAdapterItemClickListener(this)
            }
        }
    }

    private fun observeKeywordData() {
        viewModel.keywordsData.observe(viewLifecycleOwner) {

            binding.rvSuggestions.adapter?.notifyDataSetChanged()
            suggestionAdapter?.notifyDataSetChanged()
            suggestionAdapter = null

            if (it.isNotEmpty()) {
                setKeywordSuggestionAdapter(it)
            }
            binding.clSuggestion.visibleView(!it.isNullOrEmpty() && !binding.etKeywords.isBlank())

        }
    }

    private fun setKeywordSuggestionAdapter(arrayList: ArrayList<SingleChoiceData>) {
        suggestionAdapter?.notifyDataSetChanged() ?: kotlin.run {
            suggestionAdapter = KeywordSuggestionAdapter(arrayList)
            binding.rvSuggestions.adapter = suggestionAdapter
            suggestionAdapter?.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            when (items[0] as Int) {
                Constant.CLICK_VIEW -> {
                    val data = items[1] as SingleChoiceData
                    keywordsFunctionality(data.title ?: "")
                }
                Constant.CLICK_DELETE -> {
                    val position = items[1] as Int
                    viewModel.courseData.value?.keywords?.removeAt(position)
                    setKeywordAdapter()
                }
                Constant.CLICK_PLAY -> {
                    val position = items[1] as Int
                    val innerPosition = items[2] as Int


                    if (viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.contentStatus.isLectureInProcessing() || viewModel.courseData.value?.sectionData?.get(
                            position
                        )?.lessonList?.get(innerPosition)?.contentStatus.isLectureFailed()
                    ) {
                        showToastShort(baseActivity.getString(R.string.your_lecture_under_processing))
                    } else {
                        navigationToContent(position, innerPosition, MODTYPE.MODERATOR)

                    }
                }
            }
        }
    }

    override fun onLoading(message: String, apiCode: String?) {
        if (!apiCode.equals(ApiEndPoints.API_GET_KEYWORDS))
            super.onLoading(message, apiCode)
    }

    override fun onApiRetry(apiCode: String) {

    }

    private fun navigationToContent(position: Int, innerPosition: Int, modType: Int) {
        when (viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
            innerPosition
        )?.mediaType) {
            MediaType.QUIZ -> {
                mainFragment.findNavController().navigateTo(
                    R.id.action_global_quiZListForModCreatorFragment,
                    bundleOf(
                        "quizId" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.quizId,
                        "title" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureTitle,
                        "courseId" to viewModel.courseData.value?.courseId
                    )
                )
            }
            MediaType.DOC -> {
                mainFragment.findNavController().navigateTo(
                    R.id.action_addCourseBaseFragment_to_pdfReaderFragment,
                    bundleOf(
//                    "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "type" to modType,
                        "courseId" to viewModel.courseData.value?.courseId,
                        "sectionId" to viewModel.courseData.value?.sectionData?.get(position)?.sectionId,
                        "innerPosition" to innerPosition,
                        "position" to position,
                        "lessonId" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureId,
                        "lessonList" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList,

                        "title" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureTitle,
                        "section" to viewModel.courseData.value?.sectionData,


//                    "duration" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
//                        innerPosition
//                    )?.lectureContentDuration?.toInt(),
//                    "name" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
//                        innerPosition
//                    )?.lectureTitle
                    )
                )
            }
            MediaType.TEXT -> {
                mainFragment.findNavController().navigateTo(
                    R.id.action_addCourseBaseFragment_to_pdfReaderFragment,
                    bundleOf(
//                    "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "type" to modType,
                        "courseId" to viewModel.courseData.value?.courseId,
                        "sectionId" to viewModel.courseData.value?.sectionData?.get(position)?.sectionId,
                        "lessonId" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureId,
                        "innerPosition" to innerPosition,
                        "position" to position,
                        "lessonList" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList,
                        "title" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureTitle,

                        "section" to viewModel.courseData.value?.sectionData,
//                    "name" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
//                        innerPosition
//                    )?.lectureTitle,
//                    "duration" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
//                        innerPosition
//                    )?.lectureContentDuration?.toInt()
                    )
                )
            }
            MediaType.VIDEO, MediaType.AUDIO -> {
                mainFragment.findNavController().navigateTo(
                    R.id.action_global_videoBaseFragment,
                    bundleOf(
                        "courseId" to viewModel.courseData.value?.courseId,
                        "status" to viewModel.courseData.value?.status,
                        "type" to modType,
                        "sectionId" to viewModel.courseData.value?.sectionData?.get(position)?.sectionId,
                        "lectureId" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureId,
                        "title" to viewModel.courseData.value?.sectionData?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureTitle
                    )
                )
            }
            Constant.CLICK_LESSON -> {

//                if (baseActivity.tokenFromDataStore().isEmpty()) {
//                    baseActivity.guestUserPopUp()
//                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        baseActivity.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        baseActivity.window.clearFlags(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }
//
//    override fun onPause() {
//        super.onPause()
//        baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
//
//    }


}

