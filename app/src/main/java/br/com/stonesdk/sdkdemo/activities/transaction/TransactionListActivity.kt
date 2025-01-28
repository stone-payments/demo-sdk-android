package br.com.stonesdk.sdkdemo.activities.transaction

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme

class TransactionListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                TransactionListScreen()
            }
        }

    }

    fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
//        val selectedTransaction = transactionObjects!![position]
//        val optionsList: ArrayList<String?> = object : ArrayList<String?>() {
//            init {
//                add("[Pinpad] Imprimir comprovante")
//                add("[POS] Imprimir via do estabelecimento")
//                add("[POS] Imprimir via do cliente")
//                add("[POS] Imprimir comprovante customizado")
//                add("Cancelar")
//                add("Enviar via do cliente")
//                add("Enviar via do estabelecimento")
//            }
//        }
//        if (!selectedTransaction.isCapture) {
//            optionsList.add("Capturar Transação")
//        }
//        val options = arrayOfNulls<String>(optionsList.size)
//        optionsList.toArray(options)
//
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle(R.string.list_dialog_title)
//            .setItems(options, DialogInterface.OnClickListener { dialog, which ->
//                when (which) {
//                    0 -> try {
//                        // lógica da impressão
//                        val listToPrint: MutableList<PrintObject> = ArrayList()
//                        var i = 0
//                        while (i < 10) {
//                            listToPrint.add(
//                                PrintObject(
//                                    "Teste de impressão linha $i",
//                                    PrintObject.MEDIUM,
//                                    PrintObject.CENTER
//                                )
//                            )
//                            i++
//                        }
//                        // Stone.getPinpadFromListAt(0) eh o pinpad conectado, que esta na posicao zero.
//                        val printProvider = PrintProvider(
//                            this@TransactionListActivity,
//                            listToPrint,
//                            Stone.getPinpadFromListAt(0)
//                        )
//                        printProvider.dialogMessage = "Imprimindo..."
//                        printProvider.connectionCallback = object : StoneCallbackInterface {
//                            override fun onSuccess() {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Impressão realizada com sucesso",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                finish()
//                            }
//
//                            override fun onError() {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Um erro ocorreu durante a impressão",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        printProvider.execute()
//                    } catch (outException: IndexOutOfBoundsException) {
//                        Toast.makeText(
//                            applicationContext,
//                            "Conecte-se a um pinpad.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } catch (e: Exception) {
//                        Toast.makeText(
//                            applicationContext,
//                            "Houve um erro inesperado. Tente novamente mais tarde.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        e.printStackTrace()
//                    }
//
//                    1 -> printReceipt(ReceiptType.MERCHANT, selectedTransaction)
//                    2 -> printReceipt(ReceiptType.CLIENT, selectedTransaction)
//                    3 -> {
//                        // Impressão customizada
//                        val customPosPrintProvider = PosPrintProvider(this@TransactionListActivity)
//                        customPosPrintProvider.addLine("Stone")
//                        customPosPrintProvider.addLine("Comprovante customizado")
//                        customPosPrintProvider.addLine("")
//                        customPosPrintProvider.addLine("PAN : " + selectedTransaction.cardHolderNumber)
//                        customPosPrintProvider.addLine("DATE/TIME : " + selectedTransaction.date + " " + selectedTransaction.time)
//                        customPosPrintProvider.addLine("AMOUNT : " + selectedTransaction.amount)
//                        customPosPrintProvider.addLine("ATK : " + selectedTransaction.acquirerTransactionKey)
//                        customPosPrintProvider.addLine("")
//                        customPosPrintProvider.addLine("Signature")
//                        customPosPrintProvider.addBitmap(
//                            BitmapFactory.decodeResource(
//                                resources,
//                                R.drawable.signature
//                            )
//                        )
//                        customPosPrintProvider.connectionCallback =
//                            object : StoneCallbackInterface {
//                                override fun onSuccess() {
//                                    Toast.makeText(
//                                        this@TransactionListActivity,
//                                        "Recibo impresso",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//
//                                override fun onError() {
//                                    Toast.makeText(
//                                        this@TransactionListActivity,
//                                        "Erro ao imprimir: " + customPosPrintProvider.listOfErrors,
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        customPosPrintProvider.execute()
//                    }
//
//                    4 -> {
//                        val cancellationProvider =
//                            CancellationProvider(this@TransactionListActivity, selectedTransaction)
//                        cancellationProvider.dialogMessage = "Cancelando..."
//                        cancellationProvider.connectionCallback = object : StoneCallbackInterface {
//                            // chamada de retorno.
//                            override fun onSuccess() {
//                                Toast.makeText(
//                                    applicationContext,
//                                    cancellationProvider.messageFromAuthorize,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                finish()
//                            }
//
//                            override fun onError() {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Um erro ocorreu durante o cancelamento com a transacao de id: " + selectedTransaction.idFromBase,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                        cancellationProvider.execute()
//                    }
//
//                    5 -> sendReceipt(selectedTransaction, ReceiptType.CLIENT)
//                    6 -> sendReceipt(selectedTransaction, ReceiptType.MERCHANT)
//                    7 -> {
//                        val provider = CaptureTransactionProvider(
//                            this@TransactionListActivity,
//                            selectedTransaction
//                        )
//                        provider.dialogMessage = "Efetuando Captura..."
//                        provider.connectionCallback = object : StoneCallbackInterface {
//                            override fun onSuccess() {
//                                Toast.makeText(
//                                    this@TransactionListActivity, "Transação " +
//                                            "Capturada com sucesso!",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//
//                            override fun onError() {
//                                Toast.makeText(
//                                    this@TransactionListActivity, ("Ocorreu um " +
//                                            "erro captura da transacao: " +
//                                            provider.listOfErrors),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//
//                        provider.execute()
//                    }
//                }
//            })
//        builder.create()
//        builder.show()
    }

//    private fun sendReceipt(selectedTransaction: TransactionObject, receiptType: ReceiptType) {
//        val sendEmailProvider =
//            SendEmailTransactionProvider(this@TransactionListActivity, selectedTransaction)
//        sendEmailProvider.receiptType = receiptType
//        sendEmailProvider.addTo(Contact("cliente@gmail.com", "Nome do Cliente"))
//        sendEmailProvider.from = Contact("loja@gmail.com", "Nome do Estabelecimento")
//        sendEmailProvider.dialogMessage = "Enviando comprovante"
//        sendEmailProvider.connectionCallback = object : StoneCallbackInterface {
//            override fun onSuccess() {
//                Toast.makeText(applicationContext, "Enviado com sucesso", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onError() {
//                Toast.makeText(applicationContext, "Nao enviado", Toast.LENGTH_LONG).show()
//            }
//        }
//        sendEmailProvider.execute()
//    }
//
//    private fun printReceipt(receiptType: ReceiptType, transactionObject: TransactionObject) {
//        PrintController(
//            applicationContext,
//            PosPrintReceiptProvider(
//                applicationContext,
//                transactionObject, receiptType
//            )
//        ).print()
//    }
}
