# EBook（E-BookReader_Fixed）

这个仓库是一个 **Android 原生电子书阅读器示例项目**。

## 用了什么技术

- **Kotlin + Android SDK**：核心业务代码（`MainActivity`、`ReaderActivity`、`BookFunction`）  
- **Gradle Kotlin DSL**（`build.gradle.kts`）：项目构建与依赖管理  
- **AndroidX / Material Components**：界面和交互组件  
  - RecyclerView + GridLayoutManager（书架列表）
  - ConstraintLayout、ScrollView、TextView
  - FloatingActionButton、PopupMenu、AlertDialog
- **资源文件机制**（`res/raw/*.txt`）：内置电子书文本内容

## 实现了什么功能

- **书架展示**：首页网格展示书籍封面与标题  
- **点击进入阅读**：从书架跳转到阅读页并加载对应 txt 内容  
- **章节提取与跳转**：按 `Chapter` 分割章节并支持章节定位跳转  
- **字体大小切换**：阅读页可切换小/中/大字号  
- **夜间模式切换**：支持明暗主题切换  
- **沉浸式阅读**：点击文本区域可切换全屏，隐藏/显示功能按钮
