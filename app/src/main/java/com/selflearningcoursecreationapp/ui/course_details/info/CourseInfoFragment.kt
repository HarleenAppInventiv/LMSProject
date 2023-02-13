package com.selflearningcoursecreationapp.ui.course_details.info

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentCourseInfoBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorDetailsData
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class CourseInfoFragment : BaseFragment<FragmentCourseInfoBinding>(), BaseAdapter.IViewClick {
    private val viewModel: CourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private var mAdapter: AuthorBioAdapter? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_course_info
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onResume() {
        super.onResume()
        binding.getRoot().requestLayout();
    }

    private fun initUi() {
        viewModel.courseData.observe(viewLifecycleOwner, Observer { courseData ->
            mAdapter = courseData?.courseCoAuthors?.let { AuthorBioAdapter(it) }
            binding.rvAuthor.adapter = mAdapter
            mAdapter?.setOnAdapterItemClickListener(this)
            binding.tvTitle.text = courseData?.courseTitle
            binding.wvInfo.settings?.apply {
//                val fontSize = baseActivity.resources.getDimensionPixelOffset(R.dimen.textField_10);
                setDomStorageEnabled(true);
                setJavaScriptEnabled(true);
                setCacheMode(WebSettings.LOAD_NO_CACHE);
//                defaultFixedFontSize = fontSize.toInt()
//                setTextSize(WebSettings.TextSize.SMALLEST);
//textZoom=fontSize.toInt()

//                fixedFontFamily=if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    baseActivity.resources.getFont(R.font.shizuru_test
//
////                        ThemeUtils.getFont(SelfLearningApplication.fontId, ThemeConstants.FONT_REGULAR)
//                    ).toString()
//                } else ({
//                    ResourcesCompat.getFont(
//                        baseActivity,
////                        ThemeUtils.getFont(SelfLearningApplication.fontId, ThemeConstants.FONT_REGULAR)
//                        R.font.shizuru_test
//                    )
//                }).toString()
//                fixedFontFamily= Typeface.createFromAsset(baseActivity.assets,"shizuru_test.ttf").toString()

            }
            var fontName = ThemeUtils.getFontName(SelfLearningApplication.fontId)


            val takeAways = "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        @font-face {\n" +
                    "            font-family: '${fontName.first}';\n" +
                    "            src: url('font/${
                        fontName.second

                    }');\n" +
                    "        }\n" +
                    "        #font {\n" +
                    "            font-family: '${fontName.first}';\n" +
                    "        }\n" +
                    "body {\n" +
                    "    font-family: ${fontName.first};\n" +
                    "font-size: 14px;\n" +
                    "color: #262626;" +
                    "}" +

                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    courseData?.keyTakeaways?.trim()?.replace("<p><br></p>", "\n") +
                    "</body></html>"

            binding.wvInfo.loadDataWithBaseURL(
                "file:///android_res/",
                takeAways ?: "",
                "text/html",
                "utf-8",
                "about:blank"
            );

        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.parentNSV.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->


            }
        }

    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_AUTHOR_PROFILE -> {
                if (baseActivity.tokenFromDataStore() == "") {
                    baseActivity.guestUserPopUp()
                } else {
                    viewModel.authorUserId =
                        viewModel.courseData.value?.courseCoAuthors?.get(position)?.id ?: 0
                    viewModel.getAuthorDetails()


                }
            }
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COURSE_AUTHOR_DETAIL -> {
                var data = value as BaseResponse<AuthorDetailsData>
                if (data.statusCode == HTTPCode.USER_NOT_FOUND) {
                    CommonAlertDialog.builder(baseActivity)
                        .description(data.message ?: "")
                        .positiveBtnText(baseActivity.getString(R.string.okay))
                        .icon(R.drawable.ic_alert)
                        .title("")
                        .notCancellable(true)
                        .hideNegativeBtn(true)
                        .getCallback {
                        }.build()
                }

                if (data.statusCode == HTTPCode.SUCCESS) {
                    findNavController().navigateTo(
                        R.id.action_courseDetailsFragment_to_authorDetailsFragment, bundleOf(
                            "userId" to viewModel.courseData.value?.courseCoAuthors?.get(0)?.id
                        )
                    )
                }
            }
        }
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (exception.statusCode) {
            HTTPCode.USER_NOT_FOUND -> {
                hideLoading()
                CommonAlertDialog.builder(baseActivity)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .icon(R.drawable.ic_alert)
                    .title("")
                    .notCancellable(true)
                    .hideNegativeBtn(true)
                    .getCallback {
                    }.build()


            }

        }
    }
}