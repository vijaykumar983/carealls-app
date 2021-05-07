package com.carealls.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.carealls.R

object ProgressDialog {
    var dialog: Dialog? = null
    fun showProgressDialog(context: Activity) {
        if (dialog == null)
            dialog = Dialog(context)
        dialog!!.setContentView(R.layout.fullscreen_loading)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    fun hideProgressDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }
}