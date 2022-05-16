package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSectionViewBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.Constant
import java.util.*

class AddSectionAdapter(private val users: ArrayList<SectionModel>) :
    BaseAdapter<AdapterSectionViewBinding>() {
//    var expandedItemPos = -1;

    override fun getLayoutRes() = R.layout.adapter_section_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSectionViewBinding
        binding.lecture = users[position]
        binding.llChild.gone()
        binding.btSave.gone()
        binding.rvLecture.gone()
        binding.ivVisible.setImageResource(R.drawable.ic_arrow_bottom)
        binding.ivMore.setOnClickListener {
            onItemClick(Constant.CLICK_MORE, holder.adapterPosition)
        }

        binding.tvSectionNumber.text =
            String.format(binding.root.context.getString(R.string.section), position + 1)
        binding.tvLectureNumber.text = users[position].lessonList.size.toString() + " Lecture"

        binding.llChild.visibleView(users[position].isVisible)
        if (users[position].isVisible) {
            binding.ivVisible.setImageResource(R.drawable.ic_arrow_top)
//            binding.tvLessonList.visible()
        } else {
            binding.ivVisible.setImageResource(R.drawable.ic_arrow_bottom)
//            binding.tvLessonList.gone()
        }

        binding.ivVisible.setOnClickListener {
            users.forEachIndexed { index, sectionModel ->
                if (index != position) {
                    sectionModel.isVisible = false
                } else {
                    sectionModel.isVisible = !sectionModel.isVisible
                }
            }
            notifyDataSetChanged()
//            if (users[position].expandedItemPos != -1) {
//                if (users[position].expandedItemPos == holder.adapterPosition) {
//                    users[position].expandedItemPos = -1
//                    notifyItemChanged(holder.adapterPosition)
//                } else {
//                    val lastExpandPos = users[position].expandedItemPos
//                    users[position].expandedItemPos = holder.adapterPosition
//                    notifyItemChanged(lastExpandPos!!)
//                    notifyItemChanged(holder.adapterPosition)
//                }
//            } else {
//                users[position].expandedItemPos = holder.adapterPosition
//                notifyItemChanged(holder.adapterPosition)
//            }
        }

        lectureFunctionality(position, binding, holder)


        binding.btSave.apply {
            isVisible = users[position].lessonList.isEmpty().not()

            setOnClickListener {
                onItemClick(
                    Constant.CLICK_SAVE,
                    position,
                    binding.etSectionTitle.content(),
                    binding.etSectionDesc.content()
                )
            }
        }


        binding.btUpload.setOnClickListener {

            onItemClick(Constant.CLICK_UPLOAD, position)
        }

        binding.executePendingBindings()

    }

    private fun lectureFunctionality(
        position: Int,
        binding: AdapterSectionViewBinding,
        holder: BaseViewHolder
    ) {
        if (!users[position].lessonList.isEmpty()) {
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
                        users[holder.adapterPosition].lessonList,
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
            binding.rvLecture.adapter = ChildLectureAdapter(users[position].lessonList).apply {
                var itemTouchHelper = ItemTouchHelper(touchHelper)
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


    override fun getItemCount() = users.size


}