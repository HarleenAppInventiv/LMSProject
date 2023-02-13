package com.selflearningcoursecreationapp.ui.course_details.video

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.databinding.FragmentSignLanguageBinding
import com.selflearningcoursecreationapp.extensions.getTime
import com.selflearningcoursecreationapp.extensions.loadImage


class SignLanguageFragment : BaseFragment<FragmentSignLanguageBinding>() {
    private val viewModel: ContentDetailViewModel by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    override fun getLayoutRes() = R.layout.fragment_sign_language

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    fun initUI() {
        binding.childLayout.parentCard.visibility = View.GONE
        binding.noDataTV.visibility = View.VISIBLE

        viewModel.lectureLiveData.observe(viewLifecycleOwner) {
            if (!it?.lectureSignLanguageThumbnailUrl.isNullOrEmpty()) {


                binding.childLayout.tvCourseName.text = it?.lectureTitle ?: ""
                binding.childLayout.tvCourseLength.text =
                    "${it?.lectureSignLanguageContentDuration.getTime(binding.root.context)}"
//                    "${getString(R.string.video)}   . ${
//                    String.format(
//                        "%d min %d sec",
//                        TimeUnit.MILLISECONDS.toMinutes(it?.lectureSignLanguageContentDuration ?: 0),
//                        TimeUnit.MILLISECONDS.toSeconds(it?.lectureSignLanguageContentDuration ?: 0) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(it?.lectureSignLanguageContentDuration ?: 0))
//                    )
//                }"


                binding.childLayout.LMSImageView4.loadImage(
                    it?.lectureSignLanguageThumbnailUrl ?: "",
                    R.drawable.ic_default_banner,
                    it?.lectureSignLanguageThumbnailBlurHash
                )
                binding.childLayout.parentCard.visibility = View.VISIBLE
                binding.noDataTV.visibility = View.GONE
            } else {

                binding.childLayout.parentCard.visibility = View.GONE
                binding.noDataTV.visibility = View.VISIBLE
            }



            binding.childLayout.parentCard.setOnClickListener {
                viewModel.playSignVideoLiveData.value =
                    EventObserver(viewModel.lectureLiveData.value)

            }
//            binding.childLayout.i.text = it?.lectureTitle ?: ""


        }

//        viewModel.getApiResponse().observe(viewLifecycleOwner,this)
//        binding.childLayout.

    }

    override fun onApiRetry(apiCode: String) {

    }

}