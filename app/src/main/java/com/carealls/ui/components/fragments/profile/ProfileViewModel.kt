package com.carealls.ui.components.fragments.profile


import android.app.Activity
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.R
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.ProfileData
import com.carealls.pojo.UpdateProfileData
import com.carealls.utils.UtilityMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class ProfileViewModel : ViewModel() {
    var responseLiveUpdateProfileData = MutableLiveData<ApiResponse<UpdateProfileData>>()
    var apiUpdateProfileResponse: ApiResponse<UpdateProfileData>? = null

    var responseLiveGetProfileData = MutableLiveData<ApiResponse<ProfileData>>()
    var apiGetProfileResponse: ApiResponse<ProfileData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiUpdateProfileResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiGetProfileResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun updateProfileApi(
        reqMethod: MultipartBody.Part,
        reqId: MultipartBody.Part,
        reqName: MultipartBody.Part,
        reqEmail: MultipartBody.Part,
        reqPhone: MultipartBody.Part,
        reqAddress: MultipartBody.Part,
        profilePhoto: MultipartBody.Part?
    ) {
        subscription = restApi!!.updateProfile(
            reqMethod,
            reqId,
            reqName,
            reqEmail,
            reqPhone,
            reqAddress,
            profilePhoto
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveUpdateProfileData.postValue(apiUpdateProfileResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveUpdateProfileData.postValue(apiUpdateProfileResponse!!.success(result))
                },
                { throwable ->
                    responseLiveUpdateProfileData.postValue(apiUpdateProfileResponse!!.error(throwable))
                }
            )

    }

    fun getProfileApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.getProfile(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveGetProfileData.postValue(apiGetProfileResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveGetProfileData.postValue(apiGetProfileResponse!!.success(result))
                },
                { throwable ->
                    responseLiveGetProfileData.postValue(apiGetProfileResponse!!.error(throwable))
                }
            )
    }

    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }

    /*Validations*/
    fun isValidFormData(
        mActivity: Activity,
        image: String?,
        fullName: String,
        email: String?,
        phone: String,
        address: String
    ): Boolean {
       /* if (TextUtils.isEmpty(image)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.please_select_image)
            )
            return false
        }*/
        if (image!!.isNotEmpty() && image == "Upload Image") {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.please_select_image)
            )
            return false
        }
        if (TextUtils.isEmpty(fullName)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_full_name)
            )
            return false
        }
        if (fullName.length < 3) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_3_char_long)
            )
            return false
        }
        if (TextUtils.isEmpty(email!!)) {
            UtilityMethod.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_email))
            return false
        }
        if (!UtilityMethod.isValidEmail(email)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_valid_email_id)
            )
            return false
        }
        if (TextUtils.isEmpty(phone)) {
            UtilityMethod.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number))
            return false
        }
        if (phone.length < 9) {
            UtilityMethod.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number))
            return false
        }
        if (TextUtils.isEmpty(address)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_address)
            )
            return false
        }
        if (address.length < 3) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_3_char_long_address)
            )
            return false
        }
        return true
    }
}