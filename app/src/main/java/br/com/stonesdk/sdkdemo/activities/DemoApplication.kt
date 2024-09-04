package br.com.stonesdk.sdkdemo.activities

import android.app.Application
import br.com.stonesdk.sdkdemo.demoApplicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import stone.application.StoneStart

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        StoneStart.init(this)
        startKoin {
            androidContext(this@DemoApplication)
            modules(demoApplicationModule)
        }
    }
}