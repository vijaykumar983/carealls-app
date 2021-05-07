package com.carealls.ui.components.activites.addListing

import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.R
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.AddListingData
import com.carealls.pojo.AllCategoryData
import com.carealls.pojo.EditUpdateListingData
import com.carealls.utils.UtilityMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class AddListingViewModel : ViewModel() {
    var responseAddListingLiveData = MutableLiveData<ApiResponse<AddListingData>>()
    var apiAddListingResponse: ApiResponse<AddListingData>? = null

    var responseEditListingLiveData = MutableLiveData<ApiResponse<EditUpdateListingData>>()
    var apiEditListingResponse: ApiResponse<EditUpdateListingData>? = null

    var responseAllCategoryLiveData = MutableLiveData<ApiResponse<AllCategoryData>>()
    var apiAllCategoryResponse: ApiResponse<AllCategoryData>? = null



    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiAddListingResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiEditListingResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiAllCategoryResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)

    }

    fun addListingApi(
        reqMethod: MultipartBody.Part,
        req_id: MultipartBody.Part,
        req_categoryId: MultipartBody.Part,
        req_businessName: MultipartBody.Part,
        req_description: MultipartBody.Part,
        req_phone_number: MultipartBody.Part,
        req_whatapp_number: MultipartBody.Part,
        req_address: MultipartBody.Part,
        profile_photo: MultipartBody.Part?,
        profile_photo1: MultipartBody.Part?,
        profile_photo2: MultipartBody.Part?,
        profile_photo3: MultipartBody.Part?,
        profile_photo4: MultipartBody.Part?,
        profile_photo5: MultipartBody.Part?,
        profile_photo6: MultipartBody.Part?,
        profile_photo7: MultipartBody.Part?,
        profile_photo8: MultipartBody.Part?,
    ) {
        subscription = restApi!!.addListing(
            reqMethod,
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseAddListingLiveData.postValue(apiAddListingResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseAddListingLiveData.postValue(apiAddListingResponse!!.success(result))
                },
                { throwable ->
                    responseAddListingLiveData.postValue(apiAddListingResponse!!.error(throwable))
                }
            )
    }


    fun editListingApi(
        reqMethod: MultipartBody.Part,
        req_id: MultipartBody.Part,
        req_categoryId: MultipartBody.Part,
        req_businessName: MultipartBody.Part,
        req_description: MultipartBody.Part,
        req_phone_number: MultipartBody.Part,
        req_whatapp_number: MultipartBody.Part,
        req_address: MultipartBody.Part,
        profile_photo: MultipartBody.Part?,
        profile_photo1: MultipartBody.Part?,
        profile_photo2: MultipartBody.Part?,
        profile_photo3: MultipartBody.Part?,
        profile_photo4: MultipartBody.Part?,
        profile_photo5: MultipartBody.Part?,
        profile_photo6: MultipartBody.Part?,
        profile_photo7: MultipartBody.Part?,
        profile_photo8: MultipartBody.Part?,
        req_listId: MultipartBody.Part
    ) {
        subscription = restApi!!.editListing(
            reqMethod,
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
            req_listId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseEditListingLiveData.postValue(apiEditListingResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseEditListingLiveData.postValue(apiEditListingResponse!!.success(result))
                },
                { throwable ->
                    responseEditListingLiveData.postValue(apiEditListingResponse!!.error(throwable))
                }
            )
    }

    fun allCategory(reqData: HashMap<String, String>) {
        subscription = restApi!!.allCategory(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseAllCategoryLiveData.postValue(apiAllCategoryResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseAllCategoryLiveData.postValue(apiAllCategoryResponse!!.success(result))
                },
                { throwable ->
                    responseAllCategoryLiveData.postValue(apiAllCategoryResponse!!.error(throwable))
                }
            )
    }


    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }

    /*Validations*/
    fun isValidFormData(
        mActivity: AppCompatActivity,
        categoryName: String,
        businessName: String,
        description: String,
        uploadImg: String,
        uploadImg1: String,
        uploadImg2: String,
        phone: String,
        whatsappNo: String
    ): Boolean {

        if (categoryName.equals("Category Name", false)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.select_category_name)
            )
            return false
        }
        if (TextUtils.isEmpty(businessName)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_business_name)
            )
            return false
        }
        if (businessName.length < 3) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_3_char_long_business_name)
            )
            return false
        }

        if (TextUtils.isEmpty(description)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_description)
            )
            return false
        }
        if (description.length < 3) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_3_char_long_description)
            )
            return false
        }
        if (!uploadImg.isEmpty() && uploadImg == "Upload Image") {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.please_select_image)
            )
            return false
        }


       if (!uploadImg1.isEmpty() && (uploadImg1 == "Upload Image1" || uploadImg2 == "Upload Image2")) {
           UtilityMethod.showSnackBarMsgError(
               mActivity,
               mActivity.getString(R.string.please_select_gallery_img)
           )
            return false
        }

        if (TextUtils.isEmpty(phone)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_valid_mobile_number)
            )
            return false
        }
        if (phone.length < 9) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_valid_mobile_number)
            )
            return false
        }
        if (TextUtils.isEmpty(whatsappNo)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_valid_whatsapp_number)
            )
            return false
        }
        if (whatsappNo.length < 9) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_valid_whatsapp_number)
            )
            return false
        }

        return true
    }

}