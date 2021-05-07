package com.carealls.ui.components.adapters


import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.databinding.DialogLogoutBinding
import com.carealls.pojo.AllProductData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.*
import kotlinx.android.synthetic.main.row_all_product.view.*
import java.util.ArrayList


class AllProductAdapter(
    private val mActivity: Activity?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<AllProductData.Response.ProductlistItem?>?,
    private val onItemClickListener1: OnItemClickListener1?,
    private val sessionManager: SessionManager?
) : RecyclerBaseAdapter() {
    var selectedPos = 0

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_all_product

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowAllProductItem.tag = position
        viewDataBinding.root.rowAllProductItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivAllProduct,list!!.get(position)!!.image!!)
        viewDataBinding.root.tvAllProductTitle.text = UtilityMethod.toTitleCase(list.get(position)?.name)

        viewDataBinding.root.rowAllProductItem.setOnLongClickListener(View.OnLongClickListener {
            if (sessionManager!!.useR_ID.equals(list!!.get(position)!!.userId)) {
                deleteItemDialog(list!!.get(position)!!.id, list!!.get(position)!!.listid)
            }
            true
        })
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

    private fun deleteItemDialog(productId: String?, listId: String?) {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogLogoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_logout,
            null,
            false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.getRoot())


        dialogBinding.tvTitle.setText("Delete Product")
        dialogBinding.tvMessage.setText("Are you sure, you want to delete product?")
        dialogBinding.btnNo.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialogBinding.btnYes.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            onItemClickListener1!!.getClickString(productId,listId)
        })

        dialog.show()
    }


    override fun getItemCount(): Int = if (list == null) 0 else list.size
}






















