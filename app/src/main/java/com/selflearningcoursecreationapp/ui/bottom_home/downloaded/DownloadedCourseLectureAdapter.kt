package com.selflearningcoursecreationapp.ui.bottom_home.downloaded

import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterDownloadedCourseLectureBinding
import com.selflearningcoursecreationapp.extensions.getMediaType
import com.selflearningcoursecreationapp.extensions.getTime
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.offline.OfflineLessonData
import com.selflearningcoursecreationapp.utils.Constant

class DownloadedCourseLectureAdapter(private var list: ArrayList<OfflineLessonData>) :
    BaseAdapter<AdapterDownloadedCourseLectureBinding>() {
    override fun getLayoutRes() = R.layout.adapter_downloaded_course_lecture

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterDownloadedCourseLectureBinding
        val context = binding.root.context
        val data = list[position]
        binding.tvLessonName.text = data.title
        val min =
//            if (data.mediaType != MediaType.QUIZ)
//                data.lectureContentDuration?.div(10000) ?: 0
//            else {

            data.duration ?: 0

//            }


        binding.tvLessonTime.text = min.getTime(context)
        binding.tvPercent.visibility = View.GONE
        binding.pbProgress.visibility = View.GONE

        val mediaType = data.type?.getMediaType(/*true*/)
        binding.tvLessonType.text = mediaType?.second?.let { context.getString(it) } ?: ""
//        binding.ivMediaFile.setImageResource(mediaType?.first ?: R.drawable.ic_docx_icon)
        binding.ivMediaFile.loadImage(
            mediaType?.first ?: R.drawable.ic_docx_icon
        )

//        binding.ivMediaFile.
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_PLAY, position)
        }

        binding.ivDelete.setOnClickListener {
            onItemClick(Constant.CLICK_OPTION_DELETE, position)
        }


    }

    override fun getItemCount() = list.size
}