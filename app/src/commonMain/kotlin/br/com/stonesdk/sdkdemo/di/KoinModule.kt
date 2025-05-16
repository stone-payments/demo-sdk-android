package br.com.stonesdk.sdkdemo.di

import br.com.stonesdk.sdkdemo.utils.AppInfo
import co.stone.posmobile.lib.commons.platform.PlatformContext
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.stoneStart.provider.StoneStart
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

expect val targetModule: Module

fun initializeKoin(
    platformContext: PlatformContext,
    appInfo: AppInfo,
    config: (KoinApplication.() -> Unit)? = null,
) {
    startKoin {
        config?.invoke(this)
        modules(targetModule)

        // TODO use app info

        StoneStart.init(
            context = platformContext,
            appName = "",
            appVersion = "123",
            packageName = "br.com.example",
            environment = StoneStart.StoneEnvironment.CERTIFICATION,
            callback = object : StoneResultCallback<List<Merchant>> {
                override fun onSuccess(result: List<Merchant>) {

                }

                override fun onError(
                    stoneStatus: br.com.stone.sdk.android.error.StoneStatus?,
                    throwable: Throwable
                ) {

                }
            }
        )

    }
}