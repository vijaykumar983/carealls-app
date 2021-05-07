package com.carealls.ui.components.fragments.search


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel : ViewModel() {
    var responseSearchLiveData = MutableLiveData<ApiResponse<SearchData>>()
    var apiSearchResponse: ApiResponse<SearchData>? = null


     private var restApi: RestApi? = null
     private var subscription: Disposable? = null

     init {
         restApi = RestApiFactory.create()
         apiSearchResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
     }

     fun serach(reqData: HashMap<String, String>) {
         subscription = restApi!!.search(reqData)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .doOnSubscribe {
                 responseSearchLiveData.postValue(apiSearchResponse!!.loading())
             }
             .subscribe(
                 { result ->
                     responseSearchLiveData.postValue(apiSearchResponse!!.success(result))
                 },
                 { throwable ->
                     responseSearchLiveData.postValue(apiSearchResponse!!.error(throwable))
                 }
             )
     }


     fun disposeSubscriber() {
         if (subscription != null)
             subscription!!.dispose()
     }
}