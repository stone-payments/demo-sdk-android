package br.com.stonesdk.sdkdemo.activities

import ProbePendingReversalPaymentProvider
import android.content.Context
import android.util.Log
import android.widget.Toast
import stone.application.interfaces.ProbePendingReversalPaymentCallback
import stone.database.transaction.TransactionObject

class Executor {
    fun run(context: Context) {

        Toast.makeText(context, "Metodo em execucao", Toast.LENGTH_SHORT).show()
        
        Log.d("Executor", "Running")

        val itk = "SUA_ITK"

        val transactionObject = TransactionObject()

        val provider = ProbePendingReversalPaymentProvider(context, transactionObject)
        provider.probeInstantPayment(itk, object : ProbePendingReversalPaymentCallback {
            override fun onTransactionStatusUpdated(transactionObject: TransactionObject) {
                Log.d("Executor", "Transaction updated")
                Log.d("Executor", "Transaction status: ${transactionObject.transactionStatus}")
                Log.d("Executor", "Transaction type: ${transactionObject.typeOfTransaction}")
                Log.d("Executor", "Transaction all: $transactionObject")
            }
        })
    }
}