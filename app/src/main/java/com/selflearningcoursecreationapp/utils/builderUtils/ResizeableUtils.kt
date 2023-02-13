package com.selflearningcoursecreationapp.utils.builderUtils

import android.widget.TextView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class ResizeableUtils {
    companion object {
        fun builder(textview: TextView): Builder {
            return Builder(textview)
        }
    }

    class Builder(private var textview: TextView) {
        private var isShowDots: Boolean = false
        private var fullText: String = ""
        private var showFullText: Int = R.string.read_more_arrow
        private var showLessText: Int = R.string.read_less_arrow
        private var limit: Int = 100
        private var spanSize: Float = 1f
        private var isBold: Boolean = true
        private var isUnderline: Boolean = true
        private var spanColor: Int = ThemeUtils.getAppColor(textview.context)



        fun setFullText(fullString: String?): Builder {
            fullText = if (fullString.isNullOrEmpty()) {
                ""
            } else fullString
            return this
        }

//        @RequiresApi(Build.VERSION_CODES.P)
fun setFullText(fullId: Int = R.string.read_more): Builder {
    showFullText = fullId
//            textview.apply {
//                contentDescription = textview.context.getString(R.string.read_more)
//                isScreenReaderFocusable = true
//            }
    return this
}

        //        @RequiresApi(Build.VERSION_CODES.P)
        fun setLessText(lessId: Int = R.string.read_less): Builder {
            showLessText = lessId
//            textview.apply {
//                contentDescription = textview.context.getString(R.string.read_less)
//                isScreenReaderFocusable = true
//            }
            return this
        }

        fun setLimit(limit: Int = 100): Builder {
            this.limit = limit
            return this
        }

        fun setSpanSize(size: Float = 1f): Builder {
            this.spanSize = size
            return this
        }

        fun showDots(isShow: Boolean = false): Builder {
            this.isShowDots = isShow
            return this
        }

        fun setSpanColor(spanColor: Int = ThemeUtils.getAppColor(textview.context)): Builder {
            this.spanColor = spanColor

            return this
        }

        fun isUnderline(isUnderline: Boolean = true): Builder {
            this.isUnderline = isUnderline
            return this
        }

        fun isBold(isBold: Boolean = true): Builder {
            this.isBold = isBold
            return this
        }

        fun build() {
            if (fullText.isEmpty()) {
                textview.text = ""
            } else if (fullText.length <= limit) {
                textview.text = fullText
            } else {
                setLessText()

            }

        }

        private fun setLessText() {
            var limitNew = fullText.subSequence(0, limit).trim().length + 1

            val lessText =
                "${fullText.subSequence(0, limit).trim()}${
                    if (isShowDots) {
                        limitNew += 2
                        "..."
                    } else ""
                } ${textview.context.getString(showFullText)}"
            val spanText = SpanUtils.with(textview.context, lessText).startPos(limitNew)
                .textColor(spanColor).apply {
                    if (isUnderline) {
                        this.isUnderline()
                    }
                    if (isBold) {
                        this.isBold()
                    }
                }
                .setSpannedTextSize(spanSize)
                .getCallback {
                    setFullData()
                }.getSpanString()

            textview.setSpanString(spanText)
        }


        private fun setFullData() {
            val completeText = "${fullText.trim()} ${textview.context.getString(showLessText)}"
            val spanText =
                SpanUtils.with(textview.context, completeText).startPos(fullText.trim().length + 1)
                    .textColor(spanColor).apply {
                        if (isUnderline) {
                            this.isUnderline()
                        }
                        if (isBold) {
                            this.isBold()
                        }
                    }
                    .setSpannedTextSize(spanSize)
                    .getCallback {
                        setLessText()
                    }.getSpanString()
            textview.setSpanString(spanText)
        }

    }
}