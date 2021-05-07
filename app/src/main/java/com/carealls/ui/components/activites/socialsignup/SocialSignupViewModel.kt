package com.carealls.ui.components.activites.socialsignup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.LoginData
import com.carealls.pojo.SocialLoginData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SocialSignupViewModel : ViewModel() {
    var responseLiveData = MutableLiveData<ApiResponse<SocialLoginData>>()
    var apiResponse: ApiResponse<SocialLoginData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun socialLoginApi(reqData: HashMap<String, String?>) {
        subscription = restApi!!.sociallogin(reqData)
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
    }
}