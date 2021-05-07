package  com.carealls.ui.components.activites.addListing

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.asksira.bsimagepicker.BSImagePicker
import com.asksira.bsimagepicker.BSImagePicker.*
import com.bumptech.glide.Glide
import com.carealls.R
import com.carealls.databinding.ActivityAddListingBinding
import com.carealls.databinding.DialogAddAddressBinding
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.AddListingData
import com.carealls.pojo.AllCategoryData
import com.carealls.pojo.EditUpdateListingData
import com.carealls.pojo.UpdateListingDetailData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.addProduct.AddProductActivity
import com.carealls.ui.components.fragments.profile.ProfileFragment
import com.carealls.utils.Constants
import com.carealls.utils.EmojiExcludeFilter
import com.carealls.utils.ImagePathUtility
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("IMPLICIT_CAST_TO_ANY", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AddListingActivity : BaseBindingActivity(), OnSingleImageSelectedListener,OnSelectImageCancelledListener,
    OnMultiImageSelectedListener, ImageLoaderDelegate {

    private var binding: ActivityAddListingBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: AddListingViewModel? = null

    private var uploadImg = "Upload Image"
    private var uploadImg1 = "Upload Image1"
    private var uploadImg2 = "Upload Image2"
    private var tagg = "0"
    private var categoryId: String? = null

   private var mFileTemp = File("")
    private var mFileTemp1 = File("")
    private var mFileTemp2 = File("")
    private var mFileTemp3 = File("")
    private var mFileTemp4 = File("")
    private var mFileTemp5 = File("")
    private var mFileTemp6 = File("")
    private var mFileTemp7 = File("")
    private var mFileTemp8 = File("")


    private var allCategoryData: ArrayList<AllCategoryData.Response.CategoryforlistingItem?>? = null

    private var mBundle: Bundle? = null
    private var title: String? = null
    private var updateListingData: ArrayList<UpdateListingDetailData.Response.UpdatelistdetailsItem?>? = null
    private var updateListingGalleryData: ArrayList<UpdateListingDetailData.Response.UpdatelistgalleryimagItem?>? = null
    private var listId: String? = null


    companion object {
        private val TAG = AddListingActivity::class.qualifiedName
        private var selectedImagePath = ""
        private var selectedImagePath1 = ""
        private var selectedImagePath2 = ""
        private var selectedImagePath3 = ""
        private var selectedImagePath4 = ""
        private var selectedImagePath5 = ""
        private var selectedImagePath6 = ""
        private var selectedImagePath7 = ""
        private var selectedImagePath8 = ""

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, AddListingActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_listing)
        viewModel = ViewModelProvider(this).get(AddListingViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
    }

    override fun initializeObject() {
        binding!!.appBar.tvTitle.setText("Request for Listing")
        binding!!.etBusinesName.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        binding!!.edtDescription.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        allCategoryData = java.util.ArrayList<AllCategoryData.Response.CategoryforlistingItem?>()
        getBundleData()

        if (UtilityMethod.isDeviceOnline(mActivity!!)) {
            allCategoryAPI()
        } else {
            UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
        }
        viewModel!!.responseAllCategoryLiveData.observe(this, Observer {
            handleResult(it)
        })
        viewModel!!.responseAddListingLiveData.observe(this, Observer {
            handleAddListingResult(it)
        })
        viewModel!!.responseEditListingLiveData.observe(this, Observer {
            handleEditListingResult(it)
        })
    }

    private fun getBundleData() {
        mBundle = intent.getBundleExtra("bundle")
        if (mBundle != null) {
            title = mBundle!!.getString("title")
            updateListingData = mBundle!!.getSerializable("listingData") as ArrayList<UpdateListingDetailData.Response.UpdatelistdetailsItem?>?
            updateListingGalleryData = mBundle!!.getSerializable("listingGalleryData") as java.util.ArrayList<UpdateListingDetailData.Response.UpdatelistgalleryimagItem?>?

            if (title.equals("Edit Listing", false)) {
                binding!!.appBar.tvTitle.setText(title)
                if(updateListingData !=null && updateListingData!!.isNotEmpty())
                {
                    binding!!.tvCatName.setText(updateListingData!!.get(0)!!.catName)
                    binding!!.etBusinesName.setText(updateListingData!!.get(0)!!.name)
                    UtilityMethod.loadImage(binding!!.ivAddListing, updateListingData!!.get(0)!!.listImage!!)
                    binding!!.edtDescription.setText(updateListingData!!.get(0)!!.description)
                    binding!!.etPhone.setText(updateListingData!!.get(0)!!.phoneNumber)
                    binding!!.etWhatsappPhone.setText(updateListingData!!.get(0)!!.whatsappNumber)
                    binding!!.tvAddress.setText(updateListingData!!.get(0)!!.address)
                    listId = updateListingData!!.get(0)!!.id
                    categoryId = updateListingData!!.get(0)!!.catId
                }
                binding!!.btnAddProduct.setText("Submit")
                binding!!.ivGallary1.visibility = View.GONE
                binding!!.ivFullGallary1.visibility = View.VISIBLE
                binding!!.ivGallary2.visibility = View.GONE
                binding!!.ivFullGallary2.visibility = View.VISIBLE
                if(updateListingGalleryData !=null && updateListingGalleryData!!.isNotEmpty())
                {
                    UtilityMethod.loadImage(binding!!.ivFullGallary1, updateListingGalleryData!!.get(0)!!.image!!)
                    UtilityMethod.loadImage(binding!!.ivFullGallary2, updateListingGalleryData!!.get(1)!!.image!!)
                }
                uploadImg = title!!
                uploadImg1 = title!!
                uploadImg2 = title!!
            }

        }
    }


    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
        binding!!.btnAddProduct.setOnClickListener(this)
        binding!!.rlAddImage.setOnClickListener(this)
        binding!!.rlAllCategory.setOnClickListener(this)
        binding!!.ivAddAddress.setOnClickListener(this)
        binding!!.rlImg1.setOnClickListener(this)
        binding!!.rlImg2.setOnClickListener(this)
        binding!!.rlImg3.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        UtilityMethod.hideKeyboard(mActivity!!)
        when (view!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.btnAddProduct -> {
                addListingAPI()
            }
            R.id.rlAddImage -> {
                tagg = "0"
                val pickerDialog = BSImagePicker.Builder("com.carealls.fileProvider")
                    .build()
                pickerDialog.show(supportFragmentManager, "picker")
            }
            R.id.rlAllCategory -> {
                showAllCategoryDialog()
            }
            R.id.ivAddAddress -> {
                addLocationDialog()
            }
            R.id.rlImg1 -> {
                tagg = "1"
                val pickerDialog = BSImagePicker.Builder("com.carealls.fileProvider")
                    .build()
                pickerDialog.show(supportFragmentManager, "picker")
            }
            R.id.rlImg2 -> {
                tagg = "2"
                val pickerDialog = BSImagePicker.Builder("com.carealls.fileProvider")
                    .build()
                pickerDialog.show(supportFragmentManager, "picker")
            }
            R.id.rlImg3 -> {
                val pickerDialog = Builder("com.carealls.fileProvider")
                    .setMaximumDisplayingImages(Int.MAX_VALUE)
                    .isMultiSelect
                    .setMinimumMultiSelectCount(1)
                    .setMaximumMultiSelectCount(6)
                    .build()
                pickerDialog.show(supportFragmentManager, "picker")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.tvAddress.text = sessionManager!!.location
        binding!!.tvAddress.isSelected = true
    }

    private fun addLocationDialog() {
        val dialog = Dialog(mActivity!!, R.style.Theme_Dialog)
        val dialogBinding: DialogAddAddressBinding = DataBindingUtil.inflate(
            LayoutInflater.from(
                mActivity
            ), R.layout.dialog_add_address, null, false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.getRoot())
        dialogBinding.tvTitle.setText("Add Address")
        if (title.equals("Edit Listing", false)) {
            dialogBinding.edtAddress.setText(updateListingData!!.get(0)!!.address)
        }
        else{
            dialogBinding.edtAddress.setText(sessionManager!!.location)
        }
        dialogBinding.btnSubmit.setOnClickListener(View.OnClickListener {
            UtilityMethod.hideKeyboard(mActivity!!)
            if (!dialogBinding.edtAddress.getText().toString().isEmpty()) {
                if (dialogBinding.edtAddress.getText().toString().length < 3) {
                    UtilityMethod.showToastMessageError(
                        mActivity!!,
                        mActivity!!.getString(R.string.minimum_3_char_long_address)
                    )
                } else {
                    dialog.dismiss()
                    sessionManager!!.location = dialogBinding.edtAddress.getText().toString().trim()
                    binding!!.tvAddress.text = sessionManager!!.location
                    binding!!.tvAddress.isSelected = true
                }
            } else {
                UtilityMethod.showToastMessageError(mActivity!!, "Please enter address")
            }
        })
        dialogBinding.btnCancel.setOnClickListener(View.OnClickListener {
            UtilityMethod.hideKeyboard(mActivity!!)
            dialog.dismiss()
        })
        dialog.show()
    }

    private fun showAllCategoryDialog() {
        if (allCategoryData!!.size != 0) {
            val a1 = ArrayList<String>()
            for (i in allCategoryData!!.indices) {
                a1.add(allCategoryData!!.get(i)!!.catName!!)
            }
            val stockArr = a1.toTypedArray()
            val builder = AlertDialog.Builder(
                mActivity!!
            )
            builder.setTitle("Categories")
            builder.setItems(stockArr) { dialog, which -> //Toast.makeText(mActivity, "Position: " + which + " Value: " + listItems[which], Toast.LENGTH_LONG).show();
                binding!!.tvCatName.setText(stockArr[which])
                binding!!.tvCatName.setSelected(true)
                Log.e(TAG, "category Id - " + allCategoryData!!.get(which)!!.catId)
                categoryId = allCategoryData!!.get(which)!!.catId
            }
            val dialog = builder.create()
            dialog.show()
        } else {
            UtilityMethod.showToastMessageError(mActivity!!, "No Data available!")
        }
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

                    if (result.data.response!!.categoryforlisting != null && !result.data.response.categoryforlisting!!.isEmpty()) {
                        allCategoryData!!.clear()
                        allCategoryData!!.addAll(result.data.response.categoryforlisting)
                    } else {
                    }
                } else {
                }

            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
            }
        }
    }


    private fun addListingAPI() {
        val categoryName = binding!!.tvCatName.text.toString().trim()
        val businessName = binding!!.etBusinesName.text.toString().trim()
        val description = binding!!.edtDescription.text.toString().trim()
        val phone = binding!!.etPhone.text.toString().trim()
        val whatsappNo = binding!!.etWhatsappPhone.text.toString().trim()


        if (viewModel!!.isValidFormData(
                mActivity!!,
                categoryName,
                businessName,
                description,
                uploadImg,
                uploadImg1,
                uploadImg2,
                phone,
                whatsappNo
            )) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["method"] = Constants.AddListing
            reqData["category"] = categoryId!!
            reqData["business_name"] = businessName
            reqData["discription"] = description
            reqData["phone_number"] = phone
            reqData["whatapp_number"] = whatsappNo
            reqData["address"] = sessionManager!!.location
            reqData["userId"] = sessionManager!!.useR_ID


            var requestBody = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp)
            var requestBody1 = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp1)
            var requestBody2 = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp2)
            var requestBody3 = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp3)
            var requestBody4 = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp4)
            var requestBody5 = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp5)
            var requestBody6 = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp6)
            var requestBody7 = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp7)
            var requestBody8 = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp8)


            val req_id: MultipartBody.Part =
                MultipartBody.Part.createFormData("userId", sessionManager!!.useR_ID)
            val req_categoryId: MultipartBody.Part =
                MultipartBody.Part.createFormData("category", categoryId!!)
            val req_businessName: MultipartBody.Part =
                MultipartBody.Part.createFormData("business_name", businessName)
            val req_phone_number: MultipartBody.Part =
                MultipartBody.Part.createFormData("phone_number", phone)
            val req_whatapp_number: MultipartBody.Part =
                MultipartBody.Part.createFormData("whatapp_number", whatsappNo)
            val req_address: MultipartBody.Part =
                MultipartBody.Part.createFormData("address", sessionManager!!.location)
            var profile_photo: MultipartBody.Part? = null
            var profile_photo1: MultipartBody.Part? = null
            var profile_photo2: MultipartBody.Part? = null
            var profile_photo3: MultipartBody.Part? = null
            var profile_photo4: MultipartBody.Part? = null
            var profile_photo5: MultipartBody.Part? = null
            var profile_photo6: MultipartBody.Part? = null
            var profile_photo7: MultipartBody.Part? = null
            var profile_photo8: MultipartBody.Part? = null
            if (selectedImagePath.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp!!.name)
                profile_photo = MultipartBody.Part.createFormData("image", mFileTemp!!.name, requestBody)
            }
            if (selectedImagePath1.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp1!!.name)
                profile_photo1 = MultipartBody.Part.createFormData("images[0]", mFileTemp1!!.name, requestBody1)
            }
            if (selectedImagePath2.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp2!!.name)
                profile_photo2 = MultipartBody.Part.createFormData("images[1]", mFileTemp2!!.name, requestBody2)
            }
            if (selectedImagePath3.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp3!!.name)
                profile_photo3 = MultipartBody.Part.createFormData("images[2]", mFileTemp3!!.name, requestBody3)
            }
            if (selectedImagePath4.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp4!!.name)
                profile_photo4 = MultipartBody.Part.createFormData("images[3]", mFileTemp4!!.name, requestBody4)
            }
            if (selectedImagePath5.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp5!!.name)
                profile_photo5 = MultipartBody.Part.createFormData("images[4]", mFileTemp5!!.name, requestBody5)
            }
            if (selectedImagePath6.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp6!!.name)
                profile_photo6 = MultipartBody.Part.createFormData("images[5]", mFileTemp6!!.name, requestBody6)
            }
            if (selectedImagePath7.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp7!!.name)
                profile_photo7 = MultipartBody.Part.createFormData("images[6]", mFileTemp7!!.name, requestBody7)
            }
            if (selectedImagePath8.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp7!!.name)
                profile_photo8 = MultipartBody.Part.createFormData("images[7]", mFileTemp8!!.name, requestBody8)
            }
            var req_listId: MultipartBody.Part? = null
            var req_method: MultipartBody.Part? = null
            var req_description: MultipartBody.Part? = null
            if (title.equals("Edit Listing", false)) {
                req_listId = MultipartBody.Part.createFormData("list_id", listId!!)
                req_method = MultipartBody.Part.createFormData("method", Constants.EditListing)
                req_description =  MultipartBody.Part.createFormData("description", description)
            }
            else{
                req_method = MultipartBody.Part.createFormData("method", Constants.AddListing)
                req_description =  MultipartBody.Part.createFormData("discription", description)
            }

            // Log.e(TAG, "image pic - " + profile_photo +" "+mFileTemp!!.name+" "+requestBody)
            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                Log.e(TAG, "Api parameters - " + reqData)
                if (title.equals("Edit Listing", false)) {
                    viewModel!!.editListingApi(
                        req_method,
                        req_id,
                        req_categoryId,
                        req_businessName,
                        req_description,
                        req_phone_number,
                        req_whatapp_number,
                        req_address,
                        profile_photo,
                        profile_photo1,
                        profile_photo2,
                        profile_photo3,
                        profile_photo4,
                        profile_photo5,
                        profile_photo6,
                        profile_photo7,
                        profile_photo8,
                        req_listId!!
                    )
                }
                else{
                    viewModel!!.addListingApi(
                        req_method,
                        req_id,
                        req_categoryId,
                        req_businessName,
                        req_description,
                        req_phone_number,
                        req_whatapp_number,
                        req_address,
                        profile_photo,
                        profile_photo1,
                        profile_photo2,
                        profile_photo3,
                        profile_photo4,
                        profile_photo5,
                        profile_photo6,
                        profile_photo7,
                        profile_photo8
                    )
                }


            }
        }
    }


    private fun handleAddListingResult(result: ApiResponse<AddListingData>?) = when (result!!.status) {
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
                val bundle = Bundle()
                bundle.putString("listId", result.data.data!!.listId)
                bundle.putString("skip", "skip")
                AddProductActivity.startActivity(mActivity!!, bundle, false)
                finish()
            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.message!!)
            }
        }
        }
    private fun handleEditListingResult(result: ApiResponse<EditUpdateListingData>?) = when (result!!.status) {
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
        if (tagg == "0") {
            selectedImagePath = ImagePathUtility.getImagePath(mActivity!!, uri!!)!!
            uploadImg = selectedImagePath
            mFileTemp = File(selectedImagePath)
           // binding!!.ivAddListing.setImageURI(uri)

            mFileTemp?.let { imageFile ->
                lifecycleScope.launch {
                    // Default compression
                    mFileTemp = Compressor.compress(mActivity!!, imageFile)
                    mFileTemp?.let {
                        binding!!.ivAddListing.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                        Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                    }
                }
            } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")

        }
        if (tagg == "1") {
            selectedImagePath1 = ImagePathUtility.getImagePath(mActivity!!, uri!!)!!
            uploadImg1 = selectedImagePath1
            mFileTemp1 = File(selectedImagePath1)
            binding!!.ivGallary1.visibility = View.GONE
            binding!!.ivFullGallary1.visibility = View.VISIBLE

            mFileTemp1?.let { imageFile ->
                lifecycleScope.launch {
                    // Default compression
                    mFileTemp1 = Compressor.compress(mActivity!!, imageFile)
                    mFileTemp1?.let {
                        binding!!.ivFullGallary1.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                        Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                    }
                }
            } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")

           // binding!!.ivFullGallary1.setImageURI(uri)
        }
        if (tagg == "2") {
            selectedImagePath2 = ImagePathUtility.getImagePath(mActivity!!, uri!!)!!
            uploadImg2 = selectedImagePath2
            mFileTemp2 = File(selectedImagePath2)
            binding!!.ivGallary2.visibility = View.GONE
            binding!!.ivFullGallary2.visibility = View.VISIBLE

            mFileTemp2?.let { imageFile ->
                lifecycleScope.launch {
                    // Default compression
                    mFileTemp2 = Compressor.compress(mActivity!!, imageFile)
                    mFileTemp2?.let {
                        binding!!.ivFullGallary2.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                        Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                    }
                }
            } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")

            //binding!!.ivFullGallary2.setImageURI(uri)
        }
    }

    override fun onMultiImageSelected(uriList: MutableList<Uri>?, tag: String?) {
        for (i in uriList!!.indices) {
            if (i >= 6) return
            when (i) {
                0 -> {
                    selectedImagePath3 = ImagePathUtility.getImagePath(mActivity!!, uriList.get(i))!!
                    mFileTemp3 = File(selectedImagePath3)

                    mFileTemp3?.let { imageFile ->
                        lifecycleScope.launch {
                            // Default compression
                            mFileTemp3 = Compressor.compress(mActivity!!, imageFile)
                            mFileTemp3?.let {
                                Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                            }
                        }
                    } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")

                }
                1 -> {
                    selectedImagePath4 =
                        ImagePathUtility.getImagePath(mActivity!!, uriList.get(i))!!
                    mFileTemp4 = File(selectedImagePath4)

                    mFileTemp4?.let { imageFile ->
                        lifecycleScope.launch {
                            // Default compression
                            mFileTemp4 = Compressor.compress(mActivity!!, imageFile)
                            mFileTemp4?.let {
                                Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                            }
                        }
                    } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")
                }
                2 -> {
                    selectedImagePath5 = ImagePathUtility.getImagePath(mActivity!!, uriList.get(i))!!
                    mFileTemp5 = File(selectedImagePath5)
                    mFileTemp5?.let { imageFile ->
                        lifecycleScope.launch {
                            // Default compression
                            mFileTemp5 = Compressor.compress(mActivity!!, imageFile)
                            mFileTemp5?.let {
                                Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                            }
                        }
                    } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")
                }
                3 -> {
                    selectedImagePath6 = ImagePathUtility.getImagePath(mActivity!!, uriList.get(i))!!
                    mFileTemp6 = File(selectedImagePath6)
                    mFileTemp6?.let { imageFile ->
                        lifecycleScope.launch {
                            // Default compression
                            mFileTemp6 = Compressor.compress(mActivity!!, imageFile)
                            mFileTemp6?.let {
                                Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                            }
                        }
                    } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")
                }
                4 -> {
                    selectedImagePath7 = ImagePathUtility.getImagePath(mActivity!!, uriList.get(i))!!
                    mFileTemp7 = File(selectedImagePath7)
                    mFileTemp7?.let { imageFile ->
                        lifecycleScope.launch {
                            // Default compression
                            mFileTemp7 = Compressor.compress(mActivity!!, imageFile)
                            mFileTemp7?.let {
                                Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                            }
                        }
                    } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")
                }
                5 -> {
                    selectedImagePath8 = ImagePathUtility.getImagePath(mActivity!!, uriList.get(i))!!
                    mFileTemp8 = File(selectedImagePath8)
                    mFileTemp8?.let { imageFile ->
                        lifecycleScope.launch {
                            // Default compression
                            mFileTemp8 = Compressor.compress(mActivity!!, imageFile)
                            mFileTemp8?.let {
                                Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                            }
                        }
                    } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")
                }
                else -> {
                    selectedImagePath2 = ImagePathUtility.getImagePath(mActivity!!, uriList.get(i))!!
                    uploadImg2 = selectedImagePath2
                    mFileTemp2 = File(selectedImagePath2)
                    mFileTemp2?.let { imageFile ->
                        lifecycleScope.launch {
                            // Default compression
                            mFileTemp2 = Compressor.compress(mActivity!!, imageFile)
                            mFileTemp2?.let {
                                Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                            }
                        }
                    } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")
                }
            }
        }
    }
    override fun onCancelled(isMultiSelecting: Boolean, tag: String?) {

    }

    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        selectedImagePath = ""
        selectedImagePath1 = ""
        selectedImagePath2 = ""
        selectedImagePath3 = ""
        selectedImagePath4 = ""
        selectedImagePath5 = ""
        selectedImagePath6 = ""
        selectedImagePath7 = ""
        selectedImagePath8 = ""
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }




}





