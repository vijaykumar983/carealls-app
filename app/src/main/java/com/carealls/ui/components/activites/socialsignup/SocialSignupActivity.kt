package  com.carealls.ui.components.activites.socialsignup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.ActivitySocialSignupBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.SocialLoginData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.activites.login.LoginActivity
import com.carealls.ui.components.activites.signup.SignupActivity
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson


class SocialSignupActivity : BaseBindingActivity() {
    private var binding: ActivitySocialSignupBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: SocialSignupViewModel? = null

    private val RC_SIGN_IN = 128
    private var mGoogleSignInClient: GoogleSignInClient? = null


    companion object {
        private val TAG = SocialSignupActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, SocialSignupActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_social_signup)
        viewModel = ViewModelProvider(this).get(SocialSignupViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(resources.getString(R.string.googleAccountWebClientID))
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        viewModel!!.responseLiveData.observe(this, Observer {
            handleResult(it)
        })
    }


    override fun setListeners() {
        binding!!.rlGmail.setOnClickListener(this)
        binding!!.rlMail.setOnClickListener(this)
        binding!!.tvLogin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.rlGmail -> {
                googleSignIn()
            }
            R.id.rlMail -> {
                SignupActivity.startActivity(mActivity!!, null, false)
            }
            R.id.tvLogin -> {
                LoginActivity.startActivity(mActivity!!, null, false)
            }
        }
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            val map: MutableMap<String, String?> = HashMap()
            map["socialId"] = account!!.id
            map["fullName"] = account.displayName
            map["email"] = account.email
            // map["socialType"] = Constants.SOCIAL_TYPE_GOOGLE
            //map.put("picture", String.valueOf(account.getPhotoUrl()));
            Log.e(TAG, "google response - $map")
            socialSignApi(map, "google")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun socialSignApi(map: MutableMap<String, String?>, loginfrom: String) {

        val reqData: HashMap<String, String?> = HashMap()
        reqData["email"] = map["email"]
        reqData["loginfrom"] = loginfrom
        reqData["fullname"] = map["fullName"]
        reqData["socialtoken"] = map["socialId"]
        reqData["method"] = Constants.SocialLogin

        if (!UtilityMethod.isOnline(mActivity!!)) {
            UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
        } else {
            Log.e(TAG, "Api parameters - " + reqData)
            viewModel!!.socialLoginApi(reqData)
        }


    }

    private fun handleResult(result: ApiResponse<SocialLoginData>?) {
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
                    sessionManager!!.name = result.data!!.data!!.name
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





