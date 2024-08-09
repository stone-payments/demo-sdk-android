package br.com.stonesdk.sdkdemo.activities

import android.app.Application
import stone.application.StoneStart


class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        StoneStart.init(this)
    }
}