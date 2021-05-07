package com.carealls.ui.components.adapters


import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.View.OnLongClickListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.databinding.DialogLogoutBinding
import com.carealls.pojo.GalleryData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.OnItemClickedListener
import com.carealls.utils.SessionManager
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_category.view.ivCategory
import kotlinx.android.synthetic.main.row_gallery.view.*
import java.util.*


class GalleryAdapter(
    private val mActivity: Activity?,
    private val onClickListener: View.OnClickListener?,
    private val list: ArrayList<GalleryData.ResponseItem?>?,
    private val onItemClickedListener: OnItemClickedListener?,
    private val userId: String?,
    private val sessionManager: SessionManager?
) : RecyclerBaseAdapter() {
    private val SHOW_LIST_COUNT = 6
    var selectedPos = 0

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_gallery

    override fun getViewModel(position: Int): Any? = 0

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.rowGalleryItem.tag = position
        viewDataBinding.root.rowGalleryItem.setOnClickListener(onClickListener)
        UtilityMethod.loadImage(viewDataBinding.root.ivCategory, list!!.get(position)!!.image!!)

        viewDataBinding.root.rowGalleryItem.setOnLongClickListener(OnLongClickListener {
            if (sessionManager!!.useR_ID.equals(userId)) {
                deleteItemDialog(list!!.get(position)!!.Id, list!!.get(position)!!.listId)
            }
            true
        })

    }

    private fun deleteItemDialog(id: String?, listId: String?) {
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


        dialogBinding.tvTitle.setText("Delete Image")
        dialogBinding.tvMessage.setText("Are you sure, you want to delete image?")
        dialogBinding.btnNo.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialogBinding.btnYes.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            onItemClickedListener!!.getClickedString(id, listId)
        })

        dialog.show()
    }

    override fun getItemCount(): Int {
        var count: Int? = null
        if (list!!.size >= SHOW_LIST_COUNT) {
            count = SHOW_LIST_COUNT
        } else {
            count = list.size
        }
        return count
    }
}






















