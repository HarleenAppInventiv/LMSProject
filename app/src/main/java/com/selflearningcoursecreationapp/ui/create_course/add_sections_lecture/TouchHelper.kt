package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class TouchHelper : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
    0
) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

//do implementation
    }

//                override fun onSelectedChanged(
//                    viewHolder: RecyclerView.ViewHolder?,
//                    actionState: Int
//                ) {
//                    super.onSelectedChanged(viewHolder, actionState)
//                    if (actionState == ACTION_STATE_DRAG) {
//                        viewHolder?.itemView?.scaleY = 1.3f
//                        viewHolder?.itemView?.alpha = 0.7f
//
//                    }
//                }

//                override fun clearView(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder
//                ) {
//                    super.clearView(recyclerView, viewHolder)
//                    viewHolder.itemView.scaleY = 1.0f
//                    viewHolder.itemView.alpha = 1.0f
//                }


}