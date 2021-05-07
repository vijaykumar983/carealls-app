package com.carealls.ui.components.adapters


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.pojo.ProductDetailData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.ui.components.activites.reviews.ReviewsActivity
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_category.view.ivCategory
import kotlinx.android.synthetic.main.row_listing.view.*
import java.util.ArrayList


class ProductListingAdapter(
    private val mActivity: Activity?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<ProductDetailData.Response.ProductbylistnameItem?>?
) : RecyclerBaseAdapter() {
    var selectedPos = 0

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_listing

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowListingItem.tag = position
        viewDataBinding.root.rowListingItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivCategory,list!!.get(position)!!.listImage!!)
        viewDataBinding.root.tvListingTitle.text = UtilityMethod.toTitleCase(list.get(position)?.name)
        viewDataBinding.root.tvListingAddress.text = list.get(position)?.address
        viewDataBinding.root.tvRating.text = list.get(position)?.review
        viewDataBinding.root.llPhone.visibility = View.INVISIBLE
        viewDataBinding.root.llWhatsapp.visibility = View.INVISIBLE
        viewDataBinding.root.view1.visibility = View.INVISIBLE
        viewDataBinding.root.llReview.setOnClickListener(View.OnClickListener {
            val bundle = Bundle()
            bundle.putString("listId", list.get(position)?.id!!)
            ReviewsActivity.startActivity(mActivity!!, bundle, false)
        })

    }

    override fun getItemCount(): Int = if (list == null) 0 else list.size
}






















