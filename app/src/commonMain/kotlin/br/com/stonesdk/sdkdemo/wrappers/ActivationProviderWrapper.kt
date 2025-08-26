package br.com.stonesdk.sdkdemo.wrappers

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.activation.provider.ActivationProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.merchant.provider.MerchantProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ActivationProviderWrapper {

    private val activationProvider: ActivationProvider
        get() = ActivationProvider.create()

    private val merchantProvider: MerchantProvider
        get() = MerchantProvider.create()

    suspend fun activate(affiliationCode: String): ActivationStatus = suspendCancellableCoroutine { continuation ->

        activationProvider.activate(affiliationCode, object : StoneResultCallback<Any> {
            override fun onSuccess(result: Any) {
                continuation.resume(ActivationStatus.Activated)
            }

            override fun onError(
                stoneStatus: StoneStatus?,
                throwable: Throwable
            ) {
                val errorMessage = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                continuation.resume(ActivationStatus.Error(errorMessage))
            }

        })

        continuation.invokeOnCancellation {}
    }

    suspend fun deactivate(affiliationCode: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            activationProvider.deactivate(affiliationCode, object : StoneResultCallback<Boolean> {
                override fun onSuccess(result: Boolean) {
                    continuation.resume(true)
                }

                override fun onError(
                    stoneStatus: StoneStatus?,
                    throwable: Throwable
                ) {
                    continuation.resume(false)
                }
            })
            continuation.invokeOnCancellation {}
        }

    suspend fun update(affiliationCode: String): Boolean = suspendCancellableCoroutine { continuation ->
        activationProvider.update(affiliationCode, object : StoneResultCallback<Unit> {


            override fun onSuccess(result: Unit) {
                continuation.resume(true)
            }

            override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                continuation.resume(false)
            }
        })

    }

    suspend fun getActivatedAffiliationCodes(): List<String> =
        suspendCancellableCoroutine { continuation ->
            merchantProvider.getAllMerchants(object : StoneResultCallback<List<Merchant>> {
                override fun onSuccess(result: List<Merchant>) {
                    continuation.resume(result.map { it.affiliationCode })
                }

                override fun onError(
                    stoneStatus: StoneStatus?,
                    throwable: Throwable
                ) {
                    continuation.resume(emptyList())
                }
            })
            continuation.invokeOnCancellation {}
        }

    sealed class ActivationStatus{
        data object Activated : ActivationStatus()
        data class Error(val errorMessage: String) : ActivationStatus()
    }
}