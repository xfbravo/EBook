package com.example.e_bookreader_fixed
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReaderActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var scrollView: ScrollView
    private var bookContent: String = ""
    private var chapters: List<String> = listOf()
    private var chapterPositions = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        supportActionBar?.hide()
        val exit=findViewById<ImageView>(R.id.exit)
        exit.setOnClickListener {
            finish()
        }
        //TODO 设置右下角菜单按钮
        val btnMenu = findViewById<FloatingActionButton>(R.id.btnMenu)
        btnMenu.setOnClickListener { view -> showPopupMenu(view) }
        //TODO 初始化textView和scrollView
        textView = findViewById(R.id.textView)
        scrollView = findViewById(R.id.scrollView)

        val bookRawResId = intent.getIntExtra("bookRawResId", 0)
        if (bookRawResId != 0) {
            bookContent = readBookContent(bookRawResId,resources)
            chapters = extractChapters(bookContent)
        }
        displayFullBook(textView, chapters, chapterPositions)
        var fullScreen=false
        textView.isClickable = true
        textView.setOnClickListener {
            if(!fullScreen){
                enterFullScreenMode()
            }
            else{
                exitFullScreenMode()
            }
            btnMenu.isVisible = !btnMenu.isVisible
            exit.isVisible = !exit.isVisible
            fullScreen=!fullScreen
        }
    }
    //TODO 进入全屏模式
    private fun enterFullScreenMode() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
    //TODO 退出全屏模式
    private fun exitFullScreenMode() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_reader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_font_size -> {
                showFontSizeDialog()
                true
            }

            R.id.menu_theme -> {
                toggleNightMode()
                true
            }

            R.id.menu_chapters -> {
                showChapterSelectionDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    //TODO 显示字体大小对话框
    private fun showFontSizeDialog() {
        val sizes = arrayOf("小", "中", "大")
        AlertDialog.Builder(this)
            .setTitle("选择字体大小")
            .setItems(sizes) { _, which ->
                val size = when (which) {
                    0 -> 14f
                    1 -> 18f
                    2 -> 22f
                    else -> 18f
                }
                textView.textSize = size
            }
            .show()
    }
    //TODO 切换夜间模式
    private fun toggleNightMode() {
        val mode=findViewById<View>(R.id.menu_theme)
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        AppCompatDelegate.setDefaultNightMode(
            if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
        )
        recreate()
    }
    //TODO 显示章节选择对话框
    private fun showChapterSelectionDialog() {
        val chapters = extractChapters(bookContent)
        val chapterTitles = chapters.mapIndexed { index, _ -> "章节 ${index + 1}" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("选择章节")
            .setItems(chapterTitles) { _, which ->
                jumpToChapter(which, chapterPositions, scrollView)
            }
            .show()
    }
    //TODO 显示弹出菜单
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_reader, popup.menu)  // 使用 res/menu/menu.xml

        // 监听菜单项点击
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_font_size -> {
                    showFontSizeDialog()
                    true
                }

                R.id.menu_theme -> {
                    toggleNightMode()
                    true
                }

                R.id.menu_chapters -> {
                    showChapterSelectionDialog()
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
        }
        popup.show()
    }

}