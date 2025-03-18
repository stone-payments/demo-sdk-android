package br.com.stonesdk.sdkdemo

import co.stone.posmobile.sdk.activation.provider.ActivationProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.merchant.provider.MerchantProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ActivationProviderWrapper {

    private val activationProvider: ActivationProvider
        get() = ActivationProvider.create()

    private val merchantProvider : MerchantProvider
        get() = MerchantProvider.create()

    suspend fun activate(stoneCode: String): Boolean = suspendCancellableCoroutine { continuation ->

        activationProvider.activate(stoneCode, object : StoneResultCallback<Unit> {
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
            activationProvider.deactivate(stoneCode, object : StoneResultCallback<Boolean> {
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

    suspend fun getActivatedAffiliationCodes(): List<String> =
        suspendCancellableCoroutine { continuation ->
            merchantProvider.getAllMerchants(object : StoneResultCallback<List<Merchant>> {
                override fun onSuccess(result: List<Merchant>) {
                    continuation.resume(result.map { it.affiliationCode })
                }

                override fun onError(
                    stoneStatus: br.com.stone.sdk.android.error.StoneStatus?,
                    throwable: Throwable
                ) {
                    continuation.resume(emptyList())
                }
            })
            continuation.invokeOnCancellation {}
        }
}