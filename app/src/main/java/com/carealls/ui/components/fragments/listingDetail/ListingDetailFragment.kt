package com.carealls.ui.components.fragments.listingDetail


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.carealls.R
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.databinding.FragmentListingDetailBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.*
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.ui.components.activites.reviews.ReviewsActivity
import com.carealls.ui.components.activites.viewImage.ViewImageActivity
import com.carealls.ui.components.adapters.CatalogueAdapter
import com.carealls.ui.components.adapters.GalleryAdapter
import com.carealls.ui.components.adapters.ReviewAdapter
import com.carealls.ui.components.fragments.categoryDetail.CategoryDetailFragment
import com.carealls.ui.components.fragments.productDetail.ProductDetailFragment
import com.carealls.ui.components.fragments.viewAll.ViewAllFragment
import com.carealls.utils.Constants
import com.carealls.utils.EmojiExcludeFilter
import com.carealls.utils.OnItemClickedListener
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


@Suppress("IMPLICIT_CAST_TO_ANY")
class ListingDetailFragment : BaseFragment(), OnItemClickedListener {
    private var binding: FragmentListingDetailBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var homeActivity: HomeActivity? = null
    private var viewModel: ListingDetailViewModel? = null
    private var onItemClickedListener: OnItemClickedListener? = null

    private var bundle: Bundle? = null
    private var type: String? = null
    private var phone: String? = null
    private var whatsappNo: String? = null
    private var listingTitle: String? = null
    private var listingAddress: String? = null
    private var userId: String? = null

