package com.selflearningcoursecreationapp.ui.course_details

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentAuthorDetailsNewBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.scrollHandling
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.course_details.authorDetail.AuthorCoursesLIstFragment
import com.selflearningcoursecreationapp.ui.course_details.authorDetail.CoAuthorCoursesLIstFragment
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorDetailsData
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class AuthorDetailsFragment : BaseFragment<FragmentAuthorDetailsNewBinding>(),
    BaseDialog.IDialogClick {


    private val viewModel: AuthorDetailsVM by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_author_details_new
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            viewModel.authorUserId = it.getInt("userId")
        }
        initUi()
    }


    private fun initUi() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setAuthorDetailObserver()
        viewModel.getAuthorDetails()
        setTabLayout()
        scrollHandling()

//        binding.imgBack.setOnClickListener { findNavController().popBackStack() }
//        binding.imgTalk.setOnClickListener { baseActivity.checkAccessibilityService() }
    }

    private fun setTabLayout() {
        var bundle = Bundle()
        bundle.putInt("authorUserId", viewModel.authorUserId)

        var authorFrag = AuthorCoursesLIstFragment()
        var coAuthorFrag = CoAuthorCoursesLIstFragment()

        authorFrag.arguments = bundle
        coAuthorFrag.arguments = bundle

        val list = arrayListOf<Fragment>(
            authorFrag, coAuthorFrag
        )
        val nameArray = arrayListOf(
            baseActivity.getString(R.string.author),
            baseActivity.getString(R.string.co_author)
        )
        binding.vpCourses.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)

        binding.vpCourses.offscreenPageLimit = 1

        TabLayoutMediator(binding.tlCourses, binding.vpCourses) { tab, position ->
            tab.text = nameArray[position]


        }.attach()

        binding.tlCourses.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tlCourses.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )

        binding.tlCourses.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT_BOLD
                (tab?.customView as TextView?)?.isAllCaps = false
//                (tab?.customView as TextView?)?.setTextColor(
//                    ThemeUtils.getPrimaryTextColor(
//                        baseActivity
//                    )
//                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT
                (tab?.customView as TextView?)?.isAllCaps = false

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })
    }

    private fun setAuthorDetailObserver() {
        viewModel.authorDetailLiveData.observe(viewLifecycleOwner) {
            setData(it)
        }

    }

    private fun scrollHandling() {
        binding.toolbar.menu?.findItem(R.id.action_read)?.setOnMenuItemClickListener {
            baseActivity.checkAccessibilityService()
            return@setOnMenuItemClickListener true
        }
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->

            binding.toolbar.title = viewModel.authorDetailLiveData.value?.userName
            binding.toolbar.scrollHandling(-869, verticalOffset, -430)


        })
    }


    private fun setData(data: AuthorDetailsData?) {
        binding.txtUserName.text = data?.userName
        binding.imgProfileImage.loadImage(
            data?.profileUrl,
            R.drawable.ic_default_user_grey,
            data?.profileBlurHash
        )

        binding.tvDescription.visibleView(!data?.bio.isNullOrEmpty())

        ResizeableUtils.builder(binding.tvDescription).isBold(false)
            .isUnderline(false)
            .setFullText(data?.bio)
            .setFullText(R.string.read_more_arrow)
            .setLessText(R.string.read_less_arrow)
            .setSpanSize(0.9f)
            .showDots(true)
            .build()

//        if(data?.authorProfileCourses!=null)
//            binding.tvMyCourses.text= "${context?.getString(R.string.my_courses)} (${data?.authorProfileCourses?.size} ${context?.getString(R.string.courses)})"


    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


    override fun onDialogClick(vararg items: Any) {


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
                        if (it) {
                            baseActivity.onBackPressed()
                        }
                    }.build()
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
        }
    }
}



