package  com.carealls.ui.components.activites.resetPass

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
import com.carealls.databinding.ActivityResetPassBinding
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.ForgotPassData
import com.carealls.pojo.ResetPassData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.login.LoginActivity
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson


class ResetPassActivity : BaseBindingActivity() {
    private var binding: ActivityResetPassBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: ResetPassViewModel? = null
    private var mBundle: Bundle? = null
    private var email: String? = null
    private var otp: String? = null
    private var userId: String? = null


    companion object {
        private val TAG = ResetPassActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ResetPassActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_pass)
        viewModel = ViewModelProvider(this).get(ResetPassViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        getBundleData()
        viewModel!!.responseLiveResendOtpData.observe(this, Observer {
            handleResult(it)
        })
        viewModel!!.responseLiveResetPassData.observe(this, Observer {
            handleResetPassResult(it)
        })
    }

    private fun getBundleData() {
        mBundle = intent.getBundleExtra("bundle")
        if (mBundle != null) {
            email = mBundle!!.getString("email")
            userId = mBundle!!.getString("userId")
            otp = mBundle!!.getString("otp")
            binding!!.etEnterCode.setText(otp)
        }
    }


    override fun setListeners() {
        binding!!.tvResendOtp.setOnClickListener(this)
        binding!!.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.tvResendOtp -> {
                forgotPassAPI()
            }
            R.id.btnSubmit -> {
                resetPassAPI()
            }
        }
    }

    private fun forgotPassAPI() {
        if (viewModel!!.isValidFormData(mActivity!!, email)) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["email"] = email!!
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

                    otp = result.data.data!!.otp.toString()
                    binding!!.etEnterCode.setText(otp)
                    showSuccessfullyDialog()
                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.message!!)
                }
            }
        }
    }

    private fun resetPassAPI() {
        val otp = binding!!.etEnterCode.text.toString().trim()
        val newPass = binding!!.etNewPassword.text.toString().trim()
        val confirmNewPass = binding!!.etConfirmNewPassword.text.toString().trim()

        if (viewModel!!.isValidFormResetPassData(mActivity!!, otp, newPass, confirmNewPass)) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["userId"] = userId!!
            reqData["password"] = confirmNewPass
            reqData["reset_code"] = otp
            reqData["method"] = Constants.ResetPassword

            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                Log.e(TAG, "Api parameters - " + reqData)
                viewModel!!.resetPassApi(reqData)
            }
        }
    }

    private fun handleResetPassResult(result: ApiResponse<ResetPassData>?) {
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
                if (result.data!!.status!!.equals("1")) {

                    showSuccessfullyResetPassDialog()
                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg!!)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun showSuccessfullyDialog() {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialogBinding.tvTitle.setText("Resend OTP")
        dialogBinding.tvMessage.setText("Your new One Time Pin(OTP) has been sent to your registered email address.")
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

    @SuppressLint("SetTextI18n")
    fun showSuccessfullyResetPassDialog() {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialogBinding.tvTitle.setText("Successfully")
        dialogBinding.tvMessage.setText("successfully Reset password")
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
            LoginActivity.startActivity(mActivity!!, null, true)
            finish()
        })
        dialog.show()
    }


    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }


}





