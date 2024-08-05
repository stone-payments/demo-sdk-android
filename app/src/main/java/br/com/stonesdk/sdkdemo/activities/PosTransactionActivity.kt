package br.com.stonesdk.sdkdemo.activities

import android.R
import android.app.AlertDialog
import android.widget.Toast
import br.com.stone.posandroid.providers.PosPrintReceiptProvider
import br.com.stone.posandroid.providers.PosTransactionProvider
import br.com.stonesdk.sdkdemo.controller.PrintController
import stone.application.enums.Action
import stone.application.enums.ErrorsEnum
import stone.application.enums.ReceiptType
import stone.application.enums.TransactionStatusEnum

class PosTransactionActivity : BaseTransactionActivity<PosTransactionProvider?>() {

    override fun buildTransactionProvider(): PosTransactionProvider {
        return PosTransactionProvider(this, transactionObject, selectedUserModel)
    }

    private fun getTransactionProvider(): PosTransactionProvider? {
        return super.transactionProvider as PosTransactionProvider?
    }

    override fun onSuccess() {
        if (transactionObject.transactionStatus == TransactionStatusEnum.APPROVED) {
            val printMerchant =
                PrintController(
                    this@PosTransactionActivity,
                    PosPrintReceiptProvider(
                        this.applicationContext,
                        transactionObject, ReceiptType.MERCHANT
                    )
                )

            printMerchant.print()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Transação aprovada! Deseja imprimir a via do cliente?")

            builder.setPositiveButton(R.string.yes) { _, _ ->
                val printClient =
                    PrintController(
                        this@PosTransactionActivity,
                        PosPrintReceiptProvider(
                            applicationContext,
                            transactionObject, ReceiptType.CLIENT
                        )
                    )
                printClient.print()
            }

            builder.setNegativeButton(R.string.no, null)

            runOnUiThread { builder.show() }
        } else {
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    "Erro na transação: \"$authorizationMessage\"",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onError() {
        super.onError()
        if (providerHasErrorEnum(ErrorsEnum.DEVICE_NOT_COMPATIBLE)) {
            Toast.makeText(
                this,
                "Dispositivo não compatível ou dependência relacionada não está presente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStatusChanged(action: Action) {
        super.onStatusChanged(action)

        runOnUiThread {
            when (action) {
                Action.TRANSACTION_WAITING_PASSWORD -> Toast.makeText(
                    this@PosTransactionActivity,
                    "Pin tries remaining to block card: \${transactionProvider?.remainingPinTries}",
                    Toast.LENGTH_LONG
                ).show()

                Action.TRANSACTION_TYPE_SELECTION -> {
                    val options = getTransactionProvider()?.transactionTypeOptions
                    showTransactionTypeSelectionDialog(options.orEmpty())
                }

                else -> {}
            }
        }
    }

    private fun showTransactionTypeSelectionDialog(optionsList: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecione o tipo de transação")
        builder.setItems(optionsList.toTypedArray()) { _, which ->
            getTransactionProvider()?.setTransactionTypeSelected(which)
        }
        builder.show()
    }
}
