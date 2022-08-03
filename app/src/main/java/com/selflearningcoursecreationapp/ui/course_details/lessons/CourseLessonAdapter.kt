package com.selflearningcoursecreationapp.ui.course_details.lessons

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourseLessonBinding
import com.selflearningcoursecreationapp.extensions.getMediaType
import com.selflearningcoursecreationapp.extensions.getTime
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.Constant

class CourseLessonAdapter(private var list: ArrayList<ChildModel>) :
    BaseAdapter<AdapterCourseLessonBinding>() {
    override fun getLayoutRes() = R.layout.adapter_course_lesson

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterCourseLessonBinding
        val context = binding.root.context
        val data = list[position]
        binding.tvLessonName.text = data.lectureTitle
        val min =
//            if (data.mediaType != MediaType.QUIZ)
//                data.lectureContentDuration?.div(10000) ?: 0
//            else {

            data.lectureContentDuration ?: 0

//            }


        binding.tvLessonTime.text = min.getTime(context)


        val mediaType = data.mediaType?.getMediaType(true)
        binding.tvLessonType.text = mediaType?.second?.let { context.getString(it) } ?: ""
//        binding.ivMediaFile.setImageResource(mediaType?.first ?: R.drawable.ic_docx_icon)
        binding.ivMediaFile.loadImage(
            data.thumbNailURl,
            mediaType?.first ?: R.drawable.ic_docx_icon
        )

//        binding.ivMediaFile.
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_PLAY, position)
        }


    }

    override fun getItemCount() = list.size
}