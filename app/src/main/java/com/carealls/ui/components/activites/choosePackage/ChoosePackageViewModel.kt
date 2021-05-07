package com.carealls.ui.components.activites.choosePackage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.AddListingData
import com.carealls.pojo.AllCategoryData
import com.carealls.pojo.PackageData
import com.carealls.pojo.SubmitPackageData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChoosePackageViewModel : ViewModel() {
    var responseLiveGetPackageData = MutableLiveData<ApiResponse<PackageData>>()
    var apiGetPackageResponse: ApiResponse<PackageData>? = null

    var responseSubmitPackageLiveData = MutableLiveData<ApiResponse<SubmitPackageData>>()
    var apiSubmitPackageResponse: ApiResponse<SubmitPackageData>? = null



    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiGetPackageResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiSubmitPackageResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)

    }

    fun getPackageApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.getPackage(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveGetPackageData.postValue(apiGetPackageResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveGetPackageData.postValue(apiGetPackageResponse!!.success(result))
                },
                { throwable ->
                    responseLiveGetPackageData.postValue(apiGetPackageResponse!!.error(throwable))
                }
            )
    }
    fun submitPackage(reqData: HashMap<String, String>) {
        subscription = restApi!!.sumitPackage(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseSubmitPackageLiveData.postValue(apiSubmitPackageResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseSubmitPackageLiveData.postValue(apiSubmitPackageResponse!!.success(result))
                },
                { throwable ->
                    responseSubmitPackageLiveData.postValue(apiSubmitPackageResponse!!.error(throwable))
                }
            )
    }


    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }
}