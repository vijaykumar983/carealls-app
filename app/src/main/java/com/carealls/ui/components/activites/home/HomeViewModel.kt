package com.carealls.ui.components.activites.home

import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    //var responseLiveData = MutableLiveData<ApiResponse<RecentMultiVendorData>>()
    //var apiResponse: ApiResponse<RecentMultiVendorData>? = null

    /*var responseLiveData = MutableLiveData<ApiResponse<JsonElement>>()
    var apiResponse: ApiResponse<JsonElement>? = null

    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun recentAdvisoryApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.recentAllVendor(reqData)
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

    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }*/
}