package com.carealls.ui.components.fragments.manageListing


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.carealls.R
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.databinding.FragmentManageListingBinding
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.addListing.AddListingActivity
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.activites.updateListing.UpdateListingActivity
import com.carealls.utils.UtilityMethod


class ManageListingFragment : BaseFragment() {
    private var binding: FragmentManageListingBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var homeActivity: HomeActivity? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_listing, container, false)
        //   viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {

    }

    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        // viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        /* viewModel!!.responseLiveData.observe(this, Observer {
             handleResult(it)
         })*/
    }

    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
        binding!!.rlAddListing.setOnClickListener(this)
        binding!!.rlUpdateListing.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        when (q0!!.id) {
            R.id.ivBack -> {
                homeActivity!!.onBackPressed()
            }
            R.id.rlAddListing -> {
                if (sessionManager!!.lisT_ID == null || sessionManager!!.lisT_ID.isEmpty()) {
                    AddListingActivity.startActivity(mActivity!!, null, false)
                } else {
                    showSuccessfullyDialog("Please Update Listing")
                }
            }
            R.id.rlUpdateListing -> {
                if (sessionManager!!.lisT_ID != null && sessionManager!!.lisT_ID.isNotEmpty()) {
                    UpdateListingActivity.startActivity(mActivity!!, null, false)
                } else {
                    showSuccessfullyDialog("Please Add Listing")
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun showSuccessfullyDialog(msg: String) {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialogBinding.tvTitle.setText("Manage Listing")
        dialogBinding.tvMessage.setText(msg)
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

        dialogBinding.btnOK.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
        binding!!.appBar.tvTitle.text = "Manage Listing"

        if (sessionManager!!.lisT_ID == null || sessionManager!!.lisT_ID.isEmpty()) {
            binding!!.rlAddListing.visibility = View.VISIBLE
            binding!!.rlUpdateListing.visibility = View.GONE
        } else {
            binding!!.rlAddListing.visibility = View.GONE
            binding!!.rlUpdateListing.visibility = View.VISIBLE
        }
    }

}












