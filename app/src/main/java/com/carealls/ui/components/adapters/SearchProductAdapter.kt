package com.carealls.ui.components.adapters


import android.app.Activity
import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.pojo.AllProductData
import com.carealls.pojo.DashboardData
import com.carealls.pojo.SearchData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_all_product.view.*
import kotlinx.android.synthetic.main.row_location.view.ivLocation
import kotlinx.android.synthetic.main.row_product.view.*
import kotlinx.android.synthetic.main.row_product.view.ivProduct
import kotlinx.android.synthetic.main.row_product.view.rowProductItem
import kotlinx.android.synthetic.main.row_product.view.tvProductTitle
import java.util.ArrayList


class SearchProductAdapter(
    private val mActivity: Activity?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<SearchData.Response.ProductsearchItem?>?
) : RecyclerBaseAdapter() {
    var selectedPos = 0

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_all_product

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowAllProductItem.tag = position
        viewDataBinding.root.rowAllProductItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivAllProduct,list!!.get(position)!!.image!!)
        viewDataBinding.root.tvAllProductTitle.text = UtilityMethod.toTitleCase(list.get(position)?.name)

        viewDataBinding.root.llAllProductWhatsapp.setOnClickListener {
            if (list.get(position)?.whatsappNo != null && list.get(position)?.whatsappNo!!.isNotEmpty())
            {
                UtilityMethod.shareAppOnWhatsApp(mActivity!!, "91"+list.get(position)?.whatsappNo,
                    "\nCareAlls App:\n"+Constants.AppURL+"/product/"+ list.get(position)?.name!!.replace("\\s".toRegex(), "_")
                            +"?type=PDF&pId="+list.get(position)?.id)
            }
            else
            {
                UtilityMethod.showToastMessageError(mActivity!!,"Whatsapp number not found!")
            }
        }

    }

    override fun getItemCount(): Int = if (list == null) 0 else list.size
}






















