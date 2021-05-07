package com.carealls.ui.components.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.carealls.R
import com.carealls.pojo.DashboardData
import com.carealls.utils.UtilityMethod
import java.util.*


class SliderAdapter(
    private val mActivity: Activity,
    private val onClickListener: View.OnClickListener,
    private var list: ArrayList<DashboardData.Response.BannersItem?>?
) : PagerAdapter() {

    private var layoutInflater: LayoutInflater? = null

    init {
        this.list = list
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater!!.inflate(R.layout.row_slider, null)
        val bannerImg = view.findViewById<ImageView>(R.id.imgBanner)

        UtilityMethod.loadImage(bannerImg, list!!.get(position)!!.image!!)
        view.setOnClickListener(View.OnClickListener {
            if (list!!.get(position)?.link != null && list!!.get(position)?.link!!.isNotEmpty())
            {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(list!!.get(position)?.link))
                mActivity.startActivity(intent)
            }
        })



        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

    override fun getCount(): Int {
        return if (list == null) 0 else list!!.size
    }

}
