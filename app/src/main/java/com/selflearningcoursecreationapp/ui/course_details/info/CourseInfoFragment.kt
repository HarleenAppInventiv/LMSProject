package com.selflearningcoursecreationapp.ui.course_details.info

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.databinding.FragmentCourseInfoBinding
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class CourseInfoFragment : BaseFragment<FragmentCourseInfoBinding>() {
    private val viewModel: CourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    override fun getLayoutRes(): Int {
        return R.layout.fragment_course_info
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        viewModel.courseData.observe(viewLifecycleOwner, Observer { courseData ->
            binding.rvAuthor.adapter = courseData?.courseCoAuthors?.let { AuthorBioAdapter(it) }
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
            val fontName = ThemeUtils.getFontName(SelfLearningApplication.fontId)
            val takeAways = "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        @font-face {\n" +
                    "            font-family: '${fontName.first}';\n" +
                    "            src: url('font/${fontName.second}');\n" +
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
                    "</body>"

            binding.wvInfo.loadDataWithBaseURL(
                "file:///android_res/",
                takeAways ?: "",
                "text/html",
                "utf-8",
                "about:blank"
            );

        })


    }

    override fun onApiRetry(apiCode: String) {

    }
}