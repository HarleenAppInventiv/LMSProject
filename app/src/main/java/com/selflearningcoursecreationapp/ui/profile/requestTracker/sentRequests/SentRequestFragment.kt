package com.selflearningcoursecreationapp.ui.profile.requestTracker.sentRequests

import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentSentRequestBinding
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.create_course.co_author.ExistsCoAuthorResponse
import com.selflearningcoursecreationapp.ui.create_course.co_author.InviteCoAuthorDialog
import com.selflearningcoursecreationapp.ui.profile.requestTracker.PagerViewEventsRequest
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SentRequestFragment : BaseFragment<FragmentSentRequestBinding>(), BaseDialog.IDialogClick {
    private val viewModel: RequestrackerVM by viewModel()
    var courseData: CourseData? = null

    private val adapter by lazy {
        SentRequestAdapter(viewModel) { type, data, _ ->
            courseData = data
            when (type) {
                0 -> {
                    viewModel.courseId = data.courseId ?: 0
                    viewModel.existsCoAuthorDetails()

                }
                1 -> {
                    viewModel.cancelReq(data.courseId.toString())
                }
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_sent_request

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()
    }


    private fun initUI() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            viewModel.sentRequestId = it.getInt("sentRequestId")
        }


        val filterData = GetReviewsRequestModel()
        filterData.pageSize = 20
        filterData.pageNumber = 1
        filterData.RequestType = viewModel.sentRequestId
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRequestResponse(filterData).observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }


        }

        binding.recyelerSentList.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.noDataTV.isVisible = isListEmpty
            handleLoading(loadState.source.refresh is LoadState.Loading)

            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            errorState?.let {

            }
        }


    }

    private fun handleLoading(b: Boolean) {

        if (b) {
            showLoading()
        } else {
            hideLoading()
        }
    }


    override fun onApiRetry(apiCode: String) {
    }

    override fun onDialogClick(vararg items: Any) {
//        viewModel.onViewEvent(PagerViewEventsRequest.Remove(courseData ?: CourseData()))
        initUI()
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_CANCEL_REQ -> {
                showToastShort((value as BaseResponse<UserProfile>).message)
                viewModel.onViewEvent(PagerViewEventsRequest.Remove(courseData ?: CourseData()))
                refreshData()
            }

            ApiEndPoints.API_EXISTS_COAUTHOR -> {

                var data = (value as BaseResponse<ExistsCoAuthorResponse>)
                when (data.resource?.coAuthorExists) {
                    true -> {

                        val finalSpannedStr = if (data.resource?.email.isNullOrEmpty()) {
                            val desc = String.format(
                                baseActivity.getString(R.string.course_coauthor_exists_desc_text),
                                data.resource?.phone
                            )
                            SpanUtils.with(
                                baseActivity, desc

                            ).startPos(63)
                                .endPos(63.plus(data.resource?.phone?.length ?: 0).plus(2)).isBold()
                                .getSpanString()
                        } else {

                            val firstString = String.format(
                                baseActivity.getString(R.string.course_coauthor_exists_desc_text_2),
                                data.resource?.phone
                            )
                            var firstHalfSpannedStr = SpanUtils.with(
                                baseActivity, firstString

                            ).startPos(63)
                                .endPos(63.plus(data.resource?.phone?.length ?: 0).plus(1)).isBold()
                                .getSpanString()

                            val secondString = String.format(
                                baseActivity.getString(R.string.course_coauthor_exists_desc_text_with_email),
                                data.resource?.email
                            )


                            var secondHalfStr =
                                SpanUtils.with(
                                    baseActivity,
                                    secondString
                                ).startPos(11)
                                    .endPos(11.plus(data.resource?.email?.length ?: 0).plus(2))
                                    .isBold().getSpanString()





                            SpannableString(TextUtils.concat(firstHalfSpannedStr, secondHalfStr))


                        }

                        CommonAlertDialog.builder(baseActivity)
                            .title(baseActivity.getString(R.string.coauthor_already_exists))
                            .spannedText(
                                finalSpannedStr
                            )
                            .positiveBtnText(baseActivity.getString(R.string.proceed))
                            .negativeBtnText(baseActivity.getString(R.string.cancel))
                            .icon(R.drawable.ic_assessment_submitted)
                            .getCallback {
                                if (it) {
                                    openCoAuthorDialog()
                                }
                            }
                            .build()
                    }
                    false -> {
                        openCoAuthorDialog()
                    }
                }


            }
        }
    }

    private fun openCoAuthorDialog() {
        InviteCoAuthorDialog().apply {
            arguments = bundleOf("courseId" to viewModel.courseId)
            setOnDialogClickListener(this@SentRequestFragment)
        }.show(childFragmentManager, "")
    }

    fun refreshData() {
        adapter.refresh()
    }

}