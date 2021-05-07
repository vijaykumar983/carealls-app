package  com.carealls.ui.components.activites.reviews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.ActivityReviewsBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.ReviewListData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.addReview.AddReviewActivity
import com.carealls.ui.components.adapters.ReviewAdapter
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import kotlin.collections.HashMap
import kotlin.collections.set


class ReviewsActivity : BaseBindingActivity() {
    var binding: ActivityReviewsBinding? = null
    var onClickListener: View.OnClickListener? = null
    private var viewModel: ReviewsViewModel? = null

    private var reviewListData: ArrayList<ReviewListData.Response.ListingforreviewItem?>? = null
    private var reviewListAdapter: ReviewAdapter? = null
    private var mBundle: Bundle? = null
    private var listId: String? = null


    companion object {
        private val TAG = ReviewsActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ReviewsActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reviews)
        viewModel = ViewModelProvider(this).get(ReviewsViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        getBundleData()
        binding!!.appBar.tvTitle.setText("Reviews")
        reviewListData = ArrayList<ReviewListData.Response.ListingforreviewItem?>()

        viewModel!!.responseReviewListLiveData.observe(this, Observer {
            handleReviewListResult(it)
        })

    }

    private fun getBundleData() {
        mBundle = intent.getBundleExtra("bundle")
        if (mBundle != null) {
            listId = mBundle!!.getString("listId")
        }
    }

    override fun onResume() {
        super.onResume()

        if (listId!!.isNotEmpty() && listId != null)
        {
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                reviewListAPI()
            } else {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
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
                var bundle = Bundle()
                bundle.putString("listId", listId)
                AddReviewActivity.startActivity(mActivity!!, bundle, false)
            }
        }
    }

    private fun reviewListAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.ReviewList
        reqData["list_id"] = listId!!
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.reviewList(reqData)
    }


    private fun handleReviewListResult(result: ApiResponse<ReviewListData>?) =
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
                if (result.data!!.status == "1") {
                    Log.e(TAG, "Response - " + Gson().toJson(result))

                    if (result.data.response!!.listingforreview != null && !result.data.response!!.listingforreview!!.isEmpty()) {
                        binding!!.rvReview.visibility = View.VISIBLE
                        binding!!.layoutError.rlerror.visibility = View.GONE
                        reviewListData!!.clear()
                        reviewListData!!.addAll(result.data.response!!.listingforreview!!)
                        if (reviewListData!!.size != 0) {
                            reviewListAdapter = ReviewAdapter(mActivity!!, onClickListener!!, reviewListData!!)
                            binding!!.rvReview.adapter = reviewListAdapter
                        } else {
                        }
                    } else {
                        binding!!.rvReview.visibility = View.GONE
                        binding!!.layoutError.rlerror.visibility = View.VISIBLE
                    }

                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                }
            }
        }


    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }


}





