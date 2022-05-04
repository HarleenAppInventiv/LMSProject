package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAddSectionOrLectureBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.model.SectionModel
import com.selflearningcoursecreationapp.ui.dialog.SectionMoreDialog
import com.selflearningcoursecreationapp.ui.dialog.UploadDocOptionsDialog
import com.selflearningcoursecreationapp.utils.Constant
import java.util.*
import kotlin.collections.ArrayList


class AddSectionOrLectureFragment : BaseFragment<FragmentAddSectionOrLectureBinding>(),
    BaseAdapter.IViewClick, BaseBottomSheetDialog.IDialogClick {
    private var adapter: AddSectionAdapter? = null
    private var adapterPosition: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {


        setHasOptionsMenu(true)
        binding.llNoSection.visible()

        binding.btnAddSection.setOnClickListener {
            binding.llNoSection.gone()
            binding.rvSections.visible()

            setAdapter()

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun getLayoutRes() = R.layout.fragment_add_section_or_lecture
    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            adapterPosition = items[1] as Int
            val childPosition = items[2] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    Toast.makeText(requireContext(), "${adapterPosition}", Toast.LENGTH_SHORT)
                        .show()
                }
                Constant.CLICK_DELETE -> {
                    showToastShort("${childPosition}delete")
                }
                Constant.CLICK_MORE -> {
                    SectionMoreDialog().apply {
                        setOnDialogClickListener(this@AddSectionOrLectureFragment)
                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_UPLOAD -> {
                    UploadDocOptionsDialog().show(childFragmentManager, "")
                }
            }
        }
    }


    private fun setAdapter() {
        val users = ArrayList<SectionModel>()
        users.add(SectionModel("Section 1"))

        var touchHelper = object : TouchHelper() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {

                val recyclerviewAdapter = recyclerView.adapter as AddSectionAdapter
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
//                recyclerviewAdapter.moveItem(fromPosition, toPosition)
                Collections.swap(users, fromPosition, toPosition)
                recyclerviewAdapter.notifyItemMoved(fromPosition, toPosition)
                return false
            }

        }

        adapter?.notifyDataSetChanged() ?: kotlin.run {
            var itemTouchHelper = ItemTouchHelper(touchHelper)
            itemTouchHelper.attachToRecyclerView(binding.rvSections)
            adapter = AddSectionAdapter(users)
            binding.rvSections.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }


    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                Constant.CLICK_ADD -> {
                    showToastShort("add")
                }
                Constant.CLICK_DELETE -> {
                    showToastShort("delete")
                }
                Constant.CLICK_EDIT -> {
                    showToastShort("edit")
                }
            }
        }
    }


}