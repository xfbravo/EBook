package com.example.ebook.Menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.ebook.dao.ChapterInfo
import com.example.ebook.entity.Bookmark


@Composable
fun MainMenu(
    menuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onFontSizeClick: () -> Unit,
    onNightModeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onChapterClick: () -> Unit,
    onSelectBookmarkClick: () -> Unit
) {
    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(0.dp, (-10).dp)
    ) {
        DropdownMenuItem(text = { Text("改变字体大小") }, onClick = onFontSizeClick)
        DropdownMenuItem(text = { Text("夜间模式") }, onClick = onNightModeClick)
        DropdownMenuItem(text = { Text("设置书签") }, onClick = onBookmarkClick)
        DropdownMenuItem(text = { Text("切换章节") }, onClick = onChapterClick)
        DropdownMenuItem(text = { Text("选择书签") }, onClick = onSelectBookmarkClick)
    }
}

@Composable
fun FontSizeMenu(
    sizeMenuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onFontSizeSelected: (Float) -> Unit
) {
    DropdownMenu(
        expanded = sizeMenuExpanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(-(165).dp, (-100).dp)
    ) {
        DropdownMenuItem(text = { Text("小") }, onClick = { onFontSizeSelected(16f) })
        DropdownMenuItem(text = { Text("中") }, onClick = { onFontSizeSelected(20f) })
        DropdownMenuItem(text = { Text("大") }, onClick = { onFontSizeSelected(24f) })
    }
}

@Composable
fun ChapterSelectionDialog(
    chapterInfos: List<ChapterInfo>,
    onChapterSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("选择章节") },
        text = {
            LazyColumn {
                items(chapterInfos) { info ->
                    Text(
                        text = info.title,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onChapterSelected(info.chapterIndex) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        }
    )
}

@Composable
fun CreateBookmarkDialog(
    bookmarkName: String,
    onBookmarkNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,

    ){
    AlertDialog(
        onDismissRequest = {onDismissRequest},
        title = { Text("创建书签") },
        text = {
            OutlinedTextField(
                value = bookmarkName,
                onValueChange = { onBookmarkNameChange(it) },
                label = { Text("书签名") }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                // 保存书签
                onConfirmButtonClick()
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissButtonClick()
            }) {
                Text("取消")
            }
        }
    )
}

@Composable
fun BookmarkSelectionDialog(
    bookmarks: List<Bookmark>,
    onBookmarkSelected: (Bookmark) -> Unit,
    onDeleteBookmark: (Bookmark) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("选择书签") },
        text = {
            LazyColumn {
                items(bookmarks) { bookmark ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onBookmarkSelected(bookmark)
                            }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = bookmark.name)
                        IconButton(onClick = { onDeleteBookmark(bookmark) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "删除书签")
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        }
    )
}