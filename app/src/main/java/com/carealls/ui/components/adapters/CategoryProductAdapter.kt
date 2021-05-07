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
import com.carealls.pojo.CategoryProductData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.OnItemClickedListener
import com.carealls.utils.SessionManager
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_category.view.cvCategory
import kotlinx.android.synthetic.main.row_category.view.ivCategory
import kotlinx.android.synthetic.main.row_category_product.view.*
import kotlinx.android.synthetic.main.row_category_product.view.tvProductTitle
import java.util.*


class CategoryProductAdapter(
    private val mActivity: Activity?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<CategoryProductData.Response.ProductforcategoryItem?>?,
    private val onItemClickedListener: OnItemClickedListener?,
    private val sessionManager: SessionManager?
) : RecyclerBaseAdapter() {
    var selectedPos = 0
    private val SHOW_LIST_COUNT = 3

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_category_product

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowCategoryProductItem.tag = position
        viewDataBinding.root.rowCategoryProductItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivCategory, list!!.get(position)!!.image!!)
        viewDataBinding.root.tvProductTitle.text =
            UtilityMethod.toTitleCase(list.get(position)?.name)

        if (position == 2) {
            viewDataBinding.root.cvCategory.visibility = View.GONE
            viewDataBinding.root.rlViewAll.visibility = View.VISIBLE
        } else {
            viewDataBinding.root.cvCategory.visibility = View.VISIBLE
            viewDataBinding.root.rlViewAll.visibility = View.GONE

            viewDataBinding.root.rowCategoryProductItem.setOnLongClickListener(View.OnLongClickListener {
                if (sessionManager!!.useR_ID.equals(list!!.get(position)!!.userId)) {
                    deleteItemDialog(list!!.get(position)!!.id, list!!.get(position)!!.listid)
                }
                true
            })
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
            onItemClickedListener!!.getClickedString(productId,listId)
        })

        dialog.show()
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






















