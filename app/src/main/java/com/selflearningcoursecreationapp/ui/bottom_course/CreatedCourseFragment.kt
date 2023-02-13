package com.selflearningcoursecreationapp.ui.bottom_course

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.FragmentMyCourseBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseScreenType
import com.selflearningcoursecreationapp.utils.CreatedCourseStatus
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreatedCourseFragment : BaseFragment<FragmentMyCourseBinding>(), BaseAdapter.IViewClick,
    BaseAdapter.IListEnd {
    private val viewModel: MyCourseVM by viewModel()
    private val parentVM: MyCourseVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private var mAdapter: CreatedCourseAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_course
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {
        binding.tvNoData.text = getString(R.string.no_course_created_yet)
        binding.tvNoDataDesc.text =
            getString(R.string.you_dont_have_any_courses_in_your_created_list)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.reset()
            reset()
        }


        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.tvNoData.text = getString(R.string.course_list_empty)


//        binding.tvNoData.setOnClickListener {
//            binding.llNoWishlist.gone()
//            binding.rvCourse.visible()
//            binding.rvCourse.adapter = MyCourseAdapter(Constant.COURSE_IN_PROGRESS)
//
//        }
        onGoingDataObserver()
//        viewModel.getCourses(CourseScreenType.MYCOURSES)

        binding.rvCourse.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        parentVM.viewPagerScroll.value = true
                    }
                    else -> {
                        parentVM.viewPagerScroll.value = false

                    }
                }
            }
        })
    }

    private fun reset() {
        viewModel.reset()
        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.getCourses(CourseScreenType.MYCOURSES)
    }

    override fun onResume() {
        super.onResume()

//        viewModel.reset()
        reset()
    }

    private fun setAdapter() {

        if (viewModel.courseLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    CreatedCourseAdapter(
                        viewModel.courseLiveData.value ?: ArrayList()
                    )
                binding.rvCourse.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
                mAdapter?.setOnPageEndListener(this)
            }
        }
        binding.rvCourse.visibleView(!viewModel.courseLiveData.value.isNullOrEmpty())
        binding.llNoWishlist.visibleView(viewModel.courseLiveData.value.isNullOrEmpty())
    }

    override fun onApiRetry(apiCode: String) {
        binding.swipeRefresh.isRefreshing = false
        viewModel.onApiRetry(apiCode)
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        super.onException(isNetworkAvailable, exception, apiCode)
        binding.swipeRefresh.isRefreshing = false

    }

    override fun onLoading(message: String, apiCode: String?) {
        when (apiCode) {
            ApiEndPoints.API_ALL_COURSES, ApiEndPoints.API_GUEST_ALL_COURSES -> {
                if (!binding.swipeRefresh.isRefreshing) {
                    super.onLoading(message, apiCode)

                }
            }
            else -> {
                super.onLoading(message, apiCode)

            }
        }
    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                if (viewModel.courseLiveData.value?.get(position)?.status != CreatedCourseStatus.DRAFT) {
                    viewModel.courseId =
                        viewModel.courseLiveData.value?.get(position)?.courseId ?: 0
                    findNavController().navigateTo(
                        R.id.action_global_contentCourseDetailFragment,
                        bundleOf(
                            "courseId" to viewModel.courseLiveData.value?.get(position)?.courseId,
                            "status" to "creator"

                        )
                    )
                }

            }
            Constant.CLICK_EDIT -> {
                viewModel.courseId = viewModel.courseLiveData.value?.get(position)?.courseId ?: 0

//                if (BuildConfig.DEBUG) {
                val status = items[2] as Int
                if (status == CreatedCourseStatus.PARTIALREJECTED) {
                    viewModel.editToDraft(
                        viewModel.courseLiveData.value?.get(position)?.courseId ?: 0
                    )
                } else {
                    findNavController().navigateTo(
                        R.id.addCourseBaseFragment,
                        bundleOf(
                            "courseId" to viewModel.courseLiveData.value?.get(position)?.courseId,
                            "fromEdit" to true
                        )
                    )
                }
            }
            Constant.CLICK_DELETE -> {
                CommonAlertDialog.builder(baseActivity)
                    .title(getString(R.string.delete_course))
                    .description(getString(R.string.are_you_sure_you_want_to_delete))
                    .positiveBtnText(getString(R.string.yes))
                    .icon(R.drawable.ic_delete_icon)
                    .getCallback {
                        if (it) {
                            viewModel.courseId =
                                viewModel.courseLiveData.value?.get(position)?.courseId ?: 0
                            viewModel.adapterPosition = position
                            viewModel.deleteCourse()
                        }
                    }.build()

            }
//
        }

    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getCourses(CourseScreenType.MYCOURSES)

    }

    private fun onGoingDataObserver() {
        viewModel.courseLiveData.observe(viewLifecycleOwner) {
            setAdapter()

        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_ALL_COURSES, ApiEndPoints.API_GUEST_ALL_COURSES -> {
                if (!binding.swipeRefresh.isRefreshing) {
                    super.onResponseSuccess(value, apiCode)

                } else {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
            ApiEndPoints.API_EDIT_TO_DRAFT -> {

                findNavController().navigateTo(
                    R.id.addCourseBaseFragment,
                    bundleOf(
                        "courseId" to viewModel.courseId,

                        "fromEdit" to true
                    )
                )
            }
            ApiEndPoints.API_CRE_STEP_1 -> {
                (value as BaseResponse<UserProfile>).let {
                    super.onResponseSuccess(value, apiCode)
                    setAdapter()
                    showToastShort(it.message)
                }


            }
            else -> {
                super.onResponseSuccess(value, apiCode)

            }
        }

    }

}