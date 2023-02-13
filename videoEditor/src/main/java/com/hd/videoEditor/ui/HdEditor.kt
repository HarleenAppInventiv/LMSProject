package com.hd.videoEditor.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle

open class HdEditor {
    companion object {
        val REQUEST_EDIT = 100
        val EXTRA_PREFIX = "hdEditor"
        val EXTRA_INPUT_PATH = "$EXTRA_PREFIX.input_path"
        val EXTRA_OUTPUT_PATH = "$EXTRA_PREFIX.output_path"
        val EXTRA_THEME_COLOR = "$EXTRA_PREFIX.theme_color"
    }


    fun with(activity: Activity): Builder {
        return Builder(activity)
    }

    class Builder(private var activity: Activity) {
        private var intent: Intent? = null
        private var editorBundle: Bundle = Bundle()
        private var themeColor: String? = null
        fun inputPath(path: String? = null): Builder {

            editorBundle.putString(EXTRA_INPUT_PATH, path)
            return this
        }

        fun outputPath(path: String? = null): Builder {
            editorBundle.putString(EXTRA_OUTPUT_PATH, path)
            return this
        }

        fun themeColor(color: String): Builder {
            this.themeColor = color
            editorBundle.putString(EXTRA_THEME_COLOR, color)
            return this
        }

        fun createIntent(onResult: (Intent) -> Unit) {
            onResult(createIntent())
        }

        private fun createIntent(): Intent {
            val intent = Intent(activity, HdEditorActivity::class.java)
            intent.putExtras(editorBundle)
            return intent
        }
    }

}


