package com.selflearningcoursecreationapp.utils.customViews

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import com.selflearningcoursecreationapp.R

class LMSSeekBar : AppCompatSeekBar {

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        initView(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView(context, attrs)
    }


    private fun initView(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.RichSeekBar, 0, 0)
        // Extract custom attributes into member variables
        val resID: Int
        try {
            resID = typedArray.getResourceId(R.styleable.RichSeekBar_vectorThumb, -1)
        } finally { // TypedArray objects are shared and must be recycled.
            typedArray.recycle()
        }

        if (resID != -1) {
            val imageView = LMSRichView(context, attrs)
            imageView.setVectorDrawable(resID)
            val drawable = imageView.getRichPath()
            thumb = context.getDrawable(resID)
            thumb.setColorFilter(ThemeUtils.getAppColor(context), PorterDuff.Mode.MULTIPLY);

//            thumb= drawable
        }


    }
}