    private var catalogueData: ArrayList<ListingDetailData.Response.ProductforcatalogItem?>? = null
    private var catalogueAdapter: CatalogueAdapter? = null
    private var galleryData: ArrayList<GalleryData.ResponseItem?>? = null
    private var galleryAdapter: GalleryAdapter? = null
    private var reviewListData: ArrayList<ReviewListData.Response.ListingforreviewItem?>? = null
    private var reviewListAdapter: ReviewAdapter? = null
    private var listId: String? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_listing_detail, container, false)
        onClickListener = this
        onItemClickedListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = ListingDetailFragment::class.qualifiedName
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        binding!!.etName.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        getBundleData()
        catalogueData = ArrayList<ListingDetailData.Response.ProductforcatalogItem?>()
        galleryData = ArrayList<GalleryData.ResponseItem?>()
        reviewListData = ArrayList<ReviewListData.Response.ListingforreviewItem?>()
    }

    private fun getBundleData() {
        bundle = this.arguments
        if (bundle != null) {
            type = bundle!!.getString("type")
            listId = bundle!!.getString("listId")

            if (UtilityMethod.isDeviceOnline(mActivity!!)) {
                listingDetailAPI()
                reviewListAPI()
            } else {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            }
        }
    }

    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(ListingDetailViewModel::class.java)

        viewModel!!.responseSendEnquiryLiveData.observe(this, Observer {
            handleSendEnquiryResult(it)
        })
        viewModel!!.responseListingDetalLiveData.observe(this, Observer {
            handleListingDetailResult(it)
        })
        viewModel!!.responseGalleryLiveData.observe(this, Observer {
            handleGalleryResult(it)
        })
        viewModel!!.responseReviewListLiveData.observe(this, Observer {
            handleReviewListResult(it)
        })
        viewModel!!.responseDeleteGalleryLiveData.observe(this, Observer {
            handleDeleteGalleryResult(it)
        })
    }

    override fun setListeners() {
        binding!!.ivBack.setOnClickListener(this)
        binding!!.tvGalleryViewAll.setOnClickListener(this)
        binding!!.tvReviewViewAll.setOnClickListener(this)
        binding!!.btnSubmit.setOnClickListener(this)
        binding!!.llCall.setOnClickListener(this)
        binding!!.llWhatsapp.setOnClickListener(this)
        binding!!.tvGalleryViewAll.setOnClickListener(this)
        binding!!.tvCatalogueViewAll.setOnClickListener(this)
        binding!!.ivShare.setOnClickListener(this)
        binding!!.btnCopyAddress.setOnClickListener(this)
        binding!!.llRating.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        when (q0!!.id) {
            R.id.ivBack -> {
                homeActivity!!.onBackPressed()
            }
            R.id.llRating -> {
                var bundle = Bundle()
                bundle.putString("listId", listId)
                ReviewsActivity.startActivity(mActivity!!, bundle, false)
            }
            R.id.rowProductItem -> {
                val position = q0.tag as Int
                catalogueAdapter!!.selectedPos = position

                var bundle = Bundle()
                bundle.putString("productId", catalogueData!!.get(position)!!.id)
                bundle.putString("productTitle", catalogueData!!.get(position)!!.name)
                bundle.putString("productImg", catalogueData!!.get(position)!!.image)
                (mActivity as HomeActivity).changeFragment(ProductDetailFragment(), true, bundle)
            }
            R.id.rowCategoryItem -> {
                (mActivity as HomeActivity).changeFragment(CategoryDetailFragment(), true, null)
            }
            R.id.tvReviewViewAll -> {
                var bundle = Bundle()
                bundle.putString("listId", listId)
                ReviewsActivity.startActivity(mActivity!!, bundle, false)
            }
            R.id.btnSubmit -> {
                sendEnquiryAPI()
            }
            R.id.llCall -> {
                if (phone != null && phone!!.isNotEmpty())
                    UtilityMethod.audioCall(mActivity!!, phone!!)
            }
            R.id.llWhatsapp -> {
                if (whatsappNo != null && whatsappNo!!.isNotEmpty())
                    UtilityMethod.shareAppOnWhatsApp(
                        mActivity!!,
                        "91" + whatsappNo,
                        "\nCareAlls App:\n" + Constants.AppURL + "/listing/" +
                                listingTitle!!.replace(
                                    "\\s".toRegex(),
                                    "_"
                                ) + "?type=LDF&lId=" + listId
                    )
                else
                    UtilityMethod.showToastMessageError(mActivity!!, "Whatsapp number not found!")
            }
            R.id.tvGalleryViewAll -> {
                val bundle = Bundle()
                bundle.putString("type", "All Gallery")
                bundle.putString("userId", userId)
                homeActivity!!.changeFragment(ViewAllFragment(), true, bundle)
            }
            R.id.rowGalleryItem -> {
                val position = q0.tag as Int
                galleryAdapter!!.selectedPos = position
                var bundle = Bundle()
                bundle.putString("viewImage", galleryData!!.get(position)!!.image)
                ViewImageActivity.startActivity(mActivity!!, bundle, false)
            }
            R.id.tvCatalogueViewAll -> {
                val bundle = Bundle()
                bundle.putString("type", "All Catalogue")
                bundle.putSerializable("catalogueListData", catalogueData)
                bundle.putSerializable("whatsappMobile", whatsappNo)
                homeActivity!!.changeFragment(ViewAllFragment(), true, bundle)
            }
            R.id.ivShare -> {
                /* if (whatsappNo!!.isNotEmpty() && whatsappNo != null)
                     UtilityMethod.shareAppOnWhatsApp(mActivity!!, "91"+whatsappNo,Constants.AppURL+"/listing/"+
                             listingTitle!!.replace("\\s".toRegex(), "_")+"?type=LDF&lId="+listId)
                             else
                     UtilityMethod.showToastMessageError(mActivity!!,"Whatsapp number not found!")
                             */
                UtilityMethod.shareApp(
                    mActivity!!, Constants.AppURL + "/listing/" +
                            listingTitle!!.replace("\\s".toRegex(), "_") + "?type=LDF&lId=" + listId
                )

            }
            R.id.btnCopyAddress -> {
                UtilityMethod.copyTextClipboard(mActivity!!, binding!!.tvAddress.text.toString())
            }

        }
    }

    private fun listingDetailAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.ListingDetail
        reqData["list_id"] = listId!!
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.listingDetail(reqData)
    }


    private fun handleListingDetailResult(result: ApiResponse<ListingDetailData>?) =
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

                        if (result.data.response!!.listingfordetails != null && !result.data.response!!.listingfordetails!!.isEmpty()) {
                            userId = result.data.response.listingfordetails!!.get(0)!!.userId!!
                            UtilityMethod.loadImage(
                                binding!!.ivListingDetail,
                                result.data.response.listingfordetails!!.get(0)!!.listImage!!
                            )
                            binding!!.tvListingRating.setText(
                                result.data.response.listingfordetails!!.get(
                                    0
                                )!!.review!!
                            )
                            binding!!.txtListingTitle.setText(
                                result.data.response.listingfordetails!!.get(
                                    0
                                )!!.name!!
                            )
                            binding!!.tvAddress.setText(
                                result.data.response.listingfordetails!!.get(
                                    0
                                )!!.address!!.trim()
                            )
                            listingTitle = result.data.response.listingfordetails!!.get(0)!!.name!!
                            listingAddress =
                                result.data.response.listingfordetails!!.get(0)!!.address!!.trim()

                            binding!!.txtListingDetail.setText(
                                result.data.response.listingfordetails!!.get(
                                    0
                                )!!.description!!
                            )
                            phone = result.data.response.listingfordetails!!.get(0)!!.phoneNumber!!
                            whatsappNo =
                                result.data.response.listingfordetails!!.get(0)!!.whatsappNumber!!
                            if (result.data.response!!.listingfordetails!!.get(0)!!.address != null && !result.data.response!!.listingfordetails!!.get(
                                    0
                                )!!.address!!.isEmpty()
                            ) {
                                binding!!.btnCopyAddress.visibility = View.VISIBLE
                            } else {
                                binding!!.btnCopyAddress.visibility = View.GONE
                            }
                            galleryAPI()

                        }

                        if (result.data.response!!.productforcatalog != null && !result.data.response!!.productforcatalog!!.isEmpty()) {
                            binding!!.relativeCatelogue.visibility = View.VISIBLE
                            catalogueData!!.clear()
                            catalogueData!!.addAll(result.data.response!!.productforcatalog!!)
                            if (catalogueData!!.size != 0) {
                                catalogueAdapter =
                                    CatalogueAdapter(
                                        mActivity!!,
                                        onClickListener!!,
                                        catalogueData,
                                        whatsappNo
                                    )
                                binding!!.rvCatalogue.adapter = catalogueAdapter
                            } else {
                            }
                        } else {
                            binding!!.relativeCatelogue.visibility = View.GONE
                        }

                    } else {
                    }

                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                }
            }
        }

    private fun galleryAPI() {
        if (userId != null && userId!!.isNotEmpty()) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["method"] = Constants.Gallery
            reqData["userId"] = userId!!
            Log.e(TAG, "Api parameters - $reqData")
            viewModel!!.gallery(reqData)
        }
    }


    private fun handleGalleryResult(result: ApiResponse<GalleryData>?) = when (result!!.status) {
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
                    binding!!.relativeGallery.visibility = View.VISIBLE
                    galleryData!!.clear()
                    galleryData!!.addAll(result.data.response)
                    if (galleryData!!.size != 0) {
                        galleryAdapter = GalleryAdapter(
                            mActivity!!,
                            onClickListener!!,
                            galleryData,
                            onItemClickedListener,
                            userId,
                            sessionManager
                        )
                        binding!!.rvGallery.adapter = galleryAdapter
                    } else {
                    }
                } else {
                    binding!!.relativeGallery.visibility = View.GONE
                }

            } else {
                //UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                binding!!.relativeGallery.visibility = View.GONE
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
                        binding!!.relativeReview.visibility = View.VISIBLE
                        reviewListData!!.clear()
                        reviewListData!!.addAll(result.data.response!!.listingforreview!!)
                        if (reviewListData!!.size != 0) {
                            reviewListAdapter =
                                ReviewAdapter(mActivity!!, onClickListener!!, reviewListData!!)
                            binding!!.rvReview.adapter = reviewListAdapter
                        } else {
                        }
                    } else {
                        binding!!.relativeReview.visibility = View.GONE
                    }

                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
                }
            }
        }

    private fun sendEnquiryAPI() {
        val name = binding!!.etName.text.toString().trim()
        val mobile = binding!!.etMobile.text.toString().trim()


        if (viewModel!!.isValidFormData(mActivity!!, name, mobile)) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["name"] = name
            reqData["mobile"] = mobile
            reqData["method"] = Constants.SendEnquiry

            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                viewModel!!.sendEnquiry(reqData)
            }
        }
    }

    private fun handleSendEnquiryResult(result: ApiResponse<SendEnquiryData>?) {
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
                Log.e(TAG, "response - " + Gson().toJson(result.data))
                if (result.data!!.status!!.equals("1")) {
                    /*  var bundle = Bundle()
                      bundle.putString("countryCode", binding!!.ccp.selectedCountryCode)
                      bundle.putString("phone", binding!!.etMobileSignup.text.toString().trim())

                      VerifyOtpActivity.startActivity(mActivity!!, bundle, true)*/
                    showSuccessfullyDialog()
                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg!!)
                }
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


    private fun handleDeleteGalleryResult(result: ApiResponse<DeleteGalleryData>?) =
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

                    galleryAPI()

                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
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
        dialogBinding.tvMessage.setText("Send Enquiry Successfully")
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
            binding!!.etName.setText("")
            binding!!.etMobile.setText("")
        })
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
        // binding!!.appBar.tvTitle.text = "Update Profile"
    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }

    override fun getClickedString(id: String?, listId: String?) {
            deleteGalleryAPI(id, listId)
    }

}












