package com.selflearningcoursecreationapp.ui.bottom_home.downloaded

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentDownloadedCourseBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.ui.offlineCourse.OfflineCourseVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel


class DownloadedCourseFragment : BaseFragment<FragmentDownloadedCourseBinding>(),
    BaseAdapter.IViewClick {

    var mAdapter: DownloadedCourseAdapter? = null
    private lateinit var DBCourseData: ArrayList<OfflineCourseData>
    private val DBViewModel: OfflineCourseVM by viewModel()


    override fun getLayoutRes() = R.layout.fragment_downloaded_course

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()
    }

    private fun initUI() {

        DBViewModel.getApiResponse().observe(viewLifecycleOwner, this)
        DBViewModel.getUserData()
        DBViewModel.getAllOfflineCourses()


    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)

        if (apiCode == ApiEndPoints.ALL_OFFLINE_COURSES) {
            if (value != null && (value as ArrayList<OfflineCourseData>).size > 0) {
                DBCourseData = value as ArrayList<OfflineCourseData>
                binding.noDataTV.visibility = View.GONE
                binding.offlineCourseRV.visibility = View.VISIBLE
                setAdapter(DBCourseData)
            } else {
                binding.noDataTV.visibility = View.VISIBLE
                binding.offlineCourseRV.visibility = View.GONE
                binding.noDataTV.text = "No Offline course added!!"
            }

        }
    }

    private fun setAdapter(dbCourseData: java.util.ArrayList<OfflineCourseData>) {
        mAdapter = DownloadedCourseAdapter(dbCourseData)
        binding.offlineCourseRV.adapter = mAdapter
        mAdapter?.setOnAdapterItemClickListener(this)
    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                findNavController().navigateTo(
                    R.id.action_downloadedCourseFragment_to_downloadedLessonFragment,
                    bundleOf(
                        "downloadCourseData" to DBCourseData[position]
                    )
                )
            }


        }

    }

    override fun onApiRetry(apiCode: String) {
    }


}