package com.example.usbimage

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView

class ImagePagerAdapter(private val imageList: List<Int>) : PagerAdapter() {
    private lateinit var mCurPhotoView: PhotoView
    //getCount方法返回图片列表的大小，用于指定 ViewPager 中的页面数量。
    override fun getCount(): Int {
        return imageList.size
    }
    //isViewFromObject方法判断当前视图是否与给定的对象相匹配。
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
    //instantiateItem方法用于实例化每个页面的视图，并将其添加到容器中。
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val photoView = com.github.chrisbanes.photoview.PhotoView(context)

        val imageResource = imageList[position]
        photoView.setImageResource(imageResource)

        container.addView(photoView)
        return photoView
    }
    //destroyItem方法用于销毁页面的视图。
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun setPrimaryItem(p0: ViewGroup, p1: Int, p2: Any) {

        mCurPhotoView = p2 as PhotoView
    }

    fun setLeftRotation(){
        mCurPhotoView.setRotationBy(-90f)
    }

    fun setRightRotation(){
        mCurPhotoView.setRotationBy(90f)
    }
}
