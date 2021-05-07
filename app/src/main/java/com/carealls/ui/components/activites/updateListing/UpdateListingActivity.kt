package  com.carealls.ui.components.activites.updateListing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.ActivityUpdateListingBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.DeleteProductData
import com.carealls.pojo.UpdateListingDetailData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.addListing.AddListingActivity
import com.carealls.ui.components.activites.addProduct.AddProductActivity
import com.carealls.ui.components.activites.reviews.ReviewsActivity
import com.carealls.ui.components.adapters.UpdateListingProductAdapter
import com.carealls.utils.Constants
import com.carealls.utils.OnItemClickedListener
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import java.util.ArrayList


class UpdateListingActivity : BaseBindingActivity() , OnItemClickedListener {
    private var binding: ActivityUpdateListingBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: UpdateListingViewModel? = null
    private var onItemClickedListener: OnItemClickedListener? = null

    private var updateListingProductAdapter: UpdateListingProductAdapter? = null
    private var updateListingProductData: ArrayList<UpdateListingDetailData.Response.ProductforlistupdateItem?>? = null
    private var updateListingData: ArrayList<UpdateListingDetailData.Response.UpdatelistdetailsItem?>? = null
    private var updateListingGalleryData: ArrayList<UpdateListingDetailData.Response.UpdatelistgalleryimagItem?>? = null
    private var listId: String? = null


    companion object {
        private val TAG = UpdateListingActivity::class.qualifiedName

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, UpdateListingActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_listing)
        viewModel = ViewModelProvider(this).get(UpdateListingViewModel::class.java)
        onClickListener = this
        onItemClickedListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        updateListingProductData = ArrayList<UpdateListingDetailData.Response.ProductforlistupdateItem?>()
        updateListingData = ArrayList<UpdateListingDetailData.Response.UpdatelistdetailsItem?>()
        updateListingGalleryData = ArrayList<UpdateListingDetailData.Response.UpdatelistgalleryimagItem?>()

        binding!!.appBar.tvTitle.setText("Update Listing")

        viewModel!!.responseLiveUpdateDetailData.observe(this, Observer {
            handleUpdateListingDetailResult(it)
        })
        viewModel!!.responseLiveDeleteProductData.observe(this, Observer {
            handleDeleteProductResult(it)
        })
    }


    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
        binding!!.llEditListing.setOnClickListener(this)
        binding!!.llReview.setOnClickListener(this)
        binding!!.btnAddProduct.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.llEditListing -> {
                var bundle = Bundle()
                bundle.putString("title", "Edit Listing")
                bundle.putSerializable("listingData", updateListingData)
                bundle.putSerializable("listingGalleryData", updateListingGalleryData)
                AddListingActivity.startActivity(mActivity!!, bundle, false)
            }
            R.id.llReview -> {
                var bundle = Bundle()
                bundle.putString("listId", listId)
                ReviewsActivity.startActivity(mActivity!!, bundle, false)
            }
            R.id.btnAddProduct -> {
                var bundle = Bundle()
                bundle.putString("title", "Add Product")
                bundle.putString("updateAddProduct", "UpdateAddProduct")
                bundle.putString("listId", listId)
                AddProductActivity.startActivity(mActivity!!, bundle, false)
            }
            R.id.llEditProductListing -> {
                val position = view.tag as Int
                updateListingProductAdapter!!.selectedPos = position

                var bundle = Bundle()
                bundle.putString("title", "Edit Product")
                bundle.putSerializable("productData", updateListingProductData)
                bundle.putSerializable("position", position)
                bundle.putString("listId", listId)
                AddProductActivity.startActivity(mActivity!!,bundle,false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (UtilityMethod.isDeviceOnline(mActivity!!)) {
            updateListingDetailAPI()
        } else {
            UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
        }
    }

    private fun updateListingDetailAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.UpdateListingDetail
        reqData["list_id"] = sessionManager!!.lisT_ID
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.updateListingDetailApi(reqData)
    }


    private fun handleUpdateListingDetailResult(result: ApiResponse<UpdateListingDetailData>?) =
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

                    if (result.data.response != null) {
                        if (result.data.response.updatelistdetails != null && !result.data.response.updatelistdetails.isEmpty()) {
                            updateListingData!!.clear()
                            updateListingData!!.addAll(result.data.response.updatelistdetails!!)
                            UtilityMethod.loadImage(
                                binding!!.ivUpdateProfile,
                                result.data.response.updatelistdetails!!.get(0)!!.listImage!!
                            )
                            binding!!.tvTitle.setText(result.data.response.updatelistdetails.get(0)!!.name)
                            binding!!.tvAddress.setText(result.data.response.updatelistdetails.get(0)!!.address)
                            binding!!.tvRating.setText(result.data.response.updatelistdetails.get(0)!!.review)
                            listId = result.data.response.updatelistdetails.get(0)!!.id
                        }
                        if (result.data.response.updatelistgalleryimag != null && !result.data.response.updatelistgalleryimag.isEmpty()) {
                            updateListingGalleryData!!.clear()
                            updateListingGalleryData!!.addAll(result.data.response.updatelistgalleryimag)
                        }

                        if (result.data.response.productforlistupdate != null && !result.data.response.productforlistupdate.isEmpty()) {
                            binding!!.txtProducts.visibility = View.VISIBLE
                            updateListingProductData!!.clear()
                            updateListingProductData!!.addAll(result.data.response.productforlistupdate!!)
                            if (updateListingProductData!!.size != 0) {
                                updateListingProductAdapter = UpdateListingProductAdapter(mActivity!!, onClickListener!!, updateListingProductData,onItemClickedListener)
                                binding!!.rvUpdateProduct.adapter = updateListingProductAdapter
                            } else {
                            }
                        } else {
                            binding!!.txtProducts.visibility = View.INVISIBLE
                        }

                    }else{}
                }else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                }
            }
        }

    private fun deleteProductAPI(productId: String?, listId: String?) {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.DeleteProduct
        reqData["product_id"] = productId!!
        reqData["list_id"] = listId!!
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.deleteProductApi(reqData)
    }


    private fun handleDeleteProductResult(result: ApiResponse<DeleteProductData>?) = when (result!!.status) {
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

                finish()
                startActivity(getIntent())

            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
            }
        }
    }


    override fun getClickedString(productId: String?,listId: String?) {
        deleteProductAPI(productId,listId)
    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }


}





