package  com.carealls.ui.components.activites.staticPage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.carealls.R
import com.carealls.databinding.ActivityStaticPageBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.utils.UtilityMethod


class StaticPageActivity : BaseBindingActivity() {
    var binding: ActivityStaticPageBinding? = null
    var onClickListener: View.OnClickListener? = null
    private var mBundle: Bundle? = null
    private var title: String? = null
    private var url: String? = null


    companion object {
        private val TAG = StaticPageActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, StaticPageActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_static_page)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        getBundleData()

    }

    private fun getBundleData() {
        mBundle = intent.getBundleExtra("bundle")
        if (mBundle != null) {
            title = mBundle!!.getString("Title")
            url = mBundle!!.getString("url")

            binding!!.appBar.tvTitle.setText(title)

            setWebView()
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        if (UtilityMethod.isDeviceOnline(mActivity!!)) {
            ProgressDialog.showProgressDialog(mActivity!!)
            binding!!.webView.settings.setJavaScriptEnabled(true)
            binding!!.webView.getSettings().setDomStorageEnabled(true)
            binding!!.webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url!!)
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    ProgressDialog.hideProgressDialog()
                }
            }
            binding!!.webView.loadUrl(url!!)
        } else {
            UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
        }
    }


    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ivBack -> {
                finish()
            }
        }
    }

    override fun onDestroy() {
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }

}





