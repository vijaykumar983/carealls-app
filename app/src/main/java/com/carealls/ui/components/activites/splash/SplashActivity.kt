package com.carealls.ui.components.activites.splash


import android.Manifest.permission
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.carealls.R
import com.carealls.databinding.ActivitySplashBinding
import com.carealls.databinding.DialogLogoutBinding
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.activites.socialsignup.SocialSignupActivity
import com.carealls.utils.SessionManager


class SplashActivity : BaseBindingActivity() {
    private var binding: ActivitySplashBinding? = null


    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
    }

    companion object {
        private val TAG = SplashActivity::class.qualifiedName
        private const val RequestPermissionCode = 1
    }


    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {

        startSplashTimer()
    }

    override fun setListeners() {

    }

    override fun onClick(view: View?) {

    }

    private fun startSplashTimer() {
        try {
            Handler().postDelayed({
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermission()) {
                        goNextScreen()
                    } else {
                        requestPermission()
                    }
                } else {
                    goNextScreen()
                }
            }, 3000)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
            grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED ) {
            // now, you have permission go ahead
            goNextScreen()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity!!,
                    permission.CAMERA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity!!, permission.WRITE_EXTERNAL_STORAGE
                ) ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity!!,
                    permission.READ_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity!!,
                    permission.ACCESS_FINE_LOCATION
                )
            ) {
                // now, user has denied permission (but not permanently!)
                requestPermission()
            } else {
                // now, user has denied permission permanently!
                showPermissionDialog()
            }
        }
        return
    }

    private fun showPermissionDialog() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
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
        dialogBinding.tvTitle.setText(resources.getString(R.string.permission_required))
        dialogBinding.tvMessage.setText(
            resources.getString(R.string.you_have_forcefully) + " " + resources.getString(
                R.string.for_this_action
            )
        )
        dialogBinding.btnYes.setText("Settings")
        dialogBinding.btnNo.setText("Cancel")
        dialogBinding.btnNo.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            finish()
        })
        dialogBinding.btnYes.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", mActivity!!.packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        })
        dialog.show()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestPermission() {
        requestPermissions(arrayOf(permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE, permission.ACCESS_FINE_LOCATION)
            , RequestPermissionCode
        )
    }

    private fun checkPermission(): Boolean {
        val FirstPermissionResult = ContextCompat.checkSelfPermission(mActivity!!, permission.CAMERA)
        val SecondPermissionResult = ContextCompat.checkSelfPermission(mActivity!!, permission.WRITE_EXTERNAL_STORAGE)
        val ThirdPermissionResult = ContextCompat.checkSelfPermission(mActivity!!, permission.READ_EXTERNAL_STORAGE)
        val ForthPermissionResult = ContextCompat.checkSelfPermission(mActivity!!, permission.ACCESS_FINE_LOCATION)
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED && ForthPermissionResult == PackageManager.PERMISSION_GRANTED
    }

    private fun goNextScreen() {
        if (SessionManager.getInstance(this@SplashActivity).isLogin)
            HomeActivity.startActivity(this@SplashActivity, null, true)
        else SocialSignupActivity.startActivity(this@SplashActivity, null, true)
        finish()
    }


}