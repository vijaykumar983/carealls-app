package com.carealls.ui.components.adapters


import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.pojo.PackageData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_package.view.*


class PackageAdapter(
    private val context: Context?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<PackageData.ResponseItem?>
) : RecyclerBaseAdapter() {
    private var currentPosition = 0
    private val flag = true
    var selectedPos = 0

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_package

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowPackageItem.tag = position
        viewDataBinding.root.rowPackageItem.setOnClickListener(onClickListener)
        viewDataBinding.root.tvPackageName.text = UtilityMethod.toTitleCase(list.get(position)?.packageName)
        if(list.get(position)?.packagePrice!!.isNotEmpty() && list.get(position)?.packagePrice !=null)
        viewDataBinding.root.tvDetail.text = list.get(position)?.packagePrice+" Rs/Mo After Free Listing"

        if (currentPosition == position) {
            //toggling visibility
            viewDataBinding.root.rowPackageItem.setBackgroundResource(R.drawable.bg_rounded_yellow_10sdp)
            viewDataBinding.root.tvPackageName.setTextColor(
                context!!.getResources().getColor(R.color.colorWhite)
            )
            viewDataBinding.root.tvDetail.setTextColor(
                context!!.getResources().getColor(R.color.colorWhite)
            )
            viewDataBinding.root.ivCheck.visibility = View.VISIBLE

        } else {
            viewDataBinding.root.rowPackageItem.setBackgroundResource(R.drawable.bg_rounded_white_10sdp)
            viewDataBinding.root.tvPackageName.setTextColor(
                context!!.getResources().getColor(R.color.color_0057FF)
            )
            viewDataBinding.root.tvDetail.setTextColor(
                context!!.getResources().getColor(R.color.color_4D4D4D)
            )
            viewDataBinding.root.ivCheck.visibility = View.INVISIBLE
        }

        viewDataBinding.root.rowPackageItem.setOnClickListener(View.OnClickListener {
            currentPosition = position; //getting the position of the item to expand it
            notifyDataSetChanged(); //reloding the list
        })
    }

    override fun getItemCount(): Int = if (list == null) 0 else list.size
}






















