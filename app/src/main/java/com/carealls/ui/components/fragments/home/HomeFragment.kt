package com.carealls.ui.components.fragments.home


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.IntentSender.SendIntentException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.databinding.FragmentHomeBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.DashboardData
import com.carealls.pojo.DeleteProductData
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.adapters.*
import com.carealls.ui.components.fragments.categoryDetail.CategoryDetailFragment
import com.carealls.ui.components.fragments.listingDetail.ListingDetailFragment
import com.carealls.ui.components.fragments.locationListing.LocationListingFragment
import com.carealls.ui.components.fragments.productDetail.ProductDetailFragment
import com.carealls.ui.components.fragments.search.SearchFragment
import com.carealls.ui.components.fragments.viewAll.ViewAllFragment
import com.carealls.utils.*
import com.carealls.utils.SingleShotLocationProvider.requestSingleUpdate
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragment : BaseFragment() , OnItemClickedListener {
    private var binding: FragmentHomeBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: HomeViewModel? = null
    private var onItemClickedListener: OnItemClickedListener? = null

    private var sliderData: ArrayList<DashboardData.Response.BannersItem?>? = null
    private var sliderAdapter: SliderAdapter? = null
    private var categoryData: ArrayList<DashboardData.Response.CategoryforlistingItem?>? = null
    private var categoryAdapter: CategoryAdapter? = null
    private var listingData: ArrayList<DashboardData.Response.ListingforhomeItem?>? = null
    private var listingAdapter: ListingAdapter? = null
    private var locationData: ArrayList<DashboardData.Response.LocationforlistingItem?>? = null
    private var locationAdapter: LocationAdapter? = null
    private var productData: ArrayList<DashboardData.Response.ProductforlistItem?>? = null
    private var productAdapter: ProductAdapter? = null
    private var tvAddress: TextView? = null
    private var validDate: String? = null
    private var adminWhatsapp: String? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        onClickListener = this
        onItemClickedListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = HomeFragment::class.qualifiedName
    }

    override fun createActivityObject() {
        mActivity = activity
        tvAddress = mActivity!!.findViewById(R.id.txtAddress)
    }

    override fun initializeObject() {
        sliderData = ArrayList<DashboardData.Response.BannersItem?>()
        categoryData = ArrayList<DashboardData.Response.CategoryforlistingItem?>()
        listingData = ArrayList<DashboardData.Response.ListingforhomeItem?>()
        locationData = ArrayList<DashboardData.Response.LocationforlistingItem?>()
        productData = ArrayList<DashboardData.Response.ProductforlistItem?>()

        if (UtilityMethod.isDeviceOnline(mActivity!!)) {
            dashboardAPI()
        } else {
            binding!!.linearDashboard.visibility = View.GONE
            UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
        }

        binding!!.swipeToRefresh.setColorSchemeResources(R.color.color_0057FF)
        binding!!.swipeToRefresh.setOnRefreshListener {
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                dashboardAPI()
            } else {
                binding!!.linearDashboard.visibility = View.GONE
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
            binding!!.swipeToRefresh.isRefreshing = false
        }
        checkWhetherLocationSettingsAreSatisfied()
    }

    override fun initializeOnCreateObject() {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        viewModel!!.responseLiveData.observe(this, Observer {
            handleResult(it)
        })
        viewModel!!.responseLiveDeleteProductData.observe(this, Observer {
            handleDeleteProductResult(it)
        })
    }


    private fun dashboardAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.Dashboard
        reqData["userId"] = sessionManager!!.useR_ID
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.dashboardApi(reqData)
    }


    private fun handleResult(result: ApiResponse<DashboardData>?) = when (result!!.status) {
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



                if (result.data.validDate != null && !result.data.validDate.isEmpty() && result.data.adminWhatsapp != null && !result.data.adminWhatsapp.isEmpty()) {
                    validDate = UtilityMethod.getChangeDate(result.data.validDate)
                    adminWhatsapp = result.data.adminWhatsapp
                    checkValidDate()
                }
                if (result.data.response != null) {
                    binding!!.linearDashboard.visibility = View.VISIBLE
                    binding!!.layoutError.rlerror.visibility = View.GONE

                    setDashboardData(result.data.response)
                } else {
                    binding!!.linearDashboard.visibility = View.GONE
                    binding!!.layoutError.rlerror.visibility = View.VISIBLE
                }

            } else {
                //UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                binding!!.linearDashboard.visibility = View.GONE
                binding!!.layoutError.rlerror.visibility = View.VISIBLE
            }
        }
    }

    private fun checkValidDate() {
        try {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val date1: Date = formatter.parse(UtilityMethod.getCurrentDate())
            Log.e(TAG, "date - " + UtilityMethod.getCurrentDate() + " " + validDate)
            val date2: Date = formatter.parse(validDate)
            if (date1.compareTo(date2) < 0) {
                //UtilityMethod.showToastMessageError(mActivity!!, "date2 is Greater than my date1")
            }else{
                //UtilityMethod.showToastMessageError(mActivity!!, "date2 is Less than my date1")
                showCheckPackageDialog()
            }
        } catch (e1: ParseException) {
            e1.printStackTrace()
        }
    }

    private fun setDashboardData(response: DashboardData.Response?) {
        /*set Banner Data*/
        if (response!!.banners != null && !response.banners!!.isEmpty()) {
            sliderData!!.clear()
            sliderData!!.addAll(response!!.banners!!)
            if (sliderData!!.size != 0) {
                sliderAdapter = SliderAdapter(mActivity!!, onClickListener!!, sliderData)
                binding!!.viewPager.adapter = sliderAdapter
                binding!!.tabLayout.setupWithViewPager(binding!!.viewPager, true)


                binding!!.viewPager.startAutoScroll()
                binding!!.viewPager.setInterval(3000)
                binding!!.viewPager.setCycle(true)
                binding!!.viewPager.setStopScrollWhenTouch(true)
            }
        }
        /*set category Data*/
        if (response!!.categoryforlisting != null && !response.categoryforlisting!!.isEmpty()) {
            categoryData!!.clear()
            categoryData!!.addAll(response!!.categoryforlisting!!)
            if (categoryData!!.size != 0) {
                categoryAdapter = CategoryAdapter(mActivity!!, onClickListener!!, categoryData)
                binding!!.rvCategory.adapter = categoryAdapter
            }
        }
        /*set listing Data*/
        if (response!!.listingforhome != null && !response.listingforhome!!.isEmpty()) {
            listingData!!.clear()
            listingData!!.addAll(response!!.listingforhome!!)
            if (listingData!!.size != 0) {
                listingAdapter = ListingAdapter(mActivity!!, onClickListener!!, listingData)
                binding!!.rvListing.adapter = listingAdapter
            }
        }
        /*set location Data*/
        if (response!!.locationforlisting != null && !response.locationforlisting!!.isEmpty()) {
            locationData!!.clear()
            locationData!!.addAll(response!!.locationforlisting!!)
            if (locationData!!.size != 0) {
                locationAdapter = LocationAdapter(mActivity!!, onClickListener!!, locationData)
                binding!!.rvLocation.adapter = locationAdapter
            }
        }
        /*set product Data*/
        if (response!!.productforlist != null && !response.productforlist!!.isEmpty()) {
            productData!!.clear()
            productData!!.addAll(response!!.productforlist!!)
            if (productData!!.size != 0) {
                productAdapter = ProductAdapter(
                    mActivity!!,
                    onClickListener!!,
                    productData,
                    onItemClickedListener,
                    sessionManager
                )
                binding!!.rvProduct.adapter = productAdapter
            }
        }

    }

    @SuppressLint("SetTextI18n")
    fun showCheckPackageDialog() {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialogBinding.tvTitle.setText("Your Current plan is Expired!")
        dialogBinding.tvMessage.setText("Please recharge immediately.")
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
           UtilityMethod.shareAppOnWhatsApp(mActivity!!, adminWhatsapp, "\nCareAlls App:\n"+"Your Current plan is Expired!\n" + "Please recharge immediately")
        })
        dialog.show()
    }

    override fun setListeners() {
        binding!!.tvCategoryViewAll.setOnClickListener(this)
        binding!!.tvListingViewAll.setOnClickListener(this)
        binding!!.tvLocationViewAll.setOnClickListener(this)
        binding!!.tvProductViewAll.setOnClickListener(this)
        binding!!.llSearch.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        when (q0!!.id) {
            R.id.rowCategoryItem -> {
                val position = q0.tag as Int
                categoryAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("categoryId", categoryData!!.get(position)!!.catId)
                bundle.putString("categoryImg", categoryData!!.get(position)!!.catImage)
                (mActivity as HomeActivity).changeFragment(CategoryDetailFragment(), true, bundle)
            }
            R.id.rowProductItem -> {
                val position = q0.tag as Int
                productAdapter!!.selectedPos = position

                var bundle = Bundle()
                bundle.putString("productId", productData!!.get(position)!!.id)
                bundle.putString("productTitle", productData!!.get(position)!!.name)
                bundle.putString("productImg", productData!!.get(position)!!.image)
                (mActivity as HomeActivity).changeFragment(ProductDetailFragment(), true, bundle)
            }
            R.id.rowListingItem -> {
                val position = q0.tag as Int
                listingAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("listId", listingData!!.get(position)!!.id)
                (mActivity as HomeActivity).changeFragment(ListingDetailFragment(), true, bundle)
            }
            R.id.tvCategoryViewAll -> {
                val bundle = Bundle()
                bundle.putString("type", "All Categories")
                //bundle.putSerializable("CategoryListData", categoryData)
                (mActivity as HomeActivity).changeFragment(ViewAllFragment(), true, bundle)
            }
            R.id.tvListingViewAll -> {
                val bundle = Bundle()
                bundle.putString("type", "All Listing")
                //bundle.putSerializable("ListingData", listingData)
                (mActivity as HomeActivity).changeFragment(ViewAllFragment(), true, bundle)
            }
            R.id.tvLocationViewAll -> {
                val bundle = Bundle()
                bundle.putString("type", "All Location")
                //bundle.putSerializable("LocationData", locationData)
                (mActivity as HomeActivity).changeFragment(ViewAllFragment(), true, bundle)
            }
            R.id.tvProductViewAll -> {
                val bundle = Bundle()
                bundle.putString("type", "All Product")
                //bundle.putSerializable("ProductData", productData)
                (mActivity as HomeActivity).changeFragment(ViewAllFragment(), true, bundle)
            }
            R.id.rowLocationItem -> {
                val position = q0.tag as Int
                locationAdapter!!.selectedPos = position

                val bundle = Bundle()
                bundle.putString("cityName", locationData!!.get(position)!!.name)
                (mActivity as HomeActivity).changeFragment(LocationListingFragment(), true, bundle)
            }
            R.id.llSearch -> {
                //val bundle = Bundle()
                //bundle.putString("type", "All Product")
                //bundle.putSerializable("ProductData", productData)
                (mActivity as HomeActivity).changeFragment(SearchFragment(), true, null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val toolbar: Toolbar = mActivity!!.findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        val drawerLayout: DrawerLayout = activity!!.findViewById(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        tvAddress!!.setText(sessionManager!!.getLOCATION())
        tvAddress!!.setSelected(true)
    }

    private fun checkWhetherLocationSettingsAreSatisfied() {
        val mLocationRequest: LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000)
            .setNumUpdates(2)
        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        builder.setNeedBle(true)
        val client: SettingsClient = LocationServices.getSettingsClient(mActivity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            mActivity!!
        ) { locationSettingsResponse ->
            Log.w(
                TAG,
                "onSuccess() called with: locationSettingsResponse = [$locationSettingsResponse]"
            )
            //hasLocationPermission();
            try {
                requestSingleUpdate(mActivity, object : LocationCallback(),
                    SingleShotLocationProvider.LocationCallback {
                    override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates) {
                        Log.e(
                            TAG,
                            "my location is - " + location.latitude.toString() + " " + location.longitude
                        )
                        if (!SessionManager.getInstance(mActivity).isSELECT_LOCATION()) {
                            sessionManager!!.location = UtilityMethod.getCompleteAddressString(
                                mActivity,
                                location.latitude.toDouble(),
                                location.longitude.toDouble()
                            )
                            sessionManager!!.latitude = location.latitude.toString()
                            sessionManager!!.longitude = location.longitude.toString()
                            tvAddress!!.setText(sessionManager!!.getLOCATION())
                            tvAddress!!.setSelected(true)
                        }
                        Log.e(TAG, "location - " + sessionManager!!.location)
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "error - " + e.message)
            }
        }
        task.addOnFailureListener(
            mActivity!!
        ) { error ->
            Log.d(
                TAG,
                "onSuccess --> onFailure() called with: e = [$error]"
            )
            if (error is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    error.startResolutionForResult(mActivity, HomeActivity.REQUEST_CHECK_SETTINGS)
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
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

                dashboardAPI()

            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
            }
        }
    }

    override fun getClickedString(productId: String?, listId: String?) {
        deleteProductAPI(productId, listId)
    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }

}












