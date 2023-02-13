package com.selflearningcoursecreationapp.extensions

import android.webkit.WebSettings
import android.webkit.WebView
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


fun getMoreText(
    completeString: String,
    limit: Int = Constant.DESC_CHAR_COUNT_MAX,
    lineCount: Int = 3,
    showDots: Boolean = true
): Pair<Boolean, String> {
    var completeString1 =
        completeString?.trim()?.replace("<p><br></p>", "\n")?.replace("<br>", "\n")
    var startTagList = HashMap<Int, String>()
    var endTagList = HashMap<Int, String>()
    var contentList = HashMap<Int, String>()
    var limitPair: Pair<Boolean, Int>? = null
    var tag = ""
    var content = ""
    var count = 1
    completeString1.forEachIndexed { index, c ->
        if (c.toString().equals("<")) {
            if (content.isNotEmpty()) {
                contentList.put(count, content)
                count += 1
            }
            tag = "<"
            content = ""
        } else if (c.toString().equals(">")) {
            tag += c
            if (tag.contains("/")) {
                endTagList.put(count, tag)
                count += 1
            } else {
                startTagList.put(count, tag)
                count += 1
            }

            tag = ""
        } else if (tag.isNotEmpty()) {
            tag += c
        } else if (tag.isEmpty()) {
            content += c
        }
    }


    if (content.isNotEmpty()) {
        contentList.put(count, content)
        count += 1
        content = ""
    }

    var mainString = ""
    var contentString = ""
    var test = 0
    var startTag = 0
    limit@ for (i in 0 until count) {
        if (startTagList.containsKey(i)) {
            mainString += startTagList[i].toString()
            startTag = i
        } else if (endTagList.containsKey(i)) {
            mainString += endTagList[i].toString()
            startTagList.remove(startTag)
        } else if (contentList.containsKey(i)) {
            contentString += contentList[i].toString()
            mainString += contentList[i].toString()

            //call limit and line count check , return type should be pair <Boolean, Int>
            limitPair = getMoreTextLimit(limit, lineCount, contentString)
            if (limitPair.first) {
                var start = contentString.length - limitPair.second
                var mainStart = mainString.length - limitPair.second
                contentString = contentString.removeRange(start, contentString.length)
                mainString = mainString.removeRange(mainStart, mainString.length)



                test = i
                break@limit
            }
        }
    }

    for (i in test until 0) {
        if (startTagList.containsKey(i))
            mainString += "</" + startTagList[i].toString().substringAfter("<")
    }

//    if (startTagList.size != endTagList.size) {
//        startTagList.forEachIndexed { index, s ->
//            if (index <= endTagList.size) {
//                mainString += "</" + s.substringAfter("<")
//
//            }
//        }
////            startTagList.subList(endTagList.size, startTagList.size-1)?.forEach { tag->
////                mainString+= "</"+tag.substringAfter("<")
////
////            }
//    }

    mainString = mainString.replace("\n", "<br>")
    if (showDots)
        mainString = mainString + "..."

    return Pair(limitPair?.first ?: false, mainString)


}

fun getMoreTextLimit(limit: Int, lineCount: Int, unformattedText: String?): Pair<Boolean, Int> {


    var splitList = unformattedText?.split("\n")

    if ((splitList?.size ?: 0) > lineCount) {
        var concattedText = ""
        for (i in 0 until lineCount) {
            concattedText += ((splitList?.get(i) ?: "") + "\n")
        }
        if (concattedText.length > limit) {


            return Pair(
                true,
                ((unformattedText?.length
                    ?: 0) - concattedText.length) + (concattedText.length - limit)
            )
        } else {


            return Pair(true, (unformattedText?.length ?: 0) - concattedText.length)
        }

    } else if ((unformattedText?.length ?: 0) > limit) {


        var subString = unformattedText?.substring(0, limit)
        var splitList1 = subString?.split("\n")
        if ((splitList1?.size ?: 0) > lineCount) {
            var concattedText = ""
            for (i in 0 until lineCount) {
                concattedText += ((splitList?.get(i) ?: "") + "<br>")
            }


            return Pair(true, (unformattedText?.length ?: 0) - concattedText.length)

        } else {


            return Pair(true, (unformattedText?.length ?: 0) - limit)
        }


    } else {

        return Pair(false, 0)
    }


}


fun displayDataToWeb(data: String, wvDescription: WebView) {
    wvDescription.settings.apply {
//                val fontSize = baseActivity.resources.getDimensionPixelOffset(R.dimen.textField_10);
        domStorageEnabled = true
        javaScriptEnabled = true
        cacheMode = WebSettings.LOAD_NO_CACHE
        loadWithOverviewMode = true
        layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

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
            "word-wrap: break-word;" +
            "}" +

            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            data +
            "</body></html>"

    wvDescription.loadDataWithBaseURL(
        "file:///android_res/",
        takeAways,
        "text/html",
        "utf-8",
        "about:blank"
    )

}


fun checkForReadMoreDesc(unformattedText: String?, lineCount: Int = 3): Pair<Boolean, String> {

    var content = unformattedText?.trim()?.replace("<p><br></p>", "\n")?.replace("<br>", "\n")

    var splitList = content?.split("\n")
    if ((splitList?.size ?: 0) > lineCount) {
        var concattedText = ""
        for (i in 0 until lineCount) {
            concattedText += ((splitList?.get(i) ?: "") + "<br>")
        }
        if ((concattedText?.length ?: 0) > Constant.DESC_CHAR_COUNT_MAX) {
            val newT = getMoreText(content.toString())
            return newT
        } else {
            return Pair(true, concattedText + "...")
        }
//            displayDataToWeb(concattedText+"...", this)
    } else if ((content?.length ?: 0) > Constant.DESC_CHAR_COUNT_MAX) {
        val newT = getMoreText(content.toString())
        return newT
//        displayDataToWeb(newT.second, this)

    } else {
        return Pair(false, unformattedText.toString())
//        unformattedText?.let { displayDataToWeb(it,this) }
    }


}
