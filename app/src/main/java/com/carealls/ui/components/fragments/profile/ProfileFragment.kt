package com.carealls.ui.components.fragments.profile


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.text.InputFilter
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.carealls.R
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.databinding.FragmentProfileBinding
import com.carealls.dialogs.ProgressDialog
import com.carealls.network.ApiResponse
import com.carealls.pojo.ProfileData
import com.carealls.pojo.UpdateProfileData
import com.carealls.ui.base.BaseFragment
import com.carealls.ui.components.activites.addListing.AddListingActivity
import com.carealls.ui.components.activites.home.HomeActivity
import com.carealls.utils.Constants
import com.carealls.utils.EmojiExcludeFilter
import com.carealls.utils.UtilityMethod
import com.google.gson.Gson
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.log10
import kotlin.math.pow


class ProfileFragment : BaseFragment(), BSImagePicker.OnSingleImageSelectedListener,
    BSImagePicker.OnMultiImageSelectedListener,
    BSImagePicker.ImageLoaderDelegate {
    private var binding: FragmentProfileBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: ProfileViewModel? = null
    private var homeActivity: HomeActivity? = null

    private val REQ_CODE_GALLERY_PICKER3 = 30
    private var mFileTemp = File("")
    private val TEMP_PHOTO_FILE_NAME = "GoTo.png"
    private var uploadImg = "Upload Image"

    private var tvUserName: TextView? = null
    private var tvUserEmail: TextView? = null
    private var ivNavProfile: ImageView? = null
    private var ivHeaderProfile: ImageView? = null


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        onClickListener = this
        binding!!.lifecycleOwner = this
        return binding!!
    }

    companion object {
        private val TAG = ProfileFragment::class.qualifiedName
        private var selectedImagePath = ""
    }

    override fun createActivityObject() {
        mActivity = activity
    }

    override fun initializeObject() {
        binding!!.edtName.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        binding!!.edtAddress.setFilters(arrayOf<InputFilter>(EmojiExcludeFilter()))
        iniView()
        binding!!.edtName.filters = arrayOf(EmojiExcludeFilter())
        binding!!.edtAddress.filters = arrayOf(EmojiExcludeFilter())
    }

    private fun iniView() {
        tvUserName = mActivity!!.findViewById(R.id.txtuserName)
        tvUserEmail = mActivity!!.findViewById(R.id.tvEmail)
        ivNavProfile = mActivity!!.findViewById(R.id.ivNavProfile)
        ivHeaderProfile = mActivity!!.findViewById(R.id.ivHeaderProfile)
    }

    override fun initializeOnCreateObject() {
        homeActivity = activity as HomeActivity?
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        viewModel!!.responseLiveUpdateProfileData.observe(this, Observer {
            handleResult(it)
        })
        viewModel!!.responseLiveGetProfileData.observe(this, Observer {
            handleGetProfileResult(it)
        })
    }

    override fun setListeners() {
        binding!!.appBar.ivBack.setOnClickListener(this)
        binding!!.tvUploadProfile.setOnClickListener(this)
        binding!!.btnUpdateProfile.setOnClickListener(this)
    }

    override fun onClick(q0: View?) {
        UtilityMethod.hideKeyboard(mActivity!!)
        when (q0!!.id) {
            R.id.ivBack -> {
                homeActivity!!.onBackPressed()
            }
            R.id.tvUploadProfile -> {
                createAppDir()
                val pickerDialog = BSImagePicker.Builder("com.carealls.fileProvider")
                    .build()
                pickerDialog.show(childFragmentManager, "picker")
            }
            R.id.btnUpdateProfile -> {
                updateProfileAPI()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        UtilityMethod.setAppBar(mActivity!!)
        binding!!.appBar.tvTitle.text = "Update Profile"

        Log.e(TAG, "check getProfile - " + sessionManager!!.profilE_PIC)
        Log.e(TAG, "check selectImage - " + selectedImagePath)

        if (sessionManager!!.name.isEmpty() || sessionManager!!.email.isEmpty() || sessionManager!!.mobile.isEmpty()
            || sessionManager!!.address.isEmpty() || sessionManager!!.profilE_PIC.isEmpty()) {
            getProfileAPI()
        } else {
            setProfileData()
        }
    }

    private fun setProfileData() {
        binding!!.edtName.setText(sessionManager!!.name)
        binding!!.edtEmail.setText(sessionManager!!.email)
        binding!!.edtMobile.setText(sessionManager!!.mobile)
        binding!!.edtAddress.setText(sessionManager!!.address)
        if (selectedImagePath.isEmpty()) {
            if (sessionManager!!.profilE_PIC != null && !sessionManager!!.profilE_PIC.isEmpty())
                UtilityMethod.loadProfile(binding!!.ivProfilePic, sessionManager!!.profilE_PIC)
        }
    }

    private fun getProfileAPI() {
        val reqData: HashMap<String, String> = HashMap()
        reqData["method"] = Constants.GetProfile
        reqData["userId"] = sessionManager!!.useR_ID
        Log.e(TAG, "Api parameters - $reqData")
        viewModel!!.getProfileApi(reqData)
    }


    private fun handleGetProfileResult(result: ApiResponse<ProfileData>?) = when (result!!.status) {
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

                    sessionManager!!.useR_ID = result.data!!.response!!.get(0)!!.userId
                    sessionManager!!.name = result.data!!.response!!.get(0)!!.name
                    sessionManager!!.email = result.data!!.response!!.get(0)!!.email
                    sessionManager!!.profilE_PIC = result.data!!.response!!.get(0)!!.profile
                    sessionManager!!.mobile = result.data!!.response!!.get(0)!!.phone
                    sessionManager!!.address = result.data!!.response!!.get(0)!!.address

                    tvUserName!!.setText(sessionManager!!.name)
                    tvUserEmail!!.setText(sessionManager!!.email)

                    UtilityMethod.loadProfile(ivNavProfile, sessionManager!!.profilE_PIC)
                    UtilityMethod.loadProfile(ivHeaderProfile, sessionManager!!.profilE_PIC)
                    setProfileData()
                } else {
                }

            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.msg.toString())
            }
        }
    }

    private fun updateProfileAPI() {
        val fullName = binding!!.edtName.text.toString().trim()
        val email = binding!!.edtEmail.text.toString().trim()
        val mobile = binding!!.edtMobile.text.toString().trim()
        val address = binding!!.edtAddress.text.toString().trim()

        if (uploadImg.equals("Upload Image",false)) {
            if (sessionManager!!.profilE_PIC != null && sessionManager!!.profilE_PIC.isNotEmpty())
                uploadImg = sessionManager!!.profilE_PIC
        }
        Log.e(TAG,"check - "+uploadImg)

        if (viewModel!!.isValidFormData(mActivity!!, uploadImg, fullName, email, mobile, address)) {
            val reqData: HashMap<String, String> = HashMap()
            reqData["method"] = Constants.UpdateProfile
            reqData["name"] = fullName
            reqData["email"] = email
            reqData["phone"] = mobile
            reqData["address"] = address
            reqData["UserId"] = sessionManager!!.useR_ID


            var requestBody = RequestBody.create("*/*".toMediaTypeOrNull(), mFileTemp)
            val req_method: MultipartBody.Part = MultipartBody.Part.createFormData("method", Constants.UpdateProfile)
            val req_id: MultipartBody.Part = MultipartBody.Part.createFormData("UserId", sessionManager!!.useR_ID)
            val req_name: MultipartBody.Part = MultipartBody.Part.createFormData("name", fullName)
            val req_email: MultipartBody.Part = MultipartBody.Part.createFormData("email", email)
            val req_phone: MultipartBody.Part = MultipartBody.Part.createFormData("phone", mobile)
            val req_address: MultipartBody.Part = MultipartBody.Part.createFormData("address", address)
            var profile_photo: MultipartBody.Part? = null
            if (selectedImagePath.isEmpty()) {
            } else {
                Log.w(TAG, "name - " + mFileTemp!!.name)
                profile_photo = MultipartBody.Part.createFormData("image", mFileTemp!!.name, requestBody)
            }

           // Log.e(TAG, "image pic - " + profile_photo +" "+mFileTemp!!.name+" "+requestBody)
            if (!UtilityMethod.isOnline(mActivity!!)) {
                UtilityMethod.showNoInternetConnectedDialog(mActivity!!)
            } else {
                Log.e(TAG, "Api parameters - " + reqData)
                viewModel!!.updateProfileApi(
                    req_method,
                    req_id,
                    req_name,
                    req_email,
                    req_phone,
                    req_address,
                    profile_photo
                )
            }
        }
    }


    private fun handleResult(result: ApiResponse<UpdateProfileData>?) = when (result!!.status) {
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

                sessionManager!!.useR_ID = result.data!!.data!!.userId
                sessionManager!!.name = result.data!!.data!!.name
                sessionManager!!.email = result.data!!.data!!.email
                sessionManager!!.address = result.data!!.data!!.address
                sessionManager!!.profilE_PIC = result.data!!.data!!.profile.toString()

                tvUserName!!.setText(sessionManager!!.name)
                tvUserEmail!!.setText(sessionManager!!.email)

                UtilityMethod.loadProfile(ivNavProfile, sessionManager!!.profilE_PIC)
                UtilityMethod.loadProfile(ivHeaderProfile, sessionManager!!.profilE_PIC)
                setProfileData()

                showSuccessfullyDialog()

            } else {
                UtilityMethod.showToastMessageError(mActivity!!, result.data.message!!)
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
        dialogBinding.tvMessage.setText("User Profile successfully updated.")
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
            homeActivity!!.onBackPressed()
        })
        dialog.show()
    }


    private fun createAppDir() {
        val root = Environment.getExternalStorageDirectory().toString()
        File(root + "/" + getString(R.string.app_name) + "/temp").mkdirs()
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state)
        {
            mFileTemp = File(root + "/" + getString(R.string.app_name) + "/temp/",  Date().time.toString() + TEMP_PHOTO_FILE_NAME)
           // mFileTemp = File(root + "/" + getString(R.string.app_name) + "/temp/", TEMP_PHOTO_FILE_NAME + Date().time)
            //mFileTemp = File(root + "/" + getString(R.string.app_name) + "/temp/", TEMP_PHOTO_FILE_NAME)
        }
        else {
            mFileTemp = File(mActivity!!.getFilesDir(), Date().time.toString()+TEMP_PHOTO_FILE_NAME)
            //mFileTemp = File(mActivity!!.getFilesDir(), TEMP_PHOTO_FILE_NAME + Date().time)
            //mFileTemp = File(mActivity!!.getFilesDir(), TEMP_PHOTO_FILE_NAME)
        }
    }

    override fun loadImage(imageUri: Uri?, ivImage: ImageView?) {
        Glide.with(mActivity!!).load(imageUri).into(ivImage!!)
    }

    override fun onSingleImageSelected(uri: Uri?, tag: String?) {
        selectedImagePath = uri!!.path!!
        uploadImg = selectedImagePath

        var inputStream: InputStream? = null
        try {
            inputStream = mActivity!!.getContentResolver().openInputStream(uri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(mFileTemp)
            UtilityMethod.copyStream(inputStream!!, fileOutputStream)
            fileOutputStream.close()
            inputStream!!.close()

            UCrop.of(Uri.fromFile(mFileTemp), Uri.fromFile(mFileTemp))
                .withAspectRatio(4F, 4F)
                .start(mActivity!!, this, REQ_CODE_GALLERY_PICKER3)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMultiImageSelected(uriList: MutableList<Uri>?, tag: String?) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return
        }
        if (requestCode == REQ_CODE_GALLERY_PICKER3) {
            val resultUri = UCrop.getOutput(data!!)
            selectedImagePath = resultUri!!.path!!
            val bitmap: Bitmap? = UtilityMethod.decodeUriToBitmap(mActivity!!, resultUri)
            val bOut = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bOut)
            selectedImagePath = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT)

            mFileTemp?.let { imageFile ->
                lifecycleScope.launch {
                    // Default compression
                    mFileTemp = Compressor.compress(mActivity!!, imageFile)
                    mFileTemp?.let {
                        binding!!.ivProfilePic.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                        Log.d(TAG, String.format("Size : %s", UtilityMethod.getReadableFileSize(it.length()))+"\nCompressed image save in " + it.path)
                    }
                }
            } ?: UtilityMethod.showToastMessageError(mActivity!!,"Please choose an image!")

            //binding!!.ivProfilePic.setImageURI(resultUri)

            Log.e(TAG, "imageview selectImage - $resultUri")
        }
    }



    override fun onDestroy() {
        viewModel!!.disposeSubscriber()
        selectedImagePath = ""
        ProgressDialog.hideProgressDialog()
        super.onDestroy()
    }

}












