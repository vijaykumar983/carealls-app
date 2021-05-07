package com.carealls

import android.app.Application


class App : Application() {

    companion object {
        var singleton: App? = null
    }


    override fun onCreate() {
        super.onCreate()

        singleton = this
    }
}