package com.carealls.ui.components.fragments.support


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.FragmentSupportBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.SupportData
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson


class SupportFragment : BaseFragment() {
    private var binding: FragmentSupportBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var homeActivity: HomeActivity? = null
    private var viewModel: SupportViewModel? = null
    private var emailId: String? = null
    private var phone: String? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_support, container, false)
        onClickListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = SupportFragment::class.qualifiedName
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        if (UtilityMethod.isDeviceOnline(mActivity!!)) {
            supportAPI()
        } else {
            binding!!.llMain.visibility = View.GONE
            UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
        }
    }

    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(SupportViewModel::class.java)

        viewModel!!.responseLiveData.observe(this, Observer {
            handleResult(it)
        })
    }

    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
        binding!!.linearContact.setOnClickListener(this)
        binding!!.tvEmailId.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        when (q0!!.id) {
            R.id.ivBack -> {
                homeActivity!!.onBackPressed()
            }
            R.id.linearContact -> {
                if (phone != null && !phone!!.isEmpty()) {
                    UtilityMethod.audioCall(mActivity!!, phone!!)
                }
            }
            R.id.tvEmailId -> {
                if (emailId != null && !emailId!!.isEmpty()) {
                    UtilityMethod.sendEmail(mActivity!!, emailId!!)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
        binding!!.appBar.tvTitle.text = "Support"
    }


    private fun supportAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.Support
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.supportApi(reqData)
    }


    private fun handleResult(result: ApiResponse<SupportData>?) = when (result!!.status) {
        ApiResponse.Status.ERROR -> {
            ProgressDialog.hideProgressDialog()
            UtilityMethod.showToastMessageError(
                mActivity!!,
                result.error!!.message!!
            )
        }
        ApiResponse.Status.LOADING -> {
            ProgressDialog.showProgressDialog(mActivity!!)
        }
        ApiResponse.Status.SUCCESS -> {
            ProgressDialog.hideProgressDialog()
            if (result.data!!.status == "1") {
                Log.e(TAG, "Response - " + Gson().toJson(result))

                if (result.data.response != null) {
                    binding!!.llMain.visibility = View.VISIBLE
                    binding!!.layoutError.rlerror.visibility = View.GONE
                    setSupportData(result.data.response)
                } else {
                    binding!!.llMain.visibility = View.GONE
                    binding!!.layoutError.rlerror.visibility = View.VISIBLE
                }

            } else {
                //UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                binding!!.llMain.visibility = View.GONE
                binding!!.layoutError.rlerror.visibility = View.VISIBLE
            }
        }
    }

    private fun setSupportData(response: List<SupportData.ResponseItem?>?) {
        emailId = response!!.get(0)!!.supportEmail
        phone = response!!.get(0)!!.supportContact
        binding!!.tvTitle.setText(UtilityMethod.toTitleCase(response!!.get(0)!!.supportTitle))
        binding!!.tvDescription.setText(response!!.get(0)!!.description)
        binding!!.tvEmailId.setText(response!!.get(0)!!.supportEmail)
        binding!!.tvMobile.setText(response!!.get(0)!!.supportContact)
    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }


}












