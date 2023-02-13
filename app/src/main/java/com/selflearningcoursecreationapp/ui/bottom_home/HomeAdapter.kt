package com.selflearningcoursecreationapp.ui.bottom_home

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterHomeBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CourseTypeModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.recyclerView.GravitySnapHelper
import com.selflearningcoursecreationapp.utils.recyclerView.ScrollStateHolder


class HomeAdapter(
    private var courseTypeList: ArrayList<CourseTypeModel>?,
    private var scrollStateHolder: ScrollStateHolder
) :
    BaseAdapter<AdapterHomeBinding>() {

    override fun getLayoutRes() = R.layout.adapter_home

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            getLayoutRes(),
            parent,
            false
        )

        val innerLLM = LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        innerLLM.setInitialPrefetchItemCount(3)
        binding.rvCourses.apply {
            layoutManager = innerLLM

        }
        val vh = VH(binding, scrollStateHolder).apply { onCreated() }
        return vh
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        courseTypeList?.get(position)?.let { (holder as VH).onBound(it) }
    }

    override fun getItemCount() = courseTypeList?.size ?: 0

    inner class VH(
        private var viewBinding: AdapterHomeBinding,
        private val scrollStateHolder: ScrollStateHolder
    ) :
        BaseViewHolder(viewBinding), ScrollStateHolder.ScrollStateKeyProvider {


        private val adapter = HomeCoursesAdapter(ArrayList())
        private val snapHelper = GravitySnapHelper(Gravity.START)
        private var currentItem: CourseTypeModel? = null
        private val layoutManager = LinearLayoutManager(
            viewBinding.root.context,
            RecyclerView.HORIZONTAL, false
        )

        override fun getScrollStateKey(): String? = currentItem?.title

        fun onCreated() {
            viewBinding.rvCourses.adapter = adapter
            adapter.setOnAdapterItemClickListener(object : IViewClick {
                override fun onItemClick(vararg items: Any) {
                    this@HomeAdapter.onItemClick(
                        items[0] as Int,
                        bindingAdapterPosition,
                        items[1] as Int
                    )
                }
            })
            viewBinding.rvCourses.layoutManager = layoutManager
            viewBinding.rvCourses.setHasFixedSize(true)
            viewBinding.rvCourses.itemAnimator?.changeDuration = 0
            snapHelper.attachToRecyclerView(viewBinding.rvCourses)
            scrollStateHolder.setupRecyclerView(viewBinding.rvCourses, this)

        }

        fun onBound(item: CourseTypeModel) {
            currentItem = item
            viewBinding.tvSeeAll.visibleView(
                !item.courses.isNullOrEmpty() && (item.courses?.size ?: 0) > 0
            )
            viewBinding.tvTitle.text = item.title
            viewBinding.tvSeeAll.contentDescription =
                viewBinding.tvSeeAll.text.toString() + viewBinding.tvTitle.text.toString()

//            adapter.setItems(item.texts)
            adapter.setItems(item.courses)
            viewBinding.tvSeeAll.setOnClickListener {
                this@HomeAdapter.onItemClick(Constant.CLICK_SEE_ALL, bindingAdapterPosition)
            }
            scrollStateHolder.restoreScrollState(viewBinding.rvCourses, this)
        }

        fun onRecycled() {
            scrollStateHolder.saveScrollState(viewBinding.rvCourses, this)
            currentItem = null
        }

        /**
         * If we fast scroll while this ViewHolder's RecyclerView is still settling the scroll,
         * the view will be detached and won't be snapped correctly
         *
         * To fix that, we snap again without smooth scrolling.
         */
        fun onDetachedFromWindow() {
            if (viewBinding.rvCourses.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                snapHelper.findSnapView(layoutManager)?.let {
                    val snapDistance = snapHelper.calculateDistanceToFinalSnap(layoutManager, it)
                    if (snapDistance!![0] != 0 || snapDistance[1] != 0) {
                        viewBinding.rvCourses.scrollBy(snapDistance[0], snapDistance[1])
                    }
                }
            }
        }
    }


}