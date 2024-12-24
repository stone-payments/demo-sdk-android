package br.com.stonesdk.sdkdemo.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import br.com.stone.posandroid.providers.PosPrintProvider
import br.com.stone.posandroid.providers.PosValidateTransactionByCardProvider
import br.com.stonesdk.sdkdemo.FeatureFlag
import br.com.stonesdk.sdkdemo.R
import br.com.stonesdk.sdkdemo.activities.devices.DevicesActivity
import br.com.stonesdk.sdkdemo.activities.main.MainScreen
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeActivity
import br.com.stonesdk.sdkdemo.activities.validation.ValidationActivity
import br.com.stonesdk.sdkdemo.databinding.ActivityMainBinding
import stone.application.enums.Action
import stone.application.interfaces.StoneActionCallback
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.ActiveApplicationProvider
import stone.providers.DisplayMessageProvider
import stone.providers.ReversalProvider
import stone.utils.Stone

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FeatureFlag.composeRefactorEnabled) {
            onCreateCompose()
        } else {
            onCreateVintage()
        }
    }


    private fun onCreateCompose(){
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }

    private fun onCreateVintage(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.transactionOption.setOnClickListener(this)
        binding.posTransactionOption.setOnClickListener(this)
        binding.pairedDevicesOption.setOnClickListener(this)
        binding.disconnectDeviceOption.setOnClickListener(this)
        binding.deactivateOption.setOnClickListener(this)
        binding.cancelTransactionsOption.setOnClickListener(this)
        binding.displayMessageOption.setOnClickListener(this)
        binding.listTransactionOption.setOnClickListener(this)
        binding.manageStoneCodeOption.setOnClickListener(this)
        binding.posValidateCardOption.setOnClickListener(this)
        binding.posPrinterProvider.setOnClickListener(this)
        binding.posMifareProvider.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        // Para cada nova opção na lista, um novo "case" precisa ser inserido aqui.
        when (v.id) {
            binding.pairedDevicesOption.id -> {
                val devicesIntent = Intent(this@MainActivity, DevicesActivity::class.java)
                startActivity(devicesIntent)
            }

            binding.transactionOption.id -> {
                // Verifica se o bluetooth esta ligado e se existe algum pinpad conectado.
                if (Stone.getPinpadListSize() > 0) {
                    val transactionIntent =
                        Intent(this@MainActivity, TransactionActivity::class.java)
                    startActivity(transactionIntent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Conecte-se a um pinpad.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            binding.listTransactionOption.id -> {
                val transactionListIntent =
                    Intent(this@MainActivity, TransactionListActivity::class.java)
                startActivity(transactionListIntent)
            }

            binding.displayMessageOption.id -> {
                if (Stone.getPinpadListSize() > 0) {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Digite a mensagem para mostrar no pinpad")
                    val editText = EditText(this@MainActivity)
                    builder.setView(editText)
                    builder.setPositiveButton("OK") { _, _ ->
                        val text = editText.text.toString()
                        val displayMessageProvider =
                            DisplayMessageProvider(
                                this@MainActivity,
                                text,
                                Stone.getPinpadFromListAt(0)
                            )
                        displayMessageProvider.execute()
                    }
                    builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

                    builder.show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Conecte-se a um pinpad.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            binding.cancelTransactionsOption.id -> {
                val reversalProvider = ReversalProvider(this)
                reversalProvider.dialogMessage = "Cancelando transações com erro"
                reversalProvider.connectionCallback = object : StoneCallbackInterface {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@MainActivity,
                            "Transações canceladas com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError() {
                        Toast.makeText(
                            this@MainActivity,
                            "Ocorreu um erro durante o cancelamento das tabelas: " + reversalProvider.listOfErrors,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                reversalProvider.execute()
            }

            binding.deactivateOption.id -> {
                val provider = ActiveApplicationProvider(this@MainActivity)
                provider.dialogMessage = "Desativando o aplicativo..."
                provider.dialogTitle = "Aguarde"
                provider.connectionCallback = object : StoneCallbackInterface {
                    override fun onSuccess() {
                        val mainIntent = Intent(this@MainActivity, ValidationActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }

                    override fun onError() {
                        Toast.makeText(
                            this@MainActivity,
                            "Erro na ativacao do aplicativo, verifique a lista de erros do provider",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("deactivateOption", "onError: " + provider.listOfErrors.toString())
                    }
                }
                provider.deactivate()
            }

            binding.disconnectDeviceOption.id -> {
                if (Stone.getPinpadListSize() > 0) {
                    val closeBluetoothConnectionIntent =
                        Intent(this@MainActivity, DisconnectPinpadActivity::class.java)
                    startActivity(closeBluetoothConnectionIntent)
                } else {
                    Toast.makeText(this, "Nenhum device Conectado", Toast.LENGTH_SHORT).show()
                }
            }

            binding.posTransactionOption.id -> {
                startActivity(Intent(this@MainActivity, PosTransactionActivity::class.java))
            }

            binding.manageStoneCodeOption.id -> {
                startActivity(Intent(this@MainActivity, ManageStoneCodeActivity::class.java))
            }

            binding.posValidateCardOption.id -> {
                val posValidateTransactionByCardProvider = PosValidateTransactionByCardProvider(this)
                posValidateTransactionByCardProvider.setConnectionCallback(object :
                    StoneActionCallback {
                    override fun onStatusChanged(action: Action) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, action.name, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onSuccess() {
                        runOnUiThread {
                            val transactionsWithCurrentCard = posValidateTransactionByCardProvider.transactionsWithCurrentCard
                            if (transactionsWithCurrentCard.isEmpty()) {
                                Toast.makeText(this@MainActivity, "Cartão não fez transação.", Toast.LENGTH_SHORT).show()
                            }
                            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                            Log.i("posValidateCardOption", "onSuccess: $transactionsWithCurrentCard")
                        }
                    }

                    override fun onError() {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                            Log.e("posValidateCardOption", "onError: " + posValidateTransactionByCardProvider.listOfErrors)
                        }
                    }
                })
                posValidateTransactionByCardProvider.execute()
            }

            binding.posPrinterProvider.id -> {
                val customPosPrintProvider = PosPrintProvider(applicationContext)
                customPosPrintProvider.addLine("PAN : " + "123")
                customPosPrintProvider.addLine("DATE/TIME : 01/01/1900")
                customPosPrintProvider.addLine("AMOUNT : 200.00")
                customPosPrintProvider.addLine("ATK : 123456789")
                customPosPrintProvider.addLine("Signature")
                customPosPrintProvider.addBitmap(BitmapFactory.decodeResource(resources, R.drawable.signature))
                customPosPrintProvider.connectionCallback = object : StoneCallbackInterface {
                    override fun onSuccess() {
                        Toast.makeText(applicationContext, "Recibo impresso", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError() {
                        Toast.makeText(applicationContext, "Erro ao imprimir: " + customPosPrintProvider.listOfErrors, Toast.LENGTH_SHORT).show()
                    }
                }
                customPosPrintProvider.execute()

                startActivity(Intent(this@MainActivity, MifareActivity::class.java))
            }

            binding.posMifareProvider.id -> {
                startActivity(Intent(this@MainActivity, MifareActivity::class.java))
            }

            else -> {}
        }
    }
}
