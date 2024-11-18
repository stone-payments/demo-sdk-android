package br.com.stonesdk.sdkdemo.activities

import android.app.Application
import br.com.stonesdk.sdkdemo.demoApplicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DemoApplication)
            modules(demoApplicationModule)
        }
    }
}