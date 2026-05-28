package br.com.stonesdk.sdkdemo.wrappers

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.display.provider.DisplayProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DisplayProviderWrapper {
    val provider: DisplayProvider
        get() = DisplayProvider.create()

    suspend fun displayMessage(message: String): DisplayMessageStatus =
        suspendCancellableCoroutine { continuation ->
            provider.show(message = message, object : StoneResultCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    continuation.resume(DisplayMessageStatus.Success)
                }

                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    continuation.resume(
                        DisplayMessageStatus.Error(
                            stoneStatus?.message ?: throwable.message ?: "Erro desconhecido"
                        )
                    )
                }
            })

            continuation.invokeOnCancellation {}

        }
}

sealed class DisplayMessageStatus {
    data object Success : DisplayMessageStatus()
    data class Error(val errorMessage: String) : DisplayMessageStatus()

}