package com.carealls.ui.components.adapters


import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.pojo.AllLocationData
import com.carealls.pojo.DashboardData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_all_location.view.*
import kotlinx.android.synthetic.main.row_location.view.*
import java.util.*


class AllLocationAdapter(
    private val context: Context?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<AllLocationData.Response.LocationforlistingItem?>?
) : RecyclerBaseAdapter() {
    var selectedPos = 0

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_all_location

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowAllLocationItem.tag = position
        viewDataBinding.root.rowAllLocationItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivAllLocation, list!!.get(position)!!.image!!)
        viewDataBinding.root.tvAllLocationTitle.text = UtilityMethod.toTitleCase(list.get(position)?.name)
    }

    override fun getItemCount(): Int = if (list == null) 0 else list.size
}






















