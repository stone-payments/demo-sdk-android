package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.ActiveApplicationProvider
import kotlin.coroutines.resume

class ActivationProviderWrapper(
    private val context: Context
) {

    suspend fun activate(stoneCode: String): Boolean = suspendCancellableCoroutine { continuation ->
        val provider = newProvider()

        provider.connectionCallback = object : StoneCallbackInterface {
            override fun onSuccess() {
                continuation.resume(true)
            }

            override fun onError() {
                continuation.resume(false)
            }
        }

        provider.activate(stoneCode)

        continuation.invokeOnCancellation {}
    }

    suspend fun deactivate(stoneCode: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            val provider = newProvider()

            provider.connectionCallback = object : StoneCallbackInterface {
                override fun onSuccess() {
                    continuation.resume(true)
                }

                override fun onError() {
                    continuation.resume(false)
                }
            }

            provider.deactivate(stoneCode)

            continuation.invokeOnCancellation {}
        }

    fun newProvider() = ActiveApplicationProvider(context)
}