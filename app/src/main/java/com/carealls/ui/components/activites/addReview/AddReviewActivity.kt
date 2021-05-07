package  com.carealls.ui.components.activites.addReview

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
import com.carealls.databinding.ActivityAddReviewBinding
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.AddReviewData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.utils.Constants
import com.carealls.utils.EmojiExcludeFilter
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson


class AddReviewActivity : BaseBindingActivity() {
    private var binding: ActivityAddReviewBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: AddReviewViewModel? = null
    private var mBundle: Bundle? = null
    private var listId: String? = null


    companion object {
        private val TAG = AddReviewActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, AddReviewActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_review)
        viewModel = ViewModelProvider(this).get(AddReviewViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        getBundleData()
        binding!!.appBar.tvTitle.setText("Add Review")
        binding!!.etName.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        binding!!.edtDescription.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        viewModel!!.responseLiveData.observe(this, Observer {
            handleResult(it)
        })
    }

    private fun getBundleData() {
        mBundle = intent.getBundleExtra("bundle")
        if (mBundle != null) {
            listId = mBundle!!.getString("listId")
        }
    }


    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
        binding!!.btnAddReview.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnAddReview -> {
                addReviewAPI()
            }
        }
    }

    private fun addReviewAPI() {
        val name = binding!!.etName.text.toString().trim()
        val description = binding!!.edtDescription.text.toString().trim()

        if (viewModel!!.isValidFormData(mActivity!!, name, description)) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["user_name"] = name
            reqData["description"] = description
            reqData["list_id"] = listId!!
            reqData["star_rating"] = binding!!.myRatingBar.rating.toString()
            reqData["userId"] = sessionManager!!.useR_ID
            reqData["method"] = Constants.AddReview

            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                viewModel!!.addReviewApi(reqData)
            }
        }
    }

    private fun handleResult(result: ApiResponse<AddReviewData>?) {
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
                    showSuccessfullyDialog(result.data.message)
                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.message!!)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun showSuccessfullyDialog(message: String?) {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialogBinding.tvTitle.setText("Successfully")
        dialogBinding.tvMessage.setText(message)
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





