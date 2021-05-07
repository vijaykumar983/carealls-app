package com.carealls.ui.components.adapters


import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.pojo.ListingDetailData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_product.view.*
import java.util.ArrayList


class CatalogueAdapter(
    private val mActivity: Activity?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<ListingDetailData.Response.ProductforcatalogItem?>?,
    private val whatsappNo: String?
) : RecyclerBaseAdapter() {
    var selectedPos = 0
    private val SHOW_LIST_COUNT = 4

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_product

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowProductItem.tag = position
        viewDataBinding.root.rowProductItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivProduct,list!!.get(position)!!.image!!)
        viewDataBinding.root.tvProductTitle.text = UtilityMethod.toTitleCase(list.get(position)?.name)

        viewDataBinding.root.llWhatsappShare.setOnClickListener {
            if (whatsappNo != null && whatsappNo!!.isNotEmpty())
            {
                UtilityMethod.shareAppOnWhatsApp(mActivity!!, "91"+whatsappNo,
                    "\nCareAlls App:\n"+Constants.AppURL+"/product/"+ list.get(position)?.name!!.replace("\\s".toRegex(), "_")
                            +"?type=PDF&pId="+list.get(position)?.id)
            }
            else
            {
                UtilityMethod.showToastMessageError(mActivity!!,"Whatsapp number not found!")
            }

        }
    }

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






















