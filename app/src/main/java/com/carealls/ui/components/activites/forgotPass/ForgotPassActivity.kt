package  com.carealls.ui.components.activites.forgotPass

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.ActivityForgotpassBinding
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.ForgotPassData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.resetPass.ResetPassActivity
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson


class ForgotPassActivity : BaseBindingActivity() {
    var binding: ActivityForgotpassBinding? = null
    var onClickListener: View.OnClickListener? = null
    private var viewModel: ForgotPassViewModel? = null


    companion object {
        private val TAG = ForgotPassActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ForgotPassActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgotpass)
        viewModel = ViewModelProvider(this).get(ForgotPassViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        viewModel!!.responseLiveData.observe(this, Observer {
            handleResult(it)
        })
    }


    override fun setListeners() {
        binding!!.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btnSubmit -> {
                forgotPassAPI()
            }
        }
    }


    private fun forgotPassAPI() {
        val email = binding!!.etEmail.text.toString().trim()

        if (viewModel!!.isValidFormData(mActivity!!, email)) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["email"] = email
            reqData["method"] = Constants.ForgotPassword

            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                Log.e(TAG, "Api parameters - " + reqData)
                viewModel!!.forgotPassApi(reqData)
            }
        }
    }

    private fun handleResult(result: ApiResponse<ForgotPassData>?) {
        when (result!!.status) {
            ApiResponse.Status.ERROR -> {
                ProgressDialog.hideProgressDialog()
                UtilityMethod.showToastMessageError(mActivity!!, result.error!!.message!!)
            }
            ApiResponse.Status.LOADING -> {
                ProgressDialog.showProgressDialog(this)
            }
            ApiResponse.Status.SUCCESS -> {
                ProgressDialog.hideProgressDialog()
                Log.e(TAG, "response - " + Gson().toJson(result.data))
                if (result.data!!.status!!.equals(1)) {

                    //showSuccessfullyDialog(result.data.data!!.userId,result.data.data.otp)
                    val bundle = Bundle()
                    bundle.putString("email", binding!!.etEmail.text.toString().trim())
                    bundle.putString("otp", result.data.data!!.otp.toString())
                    bundle.putString("userId", result.data.data!!.userId)
                    ResetPassActivity.startActivity(mActivity!!, bundle, false)

                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.message!!)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun showSuccessfullyDialog(userId: String?, otp: Int?) {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialogBinding.tvTitle.setText("Successfully")
        dialogBinding.tvMessage.setText("One Time Pin(OTP) has been sent to your registered email address.")
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
            val bundle = Bundle()
            bundle.putString("email", binding!!.etEmail.text.toString().trim())
            bundle.putString("otp", otp.toString())
            bundle.putString("userId", userId)
            ResetPassActivity.startActivity(mActivity!!, bundle, false)
        })
        dialog.show()
    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }


}





