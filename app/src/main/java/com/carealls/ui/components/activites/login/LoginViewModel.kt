package com.carealls.ui.components.activites.login

import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.R
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.LoginData
import com.carealls.utils.UtilityMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel : ViewModel() {
    var responseLiveData = MutableLiveData<ApiResponse<LoginData>>()
    var apiResponse: ApiResponse<LoginData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun loginApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.login(reqData)
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

    /*Validations*/
    fun isValidFormData(mActivity: AppCompatActivity, email: String, password: String): Boolean {

        if (TextUtils.isEmpty(email)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_email)
            )
            return false
        }

        if (!UtilityMethod.isValidEmail(email)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_valid_email_id)
            )
            return false
        }

        if (TextUtils.isEmpty(password)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_password)
            )
            return false
        }
        if (password.length < 6) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_6_characters)
            )
            return false
        }
        return true
    }
}