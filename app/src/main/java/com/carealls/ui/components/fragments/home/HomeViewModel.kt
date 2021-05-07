package com.carealls.ui.components.fragments.home


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.BannerData
import com.carealls.pojo.DashboardData
import com.carealls.pojo.DeleteProductData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel : ViewModel() {
    var responseLiveData = MutableLiveData<ApiResponse<DashboardData>>()
    var apiResponse: ApiResponse<DashboardData>? = null

    var responseLiveDeleteProductData = MutableLiveData<ApiResponse<DeleteProductData>>()
    var apiDeleteProductResponse: ApiResponse<DeleteProductData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiDeleteProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun dashboardApi(reqData: HashMap<String, String>) {

        subscription = restApi!!.dashboard(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveData.postValue(apiResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveData.postValue(apiResponse!!.success(result))
                },
                { throwable ->
                    responseLiveData.postValue(apiResponse!!.error(throwable))
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