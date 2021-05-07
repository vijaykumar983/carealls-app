package com.carealls.ui.components.fragments.categoryDetail


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.FragmentCategoryDetailBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.CategoryDetailData
import com.carealls.pojo.CategoryProductData
import com.carealls.pojo.DeleteProductData
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.adapters.CategoryListingAdapter
import com.carealls.ui.components.adapters.CategoryProductAdapter
import com.carealls.ui.components.fragments.allCategoryProduct.AllCategoryProductFragment
import com.carealls.ui.components.fragments.listingDetail.ListingDetailFragment
import com.carealls.ui.components.fragments.productDetail.ProductDetailFragment
import com.carealls.ui.components.fragments.profile.ProfileFragment
import com.carealls.utils.Constants
import com.carealls.utils.OnItemClickedListener
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class CategoryDetailFragment : BaseFragment() , OnItemClickedListener {
    private var binding: FragmentCategoryDetailBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var drawerLayout: DrawerLayout? = null
    private var homeActivity: HomeActivity? = null
    private var viewModel: CategoryDetailViewModel? = null
    private var onItemClickedListener: OnItemClickedListener? = null

    private var categoryListingData: ArrayList<CategoryDetailData.Response.CategoryforlistingItem?>? =
        null
    private var categoryListingAdapter: CategoryListingAdapter? = null
    private var categoryProductData: ArrayList<CategoryProductData.Response.ProductforcategoryItem?>? =
        null
    private var categoryProductAdapter: CategoryProductAdapter? = null
    private var bundle: Bundle? = null
    private var categoryId: String? = null
    private var categoryImg: String? = null
    private var categoryName: String? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_category_detail, container, false)
        onClickListener = this
        onItemClickedListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = CategoryDetailFragment::class.qualifiedName
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        categoryProductData = ArrayList<CategoryProductData.Response.ProductforcategoryItem?>()
        categoryListingData = ArrayList<CategoryDetailData.Response.CategoryforlistingItem?>()
        getBundleData()
        drawerLayout = activity!!.findViewById(R.id.drawer_layout)

    }

    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(CategoryDetailViewModel::class.java)

        viewModel!!.responseCatDetailLiveData.observe(this, Observer {
            handleResult(it)
        })
        viewModel!!.responseCatProductLiveData.observe(this, Observer {
            handleCatProductResult(it)
        })
        viewModel!!.responseLiveDeleteProductData.observe(this, Observer {
            handleDeleteProductResult(it)
        })
    }

    override fun setListeners() {
        binding!!.ivCategoryMenu.setOnClickListener(this)
        binding!!.ivCategoryDetailProfile.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        when (q0!!.id) {
            R.id.ivCategoryMenu -> {
                drawerLayout!!.openDrawer(GravityCompat.START)
            }
            R.id.ivCategoryDetailProfile -> {
                homeActivity!!.changeFragment(ProfileFragment(), true)
            }
            R.id.rowCategoryProductItem -> {
                val position = q0.tag as Int
                categoryProductAdapter!!.selectedPos = position

                if (position == 2) {

                    val bundle = Bundle()
                    bundle.putString("categoryName", categoryName)
                    bundle.putString("categoryId", categoryId)
                    //bundle.putSerializable("categoryProductData", categoryProductData)
                    homeActivity!!.changeFragment(AllCategoryProductFragment(), true, bundle)
                } else {
                    var bundle = Bundle()
                    bundle.putString("productId", categoryProductData!!.get(position)!!.id)
                    bundle.putString("productTitle", categoryProductData!!.get(position)!!.name)
                    bundle.putString("productImg", categoryProductData!!.get(position)!!.image)
                    (mActivity as HomeActivity).changeFragment(ProductDetailFragment(), true, bundle)
                }
            }
            R.id.rowListingItem -> {
                val position = q0.tag as Int
                categoryListingAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("listId", categoryListingData!!.get(position)!!.id)
                (mActivity as HomeActivity).changeFragment(ListingDetailFragment(), true, bundle)
            }
        }
    }

    private fun getBundleData() {
        bundle = this.arguments
        if (bundle != null) {
            categoryId = bundle!!.getString("categoryId")
            categoryImg = bundle!!.getString("categoryImg")

            UtilityMethod.loadImage(binding!!.ivCategoryDetail, categoryImg!!)

            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                categoryProductAPI()
                categoryDetailAPI()
            } else {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        }
    }

    private fun categoryProductAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.CategoryProduct
        reqData["cat_id"] = categoryId!!
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.categoryProductApi(reqData)
    }


    private fun handleCatProductResult(result: ApiResponse<CategoryProductData>?) =
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

                    if (result.data.response!!.productforcategory != null && !result.data.response.productforcategory!!.isEmpty()) {
                        categoryProductData!!.clear()
                        categoryProductData!!.addAll(result.data.response.productforcategory)
                        if (categoryProductData!!.size != 0) {
                            categoryProductAdapter = CategoryProductAdapter(mActivity!!, onClickListener!!, categoryProductData,onItemClickedListener,sessionManager)
                            binding!!.rvCategoryProduct.adapter = categoryProductAdapter
                        } else {
                        }
                    } else {
                    }


                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                }
            }
        }

    private fun categoryDetailAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.CategoryDetail
        reqData["cat_id"] = categoryId!!
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.categoryDetailApi(reqData)
    }


    private fun handleResult(result: ApiResponse<CategoryDetailData>?) = when (result!!.status) {
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
                    binding!!.rlMain.visibility = View.VISIBLE
                    binding!!.layoutError.rlerror.visibility = View.GONE
                    setDashboardData(result.data.response)
                    categoryName = result.data.response.categoryfordetails!!.get(0)!!.catName
                } else {
                    binding!!.rlMain.visibility = View.GONE
                    binding!!.layoutError.rlerror.visibility = View.VISIBLE
                }

            } else {
                //UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                binding!!.rlMain.visibility = View.GONE
                binding!!.layoutError.rlerror.visibility = View.VISIBLE
            }
        }
    }

    private fun setDashboardData(response: CategoryDetailData.Response?) {
        /*set category listing Data*/
        if (response!!.categoryforlisting != null && !response.categoryforlisting!!.isEmpty()) {
            binding!!.txtListing.visibility = View.VISIBLE
            categoryListingData!!.clear()
            categoryListingData!!.addAll(response!!.categoryforlisting!!)
            if (categoryListingData!!.size != 0) {
                categoryListingAdapter =
                    CategoryListingAdapter(mActivity!!, onClickListener!!, categoryListingData)
                binding!!.rvCategoryDetailListing.adapter = categoryListingAdapter
            }
        } else {
            binding!!.txtListing.visibility = View.GONE
        }

    }


    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
        UtilityMethod.loadImage(binding!!.ivCategoryDetailProfile, sessionManager!!.profilE_PIC)
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

                categoryProductAPI()

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












