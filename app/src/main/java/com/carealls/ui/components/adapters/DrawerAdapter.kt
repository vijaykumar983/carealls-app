package com.carealls.ui.components.adapters

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.databinding.RowDrawerBinding
import com.carealls.pojo.DrawerData
import com.carealls.ui.base.RecyclerBaseAdapter
import com.carealls.utils.Constants


class DrawerAdapter(
    private val context: AppCompatActivity?,
    private val list: ArrayList<DrawerData>,
    private val onClickListener: View.OnClickListener?

) : RecyclerBaseAdapter() {
    var selectedPos: Int = 0
    override fun getLayoutIdForPosition(position: Int): Int = R.layout.row_drawer

    override fun getViewModel(position: Int) = list[position]

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {

        if (viewDataBinding is RowDrawerBinding) {
            viewDataBinding.llMain.tag = position
            viewDataBinding.llMain.setOnClickListener(onClickListener)
            if (selectedPos == position) {
                viewDataBinding.llMain.setBackgroundColor(
                    ContextCompat.getColor(context!!, R.color.white)
                )
                viewDataBinding.txtDrawerName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlack
                    )
                )

            } else {
                viewDataBinding.llMain.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.colorWhite
                    )
                )
                viewDataBinding.txtDrawerName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
            }
            viewDataBinding.txtDrawerName.text = list[position].name
        }
    }

    override fun getItemCount(): Int = if (list == null) 0 else list.size
}