package com.example.ebook

import com.example.ebook.Reading.Book
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ebook.ui.theme.EBookTheme
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import com.example.ebook.Menu.HideOrShowFloatingActionButton
import com.example.ebook.Reading.ReadingProgressManager
import com.example.ebook.Reading.showCustomToast
import com.example.ebook.Reading.switchFullScreenMode
import com.example.ebook.ViewModel.BookViewModel
import com.example.ebook.db.BookDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BookActivity : ComponentActivity() {
    private var fullScreen by mutableStateOf(false)
    private val bookViewModel: BookViewModel by viewModels()
    private val db: BookDatabase by lazy {
        BookDatabase.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val book= intent.getParcelableExtra<Book>("book")//TODO 获取书籍信息
        book?.let {
            bookViewModel.setCurrentBookId(it.title.hashCode())
        }
        val progress= ReadingProgressManager.getProgress(this,book?.title.hashCode())
        val initialChapter= progress?.first ?: 0
        val initialOffset= progress?.second ?: 0
        bookViewModel.setInitialChapterIndex(initialChapter)
        bookViewModel.setInitialScrollOffset(initialOffset)
        enableEdgeToEdge()
        setContent {
            EBookTheme {
                if(!fullScreen){
                    showCustomToast(this, "${book?.title}")
                }
                BookTextReader(book?.title.hashCode(),bookViewModel,toggleFullScreen = {
                    fullScreen=!fullScreen
                    switchFullScreenMode(this, fullScreen, book?.title)//TODO 进入全屏模式
                })//TODO 显示书籍信息
                HideOrShowFloatingActionButton(bookViewModel, fullScreen)//TODO 显示右下角菜单按钮
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        //TODO 保存阅读进度
        val bookId= bookViewModel.currentBookId.value
        val currentChapter= bookViewModel.currentChapterIndex.value
        val currentScrollOffset= bookViewModel.currentScrollOffset.value

        if (currentChapter != null && bookId != null&& currentScrollOffset != null) {
            ReadingProgressManager.saveProgress(this, bookId, currentChapter, currentScrollOffset)
        }
        Log.d("BookActivityLife", "onDestroy: $currentChapter")
    }
}
@Composable
fun BookTextReader(bookId:Int, viewModel: BookViewModel, toggleFullScreen: () -> Unit){
    val context=LocalContext.current
    val dao=remember{BookDatabase.getInstance(context).bookContentDao()}
    val scope= rememberCoroutineScope()
    //TODO记录章节索引和内容
    val chapterIndices=remember { mutableStateListOf<Int>() }
    val chapterContentMap= remember { mutableStateMapOf<Int, String>() }
    val listState=rememberLazyListState()
    //TODO记录阅读位置
    val initialChapter= viewModel.initialChapterIndex.value
    val initialScrollOffset= viewModel.initialScrollOffset.value
    //TODO 记录字体大小和夜间模式
    val fontSize= viewModel.fontSize.observeAsState(20f)
    val isNightMode= viewModel.isNightMode.observeAsState(false)
    val backgroundColor= if (isNightMode.value == true) Color.Black else Color.White
    val textColor= if (isNightMode.value == true) Color.White else Color.Black
    //TODO 记录是否恢复上次阅读位置
    var restored by remember { mutableStateOf(false) }

    //TODO 预加载章节内容
    LaunchedEffect(Unit) {
        val range = (initialChapter - 1)..(initialChapter + 1)
        range.forEach { index ->
            if (index >= 0) {
                dao.getChapterContent(bookId, index)?.let { chapter ->
                    chapterIndices.add(index)
                    chapterContentMap[index] = chapter.content
                }
            }
        }
        chapterIndices.sort()
    }
    //TODO 恢复上次阅读位置
    LaunchedEffect(chapterIndices.size) {
        if (!restored && chapterIndices.isNotEmpty()) {
            val targetIndex = chapterIndices.indexOf(initialChapter)
            if (targetIndex >= 0) {
                listState.scrollToItem(targetIndex, initialScrollOffset)
                restored = true
            }
        }
    }

    //TODO 章节跳转
    val jumpChapter by viewModel.jumpChapter.observeAsState(null)
    LaunchedEffect(jumpChapter) {
        jumpChapter?.let { targetChapter ->
            val range = (targetChapter - 1)..(targetChapter + 1)
            range.forEach { index ->
                if (index >= 0 && !chapterIndices.contains(index)) {
                    dao.getChapterContent(bookId, index)?.let { chapter ->
                        chapterIndices.add(index)
                        chapterContentMap[index] = chapter.content
                    }
                }
            }
            chapterIndices.sort()

            delay(50) // 等待布局更新
            val targetIndex = chapterIndices.indexOf(targetChapter)
            if (targetIndex >= 0) {
                listState.scrollToItem(targetIndex)
            }
            viewModel.requestJumpToChapter(null)
        }
    }
    //TODO 实时记录滚动位置，通过flow来实现，一直收集index和offset，并且更新viewModel中的值
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                chapterIndices.getOrNull(index)?.let { chapter ->
                    viewModel.updateCurrentChapterIndex(chapter)
                    viewModel.updateScrollOffset(offset)
                }
            }
    }
    LazyColumn (
        state=listState,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { toggleFullScreen() }
    ){
        itemsIndexed (chapterIndices){index,chapterIndex ->
            val content=chapterContentMap[chapterIndex]?:"加载中..."

            Text(//TODO 显示章节内容
                text = content,
                fontSize= fontSize.value.sp,
                modifier = Modifier.padding(15.dp),
                color = textColor,
            )
            //TODO 预加载下一章
            if(index==chapterIndices.lastIndex){
                LaunchedEffect(index) {
                    val nextIndex=chapterIndex+1
                    if(!chapterIndices.contains(nextIndex)){
                        dao.getChapterContent(bookId,nextIndex)?.let{chapter->
                            chapterIndices.add(nextIndex)
                            chapterContentMap[nextIndex]=chapter.content
                            chapterIndices.sort()
                        }
                    }
                }
            }
            //TODO 预加载上一章
            if(index==0){
                LaunchedEffect(index) {
                    val previousIndex=chapterIndex-1
                    if(previousIndex>=0&&!chapterIndices.contains(previousIndex)){
                        dao.getChapterContent(bookId,previousIndex)?.let{chapter->
                            chapterIndices.add(previousIndex)
                            chapterContentMap[previousIndex]=chapter.content
                            chapterIndices.sort()
                            scope.launch{
                                delay(100)
                                listState.scrollToItem(index+1)
                            }
                        }
                    }
                }
            }

        }
    }
}
