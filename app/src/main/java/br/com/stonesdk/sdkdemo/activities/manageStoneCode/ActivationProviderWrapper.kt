package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import co.stone.posmobile.sdk.activation.provider.ActivationProvider
import co.stone.posmobile.sdk.domain.model.response.StoneResultCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ActivationProviderWrapper {

    val provider: ActivationProvider
        get() = ActivationProvider.create()

    suspend fun activate(stoneCode: String): Boolean = suspendCancellableCoroutine { continuation ->

        provider.activate(stoneCode, object : StoneResultCallback<Unit> {
            override fun onSuccess(result: Unit) {
                continuation.resume(true)
            }

            override fun onError(
                stoneStatus: br.com.stone.sdk.android.error.StoneStatus?,
                throwable: Throwable
            ) {
                continuation.resume(false)
            }
        })

        continuation.invokeOnCancellation {}
    }

    suspend fun deactivate(stoneCode: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            provider.deactivate(stoneCode, object : StoneResultCallback<Boolean> {
                override fun onSuccess(result: Boolean) {
                    continuation.resume(true)
                }

                override fun onError(
                    stoneStatus: br.com.stone.sdk.android.error.StoneStatus?,
                    throwable: Throwable
                ) {
                    continuation.resume(false)
                }
            })
            continuation.invokeOnCancellation {}
        }

    suspend fun getActivatedStoneCodes(): List<String> =
        suspendCancellableCoroutine { continuation ->
            //
            continuation.invokeOnCancellation {}
        }
}