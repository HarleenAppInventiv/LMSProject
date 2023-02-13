package com.selflearningcoursecreationapp.ui.course_details.authorDetail

import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemAuthorCourseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.course_details.model.AuthorProfileCourses
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseStatus
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class AuthorCoursesAdapter(val list: ArrayList<AuthorProfileCourses>, var loggedInId: Int) :
    BaseAdapter<ItemAuthorCourseBinding>() {
    override fun getLayoutRes() = R.layout.item_author_course
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val binding = holder.binding as ItemAuthorCourseBinding
        val context = binding.root.context

        binding.bookmarkTimeG.visible()
        binding.tvCoin.gone()
        binding.progressG.gone()
        binding.btBuy.visibleView(list[position].createdById != loggedInId)
        binding.tvDuration.gone()
        binding.tvTime.visible()
        binding.tvNewPrice.visible()

        binding.tvName.text = list[position].courseTitle
        binding.tvAuthor.text = list[position].createdByName
        binding.tvAuthorCate.text = list[position].categoryTypeName
        binding.tvCourseLang.text = list[position].languageName
//        binding.tvLesson.text =
//            "${list[position].totalSections} " + binding.root.context.getString(R.string.lessons)
        binding.tvRating.text = "${list[position].averageRating}"
        binding.tvTime.text = list[position].courseDurations.getTime(context)


        binding.ivBookmark.gone()

        if (list[position].courseWishlisted == 0)
            binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_blue)
        else
            binding.ivBookmark.setImageResource(R.drawable.ic_blue_bookmark)

        binding.ivPreview.loadImage(
            list[position].courseBannerContentURL,
            R.drawable.ic_home_default_banner,
            position
        )

        binding.ivBookmark.setOnClickListener {
            onItemClick(Constant.CLICK_BOOKMARK, position)

        }
        binding.btBuy.setOnClickListener {
            onItemClick(Constant.CLICK_BUYBUTTON, position)
        }

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }


        var data = list[position]


//        binding.tvNewPrice.text =
//            String.format(context.getString(R.string.course_fee_symbol), data.courseFee)


        binding.tvLesson.text =
            context.getQuantityString(R.plurals.section_quantity, data.totalSections)
        //"${data.totalSections} " + binding.root.context.getString(R.string.lesson)


        when (data.userCourseStatus) {
            CourseStatus.NOT_ENROLLED -> {

                binding.tvCoin.text = data.rewardPoints.toString()

                binding.tvNewPrice.gone()
                binding.tvCoin.gone()
                binding.progressG.gone()
                binding.tvProgress.gone()
                when (data.courseTypeId) {
                    CourseType.REWARD_POINTS -> {
                        binding.tvCoin.visible()
                        binding.tvCoin.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }
                    CourseType.PAID -> {
                        binding.tvNewPrice.visible()
                        binding.tvNewPrice.text =
                            String.format("%s %s", data.currencySymbol, data.courseFee)
                    }
                    CourseType.FREE -> {
                        binding.tvNewPrice.visible()
                        binding.tvNewPrice.text = context.getString(R.string.free)

                    }
                    CourseType.RESTRICTED -> {
                        binding.tvNewPrice.visible()
                        binding.tvNewPrice.text = context.getString(R.string.restricted)
                    }
                }

            }

            CourseStatus.ENROLLED -> {

                binding.tvNewPrice.gone()
                binding.tvCoin.gone()
                binding.progressG.gone()
                binding.tvProgress.gone()
                binding.btBuy.text = context.getString(R.string.start)

            }

            CourseStatus.COMPELETD -> {

                binding.tvNewPrice.gone()
                binding.tvCoin.gone()
                binding.tvDuration.gone()
                binding.tvProgress.text =
                    (data.percentageCompleted?.toInt()).toString() + "% " + context.getString(R.string.completed)
                binding.pbProgress.apply {
                    max = 100
                    progress = data.percentageCompleted?.toInt() ?: max
                }
                binding.btBuy.text = context.getString(R.string.completed)

            }

            else -> {

                binding.progressG.visible()
                binding.bookmarkTimeG.gone()
                binding.tvNewPrice.gone()
//                    binding.tvDuration.visible()

                binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_resume)
                binding.btBuy.text = context.getString(R.string.resume)
                binding.progressG.visible()
                binding.tvProgress.text =
                    (data.percentageCompleted?.toInt()).toString() + "% " + context.getString(R.string.completed)
                binding.pbProgress.apply {
                    max = 100
                    progress = data.percentageCompleted?.toInt() ?: 0
                }
                binding.tvDuration.apply {
                    visibleView(
                        (data.percentageCompleted?.toInt()
                            ?: 0) > 0 && (data.percentageCompleted?.toInt()
                            ?: 0) < 100
                    )
                    val duration = data?.totalDurationLeft.milliSecToMin().toString()
                    val msg =
                        SpanUtils.with(
                            context,
                            "${duration}${context.getString(R.string.m_left_in_course)}"
                        )
                            .endPos(duration.length + 9)
                            .themeColor().getSpanString()


                    binding.tvDuration.setSpanString(msg)

                }


            }
        }




        binding.btBuy.setCourseButton(
            data.courseTypeId,
            data.userCourseStatus,
            data.paymentStatus
        )

        if (position + 1 == list.size) {
            onPageEnd()
        }


    }

}