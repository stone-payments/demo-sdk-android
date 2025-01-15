package br.com.stonesdk.sdkdemo.activities.main

import co.stone.posmobile.sdk.domain.model.response.StoneResultCallback
import co.stone.posmobile.sdk.reversal.provider.ReversalProvider

class ReversalProviderWrapper {

    private val reversalProvider: ReversalProvider
        get() = ReversalProvider.create()


    fun reverseTransactions(callback: StoneResultCallback<Unit>) {
        reversalProvider.reverseTransactions(callback = callback)
    }

}