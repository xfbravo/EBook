package com.example.ebook.Menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ebook.ViewModel.BookViewModel
import com.example.ebook.dao.ChapterInfo
import com.example.ebook.db.BookDatabase
import kotlinx.coroutines.launch
import com.example.ebook.entity.Bookmark

@Composable
fun HideOrShowFloatingActionButton(viewModel: BookViewModel, fullScreen: Boolean){
    if(!fullScreen){
        UseFloatingActionButton(viewModel)
    }
}

//TODO 显示右下角菜单按钮
@Composable
fun UseFloatingActionButton(viewModel: BookViewModel) {
    val context= LocalContext.current
    val scope= rememberCoroutineScope()
    val db= remember { BookDatabase.getInstance(context) }
    // TODO菜单和章节选择对话框的状态
    var menuExpanded by remember { mutableStateOf(false) }
    var sizeMenuExpanded by remember { mutableStateOf(false) }
    var chapterDialogVisible by remember { mutableStateOf(false) }
    var chapterInfos by remember { mutableStateOf<List<ChapterInfo>>(emptyList()) }
    var createBookmarkNameDialogVisible by remember { mutableStateOf(false) }
    var bookmarkName by remember { mutableStateOf("") }
    var bookmarkSelectionDialogVisible by remember { mutableStateOf(false) }
    var bookmarks by remember { mutableStateOf<List<Bookmark>>(emptyList()) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // 主菜单按钮作为第一个 DropdownMenu 的锚点
        Box(modifier = Modifier
            .padding(50.dp)
        ) {
            FloatingActionButton(
                onClick = { menuExpanded = !menuExpanded },
                modifier = Modifier.offset(x = (-8).dp, y = (-8).dp)
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            //主菜单
            MainMenu(
                menuExpanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                onFontSizeClick = { sizeMenuExpanded = true },
                onNightModeClick = {
                    menuExpanded = false
                    viewModel.toggleNightMode()
                },
                onBookmarkClick = {
                    menuExpanded = false
                    createBookmarkNameDialogVisible = true
                },
                onChapterClick = {
                    scope.launch {
                        val dao = db.bookContentDao()
                        val bookId = viewModel.currentBookId.value ?: return@launch
                        chapterInfos = dao.getAllChapterTitles(bookId)
                        chapterDialogVisible = true
                    }
                },
                onSelectBookmarkClick = {
                    menuExpanded = false
                    scope.launch {
                        val dao = db.bookmarkDao()
                        val bookId = viewModel.currentBookId.value ?: return@launch
                        bookmarks = dao.getBookmarks(bookId)
                        bookmarkSelectionDialogVisible = true
                    }
                }
            )
            //设置字体大小的菜单
            // 第二个菜单锚定在第一个按钮的下面，通过 offset 控制位置
            FontSizeMenu(
                sizeMenuExpanded = sizeMenuExpanded,
                onDismissRequest = {
                    sizeMenuExpanded = false
                    menuExpanded = false
                },
                onFontSizeSelected = { fontSize ->
                    viewModel.updateFontSize(fontSize)
                    sizeMenuExpanded = false
                    menuExpanded = false
                }
            )
        }
        //TODO章节选择对话框
        if (chapterDialogVisible) {
            ChapterSelectionDialog(
                chapterInfos = chapterInfos,
                onChapterSelected = { chapterIndex ->
                    viewModel.requestJumpToChapter(chapterIndex)
                    chapterDialogVisible = false
                    menuExpanded = false
                },
                onDismissRequest = { chapterDialogVisible = false }
            )
        }

        //TODO 书签名称输入对话框
        if(createBookmarkNameDialogVisible){
            CreateBookmarkDialog(
                bookmarkName = bookmarkName,
                onBookmarkNameChange = { bookmarkName = it },
                onDismissRequest = { createBookmarkNameDialogVisible = false },
                onConfirmButtonClick = {
                    scope.launch {
                        val dao = db.bookmarkDao()
                        val bookId = viewModel.currentBookId.value ?: return@launch
                        val chapterIndex = viewModel.currentChapterIndex.value ?: 0
                        val scrollOffset = viewModel.currentScrollOffset.value ?: 0

                        dao.insertBookmark(
                            Bookmark(
                                bookId = bookId,
                                chapterIndex = chapterIndex,
                                scrollOffset = scrollOffset,
                                name = bookmarkName
                            )
                        )
                    }
                    createBookmarkNameDialogVisible = false
                    menuExpanded = false
                },
                onDismissButtonClick = {
                    createBookmarkNameDialogVisible = false
                    bookmarkName = ""
                }
            )
        }

        //TODO 书签选择对话框
        if (bookmarkSelectionDialogVisible) {
            BookmarkSelectionDialog(
                bookmarks = bookmarks,
                onBookmarkSelected = { bookmark ->
                    viewModel.requestJumpToChapter(bookmark.chapterIndex)
                    viewModel.updateScrollOffset(bookmark.scrollOffset)
                    bookmarkSelectionDialogVisible = false
                },
                onDeleteBookmark = { bookmark ->
                    scope.launch {
                        db.bookmarkDao().delete(bookmark)
                        val bookId = viewModel.currentBookId.value ?: return@launch
                        bookmarks = db.bookmarkDao().getBookmarks(bookId)
                    }
                },
                onDismissRequest = { bookmarkSelectionDialogVisible = false }
            )
        }
    }
}




