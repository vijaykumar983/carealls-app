package com.carealls.ui.components.fragments.search


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.FragmentSearchBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.*
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.adapters.*
import com.carealls.ui.components.fragments.categoryDetail.CategoryDetailFragment
import com.carealls.ui.components.fragments.listingDetail.ListingDetailFragment
import com.carealls.ui.components.fragments.productDetail.ProductDetailFragment
import com.carealls.utils.Constants
import com.carealls.utils.EmojiExcludeFilter
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


class SearchFragment : BaseFragment() {
    private var binding: FragmentSearchBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var homeActivity: HomeActivity? = null
    private var viewModel: SearchViewModel? = null

    private var bundle: Bundle? = null
    private var type: String? = null

    private var searchCategoryData: ArrayList<SearchData.Response.SearchresultsforcategoryItem?>? = null
    private var searchCategoryAdapter: SearchCategoryAdapter? = null
    private var searchListingData: ArrayList<SearchData.Response.SearchlistingItem?>? = null
    private var searchListingAdapter: SearchListingAdapter? = null
    private var searchProductData: ArrayList<SearchData.Response.ProductsearchItem?>? = null
    private var searchProductAdapter: SearchProductAdapter? = null


    var delay: Long = 700
    var handler: Handler = Handler()
    var last_text_edit: Long = 0

    val input_finish_checker = Runnable {
        if (System.currentTimeMillis() > last_text_edit + delay - 350) {
            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                searchAPI(binding!!.etSearch.text.toString())
            } else {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        }
    }


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        onClickListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = SearchFragment::class.qualifiedName
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        binding!!.etSearch.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        getBundleData()

        searchCategoryData = ArrayList<SearchData.Response.SearchresultsforcategoryItem?>()
        searchListingData = ArrayList<SearchData.Response.SearchlistingItem?>()
        searchProductData = ArrayList<SearchData.Response.ProductsearchItem?>()

