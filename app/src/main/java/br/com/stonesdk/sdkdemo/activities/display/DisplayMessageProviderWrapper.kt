package br.com.stonesdk.sdkdemo.activities.display

import co.stone.posmobile.sdk.display.provider.DisplayProvider

class DisplayMessageProviderWrapper {

    val provider : DisplayProvider
        get() = DisplayProvider.create()

    suspend fun displayMessage(message: String) {
        provider.show(message)
    }

}