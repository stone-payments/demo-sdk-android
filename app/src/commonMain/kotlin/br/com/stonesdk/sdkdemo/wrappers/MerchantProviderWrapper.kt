package br.com.stonesdk.sdkdemo.wrappers

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

    fun getAllMerchants() : Flow<QueryAllMerchantsStatus> {
        return callbackFlow {
            trySend(QueryAllMerchantsStatus.Loading)
            merchantProvider.getAllMerchants(object : StoneResultCallback<List<Merchant>>{
                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                    trySend(QueryAllMerchantsStatus.Error(error))
                }

                override fun onSuccess(result: List<Merchant>) {
                    trySend(QueryAllMerchantsStatus.Success(result))
                }
            })
            awaitClose {  }
        }
    }

    fun getMerchantByAffiliationCode(affiliationCode: String): Flow<QueryMerchantByAffiliationCodeStatus> {
        return callbackFlow {
            trySend(QueryMerchantByAffiliationCodeStatus.Loading)
            merchantProvider.getMerchantByCode(
                affiliationCode = affiliationCode,
                object : StoneResultCallback<Merchant> {
                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        trySend(QueryMerchantByAffiliationCodeStatus.Error(error))
                    }

                    override fun onSuccess(result: Merchant) {
                        trySend(QueryMerchantByAffiliationCodeStatus.Success(result))
                    }
                },
            )
            awaitClose { }
        }
    }
}

sealed class QueryAllMerchantsStatus {
    data object Loading : QueryAllMerchantsStatus()

    data class Success(val merchants: List<Merchant>) : QueryAllMerchantsStatus()

    data class Error(val errorMessage: String) : QueryAllMerchantsStatus()
}

sealed class QueryMerchantByAffiliationCodeStatus {
    data object Loading : QueryMerchantByAffiliationCodeStatus()

    data class Success(val merchant: Merchant?) : QueryMerchantByAffiliationCodeStatus()

    data class Error(val errorMessage: String) : QueryMerchantByAffiliationCodeStatus()
}