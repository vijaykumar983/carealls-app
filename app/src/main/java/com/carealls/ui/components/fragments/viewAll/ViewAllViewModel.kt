package com.carealls.ui.components.fragments.viewAll


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ViewAllViewModel : ViewModel() {
    var responseAllCategoryLiveData = MutableLiveData<ApiResponse<AllCategoryData>>()
     var apiAllCategoryResponse: ApiResponse<AllCategoryData>? = null

    var responseAllListingLiveData = MutableLiveData<ApiResponse<AllListingData>>()
    var apiAllListingResponse: ApiResponse<AllListingData>? = null

    var responseAllLocationLiveData = MutableLiveData<ApiResponse<AllLocationData>>()
    var apiAllLocationResponse: ApiResponse<AllLocationData>? = null

    var responseAllProductLiveData = MutableLiveData<ApiResponse<AllProductData>>()
    var apiAllProductResponse: ApiResponse<AllProductData>? = null

    var responseGalleryLiveData = MutableLiveData<ApiResponse<GalleryData>>()
    var apiGalleryResponse: ApiResponse<GalleryData>? = null

    var responseDeleteGalleryLiveData = MutableLiveData<ApiResponse<DeleteGalleryData>>()
    var apiReviewDeleteGalleryResponse: ApiResponse<DeleteGalleryData>? = null

    var responseLiveDeleteProductData = MutableLiveData<ApiResponse<DeleteProductData>>()
    var apiDeleteProductResponse: ApiResponse<DeleteProductData>? = null

     private var restApi: RestApi? = null
     private var subscription: Disposable? = null

     init {
         restApi = RestApiFactory.create()
         apiAllCategoryResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiAllListingResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiAllLocationResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiAllProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiGalleryResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiReviewDeleteGalleryResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
         apiDeleteProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
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
    fun allListing(reqData: HashMap<String, String>) {
        subscription = restApi!!.allListing(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseAllListingLiveData.postValue(apiAllListingResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseAllListingLiveData.postValue(apiAllListingResponse!!.success(result))
                },
                { throwable ->
                    responseAllListingLiveData.postValue(apiAllListingResponse!!.error(throwable))
                }
            )
    }
    fun allLocation(reqData: HashMap<String, String>) {
        subscription = restApi!!.allLocation(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseAllLocationLiveData.postValue(apiAllLocationResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseAllLocationLiveData.postValue(apiAllLocationResponse!!.success(result))
                },
                { throwable ->
                    responseAllLocationLiveData.postValue(apiAllLocationResponse!!.error(throwable))
                }
            )
    }

    fun allProduct(reqData: HashMap<String, String>) {
        subscription = restApi!!.allProduct(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseAllProductLiveData.postValue(apiAllProductResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseAllProductLiveData.postValue(apiAllProductResponse!!.success(result))
                },
                { throwable ->
                    responseAllProductLiveData.postValue(apiAllProductResponse!!.error(throwable))
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