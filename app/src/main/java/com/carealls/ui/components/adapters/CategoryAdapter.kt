package com.carealls.ui.components.adapters


import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.pojo.DashboardData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_category.view.*
import java.util.ArrayList


class CategoryAdapter(
    private val context: Context?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<DashboardData.Response.CategoryforlistingItem?>?
) : RecyclerBaseAdapter() {
    var selectedPos = 0
    private val SHOW_LIST_COUNT = 6

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_category

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowCategoryItem.tag = position
        viewDataBinding.root.rowCategoryItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivCategory,list!!.get(position)!!.catImage!!)
        viewDataBinding.root.tvCategoryTitle.setText(UtilityMethod.toTitleCase(list.get(position)!!.catName))
    }

   // override fun getItemCount(): Int = if (list == null) 0 else list.size
   override fun getItemCount(): Int {
       var count : Int? = null
       if (list!!.size >= SHOW_LIST_COUNT) {
           count = SHOW_LIST_COUNT
       } else {
           count = list.size
       }
       return count
   }
}






















