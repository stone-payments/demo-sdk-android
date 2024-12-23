package br.com.stonesdk.sdkdemo.activities

import android.app.Application
import br.com.stonesdk.sdkdemo.demoApplicationModule
import co.stone.posmobile.sdk.StoneStart
import co.stone.posmobile.sdk.domain.model.merchant.Merchant
import co.stone.posmobile.sdk.domain.model.response.StoneResultCallback
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        StoneStart.init(
            context = this,
            appName = "",
            appVersion = "123",
            packageName = "",
            callback = object :
                StoneResultCallback<List<Merchant>> {
                override fun onSuccess(result: List<Merchant>) {

                }

                override fun onError(
                    stoneStatus: br.com.stone.sdk.android.error.StoneStatus?,
                    throwable: Throwable
                ) {

                }

            }
        )

        startKoin {
            androidContext(this@DemoApplication)
            modules(demoApplicationModule)
        }
    }
}