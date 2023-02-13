package com.selflearningcoursecreationapp.ui.create_course.review

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterLectureViewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ModHomeConst
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class LectureViewAdapter(
    private val lessonList: ArrayList<ChildModel>,
    private val fromModerator: Boolean = false,
    private val pending: Int = 0
) :
    BaseAdapter<AdapterLectureViewBinding>() {
    override fun getLayoutRes() = R.layout.adapter_lecture_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterLectureViewBinding
        val context = binding.root.context
//        binding.tvLessonName.text = lessonList[position].lectureTitle
//        Toast.makeText(context, ""+pending, Toast.LENGTH_SHORT).show()
        ResizeableUtils.builder(binding.tvLessonName).isBold(false)
            .isUnderline(false)
            .setFullText(lessonList[position].lectureTitle)
            .setFullText(R.string.read_more_arrow)
            .setLessText(R.string.read_less_arrow)
            .setLimit(28)
            .setSpanSize(0.9f)
            .showDots(true)
//            .setSpanColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
            .build()

        binding.tvTime.text = lessonList.get(position).courseCommentCreatedDate.changeDateFormat()

        binding.ivDelete.visibleView(!lessonList.get(position).lectureComment.isNullOrEmpty() && pending == ModHomeConst.PENDING)
        binding.ivEdit.visibleView(!lessonList.get(position).lectureComment.isNullOrEmpty() && pending == ModHomeConst.PENDING)
        binding.tvComment.visibleView(!lessonList.get(position).lectureComment.isNullOrEmpty())
        binding.tvTime.visibleView(!lessonList.get(position).lectureComment.isNullOrEmpty())
        binding.ivComment.visibleView(lessonList.get(position).lectureComment.isNullOrEmpty() && pending == ModHomeConst.PENDING)

        binding.tvComment.setSpanString(
            SpanUtils.with(
                context,
                "${context.getString(R.string.comments_semicolon)} ${lessonList.get(position).lectureComment}"
            ).endPos(7).isBold().getSpanString()
        )


        if (fromModerator) {
            if (pending == ModHomeConst.PENDING) {
                binding.ivPlay.visible()
            }
            binding.ivPlay.setVectorDrawable(R.drawable.ic_play_content_filled)
        } else {
            binding.ivPlay.visible()
            binding.ivComment.gone()
            binding.ivPlay.setVectorDrawable(R.drawable.ic_play_file)

        }


        val min =
//            if (lessonList[position].mediaType != MediaType.QUIZ)
//                lessonList[position].lectureContentDuration?.div(10000) ?: 0
//            else {

            lessonList[position].lectureContentDuration ?: 0

//            }


        binding.tvLessonTime.text = min.getTime(context)



        binding.ivPlay.setOnClickListener {

            onItemClick(Constant.CLICK_PLAY, position)

        }


        binding.ivComment.setOnClickListener {
            onItemClick(Constant.CLICK_ADD_COMMENT_LEC, position)
        }

        binding.ivEdit.setOnClickListener {
            onItemClick(Constant.CLICK_EDIT_COMMENT_LEC, position)

        }
        binding.ivDelete.setOnClickListener {
            onItemClick(Constant.CLICK_DELETE_COMMENT_LEC, position)

        }


        val mediaType = lessonList[position].mediaType?.getMediaType(/*true*/)
        binding.tvLessonType.text = mediaType?.second?.let { context.getString(it) } ?: ""
        binding.ivMediaFile.loadImage(
            lessonList[position].thumbNailURl,
            mediaType?.first ?: R.drawable.ic_docx_icon
        )


    }

    override fun getItemCount() = lessonList.size

}