package br.com.stonesdk.sdkdemo.activities

import android.app.Application
import android.util.Log
import br.com.stonesdk.sdkdemo.demoApplicationModule
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.stoneStart.provider.StoneStart
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DemoApplication)
            modules(demoApplicationModule)
        }

        // gerar uma instancia global de algum view model e salvar a lista de mercharnts

        StoneStart.init(
            context = this,
            appName = "",
            appVersion = "123",
            packageName = "br.com.example",
            environment = StoneStart.StoneEnvironment.CERTIFICATION,
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