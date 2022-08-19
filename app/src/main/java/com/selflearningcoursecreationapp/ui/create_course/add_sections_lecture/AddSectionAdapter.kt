package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSectionViewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MediaType
import java.util.*

class AddSectionAdapter(
    private val sectionData: ArrayList<SectionModel>,
    private var userId: Int,
    private var isCreator: Boolean,
    private var courseCreatorId: Int,
) :
    BaseAdapter<AdapterSectionViewBinding>(), View.OnTouchListener {
    override fun getLayoutRes() = R.layout.adapter_section_view

    @SuppressLint("DefaultLocale", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSectionViewBinding
        val context = binding.root.context
        val data = sectionData[position]
        val doEnable = isCreator || userId == data.sectionCreatedById

        binding.lecture = sectionData[position]
        binding.doEnable = doEnable
        binding.executePendingBindings()
        binding.etSectionDesc.setOnTouchListener(this)
        binding.etSectionTitle.setOnTouchListener(this)
        binding.logoGroup.visibleView(courseCreatorId != data.sectionCreatedById)
        binding.ivUserImage.loadImage(
            data.sectionCreatedByProfileURL,
            R.drawable.ic_default_user_grey
        )
        binding.ivUserLogo.loadImage(
            data.sectionLogoURL,
            R.drawable.ic_logo_default
        )
        binding.llChild.gone()
        binding.btSave.gone()
        binding.rvLecture.gone()

        binding.ivVisible.setImageResource(R.drawable.ic_arrow_bottom)
        binding.ivDelete.setOnClickListener {
            if (doEnable) {
                onItemClick(Constant.CLICK_DELETE, position)
            }
        }


        binding.etSectionDesc.doOnTextChanged { _, _, _, _ ->
            onItemClick(Constant.CLICK_TEXT_CHANGES, position)
        }

        binding.etSectionTitle.doOnTextChanged { _, _, _, _ ->
            onItemClick(Constant.CLICK_TEXT_CHANGES, position)
        }

//var millis= sectionData.get()

        var millis: Long = 0
        sectionData[position].lessonList.forEach {
            millis += if (it.mediaType != MediaType.QUIZ) {
                it.lectureContentDuration ?: 0
            } else {
                it.lectureContentDuration ?: 0
            }
        }
        binding.tvTotalTime.text = millis.getTime(context, true, true)

        binding.tvSectionNumber.text =
            String.format(binding.root.context.getString(R.string.section), position + 1)

        binding.tvLectureNumber.text = context.getQuantityString(
            R.plurals.lecture_quantity,
            sectionData[position].lessonList.size
        )

        binding.llChild.visibleView(sectionData[position].isVisible)
        binding.group.visibleView(!sectionData[position].isVisible && !data.lessonList.isNullOrEmpty())
        binding.tvLectureNumber.visibleView(!data.lessonList.isNullOrEmpty())

        if (sectionData[position].isVisible) {
            binding.ivVisible.setImageResource(R.drawable.ic_arrow_top)
        } else {
            binding.ivVisible.setImageResource(R.drawable.ic_arrow_bottom)
        }


        binding.tvLectureList.text = data.lessonList.getLessonCount(context).joinToString(", ")




        binding.ivVisible.setOnClickListener {
            sectionData.forEachIndexed { index, sectionModel ->
                if (index != position) {
                    sectionModel.isVisible = false
                } else {
                    sectionModel.isVisible = !sectionModel.isVisible
                }
            }
            notifyDataSetChanged()

        }

        lectureFunctionality(position, binding, holder, data)


        binding.btSave.apply {
            isVisible = sectionData[position].lessonList.isEmpty()
                .not() && sectionData[position].uploadLesson


        }

        binding.btSave
            .setOnClickListener {
//            notifyDataSetChanged()

                onItemClick(
                    Constant.CLICK_SAVE,
                    position,
                    binding.etSectionTitle.content(),
                    binding.etSectionDesc.content()
                )
            }

        binding.btUpload.setOnClickListener {

            onItemClick(Constant.CLICK_UPLOAD, position)
        }

        binding.executePendingBindings()


    }

    private fun lectureFunctionality(
        position: Int,
        binding: AdapterSectionViewBinding,
        holder: BaseViewHolder,
        data: SectionModel
    ) {
        if (sectionData[position].lessonList.isNotEmpty()) {
            binding.rvLecture.visible()
            val touchHelper = object : TouchHelper() {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {

                    val childLectureAdapter = recyclerView.adapter as ChildLectureAdapter
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    Collections.swap(
                        sectionData[holder.adapterPosition].lessonList,
                        fromPosition,
                        toPosition
                    )
                    childLectureAdapter.notifyItemMoved(fromPosition, toPosition)
                    onItemClick(
                        Constant.CLICK_SWAP,
                        holder.adapterPosition,
                        fromPosition,
                        toPosition
                    )

                    return false
                }
            }
            binding.rvLecture.adapter =
                ChildLectureAdapter(
                    sectionData[position].lessonList,
                    isCreator || userId == data.sectionCreatedById
                ).apply {
                    val itemTouchHelper = ItemTouchHelper(touchHelper)
                    itemTouchHelper.attachToRecyclerView(binding.rvLecture)
                    setOnAdapterItemClickListener(object : IViewClick {
                        override fun onItemClick(vararg items: Any) {
                            if (items.isNotEmpty()) {
                                val type = items[0] as Int
                                val childPosition = items[1] as Int
                                this@AddSectionAdapter.onItemClick(
                                    type,
                                    holder.adapterPosition,
                                    childPosition
                                )
                            }
                        }
                    })
                }
        } else {
            binding.rvLecture.gone()
        }
    }


    override fun getItemCount() = sectionData.size

}