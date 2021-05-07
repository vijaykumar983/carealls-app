package com.carealls.ui.components.fragments.allCategoryProduct


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
import com.carealls.databinding.FragmentAllCategoryProductBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.CategoryProductData
import com.carealls.pojo.DeleteProductData
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.adapters.AllCategoryProductAdapter
import com.carealls.ui.components.fragments.productDetail.ProductDetailFragment
import com.carealls.utils.Constants
import com.carealls.utils.OnItemClickedListener
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson


class AllCategoryProductFragment : BaseFragment() , OnItemClickedListener {
    private var binding: FragmentAllCategoryProductBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var homeActivity: HomeActivity? = null
    private var drawerLayout: DrawerLayout? = null
    private var viewModel: AllCategoryProductViewModel? = null
    private var onItemClickedListener: OnItemClickedListener? = null

    private var bundle: Bundle? = null
    private var allCategoryProductData: ArrayList<CategoryProductData.Response.ProductforcategoryItem?>? = null
    private var allCategoryProductAdapter: AllCategoryProductAdapter? = null
    private var categoryName: String? = null
    private var categoryId: String? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_all_category_product,
            container,
            false
        )
        onClickListener = this
        onItemClickedListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = AllCategoryProductFragment::class.qualifiedName
    }


    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        allCategoryProductData = ArrayList<CategoryProductData.Response.ProductforcategoryItem?>()
        getBundleData()
        drawerLayout = activity!!.findViewById(R.id.drawer_layout)
    }

    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(AllCategoryProductViewModel::class.java)

        viewModel!!.responseCatProductLiveData.observe(this, Observer {
            handleCatProductResult(it)
        })

        viewModel!!.responseLiveDeleteProductData.observe(this, Observer {
            handleDeleteProductResult(it)
        })
    }

    private fun getBundleData() {
        bundle = this.arguments
        if (bundle != null) {
            categoryName = bundle!!.getString("categoryName")
            categoryId = bundle!!.getString("categoryId")
           // allCategoryProductData = bundle!!.getSerializable("categoryProductData") as ArrayList<CategoryProductData.Response.ProductforcategoryItem?>?

            allCategoryProductAPI()
        }
    }

    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        when (q0!!.id) {
            R.id.ivBack -> {
                drawerLayout!!.openDrawer(GravityCompat.START)
            }
            R.id.rowAllProductItem -> {
                val position = q0.tag as Int
                allCategoryProductAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("productId", allCategoryProductData!!.get(position)!!.id)
                bundle.putString("productTitle", allCategoryProductData!!.get(position)!!.name)
                bundle.putString("productImg", allCategoryProductData!!.get(position)!!.image)
                (mActivity as HomeActivity).changeFragment(ProductDetailFragment(), true, bundle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
        binding!!.appBar.tvTitle.text = categoryName
        binding!!.appBar.ivBack.setImageResource(R.drawable.ic_menu)
    }

    private fun allCategoryProductAPI() {
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
                        binding!!.rlMain.visibility = View.VISIBLE
                        binding!!.layoutError.rlerror.visibility = View.GONE
                        allCategoryProductData!!.clear()
                        allCategoryProductData!!.addAll(result.data.response.productforcategory)
                        if (allCategoryProductData!!.size != 0) {
                            allCategoryProductAdapter = AllCategoryProductAdapter(mActivity!!, onClickListener!!, allCategoryProductData,onItemClickedListener,sessionManager)
                            binding!!.rvAllProduct.adapter = allCategoryProductAdapter
                        } else {
                        }
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

                allCategoryProductAPI()

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












