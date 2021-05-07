package com.carealls.ui.components.fragments.manageListing


import androidx.lifecycle.ViewModel

class ManageListingViewModel : ViewModel() {

    /* var responseLiveData = MutableLiveData<ApiResponse<ShopCategoriesData>>()
     var apiResponse: ApiResponse<ShopCategoriesData>? = null


     private var restApi: RestApi? = null
     private var subscription: Disposable? = null

     init {
         restApi = RestApiFactory.create()
         apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)

     }

     fun showCategory(reqData: HashMap<String, String>) {

         subscription = restApi!!.shopByCategory(reqData)
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