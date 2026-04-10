package com.example.ebook.ViewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel: ViewModel() {
    //TODO 字体大小
    private val _fontSize= MutableLiveData(20f)
    val fontSize: MutableLiveData<Float>
        get() = _fontSize
    fun updateFontSize(newSize: Float) {
        _fontSize.value = newSize
    }
    //TODO 夜间模式
    private val _isNightMode= MutableLiveData(false)
    val isNightMode: MutableLiveData<Boolean>
        get() = _isNightMode
    fun toggleNightMode(){
        _isNightMode.value = _isNightMode.value?.not()
    }
    //TODO 当前书籍ID,用于菜单查询章节
    val currentBookId= mutableStateOf<Int?>(null)
    fun setCurrentBookId(bookId: Int) {
        currentBookId.value = bookId
    }

    //TODO 章节跳转功能
    private val _jumpChapter= MutableLiveData<Int?>(null)
    val jumpChapter: MutableLiveData<Int?>
        get() = _jumpChapter

    fun requestJumpToChapter(chapterIndex: Int?) {
        _jumpChapter.value = chapterIndex
    }

    //TODO 当前章节索引
    private val _currentChapterIndex= mutableStateOf<Int?>(null)
    val currentChapterIndex: MutableState<Int?> = _currentChapterIndex

    fun updateCurrentChapterIndex(chapterIndex: Int) {
        _currentChapterIndex.value = chapterIndex
    }

    //TODO 当前章节滚动位置
    private val _currentScrollOffset= mutableStateOf<Int?>(null)
    val currentScrollOffset: MutableState<Int?> = _currentScrollOffset

    fun updateScrollOffset(scrollOffset: Int) {
        _currentScrollOffset.value = scrollOffset
    }

    //TODO 进入阅读界面的位置
    private val _initialChapterIndex = mutableStateOf(0)
    val initialChapterIndex: MutableState<Int> = _initialChapterIndex
    fun setInitialChapterIndex(chapterIndex: Int) {
        _initialChapterIndex.value = chapterIndex
    }

    private val _initialScrollOffset= mutableStateOf(0)
    val initialScrollOffset: MutableState<Int> = _initialScrollOffset

    fun setInitialScrollOffset(scrollOffset: Int) {
        _initialScrollOffset.value = scrollOffset
    }
}