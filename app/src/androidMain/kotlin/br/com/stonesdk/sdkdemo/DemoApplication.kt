package br.com.stonesdk.sdkdemo

import android.app.Application
import android.util.Log
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.stoneStart.provider.StoneStart

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        StoneStart.init(
            context = this,
            appName = "",
            appVersion = "123",
            packageName = "br.com.example",
            environment = StoneStart.StoneEnvironment.STAGING,
            callback = object : StoneResultCallback<List<Merchant>> {
                override fun onSuccess(result: List<Merchant>) {
                    Log.d("StoneStart", "Success: ${result.size}")
                }

                override fun onError(
                    stoneStatus: br.com.stone.sdk.android.error.StoneStatus?,
                    throwable: Throwable
                ) {
                    Log.d("StoneStart", "Error: ${throwable.message}")
                }
            }
        )
    }
}