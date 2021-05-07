package com.carealls.ui.components.fragments.locationListing


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
import com.carealls.databinding.FragmentLocationListingBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.*
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.adapters.*
import com.carealls.ui.components.fragments.listingDetail.ListingDetailFragment
import com.carealls.utils.Constants
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class LocationListingFragment : BaseFragment() {
    private var binding: FragmentLocationListingBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var homeActivity: HomeActivity? = null
    private var viewModel: LocationListingViewModel? = null

    private var bundle: Bundle? = null
    private var cityName: String? = null

    private var locationListingData: ArrayList<LocationListingData.Response.LocationfilterlistItem?>? = null
    private var locationListingAdapter: LocationListingAdapter? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_listing, container, false)
        onClickListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = LocationListingFragment::class.qualifiedName
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        locationListingData = ArrayList<LocationListingData.Response.LocationfilterlistItem?>()
        getBundleData()

    }

    private fun getBundleData() {
        bundle = this.arguments
        if (bundle != null) {
            cityName = bundle!!.getString("cityName")
            binding!!.appBar.tvTitle.setText(cityName)

            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                locationListingAPI()
            } else {
                binding!!.rlMain.visibility = View.GONE
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        }
    }


    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(LocationListingViewModel::class.java)

        viewModel!!.responseLocationListingLiveData.observe(this, Observer {
            handleLcoationListingResult(it)
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
            R.id.rowListingItem -> {
                val position = q0.tag as Int
                locationListingAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("listId", locationListingData!!.get(position)!!.id)
                (mActivity as HomeActivity).changeFragment(ListingDetailFragment(), true, bundle)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
        // binding!!.appBar.tvTitle.text = "Update Profile"
    }


    private fun locationListingAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.LocationListing
        reqData["location"] = cityName!!
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.locationListingApi(reqData)
    }


    private fun handleLcoationListingResult(result: ApiResponse<LocationListingData>?) =
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

    private fun setAllListingData(response: LocationListingData.Response?) {
        /*set location Listing Data*/
        if (response!!.locationfilterlist != null && !response.locationfilterlist!!.isEmpty()) {
            binding!!.rlMain.visibility = View.VISIBLE
            binding!!.layoutError.rlerror.visibility = View.GONE

            locationListingData!!.clear()
            locationListingData!!.addAll(response.locationfilterlist)
            if (locationListingData!!.size != 0) {
                locationListingAdapter = LocationListingAdapter(mActivity!!, onClickListener, locationListingData!!)
                binding!!.rvLocationListing.adapter = locationListingAdapter
            }
        }
        else{
            binding!!.rlMain.visibility = View.GONE
            binding!!.layoutError.rlerror.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }

}












