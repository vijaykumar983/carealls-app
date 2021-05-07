package  com.carealls.ui.components.activites.viewImage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.ActivityViewImageBinding
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.utils.UtilityMethod
import kotlinx.android.synthetic.main.row_category.view.*


class ViewImageActivity : BaseBindingActivity() {
    private var binding: ActivityViewImageBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: ViewImageViewModel? = null

    private var mBundle: Bundle? = null
    private var viewImage: String? = null


    companion object {
        private val TAG = ViewImageActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ViewImageActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_image)
        viewModel = ViewModelProvider(this).get(ViewImageViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        binding!!.appBar.tvTitle.setText("View Image")
        getBundleData()
    }

    private fun getBundleData() {
        mBundle = intent.getBundleExtra("bundle")
        if (mBundle != null) {
            viewImage = mBundle!!.getString("viewImage")

            UtilityMethod.loadImage(binding!!.mBigImage, viewImage!!)
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
        //viewModel!!.disposeSubscriber()
        super.onDestroy()
    }


}





