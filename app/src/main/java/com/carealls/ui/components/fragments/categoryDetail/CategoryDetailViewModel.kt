package com.carealls.ui.components.fragments.categoryDetail


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.CategoryDetailData
import com.carealls.pojo.CategoryProductData
import com.carealls.pojo.DashboardData
import com.carealls.pojo.DeleteProductData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CategoryDetailViewModel : ViewModel() {
    var responseCatDetailLiveData = MutableLiveData<ApiResponse<CategoryDetailData>>()
    var apiCatDetailResponse: ApiResponse<CategoryDetailData>? = null

    var responseCatProductLiveData = MutableLiveData<ApiResponse<CategoryProductData>>()
    var apiCatProductResponse: ApiResponse<CategoryProductData>? = null

    var responseLiveDeleteProductData = MutableLiveData<ApiResponse<DeleteProductData>>()
    var apiDeleteProductResponse: ApiResponse<DeleteProductData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiCatDetailResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiCatProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiDeleteProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun categoryDetailApi(reqData: HashMap<String, String>) {

        subscription = restApi!!.categoryDetail(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseCatDetailLiveData.postValue(apiCatDetailResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseCatDetailLiveData.postValue(apiCatDetailResponse!!.success(result))
                },
                { throwable ->
                    responseCatDetailLiveData.postValue(apiCatDetailResponse!!.error(throwable))
                }
            )
    }
    fun categoryProductApi(reqData: HashMap<String, String>) {

        subscription = restApi!!.categoryProduct(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseCatProductLiveData.postValue(apiCatProductResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseCatProductLiveData.postValue(apiCatProductResponse!!.success(result))
                },
                { throwable ->
                    responseCatProductLiveData.postValue(apiCatProductResponse!!.error(throwable))
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