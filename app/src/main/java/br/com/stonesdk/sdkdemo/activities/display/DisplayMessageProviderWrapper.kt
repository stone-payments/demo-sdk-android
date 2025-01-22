package br.com.stonesdk.sdkdemo.activities.display

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.display.provider.DisplayProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DisplayMessageProviderWrapper {

    val provider: DisplayProvider
        get() = DisplayProvider.create()

    suspend fun displayMessage(message: String): Boolean = suspendCancellableCoroutine { continuation ->
            provider.show(message = message, object : StoneResultCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    continuation.resume(true)
                }

                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    continuation.resume(false)
                }
            })

            continuation.invokeOnCancellation {}

        }
}