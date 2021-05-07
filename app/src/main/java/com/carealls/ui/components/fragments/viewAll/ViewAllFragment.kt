package com.carealls.ui.components.fragments.viewAll


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
import com.carealls.databinding.FragmentViewAllBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.*
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.activites.viewImage.ViewImageActivity
import com.carealls.ui.components.adapters.*
import com.carealls.ui.components.fragments.categoryDetail.CategoryDetailFragment
import com.carealls.ui.components.fragments.listingDetail.ListingDetailFragment
import com.carealls.ui.components.fragments.locationListing.LocationListingFragment
import com.carealls.ui.components.fragments.productDetail.ProductDetailFragment
import com.carealls.utils.Constants
import com.carealls.utils.OnItemClickListener1
import com.carealls.utils.OnItemClickedListener
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class ViewAllFragment : BaseFragment() , OnItemClickedListener, OnItemClickListener1 {
    private var binding: FragmentViewAllBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var homeActivity: HomeActivity? = null
    private var viewModel: ViewAllViewModel? = null
    private var onItemClickedListener: OnItemClickedListener? = null
    private var onItemClickListener1: OnItemClickListener1? = null

    private var bundle: Bundle? = null
    private var type: String? = null
    private var whatsappMobile: String? = null
    private var userId: String? = null

    // private var categoryData: ArrayList<ResponseData.CategoryforlistingItem?>? = null
    //private var listingData: ArrayList<DashboardData.Response.ListingforhomeItem?>? = null
    //private var locationData: ArrayList<DashboardData.Response.LocationforlistingItem?>? = null
    //private var productData: ArrayList<DashboardData.Response.ProductforlistItem?>? = null

    private var allCategoryData: ArrayList<AllCategoryData.Response.CategoryforlistingItem?>? = null
    private var allCategoryAdapter: AllCategoryAdapter? = null
    private var allListingData: ArrayList<AllListingData.Response.ListingItem?>? = null
    private var allListingAdapter: AllListingAdapter? = null
    private var allLocationData: ArrayList<AllLocationData.Response.LocationforlistingItem?>? = null
    private var allLocationAdapter: AllLocationAdapter? = null
    private var allProductData: ArrayList<AllProductData.Response.ProductlistItem?>? = null
    private var allProductAdapter: AllProductAdapter? = null
    private var allGalleryData: ArrayList<GalleryData.ResponseItem?>? = null
    private var allGalleryAdapter: AllGalleryAdapter? = null
    private var allCatalogueData: ArrayList<ListingDetailData.Response.ProductforcatalogItem?>? = null
    private var allCatalogueAdapter: AllCatalogueAdapter? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_all, container, false)
        onClickListener = this
        onItemClickedListener = this
        onItemClickListener1 = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = ViewAllFragment::class.qualifiedName
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        getBundleData()
        /* binding!!.rvGallery.adapter = GalleryAdapter(
             mActivity!!,
             onClickListener!!,
             imagesGallery
         )*/
        allCategoryData = ArrayList<AllCategoryData.Response.CategoryforlistingItem?>()
        allListingData = ArrayList<AllListingData.Response.ListingItem?>()
        allLocationData = ArrayList<AllLocationData.Response.LocationforlistingItem?>()
        allProductData = ArrayList<AllProductData.Response.ProductlistItem?>()
        allGalleryData = ArrayList<GalleryData.ResponseItem?>()

        if (type != null && type.equals("All Categories")) {
            binding!!.rvAllCategory.visibility = View.VISIBLE
            binding!!.appBar.tvTitle.setText("All Categories")
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                allCategoryAPI()
            } else {
                binding!!.rlMain.visibility = View.GONE
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        } else if (type != null && type.equals("All Listing")) {
            binding!!.rvAllListing.visibility = View.VISIBLE
            binding!!.appBar.tvTitle.setText("All Listing")
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                allListingAPI()
            } else {
                binding!!.rlMain.visibility = View.GONE
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        } else if (type != null && type.equals("All Location")) {
            binding!!.rvAllLocation.visibility = View.VISIBLE
            binding!!.appBar.tvTitle.setText("All Location")
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                allLocationAPI()
            } else {
                binding!!.rlMain.visibility = View.GONE
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        } else if (type != null && type.equals("All Product")) {
            binding!!.rvAllProduct.visibility = View.VISIBLE
            binding!!.appBar.tvTitle.setText("All Product")
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                allProductAPI()
            } else {
                binding!!.rlMain.visibility = View.GONE
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        }
        else if (type != null && type.equals("All Gallery")) {
            binding!!.rvAllGallery.visibility = View.VISIBLE
            binding!!.appBar.tvTitle.setText("All Gallery")
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                galleryAPI()
            } else {
                binding!!.rlMain.visibility = View.GONE
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        }
        else if (type != null && type.equals("All Catalogue")) {
            binding!!.rvAllCataglogue.visibility = View.VISIBLE
            binding!!.appBar.tvTitle.setText("All Catalogue")
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                setCatalogueAdapter()
            } else {
                binding!!.rlMain.visibility = View.GONE
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        }


    }

    private fun setCatalogueAdapter() {
        /*set All Catalogue Data*/
        if (allCatalogueData != null && !allCatalogueData!!.isEmpty()) {
            binding!!.rlMain.visibility = View.VISIBLE
            binding!!.layoutError.rlerror.visibility = View.GONE
            if (allCatalogueData!!.size != 0) {
                allCatalogueAdapter = AllCatalogueAdapter(mActivity!!, onClickListener, allCatalogueData!!,whatsappMobile)
                binding!!.rvAllCataglogue.adapter = allCatalogueAdapter
            }
        }
        else{
            binding!!.rlMain.visibility = View.GONE
            binding!!.layoutError.rlerror.visibility = View.VISIBLE
        }
    }

    private fun getBundleData() {
        bundle = this.arguments
        if (bundle != null) {
            type = bundle!!.getString("type")
            userId = bundle!!.getString("userId")
            whatsappMobile = bundle!!.getString("whatsappMobile")
            allCatalogueData = bundle!!.getSerializable("catalogueListData") as ArrayList<ListingDetailData.Response.ProductforcatalogItem?>?
            //categoryData = bundle!!.getSerializable("CategoryListData") as ArrayList<ResponseData.CategoryforlistingItem?>?
            //  listingData = bundle!!.getSerializable("ListingData") as ArrayList<DashboardData.Response.ListingforhomeItem?>?
            // locationData = bundle!!.getSerializable("LocationData") as ArrayList<DashboardData.Response.LocationforlistingItem?>?
            //  productData = bundle!!.getSerializable("ProductData") as ArrayList<DashboardData.Response.ProductforlistItem?>?
        }
        setAdapter()
    }

    private fun setAdapter() {

        // binding!!.rvAllCategory.adapter = AllCategoryAdapter(mActivity!!, onClickListener!!, categoryData)
        //binding!!.rvAllListing.adapter = AllListingAdapter(mActivity!!, onClickListener!!, listingData)
        // binding!!.rvAllLocation.adapter = AllLocationAdapter(mActivity!!, onClickListener!!, locationData)
        // binding!!.rvAllProduct.adapter = AllProductAdapter(mActivity!!, onClickListener!!, productData)
    }

    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(ViewAllViewModel::class.java)

        viewModel!!.responseAllCategoryLiveData.observe(this, Observer {
            handleResult(it)
        })
        viewModel!!.responseAllListingLiveData.observe(this, Observer {
            handleAllListingResult(it)
        })
        viewModel!!.responseAllLocationLiveData.observe(this, Observer {
            handleAllLocationResult(it)
        })
        viewModel!!.responseAllProductLiveData.observe(this, Observer {
            handleAllProductResult(it)
        })
        viewModel!!.responseGalleryLiveData.observe(this, Observer {
            handleGalleryResult(it)
        })
        viewModel!!.responseDeleteGalleryLiveData.observe(this, Observer {
            handleDeleteGalleryResult(it)
        })
        viewModel!!.responseLiveDeleteProductData.observe(this, Observer {
            handleDeleteProductResult(it)
        })
    }

    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        when (q0!!.id) {
            R.id.ivBack -> {
                homeActivity!!.onBackPressed()
            }
            R.id.rowAllLocationItem -> {
                val position = q0.tag as Int
                allLocationAdapter!!.selectedPos = position

                var bundle = Bundle()
                bundle.putString("cityName", allLocationData!!.get(position)!!.name)
                homeActivity!!.changeFragment(LocationListingFragment(), true, bundle)
            }
            R.id.rowAllProductItem -> {
                val position = q0.tag as Int
                allProductAdapter!!.selectedPos = position

                var bundle = Bundle()
                bundle.putString("productId", allProductData!!.get(position)!!.id)
                bundle.putString("productTitle", allProductData!!.get(position)!!.name)
                bundle.putString("productImg", allProductData!!.get(position)!!.image)
               homeActivity!!.changeFragment(ProductDetailFragment(), true, bundle)
            }
            R.id.rowCategoryItem -> {
                val position = q0.tag as Int
                allCategoryAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("categoryId", allCategoryData!!.get(position)!!.catId)
                bundle.putString("categoryImg", allCategoryData!!.get(position)!!.catImage)
                (mActivity as HomeActivity).changeFragment(CategoryDetailFragment(), true, bundle)
            }
            R.id.rowListingItem -> {
                val position = q0.tag as Int
                allListingAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("listId", allListingData!!.get(position)!!.id)
                (mActivity as HomeActivity).changeFragment(ListingDetailFragment(), true, bundle)
            }
            R.id.rowGalleryItem -> {
                val position = q0.tag as Int
                allGalleryAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("viewImage", allGalleryData!!.get(position)!!.image)
                ViewImageActivity.startActivity(mActivity!!,bundle,false)
            }
            R.id.rowProductItem -> {
                val position = q0.tag as Int
                allCatalogueAdapter!!.selectedPos = position

                var bundle = Bundle()
                bundle.putString("productId", allCatalogueData!!.get(position)!!.id)
                bundle.putString("productTitle", allCatalogueData!!.get(position)!!.name)
                bundle.putString("productImg", allCatalogueData!!.get(position)!!.image)
                (mActivity as HomeActivity).changeFragment(ProductDetailFragment(), true, bundle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
        // binding!!.appBar.tvTitle.text = "Update Profile"
    }

    private fun allCategoryAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.CategoryList
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.allCategory(reqData)
    }


    private fun handleResult(result: ApiResponse<AllCategoryData>?) = when (result!!.status) {
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

                    setAllCategoryData(result.data.response)

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

    private fun setAllCategoryData(response: AllCategoryData.Response?) {
        /*set All Category Data*/
        if (response!!.categoryforlisting != null && !response.categoryforlisting!!.isEmpty()) {
            allCategoryData!!.clear()
            allCategoryData!!.addAll(response.categoryforlisting)
            if (allCategoryData!!.size != 0) {
                allCategoryAdapter = AllCategoryAdapter(mActivity!!, onClickListener, allCategoryData!!)
                binding!!.rvAllCategory.adapter = allCategoryAdapter
            }
        }

    }


    private fun allListingAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.Listing
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.allListing(reqData)
    }


    private fun handleAllListingResult(result: ApiResponse<AllListingData>?) =
        when (result!!.status) {
            ApiResponse.Status.ERROR -> {
                ProgressDialog.hideProgressDialog()
                UtilityMethod.showToastMessageError(mActivity!!, result.error!!.message!!)
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

                        setAllListingData(result.data.response)

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

    private fun setAllListingData(response: AllListingData.Response) {
        /*set All Listing Data*/
        if (response!!.listing != null && !response.listing!!.isEmpty()) {
            allListingData!!.clear()
            allListingData!!.addAll(response.listing)
            if (allListingData!!.size != 0) {
                allListingAdapter = AllListingAdapter(mActivity!!, onClickListener, allListingData!!)
                binding!!.rvAllListing.adapter = allListingAdapter
            }
        }
    }


    private fun allLocationAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.LocationList
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.allLocation(reqData)
    }


    private fun handleAllLocationResult(result: ApiResponse<AllLocationData>?) =
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
                        binding!!.rlMain.visibility = View.VISIBLE
                        binding!!.layoutError.rlerror.visibility = View.GONE

                        setAllLocationData(result.data.response)

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

    private fun setAllLocationData(response: AllLocationData.Response) {
        /*set All Location Data*/
        if (response!!.locationforlisting != null && !response.locationforlisting!!.isEmpty()) {
            allLocationData!!.clear()
            allLocationData!!.addAll(response.locationforlisting)
            if (allLocationData!!.size != 0) {
                allLocationAdapter =
                    AllLocationAdapter(mActivity!!, onClickListener, allLocationData!!)
                binding!!.rvAllLocation.adapter = allLocationAdapter
            }
        }
    }

    private fun allProductAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.Product
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.allProduct(reqData)
    }


    private fun handleAllProductResult(result: ApiResponse<AllProductData>?) =
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
                        binding!!.rlMain.visibility = View.VISIBLE
                        binding!!.layoutError.rlerror.visibility = View.GONE

                        setAllProductData(result.data.response)

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

    private fun setAllProductData(response: AllProductData.Response) {
        /*set All Product Data*/
        if (response!!.productlist != null && !response.productlist!!.isEmpty()) {
            allProductData!!.clear()
            allProductData!!.addAll(response.productlist)
            if (allProductData!!.size != 0) {
                allProductAdapter =
                    AllProductAdapter(mActivity!!, onClickListener, allProductData!!,onItemClickListener1,sessionManager)
                binding!!.rvAllProduct.adapter = allProductAdapter
            }
        }
    }

    private fun galleryAPI() {
        if (userId != null && userId!!.isNotEmpty()) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["method"] = Constants.Gallery
            reqData["userId"] =userId!!
                Log.e(TAG, "Api parameters - $reqData")
            viewModel!!.gallery(reqData)
        }
    }


    private fun handleGalleryResult(result: ApiResponse<GalleryData>?) = when (result!!.status) {
        ApiResponse.Status.ERROR -> {
            ProgressDialog.hideProgressDialog()
            UtilityMethod.showToastMessageError(mActivity!!, result.error!!.message!!)
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

                    setAllGalleryData(result.data.response)

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

    private fun setAllGalleryData(response: List<GalleryData.ResponseItem?>) {
        /*set All Gallery Data*/
        if (response != null && !response.isEmpty()) {
            allGalleryData!!.clear()
            allGalleryData!!.addAll(response)
            if (allGalleryData!!.size != 0) {
                allGalleryAdapter = AllGalleryAdapter(mActivity!!, onClickListener, allGalleryData!!,onItemClickedListener,userId,sessionManager)
                binding!!.rvAllGallery.adapter = allGalleryAdapter
            }
        }
    }

    private fun deleteGalleryAPI(id: String?, listId: String?) {
        if (userId != null && userId!!.isNotEmpty()) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["method"] = Constants.DeleteGallery
            reqData["userId"] = userId!!
            reqData["id"] = id!!
            reqData["list_id"] = listId!!
            Log.e(TAG, "Api parameters - $reqData")
            viewModel!!.deleteGallery(reqData)
        }
    }


    private fun handleDeleteGalleryResult(result: ApiResponse<DeleteGalleryData>?) = when (result!!.status) {
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

                galleryAPI()

            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
            }
        }
    }

    override fun getClickedString(id: String?,listId: String?) {
        deleteGalleryAPI(id,listId)
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

                allProductAPI()

            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
            }
        }
    }


    override fun getClickString(productId: String?,listId: String?) {
        deleteProductAPI(productId,listId)
    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }

}












