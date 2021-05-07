package com.carealls.ui.components.activites.reviews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.ReviewListData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ReviewsViewModel : ViewModel() {

    var responseReviewListLiveData = MutableLiveData<ApiResponse<ReviewListData>>()
    var apiReviewListResponse: ApiResponse<ReviewListData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiReviewListResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
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

    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }
}