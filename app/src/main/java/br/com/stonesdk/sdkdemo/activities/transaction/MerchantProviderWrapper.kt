package br.com.stonesdk.sdkdemo.activities.transaction

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.merchant.provider.MerchantProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MerchantProviderWrapper {
    private val merchantProvider: MerchantProvider
        get() = MerchantProvider.create()

    fun getMerchantByAffiliationCode(affiliationCode: String): Flow<MerchantByAffiliationCodeStatus> {
        return callbackFlow {
            trySend(MerchantByAffiliationCodeStatus.Loading)
            merchantProvider.getMerchantByCode(
                affiliationCode = affiliationCode,
                object : StoneResultCallback<Merchant> {
                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        trySend(MerchantByAffiliationCodeStatus.Error(error))
                    }

                    override fun onSuccess(result: Merchant) {
                        trySend(MerchantByAffiliationCodeStatus.Success(result))
                    }
                },
            )
            awaitClose { }
        }
    }

    sealed class MerchantByAffiliationCodeStatus {
        data object Loading : MerchantByAffiliationCodeStatus()

        data class Success(val merchant: Merchant?) : MerchantByAffiliationCodeStatus()

        data class Error(val errorMessage: String) : MerchantByAffiliationCodeStatus()
    }
}
