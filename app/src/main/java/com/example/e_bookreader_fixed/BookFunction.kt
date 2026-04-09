package com.example.e_bookreader_fixed

import android.view.ViewTreeObserver
import android.widget.ScrollView
import android.widget.TextView

fun displayFullBook(textView: TextView, chapters: List<String>,chapterPositions: MutableList<Int>) {
    textView.text = chapters.joinToString("\n")
    textView.viewTreeObserver.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            calculateChapterPositions(chapterPositions, textView, chapters)
        }
    })
}
fun calculateChapterPositions(chapterPositions: MutableList<Int>, textView: TextView, chapters: List<String>) {
    chapterPositions.clear()
    for (i in chapters.indices) {
        val startOffset = textView.text.indexOf(chapters[i])
        if (startOffset != -1) {
            val startLine = textView.layout.getLineForOffset(startOffset)
            val top = textView.layout.getLineTop(startLine)
            chapterPositions.add(top)
        }
    }
}
fun jumpToChapter(chapterIndex: Int, chapterPositions: List<Int>, scrollView: ScrollView) {
    if (chapterIndex in chapterPositions.indices) {
        val targetPosition = chapterPositions[chapterIndex]
        scrollView.post {
            scrollView.smoothScrollTo(0, targetPosition)
        }
    }
}
fun readBookContent(resId: Int,resources:android.content.res.Resources): String {
    val inputStream = resources.openRawResource(resId)
    return inputStream.bufferedReader().use { it.readText() }
}
fun extractChapters(content: String): List<String> {
    return content.split("Chapter").filter { it.isNotBlank() }.map { "Chapter $it" }
}