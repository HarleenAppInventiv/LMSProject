package com.selflearningcoursecreationapp.utils.builderUtils

import android.webkit.WebSettings
import android.webkit.WebView
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class HtmlResizeableUtils {
    companion object {
        fun builder(webView: WebView): Builder {
            return Builder(webView)
        }
    }

    class Builder(private var webView: WebView) {
        private var showCompleteText: Boolean = false
        private var completeContent: String = ""
        private var formattedContent: String = ""

        private var charLimit: Int = Constant.DESC_CHAR_COUNT_MAX
        private var lineLimit: Int = 3
        private var showDots: Boolean = true

        private val startTagList = HashMap<Int, String>()
        private val endTagList = HashMap<Int, String>()
        private val contentList = HashMap<Int, String>()
        private var totalMapCount = 1
        private var finalContent: String = ""
        private var isLimitExceeds: Boolean = false
        private var mCallback: ((Boolean) -> Unit)? = null
        fun fullContent(content: String): Builder {
            completeContent = content
            return this
        }

        fun getCallback(callback: (limitExceeds: Boolean) -> Unit): Builder {
            mCallback = callback
            return this
        }

        fun limitInChar(limit: Int): Builder {
            charLimit = limit
            return this
        }

        fun lineLimit(limit: Int): Builder {
            lineLimit = limit
            return this
        }

        fun showDots(isShow: Boolean): Builder {
            showDots = isShow
            return this
        }


        fun build(): Builder {
            getFormattedContent()
            checkIfTextResizeable()

            if (isLimitExceeds && showDots) {
                finalContent += "..."
            }
            loadContentInWebView()
            mCallback?.invoke(isLimitExceeds)
            return this
        }

        private fun loadContentInWebView() {

            val content = if (showCompleteText) formattedContent else finalContent
            webView.settings.apply {
//                val fontSize = baseActivity.resources.getDimensionPixelOffset(R.dimen.textField_10);
                domStorageEnabled = true
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                loadWithOverviewMode = true
                layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

            }
            val fontName = ThemeUtils.getFontName(SelfLearningApplication.fontId)


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
                    "word-wrap: break-word;" +
                    "}" +

                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    content +
                    "</body></html>"

            webView.loadDataWithBaseURL(
                "file:///android_res/",
                takeAways,
                "text/html",
                "utf-8",
                "about:blank"
            )
        }

        private fun makeLimitedFormattedText() {
            var formattedString = ""
            var contentString = ""
            var lastStartTagIndex = 0
            var startTag = 0
            var limitPair: Pair<Boolean, Int>? = null

            limit@ for (i in 0 until totalMapCount) {
                if (startTagList.containsKey(i)) {
                    formattedString += startTagList[i].toString()
                    startTag = i
                } else if (endTagList.containsKey(i)) {
                    formattedString += endTagList[i].toString()
                    startTagList.remove(startTag)
                } else if (contentList.containsKey(i)) {
                    contentString += contentList[i].toString()
                    formattedString += contentList[i].toString()

                    //call limit and line count check , return type should be pair <Boolean, Int>
                    limitPair = checkIfLimitExceeds(contentString)
                    if (limitPair.first) {
                        val start = contentString.length - limitPair.second
                        val mainStart = formattedString.length - limitPair.second
                        contentString = contentString.removeRange(start, contentString.length)
                        formattedString =
                            formattedString.removeRange(mainStart, formattedString.length)



                        lastStartTagIndex = i
                        break@limit
                    }
                }
            }

            for (i in lastStartTagIndex until 0) {
                if (startTagList.containsKey(i))
                    formattedString += "</" + startTagList[i].toString().substringAfter("<")
            }
            formattedString = formattedString.replace("\n", "<br>")
            isLimitExceeds = limitPair?.first ?: false
            finalContent = formattedString
        }

        private fun extractAllData() {
            var tag = ""
            var content = ""
            formattedContent.forEachIndexed { index, c ->
                if (c.toString().equals("<")) {
                    if (content.isNotEmpty()) {
                        contentList.put(totalMapCount, content)
                        totalMapCount += 1
                    }
                    tag = "<"
                    content = ""
                } else if (c.toString().equals(">")) {
                    tag += c
                    if (tag.contains("/")) {
                        endTagList.put(totalMapCount, tag)
                        totalMapCount += 1
                    } else {
                        startTagList.put(totalMapCount, tag)
                        totalMapCount += 1
                    }

                    tag = ""
                } else if (tag.isNotEmpty()) {
                    tag += c
                } else if (tag.isEmpty()) {
                    content += c
                }
            }

            if (content.isNotEmpty()) {
                contentList.put(totalMapCount, content)
                totalMapCount += 1
                content = ""
            }
            makeLimitedFormattedText()

        }

        private fun getFormattedContent() {
            formattedContent =
                completeContent.trim().replace("<p><br></p>", "\n").replace("<br>", "\n")
        }

        private fun checkIfLimitExceeds(unformattedText: String): Pair<Boolean, Int> {
            val splitList = unformattedText.split("\n")

            return if (splitList.size > lineLimit) {
                getLineLimitedData(splitList, unformattedText)
            } else if (unformattedText.length > charLimit) {
                getCharLimitedData(unformattedText, splitList)
            } else {
                Pair(false, 0)
            }
        }

        private fun getCharLimitedData(
            unformattedText: String,
            splitList: List<String>
        ): Pair<Boolean, Int> {
            val subString = unformattedText.substring(0, charLimit)
            val splitList1 = subString.split("\n")
            return if (splitList1.size > lineLimit) {
                var concatenatedText = ""
                //                    for (i in 0 until lineLimit) {
                //                        concatenatedText += (splitList.get(i) + "<br>")
                //                    }
                concatenatedText =
                    splitList.subList(0, lineLimit).joinToString(separator = "<br>")


                Pair(true, unformattedText.length - concatenatedText.length)

            } else {


                Pair(true, unformattedText.length - charLimit)
            }
        }

        private fun getLineLimitedData(
            splitList: List<String>,
            unformattedText: String
        ): Pair<Boolean, Int> {
            var concatenatedText = ""
//                for (i in 0 until lineLimit) {
//                    concatenatedText += (splitList.get(i) + "\n")
//                }

            concatenatedText = splitList.subList(0, lineLimit).joinToString(separator = "<br>")
            return if (concatenatedText.length > charLimit) {
                Pair(
                    true,
                    (unformattedText.length - concatenatedText.length) + (concatenatedText.length - charLimit)
                )
            } else {
                Pair(true, unformattedText.length - concatenatedText.length)
            }
        }

        private fun checkIfTextResizeable() {
            val splitList = formattedContent.split("\n")
            if (splitList.size > lineLimit) {
                var concatenatedText = ""
//                for (i in 0 until lineLimit) {
//                    concatenatedText += ((splitList?.get(i) ?: "") + "<br>")
//                }
                concatenatedText = splitList.subList(0, lineLimit).joinToString(separator = "<br>")
                if (concatenatedText.length > charLimit) {
                    extractAllData()

                } else {
                    finalContent = concatenatedText
                    isLimitExceeds = true
                }
            } else if (formattedContent.length > charLimit) {
                extractAllData()
            } else {
                isLimitExceeds = false
                finalContent = completeContent
//                return Pair(false, completeContent.toString())
            }


        }

        fun showFullContent() {
            showCompleteText = true
            loadContentInWebView()
        }

        fun showLessContent() {
            showCompleteText = false
            loadContentInWebView()
        }

    }
}