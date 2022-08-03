package com.selflearningcoursecreationapp.ui.moderator.courseDetails.content

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModCourseContentBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.moderator.courseDetails.ModCourseDetailVM

class ModCourseContentFragment : BaseFragment<FragmentModCourseContentBinding>(),
    BaseAdapter.IViewClick {
    private val viewModel: ModCourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private var adapter: ModCourseSectionAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_mod_course_content
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        adapter?.notifyDataSetChanged()
        adapter = null

        viewModel.sectionData.observe(viewLifecycleOwner, Observer {
            setAdapter()
        })

    }

    private fun setAdapter() {
        binding.noDataTV.visibleView(viewModel.sectionData.value.isNullOrEmpty())
        if (viewModel.sectionData.value.isNullOrEmpty()) {
            adapter?.notifyDataSetChanged()
            adapter = null
        } else {
            adapter?.notifyDataSetChanged() ?: kotlin.run {
                adapter = ModCourseSectionAdapter(viewModel.sectionData.value!!)
                binding.rvLessons.adapter = adapter
                adapter!!.setOnAdapterItemClickListener(this)
            }
        }

    }

    override fun onApiRetry(apiCode: String) {
    }

    override fun onItemClick(vararg items: Any) {

    }

}