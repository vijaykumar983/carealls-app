package com.carealls.ui.components.fragments.productDetail


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.ProductDetailData
import com.carealls.pojo.SupportData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ProductDetailViewModel : ViewModel() {
    var responseLiveProductDetailData = MutableLiveData<ApiResponse<ProductDetailData>>()
    var apiProductDetailResponse: ApiResponse<ProductDetailData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiProductDetailResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)

    }

    fun productDetailApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.productDetail(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveProductDetailData.postValue(apiProductDetailResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveProductDetailData.postValue(
                        apiProductDetailResponse!!.success(
                            result
                        )
                    )
                },
                { throwable ->
                    responseLiveProductDetailData.postValue(
                        apiProductDetailResponse!!.error(
                            throwable
                        )
                    )
                }
            )
    }


    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }
}