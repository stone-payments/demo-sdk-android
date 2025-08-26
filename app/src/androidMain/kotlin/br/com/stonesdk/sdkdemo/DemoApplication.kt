package br.com.stonesdk.sdkdemo

import android.app.Application
import br.com.stonesdk.sdkdemo.di.initializeKoin
import br.com.stonesdk.sdkdemo.utils.AppInfo
import org.koin.android.ext.koin.androidContext

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeKoin(
            platformContext = this@DemoApplication,
            appInfo = getAppInfo()
        ){
            androidContext(this@DemoApplication)
        }
    }

    private fun getAppInfo(): AppInfo {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return AppInfo(packageName, packageInfo.applicationInfo?.loadLabel(packageManager).toString(), packageInfo.versionName ?: "unknown")
    }
}
