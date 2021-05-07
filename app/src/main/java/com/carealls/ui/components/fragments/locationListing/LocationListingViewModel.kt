package com.carealls.ui.components.fragments.locationListing


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LocationListingViewModel : ViewModel() {
    var responseLocationListingLiveData = MutableLiveData<ApiResponse<LocationListingData>>()
    var apiLocationListingResponse: ApiResponse<LocationListingData>? = null

     private var restApi: RestApi? = null
     private var subscription: Disposable? = null

     init {
         restApi = RestApiFactory.create()
         apiLocationListingResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
     }

    fun locationListingApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.locationListing(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLocationListingLiveData.postValue(apiLocationListingResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLocationListingLiveData.postValue(apiLocationListingResponse!!.success(result))
                },
                { throwable ->
                    responseLocationListingLiveData.postValue(apiLocationListingResponse!!.error(throwable))
                }
            )
    }


     fun disposeSubscriber() {
         if (subscription != null)
             subscription!!.dispose()
     }
}