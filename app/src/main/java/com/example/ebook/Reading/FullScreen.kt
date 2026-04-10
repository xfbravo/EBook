package com.example.ebook.Reading

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ebook.BookActivity
import com.example.ebook.R

fun showCustomToast(activity: BookActivity, message: String) {
    val inflater: LayoutInflater = activity.layoutInflater
    val customView: View = inflater.inflate(R.layout.toast, null)

    // 设置自定义消息
    val textView: TextView = customView.findViewById(R.id.toast_text)
    textView.text = message

    // 创建自定义 Toast
    val toast = Toast(activity)
    toast.view = customView
    toast.duration = Toast.LENGTH_SHORT
    toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100) // 设置位置
    toast.show()
}
//TODO 进入全屏模式
fun switchFullScreenMode(activity: BookActivity, fullScreen: Boolean, bookTitle:String? = null) {
    val window = activity.window
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    if (fullScreen) {
        insetsController?.hide(WindowInsets.Type.systemBars())
        insetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    } else {
        insetsController?.show(WindowInsets.Type.systemBars())
    }
}
