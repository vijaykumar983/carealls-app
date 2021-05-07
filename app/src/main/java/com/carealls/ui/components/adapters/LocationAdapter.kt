package com.carealls.ui.components.adapters


import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.pojo.DashboardData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_location.view.*
import java.util.ArrayList


class LocationAdapter(
    private val context: Context?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<DashboardData.Response.LocationforlistingItem?>?
) : RecyclerBaseAdapter() {
    var selectedPos = 0

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_location

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowLocationItem.tag = position
        viewDataBinding.root.rowLocationItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivLocation,list!!.get(position)!!.image!!)
        viewDataBinding.root.tvLocationTitle.text = UtilityMethod.toTitleCase(list.get(position)?.name)
    }

    override fun getItemCount(): Int = if (list == null) 0 else list.size
}






















