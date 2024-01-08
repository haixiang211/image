package com.example.usbimage

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView
import java.util.Timer
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    //图片列表:将需要展示的图片放到这个集合里
    private val imageList = listOf(
        R.drawable.test_image,
        R.drawable.test_image1,
        R.drawable.test_image2,
        R.drawable.test_image3,
        R.drawable.test_image4,
        R.drawable.test_image5,
        R.drawable.test_image6,
        R.drawable.test_image7,
        R.drawable.test_image8,
        R.drawable.test_image9,
        R.drawable.test_image10,
    )

    private lateinit var viewPager: ViewPager
    private lateinit var imagePagerAdapter: ImagePagerAdapter
    private lateinit var fullscreen: ImageView
    private lateinit var exitFullscreen : ImageView
    private var currentImageIndex: Int? = null//图片列表中展示的第哪一张图
    private var timer:Timer ?= null           //全屏状态下幻灯片效果的定时器
    private var touched: Boolean = false      //触摸操作标志
//    private var isFullscreen = false //全屏状态标志

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN//默认全屏,隐藏系统状态栏

        // 初始化ViewPager和适配器
        viewPager = findViewById(R.id.testImage)
        imagePagerAdapter = ImagePagerAdapter(imageList)
        fullscreen = findViewById(R.id.fullscreen)
        exitFullscreen = findViewById(R.id.exitFullscreen)
        val rotateLeftImageView: ImageView = findViewById(R.id.rotate_left_exe)
        val rotateRightImageView: ImageView = findViewById(R.id.rotate_right_exe)
        val arrowUpImageView: ImageView = findViewById(R.id.arrow_up_dic)
        val arrowDownImageView: ImageView = findViewById(R.id.arrow_down_dic)
        viewPager.adapter = imagePagerAdapter
        currentImageIndex = 0  //显示第一张图片

        // 设置ViewPager的页面变化监听器:更新currentImageIndex的值
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // 不需要实现
            }
            override fun onPageSelected(position: Int) {
                currentImageIndex = position // 更新currentImageIndex的值
            }

            override fun onPageScrollStateChanged(state: Int) {
                // 不需要实现
            }
        })

        //点击全屏操作
        fullscreen.setOnClickListener{
            enterFullscreen()
        }
        //点击退出全屏操作
        exitFullscreen.setOnClickListener {
            exitFullscreen()
        }
        //逆时针旋转操作
        rotateLeftImageView.setOnClickListener {
            imagePagerAdapter.setLeftRotation()
//            viewPager.rotation = viewPager.rotation.plus(-90f)//这是直接旋转viewpager，不可行
        }
        //顺时针旋转操作
        rotateRightImageView.setOnClickListener {
            imagePagerAdapter.setRightRotation()
//            viewPager.rotation = viewPager.rotation.plus(90f)
        }
        //点击上一张图片操作
        arrowUpImageView.setOnClickListener {
            showPreviousImage()
        }
        //点击下一张图片操作
        arrowDownImageView.setOnClickListener {
            showNextImage()
        }
    }
    //显示上一张图片
    private fun showPreviousImage() {
        if (currentImageIndex!! > 0 && currentImageIndex!! <= imageList.size - 1) {
            viewPager.setCurrentItem(currentImageIndex!! - 1, true)
//            currentImageIndex = currentImageIndex!! - 1
//            Log.d("currentImageIndex11","$currentImageIndex")
        }else{
            Toast.makeText(this, "已经是第一张图片！", Toast.LENGTH_SHORT).show()
        }
    }
    //显示下一张图片
    private fun showNextImage() {
        if (currentImageIndex!! >= 0 && currentImageIndex!! < imageList.size - 1) {
            viewPager.setCurrentItem(currentImageIndex!! + 1, true)
//            currentImageIndex = currentImageIndex!! + 1
//            Log.d("currentImageIndex22","$currentImageIndex")
        }else{
            Toast.makeText(this, "已经是最后一张图片！", Toast.LENGTH_SHORT).show()
        }
    }
    //进入全屏
    private fun enterFullscreen(){
        /// 切换全屏模式
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        // 设置ViewPager的布局参数为全屏
        val layoutParams = viewPager.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        viewPager.layoutParams = layoutParams

        // 添加渐入动画
        val fadeInAnimation = AlphaAnimation(0.0f, 1.0f)
        fadeInAnimation.duration = 500 // 设置动画持续时间为500毫秒
        fadeInAnimation.fillAfter = true
        viewPager.startAnimation(fadeInAnimation)

        //全屏后将退出全屏按钮设置为可见
        exitFullscreen.visibility = View.VISIBLE

        //全屏状态下自动播放
        startAutoSlide()
    }
    //退出全屏
    private fun exitFullscreen(){
        // 退出全屏模式
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//            supportActionBar?.show()

        // 恢复ViewPager的原始布局参数
        if (viewPager.layoutParams is ConstraintLayout.LayoutParams) {
            val layoutParams = viewPager.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.width = resources.getDimensionPixelSize(R.dimen.view_pager_width)
            layoutParams.height = resources.getDimensionPixelSize(R.dimen.view_pager_height)
            layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.view_pager_margin_top)
            viewPager.layoutParams = layoutParams
        }

        // 添加渐入动画
        val fadeInAnimation = AlphaAnimation(0.0f, 1.0f)
        fadeInAnimation.duration = 500 // 设置动画持续时间为500毫秒
        fadeInAnimation.fillAfter = true
        viewPager.startAnimation(fadeInAnimation)

        //退出全屏后将将退出全屏按钮设置为不可见
        exitFullscreen.visibility = View.INVISIBLE

        //退出全屏时停止自动切换
        stopAutoSlide()
    }
    //自动切换下一张图片,实现类似幻灯片播放的效果
    @SuppressLint("ClickableViewAccessibility")
    private fun startAutoSlide() {
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            runOnUiThread {
                if (!touched) {
                    val nextIndex = (currentImageIndex?.plus(1))?.rem(imageList.size)
                    if (nextIndex != null) {
                        viewPager.setCurrentItem(nextIndex, true)
                    }
                }
            }
        }, 2000, 2000) // 设置自动切换的时间间隔，这里设置为5秒

        // 添加触摸监听器,改变touched的值
        viewPager.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touched = true
                }
                MotionEvent.ACTION_UP -> {
                    touched = false
                    startAutoSlideDelayed()
                }
            }
            false
        }
    }
    private fun startAutoSlideDelayed() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(timerTask {
            runOnUiThread {
                if (!touched) {
                    val nextIndex = (currentImageIndex?.plus(1))?.rem(imageList.size)
                    if (nextIndex != null) {
                        viewPager.setCurrentItem(nextIndex, true)
                    }
                }
            }
        }, 5000) // 设置延迟5秒开始自动切换
    }
    //停止自动切换
    private fun stopAutoSlide() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }
}