package br.com.stonesdk.sdkdemo.activities

import android.app.Application
import br.com.stonesdk.sdkdemo.demoApplicationModule
import co.stone.posmobile.sdk.StoneStart
import co.stone.posmobile.sdk.domain.model.merchant.Merchant
import co.stone.posmobile.sdk.domain.model.response.StoneResultCallback
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        StoneStart.init(
            context = this,
            appName = BuildConfig.LIBRARY_PACKAGE_NAME,
            appVersion = BuildConfig.LIBRARY_PACKAGE_NAME,
            packageName = "br.com.example",
            callback = object : StoneResultCallback<List<Merchant>> {
                override fun onSuccess(result: List<Merchant>) {
                    TODO("Not yet implemented")
                }

                override fun onError(
                    stoneStatus: br.com.stone.sdk.android.error.StoneStatus?,
                    throwable: Throwable
                ) {
                    TODO("Not yet implemented")
                }


            }
        )
        startKoin {
            androidContext(this@DemoApplication)
            modules(demoApplicationModule)
        }
    }
}