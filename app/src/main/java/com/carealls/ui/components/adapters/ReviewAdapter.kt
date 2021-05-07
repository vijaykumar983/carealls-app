package com.carealls.ui.components.adapters


import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.pojo.ReviewListData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.fragment_support.view.*
import kotlinx.android.synthetic.main.row_product.view.*
import kotlinx.android.synthetic.main.row_review.view.*
import java.util.ArrayList


class ReviewAdapter(
    private val context: Context?,
    private val onClickListener: View.OnClickListener?,
    private  val list: ArrayList<ReviewListData.Response.ListingforreviewItem?>
) : RecyclerBaseAdapter() {
    private var currentPosition = 0
    private val flag = true

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_review

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowReviewItem.tag = position
        viewDataBinding.root.rowReviewItem.setOnClickListener(onClickListener)

        //UtilityMethod.loadImage(viewDataBinding.root.ivReviewProfile,list.get(position).)

        viewDataBinding.root.tvReviewTitle.text = list.get(position)?.user
        viewDataBinding.root.tvDetail.text = list.get(position)?.description
        viewDataBinding.root.myRatingBar.rating = list.get(position)!!.starRating!!.toFloat()

    }

    override fun getItemCount(): Int = if (list == null) 0 else list.size
}






















