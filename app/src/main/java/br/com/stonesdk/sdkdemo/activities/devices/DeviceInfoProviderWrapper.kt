package br.com.stonesdk.sdkdemo.activities.devices


import co.stone.posmobile.sdk.deviceinfo.provider.DeviceInfoProvider

class DeviceInfoProviderWrapper{

    private val deviceInfoProvider: DeviceInfoProvider
        get() = DeviceInfoProvider.create()

    fun isPosDevice() : Boolean = deviceInfoProvider.isPosAndroid()

}