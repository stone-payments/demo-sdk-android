package br.com.stonesdk.sdkdemo.controller

import android.content.Context
import android.widget.Toast
import br.com.stone.sdk.android.error.StoneStatus
import br.com.stone.sdk.core.providers.interfaces.StoneCallbackInterface
import br.com.stone.sdk.payment.providers.PosPrintReceiptProvider


class PrintController(private val context: Context,
                      private val provider: PosPrintReceiptProvider
) {

    fun print() {
        val callback = object : StoneCallbackInterface {
            override fun onSuccess() {
                Toast.makeText(context, "Recibo impresso", Toast.LENGTH_SHORT).show()
            }

            override fun onError(cause: StoneStatus?) {
                Toast.makeText(context, "Erro ao imprimir: "
                        + provider.listOfErrors, Toast.LENGTH_SHORT).show()
            }
        }

        provider.print(callback)
    }
}