package br.com.stonesdk.sdkdemo.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope

import br.com.stonesdk.sdkdemo.FeatureFlag
import br.com.stonesdk.sdkdemo.activities.devices.DevicesActivity
import br.com.stonesdk.sdkdemo.activities.display.DisplayMessageActivity
import br.com.stonesdk.sdkdemo.activities.main.MainNavigationOption
import br.com.stonesdk.sdkdemo.activities.main.MainScreen
import br.com.stonesdk.sdkdemo.activities.main.MainViewModel
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeActivity
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionActivity
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionListActivity
import br.com.stonesdk.sdkdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

//import stone.application.enums.Action
//import stone.application.interfaces.StoneActionCallback
//import stone.application.interfaces.StoneCallbackInterface
//import stone.providers.ActiveApplicationProvider
//import stone.providers.DisplayMessageProvider
//import stone.utils.Stone

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FeatureFlag.composeRefactorEnabled) {
            onCreateCompose()
        } else {
            onCreateVintage()
        }

        lifecycleScope.launch {
            mainViewModel.uiState.collectLatest { uiState ->
                val navigateOption = uiState.navigateToOption
                if (navigateOption != null) {
                    processNavigationFromViewModel(navigateOption)
                    mainViewModel.doneNavigating()
                }
            }
        }
    }

    private fun onCreateCompose() {
        setContent {
            MaterialTheme {
                MainScreen(viewModel = mainViewModel)
            }
        }
    }

    private fun onCreateVintage() {
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
            binding.listTransactionOption.id -> startGenericTransactionList()
            binding.cancelTransactionsOption.id -> startGenericCancelErrorTransaction()
            binding.manageStoneCodeOption.id -> startGenericManageStoneCode()
            binding.deactivateOption.id -> startGenericDeactivate()

            binding.pairedDevicesOption.id -> startPinpadPairedDevicesActivity()
            binding.transactionOption.id -> startPinpadTransaction()
            binding.displayMessageOption.id -> startPinpadDisplayMessage()
            binding.disconnectDeviceOption.id -> startPinpadDisconnect()

            binding.posTransactionOption.id -> starPosAndroidTransaction()
            binding.posValidateCardOption.id -> startPosAndroidCardValidation()
            binding.posPrinterProvider.id -> startPosAndroidPrinter()
            binding.posMifareProvider.id -> startPosAndroidMifare()

            else -> {}
        }
    }

    private fun processNavigationFromViewModel(navigateOption: MainNavigationOption) {
        when (navigateOption) {
            MainNavigationOption.GeneralCancelErrorTransactions -> startGenericCancelErrorTransaction()
            MainNavigationOption.GeneralDeactivate -> startGenericDeactivate()
            MainNavigationOption.GeneralListTransactions -> startGenericTransactionList()
            MainNavigationOption.GeneralManageStoneCodes -> startGenericManageStoneCode()

            MainNavigationOption.PinpadDisconnect -> startPinpadDisconnect()
            MainNavigationOption.PinpadMakeTransaction -> startPinpadTransaction()
            MainNavigationOption.PinpadPairedDevices -> startPinpadPairedDevicesActivity()
            MainNavigationOption.PinpadShowMessage -> startPinpadDisplayMessage()

            MainNavigationOption.PosMakeTransaction -> starPosAndroidTransaction()
            MainNavigationOption.PosMifareProvider -> startPosAndroidMifare()
            MainNavigationOption.PosPrinterProvider -> startPosAndroidPrinter()
            MainNavigationOption.PosValidateByCard -> startPosAndroidCardValidation()
        }
    }

    private fun startGenericTransactionList() {
        val transactionListIntent =
            Intent(this@MainActivity, TransactionListActivity::class.java)
        startActivity(transactionListIntent)
    }

    private fun startGenericCancelErrorTransaction() {
        mainViewModel.revertTransactionsWithErrors()
//        val reversalProvider = ReversalProvider(this)
//        reversalProvider.dialogMessage = "Cancelando transações com erro"
//        reversalProvider.connectionCallback = object : StoneCallbackInterface {
//            override fun onSuccess() {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Transações canceladas com sucesso",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onError() {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Ocorreu um erro durante o cancelamento das tabelas: " + reversalProvider.listOfErrors,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//        reversalProvider.execute()
    }

    private fun startGenericManageStoneCode() {
        startActivity(Intent(this@MainActivity, ManageStoneCodeActivity::class.java))
    }

    private fun startGenericDeactivate() {

//        val provider = ActiveApplicationProvider(this@MainActivity)
//        provider.dialogMessage = "Desativando o aplicativo..."
//        provider.dialogTitle = "Aguarde"
//        provider.connectionCallback = object : StoneCallbackInterface {
//            override fun onSuccess() {
//                val mainIntent = Intent(this@MainActivity, ValidationActivity::class.java)
//                startActivity(mainIntent)
//                finish()
//            }
//
//            override fun onError() {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Erro na ativacao do aplicativo, verifique a lista de erros do provider",
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.e("deactivateOption", "onError: " + provider.listOfErrors.toString())
//            }
//        }
//        provider.deactivate()
    }


    private fun startPinpadPairedDevicesActivity() {
        val devicesIntent = Intent(this@MainActivity, DevicesActivity::class.java)
        startActivity(devicesIntent)
    }

    private fun startPinpadTransaction() {
        val transactionIntent = Intent(this@MainActivity, TransactionActivity::class.java)
        startActivity(transactionIntent)
    }

    private fun startPinpadDisplayMessage() {
        val displayMessageIntent = Intent(this@MainActivity, DisplayMessageActivity::class.java)
        startActivity(displayMessageIntent)
    }

    private fun startPinpadDisconnect() {
//        if (Stone.getPinpadListSize() > 0) {
//            val closeBluetoothConnectionIntent =
//                Intent(this@MainActivity, DisconnectPinpadActivity::class.java)
//            startActivity(closeBluetoothConnectionIntent)
//        } else {
//            Toast.makeText(this, "Nenhum device Conectado", Toast.LENGTH_SHORT).show()
//        }
    }


    private fun starPosAndroidTransaction() {
//        startActivity(Intent(this@MainActivity, PosTransactionActivity::class.java))
    }

    private fun startPosAndroidCardValidation() {
//        val posValidateTransactionByCardProvider = PosValidateTransactionByCardProvider(this)
//        posValidateTransactionByCardProvider.setConnectionCallback(object :
//            StoneActionCallback {
//            override fun onStatusChanged(action: Action) {
//                runOnUiThread {
//                    Toast.makeText(this@MainActivity, action.name, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onSuccess() {
//                runOnUiThread {
//                    val transactionsWithCurrentCard =
//                        posValidateTransactionByCardProvider.transactionsWithCurrentCard
//                    if (transactionsWithCurrentCard.isEmpty()) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Cartão não fez transação.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
//                    Log.i("posValidateCardOption", "onSuccess: $transactionsWithCurrentCard")
//                }
//            }
//
//            override fun onError() {
//                runOnUiThread {
//                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
//                    Log.e(
//                        "posValidateCardOption",
//                        "onError: " + posValidateTransactionByCardProvider.listOfErrors
//                    )
//                }
//            }
//        })
//        posValidateTransactionByCardProvider.execute()
    }

    private fun startPosAndroidPrinter() {
//        val customPosPrintProvider = PosPrintProvider(applicationContext)
//        customPosPrintProvider.addLine("PAN : " + "123")
//        customPosPrintProvider.addLine("DATE/TIME : 01/01/1900")
//        customPosPrintProvider.addLine("AMOUNT : 200.00")
//        customPosPrintProvider.addLine("ATK : 123456789")
//        customPosPrintProvider.addLine("Signature")
//        customPosPrintProvider.addBitmap(
//            BitmapFactory.decodeResource(
//                resources,
//                R.drawable.signature
//            )
//        )
//        customPosPrintProvider.connectionCallback = object : StoneCallbackInterface {
//            override fun onSuccess() {
//                Toast.makeText(applicationContext, "Recibo impresso", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onError() {
//                Toast.makeText(
//                    applicationContext,
//                    "Erro ao imprimir: " + customPosPrintProvider.listOfErrors,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//        customPosPrintProvider.execute()
//
//        startActivity(Intent(this@MainActivity, MifareActivity::class.java))
    }

    private fun startPosAndroidMifare() {
        startActivity(Intent(this@MainActivity, MifareActivity::class.java))
    }
}
