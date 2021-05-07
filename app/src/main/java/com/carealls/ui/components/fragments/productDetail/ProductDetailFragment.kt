package com.carealls.ui.components.fragments.productDetail


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.FragmentProductDetailBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.CategoryProductData
import com.carealls.pojo.ProductDetailData
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.adapters.ProductListingAdapter
import com.carealls.ui.components.fragments.listingDetail.ListingDetailFragment
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class ProductDetailFragment : BaseFragment() {
    private var binding: FragmentProductDetailBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var homeActivity: HomeActivity? = null
    private var viewModel: ProductDetailViewModel? = null

    private var productListingAdapter: ProductListingAdapter? = null
    private var productListingData: ArrayList<ProductDetailData.Response.ProductbylistnameItem?>? = null

    private var bundle: Bundle? = null
    private var productId: String? = null
    private var productTitle: String? = null
    private var productDesc: String? = null
    private var productImg: String? = null
    private var listWhatsappNo: String? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        onClickListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = ProductDetailFragment::class.qualifiedName
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        productListingData = ArrayList<ProductDetailData.Response.ProductbylistnameItem?>()
        getBundleData()
    }

    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(ProductDetailViewModel::class.java)

        viewModel!!.responseLiveProductDetailData.observe(this, Observer {
            handleProductDetailResult(it)
        })
    }

    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
        binding!!.rlWhatsapp.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        when (q0!!.id) {
            R.id.ivBack -> {
                homeActivity!!.onBackPressed()
            }
            R.id.rlWhatsapp -> {
                if (listWhatsappNo != null && listWhatsappNo!!.isNotEmpty())
                UtilityMethod.shareAppOnWhatsApp(mActivity!!, "91"+listWhatsappNo,"\nCareAlls App:\n"+Constants.AppURL+"/product/"+ productTitle!!.replace("\\s".toRegex(), "_")
                        +"?type=PDF&pId="+productId)
                else
                    UtilityMethod.showToastMessageError(mActivity!!,"Whatsapp number not found!")
            }
            R.id.rowListingItem -> {
                val position = q0.tag as Int
                productListingAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("listId", productListingData!!.get(position)!!.id)
                (mActivity as HomeActivity).changeFragment(ListingDetailFragment(), true, bundle)
            }
        }
    }

    private fun getBundleData() {
        bundle = this.arguments
        if (bundle != null) {
            productId = bundle!!.getString("productId")
            productTitle = bundle!!.getString("productTitle")
            productImg = bundle!!.getString("productImg")

            if(productTitle !=null && productTitle!!.isNotEmpty())
            binding!!.appBar.tvTitle.setText(UtilityMethod.toTitleCase(productTitle))
            if(productImg !=null && productImg!!.isNotEmpty())
            UtilityMethod.loadImage(binding!!.ivProductDetail, productImg!!)

            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                productDetailAPI()
            } else {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
    }


    private fun productDetailAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.ProductDetail
        reqData["product_id"] = productId!!
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.productDetailApi(reqData)
    }


    private fun handleProductDetailResult(result: ApiResponse<ProductDetailData>?) =
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
                        binding!!.appBar.tvTitle.setText(UtilityMethod.toTitleCase(result.data.response.productfordetails!!.get(0)!!.name))
                        UtilityMethod.loadImage(binding!!.ivProductDetail, result.data.response.productfordetails!!.get(0)!!.image!!)
                        binding!!.txtDetail.setText(result.data.response.productfordetails.get(0)!!.productDescription)
                        binding!!.tvPrice.setText("Price: Rs. " + result.data.response.productfordetails.get(0)!!.productPrice)
                        productDesc = result.data.response.productfordetails.get(0)!!.productDescription

                        if (result.data.response.productbylistname != null && !result.data.response.productbylistname.isEmpty()) {
                            productListingData!!.clear()
                            productListingData!!.addAll(result.data.response.productbylistname)
                            if (productListingData!!.size != 0) {
                                productListingAdapter = ProductListingAdapter(
                                    mActivity!!,
                                    onClickListener!!,
                                    productListingData
                                )
                                binding!!.rvProductListing.adapter = productListingAdapter
                                listWhatsappNo = productListingData!!.get(0)!!.whatsappNumber
                            } else {
                            }
                        } else {
                        }
                    } else {
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












