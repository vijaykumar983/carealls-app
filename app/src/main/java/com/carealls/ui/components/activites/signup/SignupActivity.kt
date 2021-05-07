package  com.carealls.ui.components.activites.signup

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.ActivitySignupBinding
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.SignupData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.login.LoginActivity
import com.carealls.ui.components.activites.staticPage.StaticPageActivity
import com.carealls.utils.Constants
import com.carealls.utils.EmojiExcludeFilter
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson


class SignupActivity : BaseBindingActivity() {
    private var binding: ActivitySignupBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: SignupViewModel? = null


    companion object {
        private val TAG = SignupActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, SignupActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        viewModel = ViewModelProvider(this).get(SignupViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        binding!!.etFullName.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        viewModel!!.responseLiveData.observe(this, Observer {
            handleResult(it)
        })
    }


    override fun setListeners() {
        binding!!.btnSignUp.setOnClickListener(this)
        binding!!.tvPrivacyPolicy.setOnClickListener(this)
        binding!!.tvTermCondition.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        UtilityMethod.hideKeyboard(mActivity!!)
        when (view!!.id) {
            R.id.btnSignUp -> {
                signUpAPI()
            }
            R.id.tvPrivacyPolicy -> {
                val bundle = Bundle()
                bundle.putString("Title", "Privacy Policy")
                bundle.putString("url", "http://stageofproject.com/carealls/privacy.php")
                StaticPageActivity.startActivity(mActivity!!, bundle, false)
            }
            R.id.tvTermCondition -> {
                val bundle = Bundle()
                bundle.putString("Title", "Terms of Service")
                bundle.putString("url", "http://stageofproject.com/carealls/terms_conditions.php")
                StaticPageActivity.startActivity(mActivity!!, bundle, false)
            }
        }
    }

    private fun signUpAPI() {
        val fullName = binding!!.etFullName.text.toString().trim()
        val email = binding!!.etEmail.text.toString().trim()
        val mobile = binding!!.etPhone.text.toString().trim()
        val password = binding!!.etPassword.text.toString().trim()
        val confirmPassword = binding!!.etConfirmPassword.text.toString().trim()

        if (viewModel!!.isValidFormData(
                mActivity!!,
                fullName,
                email,
                mobile,
                password,
                confirmPassword
            )
        ) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["name"] = fullName
            reqData["email"] = email
            reqData["phone"] = mobile
            reqData["password"] = password
            reqData["password_confirmation"] = confirmPassword
            reqData["token"] = "asdjfa434"
            reqData["method"] = Constants.Registration

            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                viewModel!!.signupApi(reqData)
            }
        }
    }

    private fun handleResult(result: ApiResponse<SignupData>?) {
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
                    /*  var bundle = Bundle()
                      bundle.putString("countryCode", binding!!.ccp.selectedCountryCode)
                      bundle.putString("phone", binding!!.etMobileSignup.text.toString().trim())

                      VerifyOtpActivity.startActivity(mActivity!!, bundle, true)*/
                    showSuccessfullyDialog()
                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.message!!)
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
        dialogBinding.tvTitle.setText("Successfully")
        dialogBinding.tvMessage.setText("Registration Successfully")
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
            LoginActivity.startActivity(mActivity!!, null, false)
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





