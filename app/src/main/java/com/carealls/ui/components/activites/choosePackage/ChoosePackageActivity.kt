package  com.carealls.ui.components.activites.choosePackage

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
import com.carealls.databinding.ActivityChoosePackageBinding
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.PackageData
import com.carealls.pojo.SubmitPackageData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.adapters.PackageAdapter
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson


@Suppress("IMPLICIT_CAST_TO_ANY")
class ChoosePackageActivity : BaseBindingActivity() {
    private var binding: ActivityChoosePackageBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: ChoosePackageViewModel? = null

    private var packageData: ArrayList<PackageData.ResponseItem?>? = null
    private var packageAdapter: PackageAdapter? = null
    private var package_id: String? = null
    private var package_name: String? = null
    private var mBundle: Bundle? = null
    private var listId: String? = null


    companion object {
        private val TAG = ChoosePackageActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ChoosePackageActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_package)
        viewModel = ViewModelProvider(this).get(ChoosePackageViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        binding!!.appBar.tvTitle.setText("Choose Package")
        getBundleData()
        packageData = ArrayList<PackageData.ResponseItem?>()
        viewModel!!.responseLiveGetPackageData.observe(this, Observer {
            handleResult(it)
        })
        viewModel!!.responseSubmitPackageLiveData.observe(this, Observer {
            handleSubmitPackageResult(it)
        })

        if (UtilityMethod.isDeviceOnline(mActivity!!)) {
            getPackageAPI()
        } else {
            binding!!.linearMain.visibility = View.GONE
            UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
        }

    }
    private fun getBundleData() {
        mBundle = intent.getBundleExtra("bundle")
        if (mBundle != null) {
            listId = mBundle!!.getString("listId")
        }
    }



    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
       binding!!.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.rowPackageItem -> {
                val position = view.tag as Int
                packageAdapter!!.selectedPos = position
                package_id = packageData!!.get(position)!!.packageId
                package_name = packageData!!.get(position)!!.packageName
            }
            R.id.btnSubmit -> {
                if (!UtilityMethod.isOnline(mActivity!!)) {
                    UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
                } else {
                    submitPackageAPI()
                }
            }
        }
    }

    private fun getPackageAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.GetPackage
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.getPackageApi(reqData)
    }


    private fun handleResult(result: ApiResponse<PackageData>?) = when (result!!.status) {
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

                if (result.data.response != null && !result.data.response.isEmpty()) {
                    binding!!.linearMain.visibility = View.VISIBLE
                    binding!!.layoutError.rlerror.visibility = View.GONE

                    packageData!!.clear()
                    packageData!!.addAll(result.data.response)
                    if (packageData!!.size != 0) {
                        packageAdapter = PackageAdapter(mActivity!!, onClickListener!!, packageData!!)
                        binding!!.rvPackage.adapter = packageAdapter

                        package_id = packageData!!.get(0)!!.packageId
                        package_name = packageData!!.get(0)!!.packageName
                    } else {
                    }

                } else {
                    binding!!.linearMain.visibility = View.GONE
                    binding!!.layoutError.rlerror.visibility = View.VISIBLE
                }

            } else {
                //UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                binding!!.linearMain.visibility = View.GONE
                binding!!.layoutError.rlerror.visibility = View.VISIBLE
            }
        }
    }

    private fun submitPackageAPI() {
        if (listId != null && listId!!.isNotEmpty() && package_id != null && package_id!!.isNotEmpty()
            && package_name != null && package_name!!.isNotEmpty()) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["method"] = Constants.SubmitPackage
            reqData["userId"] = sessionManager!!.useR_ID
            reqData["list_id"] = listId!!
            reqData["package_id"] = package_id!!
            reqData["package_name"] = package_name!!
            Log.e(TAG, "Api parameters - $reqData")
            viewModel!!.submitPackage(reqData)
        }
    }


    private fun handleSubmitPackageResult(result: ApiResponse<SubmitPackageData>?) =
        when (result!!.status) {
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
                if (result.data!!.status == 1) {
                    Log.e(TAG, "Response - " + Gson().toJson(result))

                    sessionManager!!.lisT_ID = result.data.data!!.listId

                    showSuccessfullyDialog()

                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.message.toString())
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
        dialogBinding.tvMessage.setText("Your request has been submitted to admin for approval")
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





