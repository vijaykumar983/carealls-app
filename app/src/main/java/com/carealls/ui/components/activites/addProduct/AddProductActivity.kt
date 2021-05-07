package  com.carealls.ui.components.activites.addProduct

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.InputFilter
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.carealls.R
import com.carealls.databinding.ActivityAddProductBinding
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.AddProductData
import com.carealls.pojo.EditProductData
import com.carealls.pojo.UpdateListingDetailData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.addListing.AddListingActivity
import com.carealls.ui.components.activites.choosePackage.ChoosePackageActivity
import com.carealls.utils.Constants
import com.carealls.utils.EmojiExcludeFilter
import com.carealls.utils.ImagePathUtility
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.util.*
import kotlin.collections.HashMap


@Suppress("IMPLICIT_CAST_TO_ANY")
class AddProductActivity : BaseBindingActivity(), BSImagePicker.OnSingleImageSelectedListener,
    BSImagePicker.OnMultiImageSelectedListener,
    BSImagePicker.ImageLoaderDelegate {
    private var binding: ActivityAddProductBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: AddProductViewModel? = null

    private var mFileTemp = File("")
    private var uploadImg = "Upload Image"

    //private var allProductData: ArrayList<AllProductData.Response.ProductlistItem?>? = null
    private var mBundle: Bundle? = null
    private var title: String? = null
    private var position: Int? = null
    private var productId: String? = null
    private var listId: String? = null
    private var skip: String? = null
    private var updateAddProduct: String? = null
    private var productEditData: ArrayList<UpdateListingDetailData.Response.ProductforlistupdateItem?>? = null


    companion object {
        private val TAG = AddProductActivity::class.qualifiedName
        private var selectedImagePath = ""

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, AddProductActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product)
        viewModel = ViewModelProvider(this).get(AddProductViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        binding!!.etProductName.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        binding!!.edtDescription.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        getBundleData()
        //allProductData = ArrayList<AllProductData.Response.ProductlistItem?>()

        /* if (UtilityMethod.isDeviceOnline(mActivity!!)) {
             allProductAPI()
         } else {
             UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
         }*/
        /*viewModel!!.responseAllProductLiveData.observe(this, Observer {
            handleResult(it)
        })*/

        viewModel!!.responseLiveAddProductData.observe(this, Observer {
            handleAddProductResult(it)
        })
        viewModel!!.responseLiveEditProductData.observe(this, Observer {
            handleEditProductResult(it)
        })
    }


    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
        binding!!.btnSubmit.setOnClickListener(this)
        binding!!.rlAddImage.setOnClickListener(this)
        // binding!!.rlAllProduct.setOnClickListener(this)
        binding!!.btnSkip.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        UtilityMethod.hideKeyboard(mActivity!!)
        when (view!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnSubmit -> {
                addProductAPI()
            }
            R.id.rlAddImage -> {
                val pickerDialog = BSImagePicker.Builder("com.carealls.fileProvider")
                    .build()
                pickerDialog.show(supportFragmentManager, "picker")
            }
            /*R.id.rlAllProduct -> {
                showAllProductDialog()
            }*/
            R.id.btnSkip -> {
                val bundle = Bundle()
                bundle.putString("listId", listId)
                ChoosePackageActivity.startActivity(mActivity!!, bundle, false)
                finish()
            }
        }
    }

    private fun getBundleData() {
        mBundle = intent.getBundleExtra("bundle")
        if (mBundle != null) {
            listId = mBundle!!.getString("listId")
            updateAddProduct = mBundle!!.getString("updateAddProduct")
            title = mBundle!!.getString("title")
            position = mBundle!!.getInt("position")
            skip = mBundle!!.getString("skip")
            productEditData =
                mBundle!!.getSerializable("productData") as ArrayList<UpdateListingDetailData.Response.ProductforlistupdateItem?>?

            if (title.equals("Edit Product", false)) {
                binding!!.appBar.tvTitle.setText(title)
                binding!!.etProductName.setText(productEditData!!.get(position!!)!!.name)
                UtilityMethod.loadImage(
                    binding!!.ivAddProduct,
                    productEditData!!.get(position!!)!!.image!!
                )
                binding!!.etPrice.setText(productEditData!!.get(position!!)!!.productPrice)
                binding!!.edtDescription.setText(productEditData!!.get(position!!)!!.productDescription)
                binding!!.btnSubmit.setText("Submit")
                productId = productEditData!!.get(position!!)!!.id
                uploadImg = title!!
            } else {
                binding!!.appBar.tvTitle.setText("Add Product")
                productId = ""
                if(skip.equals("skip", false))
                { binding!!.btnSkip.visibility = View.VISIBLE
                }
                if (updateAddProduct.equals("UpdateAddProduct", false)) {
                    binding!!.btnSubmit.setText("Submit")
                }
            }

        }
    }

    /* private fun showAllProductDialog() {
         if (allProductData!!.size != 0) {

             val a1 = ArrayList<String>()
             for (i in allProductData!!.indices) {
                 a1.add(allProductData!!.get(i)!!.name!!)
             }
             val stockArr = a1.toTypedArray()
             val builder = AlertDialog.Builder(
                 mActivity!!
             )
             builder.setTitle("Products")
             builder.setItems(stockArr) { dialog, which -> //Toast.makeText(mActivity, "Position: " + which + " Value: " + listItems[which], Toast.LENGTH_LONG).show();
                 binding!!.tvProductName.setText(stockArr[which])
                 binding!!.tvProductName.setSelected(true)
                 Log.e(TAG, "product Id - " + allProductData!!.get(which)!!.id)
             }
             val dialog = builder.create()
             dialog.show()
         } else {
             UtilityMethod.showToastMessageError(mActivity!!, "No Data available!")
         }
     }*/

    /*private fun allProductAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.Product
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.allProduct(reqData)
    }


    private fun handleResult(result: ApiResponse<AllProductData>?) = when (result!!.status) {
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

                    if (result.data.response!!.productlist != null && !result.data.response.productlist!!.isEmpty()) {
                        allProductData!!.clear()
                        allProductData!!.addAll(result.data.response.productlist)
                    } else {
                    }
                } else {
                }

            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
            }
        }
    }*/


    private fun addProductAPI() {
        val productName = binding!!.etProductName.text.toString().trim()
        val description = binding!!.edtDescription.text.toString().trim()
        val price = binding!!.etPrice.text.toString().trim()

        if (viewModel!!.isValidFormData(
                mActivity!!,
                productName,
                description,
                uploadImg,
                price
            )
        ) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["method"] = Constants.AddProduct
            reqData["product_name"] = productName
            reqData["product_description"] = description
            reqData["product_price"] = price
            reqData["list_id"] = listId!!
            reqData["product_id"] = productId!!
            reqData["userId"] = sessionManager!!.useR_ID


            var requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), mFileTemp)
            val req_product_name: MultipartBody.Part =
                MultipartBody.Part.createFormData("product_name", productName)
            val req_product_desc: MultipartBody.Part =
                MultipartBody.Part.createFormData("product_description", description)
            val req_product_price: MultipartBody.Part =
                MultipartBody.Part.createFormData("product_price", price)
            val req_listId: MultipartBody.Part =
                MultipartBody.Part.createFormData("list_id", listId!!)
            val req_productId: MultipartBody.Part =
                MultipartBody.Part.createFormData("product_id", productId!!)
            var profile_photo: MultipartBody.Part? = null
            if (selectedImagePath.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp!!.name)
                profile_photo =
                    MultipartBody.Part.createFormData("image", mFileTemp!!.name, requestBody)
            }
            var req_id: MultipartBody.Part? = null
            var req_method: MultipartBody.Part? = null
            if (title.equals("Edit Product", false)) {
                req_id = MultipartBody.Part.createFormData("product_id", productId!!)
                req_method = MultipartBody.Part.createFormData("method", Constants.EditProduct)
            } else {
                req_id = MultipartBody.Part.createFormData("userId", sessionManager!!.useR_ID)
                req_method = MultipartBody.Part.createFormData("method", Constants.AddProduct)
            }


            // Log.e(TAG, "image pic - " + profile_photo +" "+mFileTemp!!.name+" "+requestBody)
            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                Log.e(TAG, "Api parameters - " + reqData)
                if (title.equals("Edit Product", false)) {
                    viewModel!!.editProductApi(
                        req_method,
                        req_id,
                        req_product_name,
                        req_product_desc,
                        req_product_price,
                        req_listId,
                        profile_photo
                    )
                } else {
                    viewModel!!.addProductApi(
                        req_method,
                        req_id,
                        req_product_name,
                        req_product_desc,
                        req_product_price,
                        req_listId,
                        profile_photo
                    )
                }

            }
        }
    }


    private fun handleAddProductResult(result: ApiResponse<AddProductData>?) =
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
                if (result.data!!.status == 1) {
                    Log.e(TAG, "Response - " + Gson().toJson(result))

                    if (updateAddProduct.equals("UpdateAddProduct", false)) {
                       finish()
                    }
                    else{
                        val bundle = Bundle()
                        bundle.putString("listId", result.data.data!!.listId)
                        ChoosePackageActivity.startActivity(mActivity!!, bundle, false)
                        finish()
                    }

                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.message!!)
                }
            }
        }

    private fun handleEditProductResult(result: ApiResponse<EditProductData>?) =
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
                if (result.data!!.status == 1) {
                    Log.e(TAG, "Response - " + Gson().toJson(result))

                    showSuccessfullyDialog(result.data.message)

                } else {
                    UtilityMethod.showToastMessageError(mActivity!!, result.data.message!!)
                }
            }
        }

    @SuppressLint("SetTextI18n")
    fun showSuccessfullyDialog(message: String?) {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialogBinding.tvTitle.setText("Successfully")
        dialogBinding.tvMessage.setText(message)
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
            finish()
        })
        dialog.show()
    }

    override fun loadImage(imageUri: Uri?, ivImage: ImageView?) {
        Glide.with(mActivity!!).load(imageUri).into(ivImage!!)
    }

    override fun onSingleImageSelected(uri: Uri?, tag: String?) {
        selectedImagePath = ImagePathUtility.getImagePath(mActivity!!, uri!!)!!
        uploadImg = selectedImagePath
        mFileTemp = File(selectedImagePath)
        mFileTemp?.let { imageFile ->
            lifecycleScope.launch {
                // Default compression
                mFileTemp = Compressor.compress(mActivity!!, imageFile)
                mFileTemp?.let {
                    binding!!.ivAddProduct.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                    Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                }
            }
        } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")

        //binding!!.ivAddProduct.setImageURI(uri)
    }

    override fun onMultiImageSelected(uriList: MutableList<Uri>?, tag: String?) {

    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        selectedImagePath = ""
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }

}





