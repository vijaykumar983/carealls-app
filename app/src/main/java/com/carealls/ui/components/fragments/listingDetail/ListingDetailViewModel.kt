package com.carealls.ui.components.fragments.listingDetail


import android.app.Activity
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.R
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.*
import com.carealls.utils.UtilityMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ListingDetailViewModel : ViewModel() {
    var responseSendEnquiryLiveData = MutableLiveData<ApiResponse<SendEnquiryData>>()
     var apiSendEnquiryResponse: ApiResponse<SendEnquiryData>? = null

    var responseListingDetalLiveData = MutableLiveData<ApiResponse<ListingDetailData>>()
    var apiListingDetalResponse: ApiResponse<ListingDetailData>? = null

    var responseGalleryLiveData = MutableLiveData<ApiResponse<GalleryData>>()
    var apiGalleryResponse: ApiResponse<GalleryData>? = null

    var responseReviewListLiveData = MutableLiveData<ApiResponse<ReviewListData>>()
    var apiReviewListResponse: ApiResponse<ReviewListData>? = null

    var responseDeleteGalleryLiveData = MutableLiveData<ApiResponse<DeleteGalleryData>>()
    var apiReviewDeleteGalleryResponse: ApiResponse<DeleteGalleryData>? = null


     private var restApi: RestApi? = null
     private var subscription: Disposable? = null

     init {
         restApi = RestApiFactory.create()
         apiSendEnquiryResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiListingDetalResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiGalleryResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiReviewListResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiReviewDeleteGalleryResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
     }

     fun sendEnquiry(reqData: HashMap<String, String>) {
         subscription = restApi!!.sendEnquiry(reqData)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .doOnSubscribe {
                 responseSendEnquiryLiveData.postValue(apiSendEnquiryResponse!!.loading())
             }
             .subscribe(
                 { result ->
                     responseSendEnquiryLiveData.postValue(apiSendEnquiryResponse!!.success(result))
                 },
                 { throwable ->
                     responseSendEnquiryLiveData.postValue(apiSendEnquiryResponse!!.error(throwable))
                 }
             )
     }
    fun listingDetail(reqData: HashMap<String, String>) {
        subscription = restApi!!.listingDetail(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseListingDetalLiveData.postValue(apiListingDetalResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseListingDetalLiveData.postValue(apiListingDetalResponse!!.success(result))
                },
                { throwable ->
                    responseListingDetalLiveData.postValue(apiListingDetalResponse!!.error(throwable))
                }
            )
    }
    fun gallery(reqData: HashMap<String, String>) {
        subscription = restApi!!.gallery(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseGalleryLiveData.postValue(apiGalleryResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseGalleryLiveData.postValue(apiGalleryResponse!!.success(result))
                },
                { throwable ->
                    responseGalleryLiveData.postValue(apiGalleryResponse!!.error(throwable))
                }
            )
    }

    fun reviewList(reqData: HashMap<String, String>) {
        subscription = restApi!!.reviewList(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseReviewListLiveData.postValue(apiReviewListResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseReviewListLiveData.postValue(apiReviewListResponse!!.success(result))
                },
                { throwable ->
                    responseReviewListLiveData.postValue(apiReviewListResponse!!.error(throwable))
                }
            )
    }

    fun deleteGallery(reqData: HashMap<String, String>) {
        subscription = restApi!!.deleteGallery(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseDeleteGalleryLiveData.postValue(apiReviewDeleteGalleryResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseDeleteGalleryLiveData.postValue(apiReviewDeleteGalleryResponse!!.success(result))
                },
                { throwable ->
                    responseDeleteGalleryLiveData.postValue(apiReviewDeleteGalleryResponse!!.error(throwable))
                }
            )
    }


     fun disposeSubscriber() {
         if (subscription != null)
             subscription!!.dispose()
     }

    /*Validations*/
    fun isValidFormData(mActivity: Activity, name: String, phone: String): Boolean {

        if (TextUtils.isEmpty(name)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_full_name)
            )
            return false
        }
        if (name.length < 3) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_3_char_long)
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
        return true
    }
}