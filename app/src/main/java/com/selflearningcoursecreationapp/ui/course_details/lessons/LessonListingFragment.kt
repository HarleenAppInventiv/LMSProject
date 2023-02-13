package com.selflearningcoursecreationapp.ui.course_details.lessons

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentLessonListingBinding
import com.selflearningcoursecreationapp.extensions.isLectureFailed
import com.selflearningcoursecreationapp.extensions.isLectureInProcessing
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.models.offline.OfflineLessonData
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.moderator.ModeratorActivity
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.downloadManager.util.Log


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

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    private fun init() {
        viewModel.sectionData.value?.clear()
        adapter?.notifyDataSetChanged()
        adapter = null


        viewModel.getLessonList()


        viewModel.sectionData.observe(viewLifecycleOwner) {
            setAdapter()
        }



        binding.rvLessons.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        viewModel.viewPagerScroll.value = true
                    }
                    else -> {
                        viewModel.viewPagerScroll.value = false

                    }
                }
            }
        })

        binding.ivEye.setOnClickListener {
            CommonAlertDialog.builder(baseActivity)
                .title("")
                .notCancellable(false)
                .description(
                    getString(R.string.in_this_mode_you_need_to_access_all_the_content_in_sequential_order_only_a_particular_lesson_will_only_be_accessible_only_if_you_complete_all_the_lessons_before_it)
                )
                .positiveBtnText(baseActivity.getString(R.string.okay))
                .hideNegativeBtn(true)
                .icon(R.drawable.ic_info_large)
                .setThemeIconColor(true)
                .build()
        }

    }

    private fun setAdapter() {
//        if (viewModel.courseData.value?.courseMandatory == true) {
//
//        }

        binding.tvContentFreeze.visibleView(viewModel.courseData.value?.courseMandatory == true)
        binding.ivEye.visibleView(viewModel.courseData.value?.courseMandatory == true)

        viewModel.sectionData.value?.forEachIndexed { index, sectionModel ->
            Log.e("looping", "index$index")

            sectionModel.lessonList.forEachIndexed { childIndex, childModel ->
                if (viewModel.courseData.value?.courseMandatory == true) {
                    sectionModel.lessonList.set(childIndex, childModel).isEnabled =
                        childIndex == 0 && index == 0 || childModel.lectureIsCompleted == true || childModel.lecturePercentageCompleted != 0.0

                }

                sectionModel.lessonList.set(childIndex, childModel).sectionId =
                    sectionModel.sectionId ?: 0
                sectionModel.lessonList.set(childIndex, childModel).sectionId =
                    sectionModel.sectionId ?: 0
                sectionModel.lessonList.set(childIndex, childModel).sectionTitle =
                    sectionModel.sectionTitle
                sectionModel.lessonList.set(childIndex, childModel).sectionDesc =
                    sectionModel.sectionDescription
                sectionModel.lessonList.set(childIndex, childModel).courseTitle =
                    viewModel.courseData.value?.courseTitle
                sectionModel.lessonList.set(childIndex, childModel).courseDesc =
                    viewModel.courseData.value?.courseDescription
                sectionModel.lessonList.set(childIndex, childModel).courseCategory =
                    viewModel.courseData.value?.categoryName


            }
            if (viewModel.courseData.value?.courseMandatory == true) {


                for (i in sectionModel.lessonList.indices) {
                    val a = i + 1

                    if (sectionModel.lessonList.get(i).lectureIsCompleted == true && a <= sectionModel.lessonList.size - 1) {


                        sectionModel.lessonList.set(
                            a,
                            sectionModel.lessonList.get(a)
                        ).isEnabled = true


                    }
                }
                viewModel.sectionData.value?.let {
                    for (i in it.indices) {
                        val b = i + 1

                        if (viewModel.sectionData.value?.get(i)?.sectionPercentageCompleted?.toInt() == 100 && b <= it.size - 1) {
                            viewModel.sectionData.value?.set(
                                b,
                                viewModel.sectionData.value?.get(b) ?: SectionModel()
                            )?.lessonList?.set(
                                0,
                                viewModel.sectionData.value?.get(b)?.lessonList?.get(0)
                                    ?: ChildModel()
                            )?.isEnabled = true
                        }
                    }
                }


            }
            if (viewModel.sectionData.value.isNullOrEmpty()) {
                adapter?.notifyDataSetChanged()
                adapter = null
            } else {


                adapter?.notifyDataSetChanged() ?: kotlin.run {
                    adapter = CourseSectionAdapter(
                        viewModel.sectionData.value ?: ArrayList(),
                        baseActivity.tokenFromDataStore(),
                        (viewModel.courseData.value?.status != CreatedCourseStatus.SUBMIT || viewModel.courseContentType.equals(
                            "moderatorComments"
                        )),
                        viewModel.courseData.value?.courseMandatory,
                        viewModel.userProfile?.id == viewModel.courseData.value?.createdById || baseActivity is ModeratorActivity
                    )
                    binding.rvLessons.adapter = adapter
                    adapter?.setOnAdapterItemClickListener(this)
                }
            }


//    else {
//            if (viewModel.sectionData.value.isNullOrEmpty()) {
//                adapter?.notifyDataSetChanged()
//                adapter = null
//            } else {
//                adapter?.notifyDataSetChanged() ?: kotlin.run {
//                    adapter = CourseSectionAdapter(
//                        viewModel.sectionData.value ?: ArrayList(),
//                        baseActivity.tokenFromDataStore(),
//                        (viewModel.courseData.value?.status != CreatedCourseStatus.SUBMIT || viewModel.courseContentType.equals(
//                            "moderatorComments"
//                        )),
//                        viewModel.courseData.value?.courseMandatory,
//                        viewModel.userProfile?.id == viewModel.courseData.value?.createdById || baseActivity is ModeratorActivity
//                    )
//                    binding.rvLessons.adapter = adapter
//                    adapter?.setOnAdapterItemClickListener(this)
//                }
//            }


        }
        binding.noDataTV.visibleView(viewModel.sectionData.value.isNullOrEmpty())

    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int

        when (type) {
            Constant.CLICK_VIEW -> {
                showToastShort(baseActivity.getString(R.string.this_course_is_in_content_freeze_mode_to_access_this_lesson_you_must_complete_all_lessons_before_this))

            }

            Constant.CLICK_QUIZ_REPORT -> {
                val innerPosition = items[2] as Int
                if (viewModel.userProfile?.id != viewModel.courseData.value?.createdById) {

                    findNavController().navigateTo(
                        R.id.action_courseDetailsFragment_to_quizReportFragment,
                        bundleOf(
                            "attemptId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.attemptedId,
                            "courseId" to viewModel.courseId,
                            "lessonList" to viewModel.sectionData.value?.get(position)?.lessonList,
                            "section" to viewModel.sectionData.value,

                            "isQuizReport" to true
                        )
                    )
                }
            }

            Constant.CLICK_PLAY -> {
                val innerPosition = items[2] as Int

                if (viewModel.userProfile?.id == viewModel.courseData.value?.createdById) {
                    if (viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.contentStatus.isLectureInProcessing() or viewModel.sectionData.value?.get(
                            position
                        )?.lessonList?.get(
                            innerPosition
                        )?.contentStatus.isLectureFailed()
                    ) {
                        showToastShort(baseActivity.getString(R.string.your_lecture_under_processing))
                    } else {
                        navigationToContent(position, innerPosition, MODTYPE.MODERATOR)
                    }
                } else if (baseActivity.tokenFromDataStore().isNotEmpty()) {
                    if (viewModel.courseData.value?.userCourseStatus == CourseStatus.NOT_ENROLLED) {
                        if (viewModel.sectionData.value?.get(0)?.lessonList?.get(0)?.mediaType == MediaType.VIDEO
                            && (viewModel.sectionData.value?.get(0)?.lessonList?.size
                                ?: 0) > 1
                            && position == 0
                            && innerPosition == 0
                        ) {
                            playVideo(position, innerPosition, MODTYPE.MODERATOR)

                        } else {
                            showToastShort(baseActivity.getString(R.string.this_feature_is_accessible_after_you_enroll_course))
                        }
                    } else {
                        navigationToContent(position, innerPosition, MODTYPE.LEARNER)
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

    fun navigationToContent(position: Int, innerPosition: Int, modType: Int) {
        viewModel.lessonList.clear()
        viewModel.sectionData.value?.let { section ->
            for (i in section.indices) {
                viewModel.sectionData.value?.get(i)?.lessonList?.let { lessonList ->
                    for (j in lessonList?.indices) {
                        viewModel.lessonList.add(lessonList.get(j))
                    }
                }
            }
        }
        val offlineCourseData: OfflineCourseData =
            getOfflineCourseData(position, innerPosition)

        when (viewModel.sectionData.value?.get(position)?.lessonList?.get(
            innerPosition
        )?.mediaType) {

            MediaType.QUIZ -> {
                if (modType == MODTYPE.LEARNER) {
                    findNavController().navigateTo(
                        R.id.action_global_quizBaseFragment,
                        bundleOf(
                            "quizId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.quizId,
                            "title" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.lectureTitle,
                            "courseId" to viewModel.courseId,
                            "sectionId" to viewModel.sectionData.value?.get(position)?.sectionId,

                            "lessonList" to viewModel.lessonList,
                            "innerPosition" to setPositionData(
                                viewModel.lessonList,
                                viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                    innerPosition
                                )?.lectureId
                            ),
                            "attemptId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.attemptedId,
                            "position" to position,
                            "modType" to modType,
                            "from" to 1,
                            "lessonId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.lectureId,

                            "section" to viewModel.sectionData.value,
                            "isCompleted" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.lectureIsCompleted,
                            "courseStatus" to viewModel.courseData.value?.courseStatus,


                            "isQuizReport" to true,
                            "freezeContent" to viewModel.courseData.value?.courseMandatory

                        )
                    )
                } else {
                    findNavController().navigateTo(
                        R.id.action_global_quiZListForModCreatorFragment,
                        bundleOf(
                            "quizId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.quizId,
                            "title" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.lectureTitle,
                            "courseId" to viewModel.courseId,
                            "lessonList" to viewModel.lessonList,
                            "lessonId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.lectureId,
                            "innerPosition" to setPositionData(
                                viewModel.lessonList,
                                viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                    innerPosition
                                )?.lectureId
                            ), "section" to viewModel.sectionData.value,
                            "modType" to modType,
                            "courseStatus" to viewModel.courseData.value?.courseStatus,

                            "position" to position


                        )
                    )
                }
            }
            MediaType.DOC -> {
//                val offlineCourseData: OfflineCourseData =
//                    getOfflineCourseData(position, innerPosition)

                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
                    bundleOf(
//                        "fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "type" to modType,
                        "offlineCourseData" to offlineCourseData,
                        "courseId" to viewModel.courseData.value?.courseId,
                        "sectionId" to viewModel.sectionData.value?.get(position)?.sectionId,
                        "lessonId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureId,
                        "title" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureTitle,
                        "lessonList" to viewModel.lessonList,
                        "innerPosition" to setPositionData(
                            viewModel.lessonList,
                            viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.lectureId
                        ),
                        "position" to position,
                        "isCompleted" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureIsCompleted,
                        "modType" to modType,
                        "lessonId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureId,
                        "courseStatus" to viewModel.courseData.value?.courseStatus,
                        "section" to viewModel.sectionData.value,
                        "freezeContent" to viewModel.courseData.value?.courseMandatory

                    )
                )
            }
            MediaType.TEXT -> {
//
//                val offlineCourseData: OfflineCourseData =
//                    getOfflineCourseData(position, innerPosition)

                findNavController().navigateTo(
                    R.id.action_global_pdfViewerFragment,
                    bundleOf(
                        //"fileUri" to "https://web.stanford.edu/class/archive/cs/cs161/cs161.1168/lecture4.pdf",
                        "type" to modType,
                        "courseId" to viewModel.courseData.value?.courseId,
                        "sectionId" to viewModel.sectionData.value?.get(position)?.sectionId,
                        "offlineCourseData" to offlineCourseData,
                        "lessonId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureId,
                        "title" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureTitle,
                        "lessonList" to viewModel.lessonList,
                        "innerPosition" to setPositionData(
                            viewModel.lessonList,
                            viewModel.sectionData.value?.get(position)?.lessonList?.get(
                                innerPosition
                            )?.lectureId
                        ),
                        "isCompleted" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureIsCompleted,
                        "lessonId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                            innerPosition
                        )?.lectureId,
                        "position" to position,
                        "modType" to modType,
                        "section" to viewModel.sectionData.value,
                        "courseStatus" to viewModel.courseData.value?.courseStatus,

                        "freezeContent" to viewModel.courseData.value?.courseMandatory

                    )
                )
            }
            MediaType.VIDEO, MediaType.AUDIO -> {
                playVideo(position, innerPosition, modType)
//
            }
            Constant.CLICK_LESSON -> {

            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }

    fun playVideo(position: Int, innerPosition: Int, modType: Int) {
        val offlineCourseData: OfflineCourseData =
            getOfflineCourseData(position, innerPosition)
        findNavController().navigateTo(
            R.id.action_global_videoBaseFragment,
            bundleOf(
                "courseId" to viewModel.courseData.value?.courseId,
                "status" to viewModel.courseData.value?.status,
                "offlineCourseData" to offlineCourseData,
                "type" to modType,
                "sectionId" to viewModel.sectionData.value?.get(position)?.sectionId,
                "lectureId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                    innerPosition
                )?.lectureId,
                "isCompleted" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                    innerPosition
                )?.lectureIsCompleted,

                "courseStatus" to viewModel.courseData.value?.courseStatus,


                "title" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                    innerPosition
                )?.lectureTitle,
                "lessonList" to viewModel.lessonList,
                "innerPosition" to setPositionData(
                    viewModel.lessonList,
                    viewModel.sectionData.value?.get(position)?.lessonList?.get(
                        innerPosition
                    )?.lectureId
                ),
                "position" to position,
                "modType" to modType,

                "section" to viewModel.sectionData.value,
                "lessonId" to viewModel.sectionData.value?.get(position)?.lessonList?.get(
                    innerPosition
                )?.lectureId,
                "freezeContent" to viewModel.courseData.value?.courseMandatory


            )
        )
    }

    private fun setPositionData(lessonList: ArrayList<ChildModel?>, lectureId: Int?): Int {
        var position = 0

        lessonList.forEachIndexed { index, childModel ->
            if (childModel?.lectureId == lectureId) {
                position = index

            }

        }
        return position


    }


    private fun getOfflineCourseData(position: Int, innerPosition: Int): OfflineCourseData {
        val offlineCourseData = OfflineCourseData()


        offlineCourseData.userId = viewModel.userProfile?.id ?: 0
        offlineCourseData.courseId = viewModel.courseId
        offlineCourseData.description = viewModel.courseData.value?.courseDescription
        offlineCourseData.createdByName = viewModel.courseData.value?.createdByName
        offlineCourseData.categoryTypeName = viewModel.courseData.value?.categoryName
        offlineCourseData.title = viewModel.courseData.value?.courseTitle

        val offlineSection = OfflineSectionData()
        offlineSection.courseId = viewModel.courseId
        offlineSection.userId = viewModel.userProfile?.id ?: 0
        offlineSection.description =
            viewModel.sectionData.value?.get(position)?.sectionDescription
        offlineSection.sectionId = viewModel.sectionData.value?.get(position)?.sectionId
        offlineSection.title = viewModel.sectionData.value?.get(position)?.sectionTitle

        val offlineLesson = OfflineLessonData()
        offlineLesson.duration =
            viewModel.sectionData.value?.get(position)?.lessonList?.get(innerPosition)?.lectureContentDuration
        offlineLesson.lessonId =
            viewModel.sectionData.value?.get(position)?.lessonList?.get(innerPosition)?.lectureId
        offlineLesson.title =
            viewModel.sectionData.value?.get(position)?.lessonList?.get(innerPosition)?.lectureTitle
        offlineLesson.type =
            viewModel.sectionData.value?.get(position)?.lessonList?.get(innerPosition)?.mediaType

        val offlineLessonList: ArrayList<OfflineLessonData> = ArrayList()
        offlineLessonList.add(offlineLesson)
        offlineSection.lessonList = offlineLessonList


        val offlineSectionList: ArrayList<OfflineSectionData> = ArrayList()
        offlineSectionList.add(offlineSection)


        offlineCourseData.sectionList = offlineSectionList
        return offlineCourseData
    }


}