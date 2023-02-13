package com.selflearningcoursecreationapp.ui.bottom_home.downloaded

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDownloadedLessonBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.models.offline.OfflineLessonData
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData
import com.selflearningcoursecreationapp.ui.offlineCourse.AddOfflineCourseVM
import com.selflearningcoursecreationapp.ui.offlineCourse.OfflineCourseVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * A simple [Fragment] subclass.
 * Use the [DownloadedLessonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownloadedLessonFragment : BaseFragment<FragmentDownloadedLessonBinding>(),
    BaseAdapter.IViewClick {
    // TODO: Rename and change types of parameters
    private val addOfflineCourseVM: AddOfflineCourseVM by viewModel()
    private val DBViewModel: OfflineCourseVM by viewModel()

    private lateinit var mAdapter: DownloadedCourseSectionAdapter
    private lateinit var downloadedCource: OfflineCourseData
    var sectionList: ArrayList<OfflineSectionData>? = null


    override fun getLayoutRes() = R.layout.fragment_downloaded_lesson

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        arguments?.let {
            downloadedCource = it.get("downloadCourseData") as OfflineCourseData
        }
        addOfflineCourseVM.getApiResponse().observe(viewLifecycleOwner, this)
        DBViewModel.getApiResponse().observe(viewLifecycleOwner, this)

        setData()

    }

    private fun setData() {
        sectionList = downloadedCource.sectionList
        baseActivity.setToolbar(showToolbar = true, title = downloadedCource.title)

        try {
            if (downloadedCource != null && sectionList != null && sectionList!!.size > 0) {
                binding.noDataTV.visibility = View.GONE
                binding.RVLessons.visibility = View.VISIBLE
                setAdapter(sectionList!!)
            } else {
                binding.noDataTV.visibility = View.VISIBLE
                binding.RVLessons.visibility = View.GONE
                binding.noDataTV.text = getString(R.string.no_offline_lesson_added)
            }
        } catch (e: Exception) {
            binding.noDataTV.visibility = View.VISIBLE
            binding.RVLessons.visibility = View.GONE
            binding.noDataTV.text = getString(R.string.no_offline_lesson_added)
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        if (apiCode == ApiEndPoints.OFFLINE_COURSE) {
            downloadedCource = value as OfflineCourseData
            setData()
        } else if (apiCode == ApiEndPoints.DELETE_OFFLINE_COURSE) {
            baseActivity.onBackPressed()
        } else if (apiCode == ApiEndPoints.UPDATE_OFFLINE_COURSE) {
            DBViewModel.getOfflineCourse(downloadedCource.courseId!!)
        }
    }

    private fun setAdapter(sectionList: ArrayList<OfflineSectionData>) {
        mAdapter = DownloadedCourseSectionAdapter(sectionList)
        binding.RVLessons.adapter = mAdapter
        mAdapter?.setOnAdapterItemClickListener(this)
    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun onItemClick(vararg items: Any) {

        val type = items[0] as Int
        val positionSection = items[1] as Int
        val positionLesson = items[2] as Int



        when (type) {
            Constant.CLICK_PLAY -> {
                var offlineLessonSelected: OfflineLessonData =
                    (downloadedCource?.sectionList?.get(positionSection)?.lessonList?.get(
                        positionLesson
                    )
                        ?: null) as OfflineLessonData
                findNavController().navigateTo(
                    R.id.action_downloadedLessonFragment_to_downloadedLectureViewFragment,
                    bundleOf(
                        "offlineLessonSelected" to offlineLessonSelected
                    )
                )
            }
            Constant.CLICK_OPTION_DELETE -> {
                CommonAlertDialog.builder(baseActivity)
                    .title(getString(R.string.delete_lesson))
                    .description(getString(R.string.sure_delete_lecture))
                    .positiveBtnText(getString(R.string.yes))
                    .icon(R.drawable.ic_delete_icon)
                    .getCallback {
                        if (it) {
                            deleteDataFromDB(positionSection, positionLesson)
                        }
                    }.build()
            }
        }
    }

    private fun deleteDataFromDB(positionSection: Int, positionLesson: Int) {

        if (downloadedCource?.sectionList?.get(positionSection)?.lessonList?.size!! > 1) {
            downloadedCource?.sectionList?.get(positionSection)?.lessonList?.removeAt(positionLesson)
            updateCource()
        } else if (downloadedCource?.sectionList?.size!! > 1) {
            downloadedCource?.sectionList?.removeAt(positionSection)
            updateCource()
            //delete lesson and update db
        } else {
            downloadedCource.courseId
            addOfflineCourseVM.deleteCourse(downloadedCource)
            //delete cource from  downloadedCource.courseId
        }

    }

    private fun updateCource() {
        addOfflineCourseVM.updateOfflineCourse(downloadedCource!!)

    }
}