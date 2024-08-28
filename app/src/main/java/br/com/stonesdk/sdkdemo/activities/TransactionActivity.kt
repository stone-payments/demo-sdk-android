package br.com.stonesdk.sdkdemo.activities

import android.widget.Toast
import stone.application.enums.TransactionStatusEnum
import stone.providers.TransactionProvider
import stone.utils.Stone

class TransactionActivity : BaseTransactionActivity<TransactionProvider?>() {
    override fun buildTransactionProvider(): TransactionProvider {
        return TransactionProvider(
            this@TransactionActivity,
            transactionObject,
            selectedUserModel,
            Stone.getPinpadFromListAt(0)
        )
    }

    override fun onSuccess() {
        if (transactionObject.transactionStatus == TransactionStatusEnum.APPROVED) {
            Toast.makeText(
                applicationContext,
                "Transação enviada com sucesso e salva no banco. Para acessar, use o TransactionDAO.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            var msg = "Erro na transação"

            msg += if (authorizationMessage != null) {
                ":$authorizationMessage"
            } else {
                "!"
            }

            Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onError() {
        super.onError()
    }
}
