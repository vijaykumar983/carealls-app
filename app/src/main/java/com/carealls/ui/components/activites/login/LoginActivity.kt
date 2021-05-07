package  com.carealls.ui.components.activites.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.ActivityLoginBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.LoginData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.forgotPass.ForgotPassActivity
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.activites.staticPage.StaticPageActivity
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson


class LoginActivity : BaseBindingActivity() {
    private var binding: ActivityLoginBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: LoginViewModel? = null


    companion object {
        private val TAG = LoginActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, LoginActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
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
        binding!!.btnLogin.setOnClickListener(this)
        binding!!.tvForgotPass.setOnClickListener(this)
        binding!!.tvPrivacyPolicy.setOnClickListener(this)
        binding!!.tvTermCondition.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btnLogin -> {
                loginAPI()
                //notificationCheckApi()
            }
            R.id.tvForgotPass -> {
                ForgotPassActivity.startActivity(mActivity!!, null, false)
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


    private fun loginAPI() {
        val email = binding!!.etEmail.text.toString().trim()
        val password = binding!!.etPassword.text.toString().trim()

        if (viewModel!!.isValidFormData(mActivity!!, email, password)) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["email"] = email
            reqData["password"] = password
            reqData["method"] = Constants.Login

            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                Log.e(TAG, "Api parameters - " + reqData)
                viewModel!!.loginApi(reqData)
            }
        }
    }

    private fun handleResult(result: ApiResponse<LoginData>?) {
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
                    sessionManager!!.setLogin()
                    sessionManager!!.useR_ID = result.data!!.data!!.userId
                    sessionManager!!.name = result.data!!.data!!.username
                    sessionManager!!.email = result.data!!.data!!.email
                    sessionManager!!.profilE_PIC = result.data!!.data!!.profile.toString()
                    sessionManager!!.mobile = result.data!!.data!!.phone
                    sessionManager!!.address = result.data!!.data!!.address
                    sessionManager!!.lisT_ID = result.data!!.data!!.listId

                    HomeActivity.startActivity(mActivity!!, null, true)
                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg!!)
                }
            }
        }
    }


    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }


}





