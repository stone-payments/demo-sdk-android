package br.com.stonesdk.sdkdemo.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.stonesdk.sdkdemo.DemoApp
import br.com.stonesdk.sdkdemo.utils.AppInfo

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DemoApp(applicationContext, getAppInfo())
        }
    }


    private fun getAppInfo() : AppInfo{
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return AppInfo(packageName, packageInfo.applicationInfo?.loadLabel(packageManager).toString(), packageInfo.versionName ?: "unknown")
    }
}