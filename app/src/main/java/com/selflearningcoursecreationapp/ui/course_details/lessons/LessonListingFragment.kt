package com.selflearningcoursecreationapp.ui.course_details.lessons

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentLessonListingBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MediaType


class LessonListingFragment : BaseFragment<FragmentLessonListingBinding>(), BaseAdapter.IViewClick {
    private var adapter: CourseSectionAdapter? = null
    private val viewModel: CourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    override fun getLayoutRes(): Int {
        return R.layout.fragment_lesson_listing
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        adapter?.notifyDataSetChanged()
        adapter = null
        viewModel.getLessonList()


        viewModel.sectionData.observe(viewLifecycleOwner, Observer {
            setAdapter()
        })

    }

    private fun setAdapter() {
        binding.noDataTV.visibleView(viewModel.sectionData.value.isNullOrEmpty())
        if (viewModel.sectionData.value.isNullOrEmpty()) {
            adapter?.notifyDataSetChanged()
            adapter = null
        } else {
            adapter?.notifyDataSetChanged() ?: kotlin.run {
                adapter = CourseSectionAdapter(
                    viewModel.sectionData.value ?: ArrayList(),
                    baseActivity.tokenFromDataStore()
                )
                binding.rvLessons.adapter = adapter
                adapter?.setOnAdapterItemClickListener(this)
            }
        }

    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int

        when (type) {
            Constant.CLICK_VIEW -> {
//                if (position % 2 == 0) {
//                    val action =
//                        CourseDetailsFragmentDirections.actionCourseDetailsFragmentToQuizBaseFragment()
//                    findNavController().navigate(action)
//                } else {
//                    val action =
//                        CourseDetailsFragmentDirections.actionCourseDetailsFragmentToVideoBaseFragment()
//                    findNavController().navigate(action)
//                }

            }
            Constant.CLICK_PLAY -> {
                if (!baseActivity.tokenFromDataStore()
                        .isEmpty()
                ) {
                    if (viewModel.courseData.value?.userCourseStatus == 1) {
                        val innerPosition = items[2] as Int
                        when (viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.mediaType) {
                            MediaType.QUIZ -> {
                                findNavController().navigate(
                                    R.id.action_courseDetailsFragment_to_quizBaseFragment,
                                    bundleOf(
                                        "quizId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                            innerPosition
                                        )?.quizId,
                                        "title" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                            innerPosition
                                        )?.lectureTitle
                                    )
                                )
                            }
                            MediaType.DOC -> {
//                            findNavController().navigate(R.id.action_courseDetailsFragment_to_pdfReaderFragment,
//                                bundleOf("fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf"))
                            }
                            MediaType.TEXT -> {
                                findNavController().navigate(
                                    R.id.action_courseDetailsFragment_to_pdfReaderFragment,
                                    bundleOf("fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf")
                                )
                            }

                        }
                    } else {
                        showToastShort("This feature is accessible after you'll enroll the course")

                    }
                } else {
                    baseActivity.guestUserPopUp()

                }
            }
            Constant.CLICK_LESSON -> {

//                if (baseActivity.tokenFromDataStore().isEmpty()) {
//                    baseActivity.guestUserPopUp()
//                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }

}