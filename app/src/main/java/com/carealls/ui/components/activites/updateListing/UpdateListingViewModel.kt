package com.carealls.ui.components.activites.updateListing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.DeleteProductData
import com.carealls.pojo.ProductDetailData
import com.carealls.pojo.UpdateListingDetailData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UpdateListingViewModel : ViewModel() {
    var responseLiveUpdateDetailData = MutableLiveData<ApiResponse<UpdateListingDetailData>>()
    var apiUpdateDetailResponse: ApiResponse<UpdateListingDetailData>? = null

    var responseLiveDeleteProductData = MutableLiveData<ApiResponse<DeleteProductData>>()
    var apiDeleteProductResponse: ApiResponse<DeleteProductData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiUpdateDetailResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiDeleteProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun updateListingDetailApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.updateListingDetail(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveUpdateDetailData.postValue(apiUpdateDetailResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveUpdateDetailData.postValue(
                        apiUpdateDetailResponse!!.success(
                            result
                        )
                    )
                },
                { throwable ->
                    responseLiveUpdateDetailData.postValue(
                        apiUpdateDetailResponse!!.error(
                            throwable
                        )
                    )
                }
            )
    }

    fun deleteProductApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.deleteProduct(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveDeleteProductData.postValue(apiDeleteProductResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveDeleteProductData.postValue(apiDeleteProductResponse!!.success(result))
                },
                { throwable ->
                    responseLiveDeleteProductData.postValue(apiDeleteProductResponse!!.error(throwable))
                }
            )
    }


    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }
}