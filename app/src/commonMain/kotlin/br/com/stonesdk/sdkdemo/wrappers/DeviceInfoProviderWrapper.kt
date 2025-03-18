package br.com.stonesdk.sdkdemo.wrappers


import co.stone.posmobile.sdk.deviceinfo.provider.DeviceInfoProvider

class DeviceInfoProviderWrapper{

    private val deviceInfoProvider: DeviceInfoProvider
        get() = DeviceInfoProvider.create()

    fun isPosDevice() : Boolean = deviceInfoProvider.isPosAndroid()

}