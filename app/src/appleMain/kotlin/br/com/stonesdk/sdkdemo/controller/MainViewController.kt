package br.com.stonesdk.sdkdemo.controller

import androidx.compose.ui.window.ComposeUIViewController
import br.com.stonesdk.sdkdemo.DemoApp
import br.com.stonesdk.sdkdemo.di.initializeKoin
import br.com.stonesdk.sdkdemo.utils.AppInfo
import co.stone.posmobile.lib.commons.platform.PlatformContext
import platform.Foundation.NSBundle


private fun getAppInfo(): AppInfo {
    val bundle = NSBundle.mainBundle
    return AppInfo(
        appName = bundle.objectForInfoDictionaryKey("CFBundleName") as? String ?: "Unknown",
        appVersion = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown",
        packageName = bundle.bundleIdentifier ?: "Unknown"
    )
}

fun MainViewController() = ComposeUIViewController(
    configure = {
        initializeKoin(
            platformContext = PlatformContext.INSTANCE,
            appInfo = getAppInfo()
        )
    }
) { DemoApp() }