package com.selflearningcoursecreationapp.ui.course_details.ratings

import com.bumptech.glide.Glide
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterReviewListBinding
import com.selflearningcoursecreationapp.extensions.getTimeAgo
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants


class ReviewNewAdapter(
    var list: ArrayList<CourseData>?,
    var tokenFromDataStore: String,
    private val clickListener: (type: Int, wishListItem: CourseData, position: Int) -> Unit
) : BaseAdapter<AdapterReviewListBinding>() {

    //    var list: ArrayList<CourseData>? = ArrayList()
    override fun getLayoutRes() = R.layout.adapter_review_list

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {


        val binding = holder.binding as AdapterReviewListBinding
        val data = list?.get(position)
        val context = binding.root.context
        binding.courseData = data
        binding.tvUserName.text = data?.name

        binding.tvLikeCount.apply {
            text = data?.totalLikes.toString()
            contentDescription = "$text users like this comment"
        }


        binding.tvReport.apply {
            setCompoundDrawablesWithIntrinsicBounds(
                0,
                if (data?.reportReviewAlready == true) R.drawable.flag_full_icon else R.drawable.ic_flag,
                0,
                0
            )
            if (data?.reportReviewAlready == true) {
                text = context.getString(R.string.reported)
            } else {
                text = context.getString(R.string.report)

            }
        }



        binding.tvDislike.apply {
            text = data?.totalDislikes.toString()
            contentDescription = "$text users dislike this comm ent"
        }
        binding.tvDate.text = getTimeAgo(data?.createdDate.toString())
//        binding.ivUserLogo.loadImage(
//            data?.profileUrl,
//            R.drawable.ic_default_user_grey,
//            data?.profileBlurHash
//        )

//        binding.ivUserLogo.loadImage(data?.profileUrl,R.drawable.ic_default_user)
        if (!data?.profileUrl.isNullOrEmpty()) {
            Glide.with(binding.rating.context).load(data?.profileUrl ?: "").into(binding.ivUserLogo)

        } else {
//    Glide.with(binding.rating.context).load(data?.profileUrl ?: "").into(binding.ivUserLogo)
            binding.ivUserLogo.setImageResource(R.drawable.ic_default_user_grey)
        }


        binding.rating.rating = data?.courseRating?.toFloat() ?: 0f
        binding.ivDislike.isClickable = true
        binding.ivLike.isClickable = true
        binding.tvReport.setOnClickListener {
            if (data != null) {
                clickListener.invoke(Constant.CLICK_REPORT, data, position)
            }
        }
        ResizeableUtils.builder(binding.tvDescription).isBold(false)
            .isUnderline(false)
            .setFullText(data?.contentDescription)
            .setFullText(R.string.read_more_arrow)
            .setLessText(R.string.read_less_arrow)
            .setSpanSize(0.9f)
            .showDots(true)
            .build()
        if (data?.userDisLiked == 1) {

            binding.ivDislike.changeBackground(ThemeConstants.TYPE_THEME)
        } else {
            binding.ivDislike.imageTintList = null


        }

        if (data?.userLiked == 1) {

            binding.ivLike.changeBackground(ThemeConstants.TYPE_THEME)
        } else {
            binding.ivLike.imageTintList = null


        }

        binding.ivLike.setOnClickListener {
            if (tokenFromDataStore.isNotEmpty()) {
                if (data != null) {
                    clickListener.invoke(1, data, position)
                }
                binding.ivLike.isClickable = false
            } else {
                if (data != null) {
                    clickListener.invoke(-1, data, position)
                }
            }


        }


        binding.ivDislike.setOnClickListener {
            if (tokenFromDataStore.isNotEmpty()) {
                if (data != null) {
                    clickListener.invoke(2, data, position)
                }
                binding.ivDislike.isClickable = false
            } else {
                if (data != null) {
                    clickListener.invoke(-1, data, position)
                }
            }

        }

        if (position + 1 == list?.size)
            onPageEnd()


    }

    fun setData(courseData: CourseData, position: Int) {
        list?.set(position, courseData)
//        courseData.notifyPropertyChanged(BR.userLiked)
//        courseData.notifyChange()
//    notifyItemChanged(position)

        notifyDataSetChanged()

    }

    fun setListData(argumentList: ArrayList<CourseData>) {
        list?.clear()
        list?.addAll(argumentList)
        notifyDataSetChanged()
//        notifyItemRangeChanged(0, list?.size ?: 0)
    }


}