        binding!!.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e(TAG, s.toString())
                last_text_edit = System.currentTimeMillis()
                handler.postDelayed(input_finish_checker, delay)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(input_finish_checker)
            }
        })


    }

    private fun getBundleData() {
        bundle = this.arguments
        if (bundle != null) {
            type = bundle!!.getString("type")

        }
    }



    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        viewModel!!.responseSearchLiveData.observe(this, Observer {
            handleResult(it)
        })
    }

    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        UtilityMethod.hideKeyboard(mActivity!!)
        when (q0!!.id) {
            R.id.ivBack -> {
                homeActivity!!.onBackPressed()
            }
            R.id.rowAllProductItem -> {
                val position = q0.tag as Int
                searchProductAdapter!!.selectedPos = position

                var bundle = Bundle()
                bundle.putString("productId", searchProductData!!.get(position)!!.id)
                bundle.putString("productTitle", searchProductData!!.get(position)!!.name)
                bundle.putString("productImg", searchProductData!!.get(position)!!.image)
                homeActivity!!.changeFragment(ProductDetailFragment(), true, bundle)
            }
            R.id.rowCategoryItem -> {
                val position = q0.tag as Int
                searchCategoryAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("categoryId", searchCategoryData!!.get(position)!!.catId)
                bundle.putString("categoryImg", searchCategoryData!!.get(position)!!.catImage)
                (mActivity as HomeActivity).changeFragment(CategoryDetailFragment(), true, bundle)
            }
            R.id.rowListingItem -> {
                val position = q0.tag as Int
                searchListingAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("listId", searchListingData!!.get(position)!!.id)
                (mActivity as HomeActivity).changeFragment(ListingDetailFragment(), true, bundle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
         binding!!.appBar.tvTitle.text = "Search"

        binding!!.etSearch.post(Runnable {
            binding!!.etSearch.requestFocus()
            val imgr: InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imgr.showSoftInput(binding!!.etSearch, InputMethodManager.SHOW_IMPLICIT)
        })
    }

    private fun searchAPI(txt: String) {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.Search
        reqData["keyword"] = txt
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.serach(reqData)
    }


    private fun handleResult(result: ApiResponse<SearchData>?) = when (result!!.status) {
        ApiResponse.Status.ERROR -> {
           // ProgressDialog.hideProgressDialog()
            binding!!.aviLoader.visibility = View.GONE
            UtilityMethod.showToastMessageError(mActivity!!, result.error!!.message!!)
        }
        ApiResponse.Status.LOADING -> {
           // ProgressDialog.showProgressDialog(mActivity!!)
            binding!!.aviLoader.visibility = View.VISIBLE
            binding!!.nestedScrollView.visibility = View.GONE
            binding!!.layoutError.rlerror.visibility = View.GONE
        }
        ApiResponse.Status.SUCCESS -> {
            //ProgressDialog.hideProgressDialog()
            binding!!.aviLoader.visibility = View.GONE
            if (result.data!!.status == "1") {
                Log.e(TAG, "Response - " + Gson().toJson(result))

                if (result.data.response != null) {
                    binding!!.nestedScrollView.visibility = View.VISIBLE
                    binding!!.layoutError.rlerror.visibility = View.GONE

                    setAllCategoryData(result.data.response)

                } else {
                    binding!!.nestedScrollView.visibility = View.GONE
                    binding!!.layoutError.rlerror.visibility = View.VISIBLE
                }

            } else {
                //UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                binding!!.nestedScrollView.visibility = View.GONE
                binding!!.layoutError.rlerror.visibility = View.VISIBLE
            }
        }
    }

    private fun setAllCategoryData(response: SearchData.Response?) {
        if(response!!.searchlisting != null && !response.searchlisting!!.isEmpty() || response!!.searchresultsforcategory != null && !response.searchresultsforcategory!!.isEmpty() ||
            response!!.productsearch != null && !response.productsearch!!.isEmpty())
        {
            binding!!.nestedScrollView.visibility = View.VISIBLE
            binding!!.layoutError.rlerror.visibility = View.GONE
            if (response!!.searchresultsforcategory != null && !response.searchresultsforcategory!!.isEmpty()) {
                binding!!.tvCategory.visibility = View.VISIBLE
                binding!!.rvAllCategory.visibility = View.VISIBLE
                searchCategoryData!!.clear()
                searchCategoryData!!.addAll(response.searchresultsforcategory)
                if (searchCategoryData!!.size != 0) {
                    searchCategoryAdapter = SearchCategoryAdapter(
                        mActivity!!,
                        onClickListener,
                        searchCategoryData!!
                    )
                    binding!!.rvAllCategory.adapter = searchCategoryAdapter
                }
            }else{
                binding!!.tvCategory.visibility = View.GONE
                binding!!.rvAllCategory.visibility = View.GONE
            }
            if (response!!.searchlisting != null && !response.searchlisting!!.isEmpty()) {
                binding!!.tvListing.visibility = View.VISIBLE
                binding!!.rvAllListing.visibility = View.VISIBLE
                searchListingData!!.clear()
                searchListingData!!.addAll(response.searchlisting)
                if (searchListingData!!.size != 0) {
                    searchListingAdapter = SearchListingAdapter(
                        mActivity!!,
                        onClickListener,
                        searchListingData!!
                    )
                    binding!!.rvAllListing.adapter = searchListingAdapter
                }
            }else{
                binding!!.tvListing.visibility = View.GONE
                binding!!.rvAllListing.visibility = View.GONE
            }
            if (response!!.productsearch != null && !response.productsearch!!.isEmpty()) {
                binding!!.tvProduct.visibility = View.VISIBLE
                binding!!.rvAllProduct.visibility = View.VISIBLE
                searchProductData!!.clear()
                searchProductData!!.addAll(response.productsearch)
                if (searchProductData!!.size != 0) {
                    searchProductAdapter = SearchProductAdapter(
                        mActivity!!,
                        onClickListener,
                        searchProductData!!
                    )
                    binding!!.rvAllProduct.adapter = searchProductAdapter
                }
            }else{
                binding!!.tvProduct.visibility = View.GONE
                binding!!.rvAllProduct.visibility = View.GONE
            }
        }else{
           binding!!.nestedScrollView.visibility = View.GONE
            binding!!.layoutError.rlerror.visibility = View.VISIBLE
        }


    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }

}












